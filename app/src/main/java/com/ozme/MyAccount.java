package com.ozme;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

public class MyAccount extends AppCompatActivity {
    private ImageView profile;
    RoundImage roundImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);

        MyAccountNotificationAdapter adapter = new MyAccountNotificationAdapter(getApplicationContext());
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        profile=(ImageView)findViewById(R.id.profile);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.papa_mariage_enzo);
        roundImage = new RoundImage(bm);
        profile.setImageDrawable(roundImage);






    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
