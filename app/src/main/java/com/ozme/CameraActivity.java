package com.ozme;

import android.Manifest;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.ozme.CameraHelper.MEDIA_TYPE_IMAGE;
import static com.ozme.CameraHelper.MEDIA_TYPE_VIDEO;

public class CameraActivity extends AppCompatActivity {
    RelativeLayout cameraPreviewLayout;
    private ImageSurfaceView mImageSurfaceView;
    private Camera camera;
    private ImageView back, close;
    private Button capture;
    private Button capture2;
    private Button capture3;
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






    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FULL SCREEN
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.camera);

        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setProgress(40);

        //Options
        switcher=(ImageView)findViewById(R.id.switcher);
        flash=(ImageView)findViewById(R.id.flash);
        gallery=(ImageView)findViewById(R.id.gallery);
        switcher.setOnClickListener(onClickListener);
        flash.setOnClickListener(onClickListener);
        gallery.setOnClickListener(onClickListener);
        options=(LinearLayout)findViewById(R.id.options);
        options2=(LinearLayout)findViewById(R.id.options2);
        options3=(LinearLayout)findViewById(R.id.options3);
        oz=(TextView)findViewById(R.id.oz);
        capture3=(Button)findViewById(R.id.capture3);




        cameraPreviewLayout=(RelativeLayout)findViewById(R.id.cameraPreview);
        back=(ImageView)findViewById(R.id.back);
        close=(ImageView)findViewById(R.id.delete);
        back.setOnClickListener(onClickListener);
        close.setOnClickListener(onClickListener);
        camera = checkDeviceCamera();
        mImageSurfaceView = new ImageSurfaceView(this, camera);
        cameraPreviewLayout.addView(mImageSurfaceView, 0);

        //VIDEO
        mPreview = (TextureView) findViewById(R.id.surface_view);

        capture = (Button)findViewById(R.id.capture);
        capture2=(Button)findViewById(R.id.capture2);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, pictureCallback);
                setView(2);
            }
        });

    }

    private void setView(int i){
        switch(i){
            case 1:
                options.setVisibility(View.VISIBLE);
                options2.setVisibility(View.GONE);
                options3.setVisibility(View.GONE);

                capture.setVisibility(View.VISIBLE);
                capture2.setVisibility(View.GONE);
                break;
            case 2:
                options.setVisibility(View.GONE);
                options2.setVisibility(View.VISIBLE);
                options3.setVisibility(View.GONE);

                capture.setVisibility(View.GONE);
                capture2.setVisibility(View.GONE);

                oz.setVisibility(View.GONE);

                break;
            case 3 :
                options.setVisibility(View.GONE);
                options2.setVisibility(View.GONE);
                options3.setVisibility(View.VISIBLE);

                oz.setVisibility(View.GONE);

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
            //TODO Save img only if the user wants it
            new fileFromBitmap().execute();
            galleryAddPic();
            camera.stopPreview();
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

                    break;
            }
        }
    };

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Ozme");
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
            close.setVisibility(View.VISIBLE);
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

        // Step 1: Unlock and set camera to MediaRecorder
        camera.unlock();
        mMediaRecorder.setCamera(camera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT );
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(profile);

        // Step 4: Set output file
        mOutputFile = CameraHelper.getOutputMediaFile(MEDIA_TYPE_VIDEO);
        if (mOutputFile == null) {
            return false;
        }
        //mMediaRecorder.setOutputFile(getExternalFilesDir(CameraHelper.MEDIA_TYPE_VIDEO));
        mMediaRecorder.setOutputFile(mOutputFile.getPath());
        Log.e("JPEC", mOutputFile.getPath());

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
            //setCaptureButtonText("Stop");

        }

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

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "Ozme");

            // Create the storage directory if it does not exist
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()) {
                    Log.e("JPEC", "failed to create directory");
                    return null;
                }
            }
            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            //END

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //file  = new File(context.getCacheDir(), "temporary_file.jpg");
            file = new File(mediaStorageDir.getPath() + File.separator +
                    "OZME_IMG_"+ timeStamp + ".jpg");            try {
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
                fo.flush();
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // back to main thread after finishing doInBackground
            // update your UI or take action after
            // exp; make progressbar gone
            //sendFile(file);

        }
    }

}
