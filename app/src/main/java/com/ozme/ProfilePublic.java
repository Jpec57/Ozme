package com.ozme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class ProfilePublic extends AppCompatActivity {

    ArrayList<String> think= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_public);
        RelativeLayout helpMe=(RelativeLayout)findViewById(R.id.helpme);
        ImageView imageView=(ImageView)findViewById(R.id.imghelp);
        Display display = getWindowManager().getDefaultDisplay();
        RelativeLayout.LayoutParams img = new RelativeLayout.LayoutParams(display.getWidth(), display.getHeight()*38/100);
        RelativeLayout.LayoutParams help = new RelativeLayout.LayoutParams(display.getWidth(), display.getHeight()*2/5);
        helpMe.setLayoutParams(help);
        imageView.setLayoutParams(img);
        setThink();
    }

    public void setThink(){
        think.add("Je ne veux rien dire de particulier");
        think.add("J'en ai marre...");
        IthinkAdapter adapter = new IthinkAdapter(this, think);
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }


}
