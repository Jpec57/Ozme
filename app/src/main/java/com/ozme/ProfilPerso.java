package com.ozme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

public class ProfilPerso extends AppCompatActivity {
    TextView challenge_text;
    TextView fb_name_age;
    ImageView photo;
    ProfilePictureView profilePictureView;
    SeekBar seekBar1;
    SeekBar seekBar2;
    SeekBar seekBar3;
    SharedPreferences sharedPreferences;
    Display display;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_perso);

        pageLayout();

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        challenge_text=(TextView)findViewById(R.id.challenge_text);
        fb_name_age=(TextView)findViewById(R.id.fb_name_age);
        profilePictureView=(ProfilePictureView)findViewById(R.id.test);
        photo=(ImageView)findViewById(R.id.photo);
        photo.setOnClickListener(onClickListener);

        fbProfilePic();
        bubbleSettings();
        setSeekBar();





    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.photo:
                    Intent intent = new Intent(getApplicationContext(), MyPhotosActivity.class);
                    startActivity(intent);
                break;
                case R.id.challenge_text:
                    Intent intent1 = new Intent(getApplicationContext(), ChallengeChoiceActivity.class);
                    startActivity(intent1);
                    break;

                default:
                    break;
            }
        }
    };

    private void saveSeekBars(int seekbar, int progress){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(seekbar+"", progress);
        editor.apply();
    }
    private void setSeekBar(){
        seekBar1=(SeekBar)findViewById(R.id.seekBar1);
        seekBar2=(SeekBar)findViewById(R.id.seekBar2);
        seekBar3=(SeekBar)findViewById(R.id.seekBar3);
        seekBar1.setProgress(sharedPreferences.getInt(seekBar1.getId()+"", 0));
        seekBar2.setProgress(sharedPreferences.getInt(seekBar2.getId()+"", 0));
        seekBar3.setProgress(sharedPreferences.getInt(seekBar3.getId()+"", 0));



        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Save now
                saveSeekBars(seekBar.getId(), seekBar.getProgress());
            }
        };
        seekBar1.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBar2.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBar3.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    private void bubbleSettings(){
        //Challenge bubble-Settings
        challenge_text.setText(" "+sharedPreferences.getString("challenge", "me faire des lasagnes"));
        challenge_text.setOnClickListener(onClickListener);
        String name_age = sharedPreferences.getString("first_name", "Julie");
        name_age=name_age+", "+sharedPreferences.getString("birthday", "28");
        fb_name_age.setText(name_age);
        //END
    }

    private void pageLayout(){
        RelativeLayout helpMe=(RelativeLayout)findViewById(R.id.helpme);
        display = getWindowManager().getDefaultDisplay();
        RelativeLayout.LayoutParams help = new RelativeLayout.LayoutParams(display.getWidth(), display.getHeight()/2);
        helpMe.setLayoutParams(help);
    }

    private void fbProfilePic(){
        //Facebook profile pic - Settings
        profilePictureView.setProfileId(sharedPreferences.getString("profil_pic", null));
        //We must use the LayoutParams coming from the parent layout (ie a relative layout here)
        RelativeLayout.LayoutParams newLayoutParams = new RelativeLayout.LayoutParams(display.getWidth(), display.getWidth());
        profilePictureView.setLayoutParams(newLayoutParams);
        //Have to be custom to be freely set
        profilePictureView.setPresetSize(ProfilePictureView.CUSTOM);
        //END
    }
}
