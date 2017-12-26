package com.ozme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageSwitcher sw;
    int [] arrayList=new int[3];
    int indexArr=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_flipper);
/*
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                } else {
                    // The toggle is disabled
                }
            }
        });

*/
        arrayList[0]=(R.drawable.goku_training);
        arrayList[1]=(R.drawable.a7x);
        sw = (ImageSwitcher) findViewById(R.id.imageSwitcher1);

        sw.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                myView.setLayoutParams(new
                        ImageSwitcher.LayoutParams(Gallery.LayoutParams.WRAP_CONTENT,
                        Gallery.LayoutParams.WRAP_CONTENT));
                return myView;
            }
        });
        sw.setImageResource(R.drawable.goku_training);

        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++indexArr;
                sw.setImageResource(arrayList[indexArr%2]);
            }
        });
        
    }
}
