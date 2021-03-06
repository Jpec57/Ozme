package com.ozme;

/**
 * Created by jpec on 15/01/18.
 */

import android.hardware.Camera;
import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class ImageSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Context context;
    private Camera camera;
    private SurfaceHolder surfaceHolder;

    public ImageSurfaceView(CameraActivity context, Camera camera) {
        super(context);
        this.camera = camera;
        this.context=context;
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {

            Camera.Parameters parameters = camera.getParameters();
            Camera.Size initSize = parameters.getPreviewSize();
            if (initSize.height<initSize.width){
                camera.setDisplayOrientation(90);
            }

            camera.setParameters(parameters);
            this.camera.setPreviewDisplay(holder);
            this.camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            this.camera.stopPreview();
        }catch (Exception e){

        }
        this.camera.release();
    }
}
