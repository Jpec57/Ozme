package com.ozme;

/**
 * Created by jpec on 12/11/17.
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter implements View.OnClickListener {
    Context context;
    LayoutInflater inflater;
    ImageSwitcher sw;
    TextView name_age;
    TextView desc;
    TextView time;
    ImageView oz, lightning;
    ArrayList<String> m_imgArray;
    ArrayList<String> m_name_ageArray;
    ArrayList<String> m_descArray;
    ArrayList<String> m_timeArray;
    ArrayList<Long> m_profilesId;

    public CustomAdapter(Context applicationContext, ArrayList<String> imgArray, ArrayList<String> name_ageArray, ArrayList<String> descArray, ArrayList<String> timeArray, ArrayList<Long> profilesId) {
        this.context = applicationContext;
        inflater = (LayoutInflater.from(applicationContext));
        this.m_imgArray=imgArray;
        this.m_descArray=descArray;
        this.m_name_ageArray=name_ageArray;
        this.m_timeArray=timeArray;
        this.m_profilesId=profilesId;
    }
    @Override
    public int getCount() {
        return m_imgArray.size();
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.timeline_profile, null); // inflate the layout
        oz=(ImageView)view.findViewById(R.id.oz);
        lightning=(ImageView)view.findViewById(R.id.lightning);
        oz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CameraActivity.class);
                intent.putExtra("strangerId", m_profilesId.get(i));
                context.startActivity(intent);
            }
        });
        lightning.setOnClickListener(this);

        sw = (ImageSwitcher) view.findViewById(R.id.imageSwitcher1);
        name_age = (TextView)view.findViewById(R.id.name_age);
        time=(TextView)view.findViewById(R.id.time);
        desc=(TextView)view.findViewById(R.id.desc);

        final View finalView = view;
        sw.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(finalView.getContext());
                myView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                myView.setLayoutParams(new
                        ImageSwitcher.LayoutParams(Gallery.LayoutParams.WRAP_CONTENT,
                        Gallery.LayoutParams.WRAP_CONTENT));
                return myView;
            }
        });
        byte[] decodedString = Base64.decode(m_imgArray.get(i), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        sw.setImageDrawable(new BitmapDrawable(context.getResources(), decodedByte) );
        //sw.setImageResource(m_imgArray.get(i));
        name_age.setText(m_name_ageArray.get(i));
        time.setText(m_timeArray.get(i));
        desc.setText(m_descArray.get(i));

        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfilePublic.class);
                intent.putExtra("id", m_profilesId.get(i));
                context.startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lightning:
                break;
        }
    }
}
