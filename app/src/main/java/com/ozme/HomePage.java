package com.ozme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

public class HomePage extends AppCompatActivity {
    private ImageSwitcher sw;
    private Button button1, button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        sw= (ImageSwitcher)findViewById(R.id.imageSwitcher1);
        button1=(Button)findViewById(R.id.button1);
        button2=(Button)findViewById(R.id.button2);
        button2.bringToFront();
        button1.bringToFront();


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
        sw.setImageResource(R.drawable.papa_mariage_enzo);

        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                index=i;
                index++;
                sw.setImageResource(imgs[index%5]);
                */
            }
        });
    }
}
