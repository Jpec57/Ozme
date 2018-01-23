package com.ozme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.InputStream;

/**
 * Created by jpec on 23/01/18.
 *
 * DOCS : https://www.techrepublic.com/blog/software-engineer/browse-androids-media-gallery-via-intents/
 *
 */

public class BrowsePicture extends Activity implements View.OnClickListener {

    private static final int REQUEST_ID = 1;
    private static final int HALF = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test);

        findViewById(R.id.browse_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_ID);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        InputStream stream = null;
        if (requestCode == REQUEST_ID && resultCode == Activity.RESULT_OK) {
            try {
                stream = getContentResolver().openInputStream(data.getData());
                Bitmap original = BitmapFactory.decodeStream(stream);
                ((ImageView) findViewById(R.id.image_holder)).setImageBitmap(Bitmap.createScaledBitmap(original,
                        original.getWidth() / HALF, original.getHeight() / HALF, true));
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
