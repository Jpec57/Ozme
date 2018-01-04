package com.ozme;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

/*
DOCS :

https://github.com/ArthurHub/Android-Image-Cropper
 */

public class MyPhotosActivity extends AppCompatActivity {
    View small1;
    View small2;
    View small3;
    View small4;
    View small5;
    View big;
    RelativeLayout bigLayout;
    String stockFile;
    ImageView view;
    ArrayList<ImageView> imgViews = new ArrayList<>();
    SharedPreferences sharedPreferences;
    ArrayList<String> imgsArray;
    int index = 0;
    String help="nothing";
    TextView helpText;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_photos_activity);

        //Initialization
        bigLayout = (RelativeLayout) findViewById(R.id.bigLayout);
        big = (ImageView) findViewById(R.id.big);
        small1 = findViewById(R.id.small1);
        small2 = findViewById(R.id.small2);
        small3 = findViewById(R.id.small3);
        small4 = findViewById(R.id.small4);
        small5 = findViewById(R.id.small5);
        small1.setOnClickListener(onClickListener);
        small2.setOnClickListener(onClickListener);
        small3.setOnClickListener(onClickListener);
        small4.setOnClickListener(onClickListener);
        small5.setOnClickListener(onClickListener);
        imgViews.add((ImageView) small1);
        imgViews.add((ImageView) small2);
        imgViews.add((ImageView) small3);
        imgViews.add((ImageView) small4);
        imgViews.add((ImageView) small5);
        imgsArray = new ArrayList<>();
        imgsArray.add("img1.jpg");
        imgsArray.add("img2.jpg");
        imgsArray.add("img3.jpg");
        imgsArray.add("img4.jpg");
        imgsArray.add("img5.jpg");
        helpText=(TextView)findViewById(R.id.help);



        //Test for big image
        Bitmap yourBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.papa_mariage_enzo);
        Drawable d = new BitmapDrawable(getResources(), yourBitmap);
        bigLayout.removeView(big);
        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(d);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        bigLayout.addView(imageView);

        saveToInternalStorage(yourBitmap, "profile.jpg", null);
        Toast.makeText(getApplicationContext(), stockFile, Toast.LENGTH_SHORT).show();
        imgLoader();


    }


    private void imgLoader() {
        for (int k = 0; k < imgsArray.size(); k++) {
            try {
                //index = 0;
                //Same path as saveInternal
                File f = new File(stockFile, imgsArray.get(k));
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                view = imgViews.get(k);
                final int finalK = k;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imgCrop(imgViews.get(finalK), stockFile + "/" + imgsArray.get(finalK));
                    }
                });
                view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Bitmap yourBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.papa_mariage_enzo);
                Drawable d = new BitmapDrawable(getResources(), yourBitmap);
                view.setImageDrawable(d);
                if (b != null){
                    view.setImageBitmap(b);
                }
                Toast.makeText(getApplicationContext(), "Load success "+finalK, Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                return;
                //Toast.makeText(getApplicationContext(), "Load failed : "+e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }


    }

    private String saveToInternalStorage(Bitmap bitmapImage, String path, Uri crop) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/com.ozme/app_data/Images
        File directory = cw.getDir("Images", Context.MODE_PRIVATE);
        // Create imageDir only if doesn't exist already
        File mypath = new File(directory, path);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            if (crop != null){
                bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), crop);
                helpText.setText(help+" \n OHOH");
            }
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
        stockFile = directory.getAbsolutePath();
        return mypath.getAbsolutePath();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            String res = "Not saved";
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Toast.makeText(getApplicationContext(), resultUri.getPath(), Toast.LENGTH_SHORT).show();
                try {
                    imgViews.get(0).setImageURI(resultUri);
                    help = saveToInternalStorage(result.getBitmap(), "img" + (index+1) + ".jpg", resultUri);
                    helpText.setText(help);

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),res+" ntm", Toast.LENGTH_LONG).show();
                }
                /*
                Toast.makeText(getApplicationContext(), "SAVED : "+res, Toast.LENGTH_LONG).show();
                //Depending on the view v we have initialized during the onclick, we set the right img
                //imgSet(resultUri, view);
                imgLoader();
                */
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(),res+" "+error.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }


    private void imgCrop(ImageView v, String uri) {
        view = v;
        Uri imageUri = Uri.parse(uri);
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
// start cropping activity for pre-acquired image saved on the device
        CropImage.activity(imageUri)
                .start(this);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.small1:
                    imgCrop(imgViews.get(0), stockFile+"/img1.jpg");
                    break;
                case R.id.small2:
                    break;
                case R.id.small3:
                    break;
                case R.id.small4:
                    break;
                case R.id.small5:
                    break;

            }
        }
    };

}
