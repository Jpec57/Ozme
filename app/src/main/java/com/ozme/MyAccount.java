package com.ozme;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyAccount extends AppCompatActivity {
    private ImageView profile;
    RoundImage roundImage;
    TextView challenge_text;
    ImageView settings;
    ImageView add;
    ImageView help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);
        settings=(ImageView)findViewById(R.id.settings);
        add=(ImageView)findViewById(R.id.add);
        help=(ImageView)findViewById(R.id.help);
        //Setting onClickListener
        settings.setOnClickListener(onClickListener);
        add.setOnClickListener(onClickListener);
        help.setOnClickListener(onClickListener);

        //Adapter for notifications
        MyAccountNotificationAdapter adapter = new MyAccountNotificationAdapter(getApplicationContext());
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        profile=(ImageView)findViewById(R.id.profile);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.papa_mariage_enzo);
        roundImage = new RoundImage(bm);
        profile.setImageDrawable(roundImage);

        challenge_text=(TextView)findViewById(R.id.challenge_text);








    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()){
                case R.id.settings:
                    intent = new Intent(getApplicationContext(),SettingsActivity.class);
                    break;
                default:
                    intent=null;
                    break;


            }
            if (intent != null){
                startActivity(intent);
            }
        }
    };

}
