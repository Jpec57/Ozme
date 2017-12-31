package com.ozme;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.content.ContentUris;
import android.provider.MediaStore.Images;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class MyPhotosActivity extends AppCompatActivity {
    View small1;
    View small2;
    View small3;
    View small4;
    View small5;
    View big;
    RelativeLayout bigLayout;
    String success;
    View view;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_photos_activity);

        bigLayout=(RelativeLayout) findViewById(R.id.bigLayout);
        big=(ImageView)findViewById(R.id.big);
        small1=findViewById(R.id.small1);
        small2=findViewById(R.id.small2);
        small3=findViewById(R.id.small3);
        small4=findViewById(R.id.small4);
        small5=findViewById(R.id.small5);
        small1.setOnClickListener(onClickListener);

        Bitmap yourBitmap= BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.papa_mariage_enzo);
        Drawable d = new BitmapDrawable(getResources(), yourBitmap);
        bigLayout.removeView(big);
        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(d);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        bigLayout.addView(imageView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            bigLayout.setBackground(d);
        }
        success = saveToInternalStorage(yourBitmap);
        Toast.makeText(getApplicationContext(), success, Toast.LENGTH_SHORT).show();
        loadImageFromStorage(success);


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.small1:
                    break;

            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            final ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(b);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgCrop(imageView, success);
                }
            });
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            small1=(ImageView)findViewById(R.id.small1);
            ViewGroup parent = (ViewGroup) small1.getParent();
            parent.removeView(small1);
            imageView.setId(R.id.small1);
            parent.addView(imageView, 0);

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
    private void imgSet(Uri uri){
        final ImageView imageView = new ImageView(this);
        imageView.setImageURI(uri);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgCrop(imageView, success);
            }
        });
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        small1=(ImageView)findViewById(R.id.small1);
        ViewGroup parent = (ViewGroup) small1.getParent();
        parent.removeView(small1);
        imageView.setId(R.id.small1);
        parent.addView(imageView, 0);

    }

    private void imgCrop (View v, String uri){
        view=v;
        Uri imageUri= Uri.parse(uri);
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
// start cropping activity for pre-acquired image saved on the device
        CropImage.activity(imageUri)
                .start(this);
    }
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("Images", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imgSet(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * A copy of the Android internals StoreThumbnail method, it used with the insertImage to
     * populate the android.provider.MediaStore.Images.Media#insertImage with all the correct
     * meta data. The StoreThumbnail method is private so it must be duplicated here.
     * @see android.provider.MediaStore.Images.Media (StoreThumbnail private method)
     */
    private static final Bitmap storeThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width,
            float height,
            int kind) {

        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true
        );

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND,kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID,(int)id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT,thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH,thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

}
