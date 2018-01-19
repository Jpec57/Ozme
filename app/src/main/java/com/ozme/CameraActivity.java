package com.ozme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.CamcorderProfile;
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
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class CameraActivity extends AppCompatActivity {
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
    boolean isRecording=false;
    private static final String TAG = "Recorder";
    private TextureView mPreview;

    private ImageView switcher;
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

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    String encoded="";
    String type="image";
    public FirebaseStorage firebaseStorage;
    byte[] dataByteArray;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FULL SCREEN
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.camera);

        //Purge cache directory
        trimCache(CameraActivity.this);

        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setProgress(40);

        bindingView();

    }

    private void setView(int i){
        switch(i){
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
            case 3 :
                options.setVisibility(View.GONE);
                options2.setVisibility(View.GONE);
                options3.setVisibility(View.VISIBLE);

                oz.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
                findViewById(R.id.sablier).setVisibility(View.VISIBLE);

                capture3.setVisibility(View.INVISIBLE);

                break;

        }
    }

    private Camera checkDeviceCamera(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 50);
        }
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("JPEC", e.getLocalizedMessage());
        }
        return mCamera;
    }


    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            picture = BitmapFactory.decodeByteArray(data, 0, data.length);
            if(picture==null){
                Log.e("JPEC", "Fail in taking photo");
                return;
            }
            picture = RotateBitmap(picture);
            //TO DELETE

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            dataByteArray=byteArray;

            //END
            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            galleryAddPic();
            camera.stopPreview();
            setView(2);
            close.setVisibility(View.VISIBLE);

        }
    };



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back:
                    capture.setVisibility(View.VISIBLE);
                    close.setVisibility(View.GONE);
                    camera.startPreview();
                    break;
                case R.id.delete:
                    Intent intent=new Intent(CameraActivity.this, CameraActivity.class);
                    startActivity(intent);
                    break;

                case R.id.switcher:
                    if (findViewById(R.id.capture).getVisibility()==View.VISIBLE){
                        findViewById(R.id.capture).setVisibility(View.GONE);
                        findViewById(R.id.capture2).setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        layoutParams.addRule(RelativeLayout.ABOVE, R.id.capture2);
                        findViewById(R.id.oz).setLayoutParams(layoutParams);
                    }else{
                        findViewById(R.id.capture2).setVisibility(View.GONE);
                        findViewById(R.id.capture).setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        layoutParams.addRule(RelativeLayout.ABOVE, R.id.capture);
                        findViewById(R.id.oz).setLayoutParams(layoutParams);
                    }
                    break;
                case R.id.gallery:
                    break;
                case R.id.flash:
                    break;
                case R.id.capture3:
                    if (findViewById(R.id.options3).getVisibility()==View.VISIBLE){

                    }else{
                        setView(3);
                    }
                    /*
                    Intent intent1 = new Intent(getApplicationContext(), MainTimelineFragment.class);
                    startActivity(intent1);*/
                    break;
                case R.id.save :
                    if (mOutputFile==null){
                        new fileFromBitmap().execute();
                    }else{
                        File mediaStorageDir=new File(Environment.getExternalStoragePublicDirectory("Ozme")+"/Ozme");

                        if (! mediaStorageDir.exists()){
                            if (! mediaStorageDir.mkdirs()) {
                                Log.d("JPEC", "failed to create directory");
                            }
                        }
                        moveFile(mOutputFile.getParentFile().getPath()+"/", mOutputFile.getName(),mediaStorageDir.getPath()+"/");
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
                    //TODO
                    List<Long> test= new ArrayList<Long>(){};
                    test.add(11111111L);
                    test.add(1155490200L);
                    sendVideoPicture(test);
                    Intent intent1=new Intent(CameraActivity.this, MainTimelineFragment.class);
                    startActivity(intent1);
                    break;
                case R.id.friends:
                    break;

            }
        }
    };

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                "Ozme"), "Ozme");
        Uri contentUri = Uri.fromFile(mediaStorageDir);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
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
            setView(2);
            //TODO we have to encode in base64 String the video
            new encodeVideoBase64().execute();

            //TODO We have to show the preview to the user

            // END_INCLUDE(stop_release_media_recorder)

        } else {
            //Background task for preparing to capture
            new CameraActivity.MediaPrepareTask().execute(null, null, null);

        }
    }

    private void releaseMediaRecorder(){
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
    private void releaseCamera(){
        if (camera != null){
            // release the camera for other applications
            camera.release();
            camera = null;
        }
    }
    private boolean prepareVideoRecorder(){

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
        if (parameters.getPreviewSize().width > parameters.getPreviewSize().height){
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
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT );
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
        mOutputFile=new File(CameraActivity.this.getCacheDir(), "OZME_VID_"+ timeStamp + ".mp4");


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

    public static Bitmap RotateBitmap(Bitmap source)
    {
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
                return  null;
            }
            File dir = new File(Environment.getExternalStoragePublicDirectory(
                    "Ozme"), "Ozme");

            // Create the storage directory if it does not exist
            if (! dir.exists()){
                if (! dir.mkdirs()) {
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
            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            file = new File(dir.getPath() + File.separator +
                    "OZME_IMG_"+ timeStamp + ".png");            try {
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
            MediaScannerConnection.scanFile(CameraActivity.this, new String[] {file.getPath()}, null, null);

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
            File dir = new File (outputPath);
            if (!dir.exists())
            {
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
            MediaScannerConnection.scanFile(this, new String[] {outputPath + inputFile}, null, null);

            Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri fileContentUri = Uri.fromFile(dir);
            mediaScannerIntent.setData(fileContentUri);
            this.sendBroadcast(mediaScannerIntent);
            Log.e("JPEC", dir.getPath());



        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        save.setVisibility(View.GONE);
        Toast.makeText(this, "Votre vidéo a bien été sauvegardée", Toast.LENGTH_SHORT).show();

    }


    @SuppressLint("StaticFieldLeak")
    public class encodeVideoBase64 extends AsyncTask<Void, Void, Boolean>  {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            File file = new File(mOutputFile.getPath());
            byte[] bytesArray = new byte[(int) file.length()];
            dataByteArray=bytesArray;
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
            //Use it to share the video in the database
            encoded=Base64.encodeToString(bytesArray, Base64.DEFAULT);
            type="video";
            Log.e("JPEC", "SUCCESS");
            return null;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
        }


    }


    private void sendVideoPicture(List<Long> friends){
        //We get the user's Id
        long persoId = Long.parseLong(Profile.getCurrentProfile().getId());
        //We build the message we have to send to the different selected users
        final ConversationActivity.Message message = new ConversationActivity.Message();
        message.setText("Ceci est un test pour les vidéos et photos");
        message.setType(type);
        message.setData(encoded);
        message.setSender(persoId);
        message.setTime(time);

        String path="";
        String pathStorage="";
        StorageReference storageReference;

        //Sending loop
        for (int k=0; k < friends.size(); k++){

            long strangerId = friends.get(k);
            if (strangerId < persoId){
                path=strangerId+"/"+persoId;
                pathStorage="responses/"+path+"/"+System.currentTimeMillis();
                databaseReference=database.getReference("data/conversations/"+ path);
                storageReference = firebaseStorage.getReference(pathStorage);


            }else{
                path=persoId+"/"+strangerId;
                pathStorage="responses/"+path+"/"+System.currentTimeMillis();
                databaseReference=database.getReference("data/conversations/"+ path);
                storageReference = firebaseStorage.getReference(pathStorage);


            }
            //Using Storage
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setCustomMetadata("text", "First test with online storage")
                    .build();
            UploadTask uploadTask = storageReference.putBytes(dataByteArray, metadata);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(CameraActivity.this, "Upload success", Toast.LENGTH_SHORT).show();

                    Uri url= taskSnapshot.getDownloadUrl();
                    message.setData(url.toString());
                    databaseReference.child(System.currentTimeMillis()+"").setValue(message);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("JPEC", e.getLocalizedMessage());
                }
            });


        }

    }
    private void bindingView(){
        switcher=(ImageView)findViewById(R.id.switcher);
        flash=(ImageView)findViewById(R.id.flash);
        gallery=(ImageView)findViewById(R.id.gallery);
        options=(LinearLayout)findViewById(R.id.options);
        options2=(LinearLayout)findViewById(R.id.options2);
        options3=(LinearLayout)findViewById(R.id.options3);
        oz=(TextView)findViewById(R.id.oz);
        capture3=(Button)findViewById(R.id.capture3);
        audio_off=(ImageView)findViewById(R.id.audio_off);
        audio_on=(ImageView)findViewById(R.id.audio_on);
        save=(ImageView)findViewById(R.id.save);
        friends=(TextView)findViewById(R.id.friends);
        send=(Button)findViewById(R.id.send);
        cameraPreviewLayout=(RelativeLayout)findViewById(R.id.cameraPreview);
        back=(ImageView)findViewById(R.id.back);
        close=(ImageView)findViewById(R.id.delete);
        numberPicker2=(HorizontalPicker)findViewById(R.id.numberPicker2);
        mPreview = (TextureView) findViewById(R.id.surface_view);
        capture = (Button)findViewById(R.id.capture);


        switcher.setOnClickListener(onClickListener);
        flash.setOnClickListener(onClickListener);
        gallery.setOnClickListener(onClickListener);
        capture3.setOnClickListener(onClickListener);
        save.setOnClickListener(onClickListener);
        audio_off.setOnClickListener(onClickListener);
        audio_on.setOnClickListener(onClickListener);
        send.setOnClickListener(onClickListener);
        friends.setOnClickListener(onClickListener);


        final CharSequence[] values = new CharSequence[]{"0","1","2","3","4","5","6","7","8", "9", "10", "11", "12", "13", "14", "15"};
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
                time= Integer.parseInt(values[index].toString());
            }
        });

        database= FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();


        back.setOnClickListener(onClickListener);
        close.setOnClickListener(onClickListener);
        camera = checkDeviceCamera();
        mImageSurfaceView = new ImageSurfaceView(this, camera);
        cameraPreviewLayout.addView(mImageSurfaceView, 0);
        capture2=(Button)findViewById(R.id.capture2);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, pictureCallback);
                setView(2);
            }
        });

    }

}
