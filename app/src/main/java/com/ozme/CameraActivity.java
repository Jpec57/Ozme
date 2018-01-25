package com.ozme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wefika.horizontalpicker.HorizontalPicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
DOCS :
NUMBER PICKER : https://github.com/blazsolar/HorizontalPicker
 */

public class CameraActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener{
    RelativeLayout cameraPreviewLayout;
    private ImageSurfaceView mImageSurfaceView;
    private Camera camera;
    private ImageView back, close;
    private Button capture;
    private Button capture2;
    private Button capture3;
    private ImageView save, audio_on, audio_off;
    private MediaRecorder mMediaRecorder;
    private ProgressBar progressBar;

    private File mOutputFile;
    boolean isRecording = false;
    private static final String TAG = "Recorder";
    private TextureView mPreview;

    private ImageView switcher, photoOrVideo;
    private ImageView gallery;
    private ImageView flash;
    private TextView oz;

    private LinearLayout options;
    private LinearLayout options2;
    private LinearLayout options3;

    private File file;
    private Bitmap picture;
    private HorizontalPicker numberPicker2;
    private int time;
    private Button send;
    private TextView friends;
    private long chosenFriend = 0L;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    String type = "image";
    public FirebaseStorage firebaseStorage;
    byte[] dataByteArray;
    int currentCameraId = -1;
    boolean hasFlash = false;
    private static final int REQUEST_ID = 1;
    private static final int HALF = 2;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FULL SCREEN
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.camera);
        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

        //Check if we have clicked on a "Oz" button from the timeline
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            chosenFriend=extras.getLong("strangerId");
        }

        //Purge cache directory
        trimCache(CameraActivity.this);

        //TODO
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setProgress(40);

        bindingView();

    }

    private void setView(int i) {
        switch (i) {
            case 1:
                options.setVisibility(View.VISIBLE);
                options2.setVisibility(View.GONE);
                options3.setVisibility(View.GONE);

                capture.setVisibility(View.VISIBLE);
                capture2.setVisibility(View.GONE);
                capture3.setVisibility(View.GONE);
                break;
            case 2:
                options.setVisibility(View.GONE);
                options2.setVisibility(View.VISIBLE);
                options3.setVisibility(View.GONE);

                capture.setVisibility(View.GONE);
                capture2.setVisibility(View.GONE);
                capture3.setVisibility(View.VISIBLE);


                oz.setVisibility(View.GONE);
                close.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                findViewById(R.id.sablier).setVisibility(View.GONE);

                break;
            case 3:
                options.setVisibility(View.GONE);
                options2.setVisibility(View.GONE);
                options3.setVisibility(View.VISIBLE);

                oz.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
                findViewById(R.id.sablier).setVisibility(View.VISIBLE);

                capture.setVisibility(View.GONE);
                capture2.setVisibility(View.GONE);
                capture3.setVisibility(View.GONE);

                //Check whether we already have someone to whom we can send the message
                if (chosenFriend != 0L){
                    friends.setText(""+chosenFriend);
                    DatabaseReference ref = database.getReference("/data/users/"+chosenFriend+"/username");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            friends.setText(dataSnapshot.getValue(String.class));
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

                break;

        }
    }

    private Camera checkDeviceCamera() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 50);
        }
        Camera mCamera = null;
        try {
            mCamera = CameraHelper.getDefaultBackFacingCameraInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCamera;
    }


    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            picture = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (picture == null) {
                Toast.makeText(CameraActivity.this, "Problème en prenant une photo", Toast.LENGTH_SHORT).show();
                return;
            }
            picture = RotateBitmap(picture);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
            dataByteArray = stream.toByteArray();
            camera.stopPreview();
            setView(2);
            close.setVisibility(View.VISIBLE);
        }
    };


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    try {
                        camera.stopPreview();
                    } catch (Exception e) {

                    }
                    Intent intent7 = new Intent(getApplicationContext(), MainTimelineFragment.class);
                    startActivity(intent7);
                    break;
                case R.id.delete:
                    Intent intent = new Intent(CameraActivity.this, CameraActivity.class);
                    startActivity(intent);
                    break;

                case R.id.photoOrVideo:
                    if (findViewById(R.id.capture).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.capture).setVisibility(View.GONE);
                        findViewById(R.id.capture2).setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        layoutParams.addRule(RelativeLayout.ABOVE, R.id.capture2);
                        findViewById(R.id.oz).setLayoutParams(layoutParams);
                    } else {
                        findViewById(R.id.capture2).setVisibility(View.GONE);
                        findViewById(R.id.capture).setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        layoutParams.addRule(RelativeLayout.ABOVE, R.id.capture);
                        findViewById(R.id.oz).setLayoutParams(layoutParams);
                    }
                    break;
                case R.id.switcher:
                    try {
                        camera.stopPreview();
                        camera.release();
                    } catch (Exception e) {

                    }
                    if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                        camera = CameraHelper.getDefaultFrontFacingCameraInstance();
                    } else {
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                        camera = CameraHelper.getDefaultBackFacingCameraInstance();
                    }

                        /*
                        Camera.Parameters parameters = camera.getParameters();
                        List<Camera.Size> supportedSizes = parameters.getSupportedPictureSizes();
                        int w = 0, h = 0;
                        for (Camera.Size size : supportedSizes) {
                            if (size.width > w || size.height > h) {
                                w = size.width;
                                h = size.height;
                            }

                        }
                        setCameraDisplayOrientation(CameraActivity.this, currentCameraId, camera);
                        parameters.setJpegQuality(100);
                        parameters.setJpegThumbnailQuality(100);
                        parameters.setPictureSize(w, h);
                        camera.setParameters(parameters);*/
                    Camera.Parameters params = camera.getParameters();
                    List<Camera.Size> sizes = params.getSupportedPictureSizes();
                    Camera.Size size = sizes.get(0);
                    for (int i = 0; i < sizes.size(); i++) {
                        if (sizes.get(i).width > size.width)
                            size = sizes.get(i);
                    }
                    params.setPictureSize(size.width, size.height);
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
                    params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
                    params.setExposureCompensation(0);
                    params.setPictureFormat(ImageFormat.JPEG);
                    params.setJpegQuality(100);
                    params.setRotation(90);
                    camera.setParameters(params);

                    camera = Camera.open(currentCameraId);
                    cameraPreviewLayout.removeView(cameraPreviewLayout.getChildAt(0));
                    mImageSurfaceView = new ImageSurfaceView(CameraActivity.this, camera);
                    cameraPreviewLayout.addView(mImageSurfaceView, 0);


                    break;
                case R.id.gallery:
                    Intent intent1 = new Intent();
                    intent1.setAction(Intent.ACTION_GET_CONTENT);
                    intent1.addCategory(Intent.CATEGORY_OPENABLE);
                    intent1.setType("image/*");
                    startActivityForResult(intent1, REQUEST_ID);
                    break;
                case R.id.flash:
                    setFlash();
                    break;
                case R.id.capture3:
                    if (findViewById(R.id.options3).getVisibility() == View.VISIBLE) {

                    } else {
                        setView(3);
                    }
                    /*
                    Intent intent1 = new Intent(getApplicationContext(), MainTimelineFragment.class);
                    startActivity(intent1);*/
                    break;
                case R.id.save:
                    if (mOutputFile == null) {
                        new fileFromBitmap().execute();
                    } else {
                        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory("Ozme") + "/Ozme");

                        if (!mediaStorageDir.exists()) {
                            if (!mediaStorageDir.mkdirs()) {
                                Log.d("JPEC", "failed to create directory");
                            }
                        }
                        moveFile(mOutputFile.getParentFile().getPath() + "/", mOutputFile.getName(), mediaStorageDir.getPath() + "/");
                    }
                    break;
                //TODO handle sound here
                case R.id.audio_on:
                    findViewById(R.id.audio_on).setVisibility(View.GONE);
                    findViewById(R.id.audio_off).setVisibility(View.VISIBLE);
                    break;
                case R.id.audio_off:
                    findViewById(R.id.audio_on).setVisibility(View.VISIBLE);
                    findViewById(R.id.audio_off).setVisibility(View.GONE);
                    break;
                case R.id.send:
                    if (chosenFriend == 0L) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(CameraActivity.this);
                        dialog.setMessage("Choississez une personne à qui envoyer votre photo/vidéo");
                        dialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    }
                    sendVideoPicture(chosenFriend);
                    Intent intent8 = new Intent(CameraActivity.this, MainTimelineFragment.class);
                    startActivity(intent8);
                    break;
                case R.id.friends:
                    final Dialog dialog = new Dialog(CameraActivity.this);
                    dialog.setTitle("Choissis la personne à qui tu veux envoyer cette photo/vidéo");
                    dialog.setContentView(R.layout.list_view_prop);
                    final ListView listView = (ListView) dialog.findViewById(R.id.listView);
                    DatabaseReference ami = database.getReference("/data/users/" + Profile.getCurrentProfile().getId() + "/messagers");
                    ami.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<Long> items = null;
                            if (dataSnapshot.exists()) {
                                GenericTypeIndicator<List<Long>> genericTypeIndicator = new GenericTypeIndicator<List<Long>>() {
                                };
                                items = dataSnapshot.getValue(genericTypeIndicator);
                                final List<String> itemsName = new ArrayList<String>();
                                for (int i=0; i <items.size(); i++){
                                    DatabaseReference nameFriend = database.getReference("/data/users/"+items.get(i)+"/username");
                                    final List<Long> finalItems = items;
                                    nameFriend.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            itemsName.add(dataSnapshot.getValue(String.class));

                                            ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(CameraActivity.this, android.R.layout.simple_list_item_1, itemsName);
                                            listView.setAdapter(itemsAdapter);
                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    chosenFriend = finalItems.get(position);
                                                    dialog.dismiss();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }




                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    dialog.show();
                    break;

            }
        }
    };

    public static void setCameraDisplayOrientation(AppCompatActivity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    //VIDEO--------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        // if we are using MediaRecorder, release it first
        releaseMediaRecorder();
        // release the camera immediately on pause event
        releaseCamera();
    }

    public void onCaptureClick(View view) {
        if (isRecording) {
            // stop recording and release camera
            try {
                mMediaRecorder.stop();  // stop the recording
            } catch (RuntimeException e) {
                // RuntimeException is thrown when stop() is called immediately after start().
                // In this case the output file is not properly constructed ans should be deleted.
                Log.d(TAG, "RuntimeException: stop() is called immediately after start()");
                //noinspection ResultOfMethodCallIgnored
                mOutputFile.delete();
            }
            releaseMediaRecorder(); // release the MediaRecorder object
            camera.lock();         // take camera access back from MediaRecorder

            // inform the user that recording has stopped
            //setCaptureButtonText("Capture");
            isRecording = false;
            releaseCamera();
            setView(3);

            //Video preview
            final VideoView videoView = (VideoView)findViewById(R.id.video_view);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoPath(mOutputFile.getPath());
            videoView.start();
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoView.start();
                }
            });
            //End video preview

            //TODO we have to encode in base64 String the video
            new encodeVideoBase64().execute();
            //TODO We have to show the preview to the user
        } else {
            //Background task for preparing to capture
            new CameraActivity.MediaPrepareTask().execute(null, null, null);

        }
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
//start playback
    }



    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            // clear recorder configuration
            mMediaRecorder.reset();
            // release the recorder object
            mMediaRecorder.release();
            mMediaRecorder = null;
            // Lock camera for later use i.e taking it back from MediaRecorder.
            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
            camera.lock();
        }
    }

    private void releaseCamera() {
        if (camera != null) {
            // release the camera for other applications
            camera.release();
            camera = null;
        }
    }

    private boolean prepareVideoRecorder() {

        // BEGIN_INCLUDE (configure_preview)
        camera = CameraHelper.getDefaultCameraInstance();

        // We need to make sure that our preview and recording video size are supported by the
        // camera. Query camera to find all the sizes and choose the optimal size given the
        // dimensions of our preview surface.
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();
        Camera.Size optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes,
                mSupportedPreviewSizes, mPreview.getWidth(), mPreview.getHeight());

        // Use the same size for recording profile.
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        profile.videoFrameWidth = optimalSize.width;
        profile.videoFrameHeight = optimalSize.height;

        // likewise for the camera object itself.
        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
        if (parameters.getPreviewSize().width > parameters.getPreviewSize().height) {
            camera.setDisplayOrientation(90);
        }
        camera.setParameters(parameters);
        try {
            // Requires API level 11+, For backward compatibility use {@link setPreviewDisplay}
            // with {@link SurfaceView}
            camera.setPreviewTexture(mPreview.getSurfaceTexture());
        } catch (IOException e) {
            Log.e(TAG, "Surface texture is unavailable or unsuitable" + e.getMessage());
            return false;
        }
        mMediaRecorder = new MediaRecorder();
        if (parameters.getPreviewSize().width > parameters.getPreviewSize().height) {
            mMediaRecorder.setOrientationHint(90);
        }


        // Step 1: Unlock and set camera to MediaRecorder
        camera.unlock();
        mMediaRecorder.setCamera(camera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(profile);

        // Step 4: Set output file
        /*
        mOutputFile = CameraHelper.getOutputMediaFile(MEDIA_TYPE_VIDEO);
        if (mOutputFile == null) {
            return false;
        }*/
        //TODO delete the following line to save the video not in the cache
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        mOutputFile = new File(CameraActivity.this.getCacheDir(), "OZME_VID_" + timeStamp + ".mp4");


        //mMediaRecorder.setOutputFile(getExternalFilesDir(CameraHelper.MEDIA_TYPE_VIDEO));
        mMediaRecorder.setOutputFile(mOutputFile.getPath());

        // Step 5: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }


    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();

                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                CameraActivity.this.finish();
            }
            // inform the user that recording has started
        }

    }

    public static Bitmap RotateBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.preRotate(90);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @SuppressLint("StaticFieldLeak")
    public class fileFromBitmap extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                Log.e("JPEC", "Permission denied");
                return null;
            }
            File dir = new File(Environment.getExternalStoragePublicDirectory(
                    "Ozme"), "Ozme");

            // Create the storage directory if it does not exist
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.e("JPEC", "failed to create directory");
                    return null;
                }
            }
            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            //END

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 100, bytes);

            //use it to share the picture in database
            byte[] byteArray = bytes.toByteArray();

            file = new File(dir.getPath() + File.separator +
                    "OZME_IMG_" + timeStamp + ".png");
            try {
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(byteArray);
                fo.flush();
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //MTP Scan to make it visible to the user
            dir.setExecutable(true);
            dir.setReadable(true);
            dir.setWritable(true);
            MediaScannerConnection.scanFile(CameraActivity.this, new String[]{file.getPath()}, null, null);

            Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri fileContentUri = Uri.fromFile(dir);
            mediaScannerIntent.setData(fileContentUri);
            CameraActivity.this.sendBroadcast(mediaScannerIntent);

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // back to main thread after finishing doInBackground
            // update your UI or take action after
            // exp; make progressbar gone
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    save.setVisibility(View.GONE);
                    Toast.makeText(CameraActivity.this, "Votre image/vidéo a bien été enregistrée", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            Log.e("JPEC", "Error deleting cache file");
        }
    }

    private void setFlash() {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            if (!hasFlash) {
                Camera.Parameters p = camera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);

            } else {
                Intent intent = new Intent(CameraActivity.this, CameraActivity.class);
                startActivity(intent);
            }

        }
        hasFlash = !hasFlash;

    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    private void moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();
            dir.setExecutable(true);
            dir.setReadable(true);
            dir.setWritable(true);
            MediaScannerConnection.scanFile(this, new String[]{outputPath + inputFile}, null, null);

            Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri fileContentUri = Uri.fromFile(dir);
            mediaScannerIntent.setData(fileContentUri);
            this.sendBroadcast(mediaScannerIntent);
            Log.e("JPEC", dir.getPath());


        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        save.setVisibility(View.GONE);
        Toast.makeText(this, "Votre vidéo a bien été sauvegardée", Toast.LENGTH_SHORT).show();

    }


    @SuppressLint("StaticFieldLeak")
    public class encodeVideoBase64 extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            File file = new File(mOutputFile.getPath());
            byte[] bytesArray = new byte[(int) file.length()];
            dataByteArray = bytesArray;
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                try {
                    fis.read(bytesArray); //read file into bytes[]
                } catch (IOException e) {
                    Log.e("JPEC", e.getMessage());
                }
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.e("JPEC", e.getMessage());

                }
            } catch (FileNotFoundException e) {
                Log.e("JPEC", e.getMessage());
            }
            type = "video";
            return null;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
        }


    }


    private void sendVideoPicture(final long strangerId) {
        //We get the user's Id
        final long persoId = Long.parseLong(Profile.getCurrentProfile().getId());
        //We build the message we have to send to the different selected users
        final ConversationActivity.Message message = new ConversationActivity.Message();
        message.setText("Image/Vidéo");
        message.setType(type);
        message.setSender(persoId);
        message.setTime(time);

        //Add stranger to conversation's friends of user
        databaseReference=database.getReference("data/users/"+persoId+"/messagers");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(strangerId+"").exists()){
                    GenericTypeIndicator<List<Long>> listGenericTypeIndicator = new GenericTypeIndicator<List<Long>>(){};
                    List<Long> friends = dataSnapshot.getValue(listGenericTypeIndicator);
                    friends.add(strangerId);
                    dataSnapshot.getRef().setValue(friends);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String path = "";
        String pathStorage = "";
        StorageReference storageReference;
        if (strangerId < persoId) {
            path = strangerId + "/" + persoId;
            pathStorage = "responses/" + path + "/" + System.currentTimeMillis();
            databaseReference = database.getReference("data/conversations/" + path);
            storageReference = firebaseStorage.getReference(pathStorage);
        } else {
            path = persoId + "/" + strangerId;
            pathStorage = "responses/" + path + "/" + System.currentTimeMillis();
            databaseReference = database.getReference("data/conversations/" + path);
            storageReference = firebaseStorage.getReference(pathStorage);
        }

        //Using Storage
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("text", "Second chosenFriend with online storage")
                .build();
        UploadTask uploadTask = storageReference.putBytes(dataByteArray, metadata);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CameraActivity.this, "Votre photo/vidéo a bien été envoyée", Toast.LENGTH_SHORT).show();

                Uri url = taskSnapshot.getDownloadUrl();
                message.setData(url.toString());
                databaseReference.child(System.currentTimeMillis() + "").setValue(message);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("JPEC", "échec : " + e.getLocalizedMessage());
            }
        });


    }

    private void bindingView() {
        switcher = (ImageView) findViewById(R.id.switcher);
        flash = (ImageView) findViewById(R.id.flash);
        gallery = (ImageView) findViewById(R.id.gallery);
        options = (LinearLayout) findViewById(R.id.options);
        options2 = (LinearLayout) findViewById(R.id.options2);
        options3 = (LinearLayout) findViewById(R.id.options3);
        oz = (TextView) findViewById(R.id.oz);
        capture3 = (Button) findViewById(R.id.capture3);
        audio_off = (ImageView) findViewById(R.id.audio_off);
        audio_on = (ImageView) findViewById(R.id.audio_on);
        save = (ImageView) findViewById(R.id.save);
        friends = (TextView) findViewById(R.id.friends);
        send = (Button) findViewById(R.id.send);
        cameraPreviewLayout = (RelativeLayout) findViewById(R.id.cameraPreview);
        back = (ImageView) findViewById(R.id.back);
        photoOrVideo = (ImageView) findViewById(R.id.photoOrVideo);
        close = (ImageView) findViewById(R.id.delete);
        numberPicker2 = (HorizontalPicker) findViewById(R.id.numberPicker2);
        mPreview = (TextureView) findViewById(R.id.surface_view);
        capture = (Button) findViewById(R.id.capture);


        photoOrVideo.setOnClickListener(onClickListener);
        switcher.setOnClickListener(onClickListener);
        flash.setOnClickListener(onClickListener);
        gallery.setOnClickListener(onClickListener);
        capture3.setOnClickListener(onClickListener);
        save.setOnClickListener(onClickListener);
        audio_off.setOnClickListener(onClickListener);
        audio_on.setOnClickListener(onClickListener);
        send.setOnClickListener(onClickListener);
        friends.setOnClickListener(onClickListener);


        final CharSequence[] values = new CharSequence[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
        numberPicker2.setValues(values);
        numberPicker2.computeScroll();
        numberPicker2.setOnItemClickedListener(new HorizontalPicker.OnItemClicked() {
            @Override
            public void onItemClicked(int index) {
                numberPicker2.setSelectedItem(index);
            }
        });
        numberPicker2.setOnItemSelectedListener(new HorizontalPicker.OnItemSelected() {
            @Override
            public void onItemSelected(int index) {
                time = Integer.parseInt(values[index].toString());
            }
        });

        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();


        back.setOnClickListener(onClickListener);
        close.setOnClickListener(onClickListener);
        camera = checkDeviceCamera();
        mImageSurfaceView = new ImageSurfaceView(this, camera);
        cameraPreviewLayout.addView(mImageSurfaceView, 0);
        capture2 = (Button) findViewById(R.id.capture2);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, pictureCallback);
                setView(2);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ID && resultCode == Activity.RESULT_OK) {
            InputStream stream = null;
            try {
                stream = getContentResolver().openInputStream(data.getData());

                Bitmap original = BitmapFactory.decodeStream(stream);
                try {
                    camera.stopPreview();
                } catch (Exception e) {

                }
                //We create a scaled bitmap in order to share quicker our original bitmap
                Bitmap created = Bitmap.createScaledBitmap(original,
                        original.getWidth() / HALF, original.getHeight() / HALF, true);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                created.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                //Don't forget to set the dataByteArray for firebase
                dataByteArray = outputStream.toByteArray();

                //Show the bitmap to the user (original)
                ImageView imageView = (ImageView) findViewById(R.id.galleryResult);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(original);
                setView(3);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
