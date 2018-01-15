package com.ozme;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CameraActivity extends AppCompatActivity {
    RelativeLayout cameraPreviewLayout;
    private ImageSurfaceView mImageSurfaceView;
    private Camera camera;
    private ImageView capturedImageHolder;
    private ImageView back, close;
    private Button capture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FULL SCREEN
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.camera);



        cameraPreviewLayout=(RelativeLayout)findViewById(R.id.cameraPreview);
        back=(ImageView)findViewById(R.id.back);
        close=(ImageView)findViewById(R.id.delete);
        back.setOnClickListener(onClickListener);
        close.setOnClickListener(onClickListener);
        //capturedImageHolder = (ImageView)findViewById(R.id.captured_image);
        camera = checkDeviceCamera();
        mImageSurfaceView = new ImageSurfaceView(this, camera);
        cameraPreviewLayout.addView(mImageSurfaceView, 0);

        capture = (Button)findViewById(R.id.capture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, pictureCallback);
                capture.setVisibility(View.GONE);

            }
        });

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
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if(bitmap==null){
                Log.e("JPEC", "Fail in taking photo");
                return;
            }
            camera.stopPreview();
            //capturedImageHolder.setImageBitmap(rotateBitmap(bitmap, 90));

        }
    };

    public Bitmap rotateBitmap(Bitmap original, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(original , 0, 0, original.getWidth(), original.getHeight(), matrix, true);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back:
                    capture.setVisibility(View.VISIBLE);
                    camera.startPreview();
                    break;
                case R.id.delete:
                    capture.setVisibility(View.VISIBLE);
                    camera.startPreview();
                    break;
            }
        }
    };


}
