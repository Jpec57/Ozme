package com.ozme;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;

import java.util.ArrayList;

public class ProfilePublic extends AppCompatActivity {

    ArrayList<String> think= new ArrayList<>();
    private SeekBar seekBar1;
    private SeekBar seekBar2;
    private SeekBar seekBar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_public);
        setProfilePic();

        setThink();
    }

    private void setTastes(){

    }

    private void setProfilePic(){
        RelativeLayout helpMe=(RelativeLayout)findViewById(R.id.helpme);
        ProfilePictureView pictureView=(ProfilePictureView)findViewById(R.id.imghelp);

        Display display = getWindowManager().getDefaultDisplay();
        RelativeLayout.LayoutParams img = new RelativeLayout.LayoutParams(display.getWidth(), display.getHeight()*38/100);
        RelativeLayout.LayoutParams help = new RelativeLayout.LayoutParams(display.getWidth(), display.getHeight()*2/5);
        helpMe.setLayoutParams(help);
        helpMe.setBackgroundColor(Color.BLACK);
        pictureView.setLayoutParams(img);

        Intent intent= getIntent();
        long id = intent.getLongExtra("id", 0);
        if (id ==0){
            Toast.makeText(getApplicationContext(), "Problem", Toast.LENGTH_SHORT).show();
        }else{
            pictureView.setProfileId(id+"");
        }
    }

    public void setThink(){
        think.add("Je ne veux rien dire de particulier");
        think.add("J'en ai marre...");
        IthinkAdapter adapter = new IthinkAdapter(this, think);
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }


}
