package com.ozme;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

public class ProfilPerso extends AppCompatActivity {
    TextView challenge_text;
    TextView fb_name_age;
    ImageView photo;
    ProfilePictureView profilePictureView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_perso);

        RelativeLayout helpMe=(RelativeLayout)findViewById(R.id.helpme);
        Display display = getWindowManager().getDefaultDisplay();
        RelativeLayout.LayoutParams help = new RelativeLayout.LayoutParams(display.getWidth(), display.getHeight()/2);
        helpMe.setLayoutParams(help);

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        challenge_text=(TextView)findViewById(R.id.challenge_text);
        fb_name_age=(TextView)findViewById(R.id.fb_name_age);
        profilePictureView=(ProfilePictureView)findViewById(R.id.test);
        photo=(ImageView)findViewById(R.id.photo);

        profilePictureView.setProfileId(sharedPreferences.getString("profil_pic", null));
        //We must use the LayoutParams coming from the parent layout (ie a relative layout here)
        RelativeLayout.LayoutParams newLayoutParams = new RelativeLayout.LayoutParams(display.getWidth(), display.getWidth());
        profilePictureView.setLayoutParams(newLayoutParams);
        //Have to be custom to be freely set
        profilePictureView.setPresetSize(ProfilePictureView.CUSTOM);

        //Challenge bubble-Settings
        challenge_text.setText(" "+sharedPreferences.getString("challenge", "me faire des lasagnes"));
        String name_age = sharedPreferences.getString("first_name", "Julie");
        name_age=name_age+", "+sharedPreferences.getString("birthday", "28");
        fb_name_age.setText(name_age);
        //END


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.photo:

                break;

                default:
                    break;
            }
        }
    };
}
