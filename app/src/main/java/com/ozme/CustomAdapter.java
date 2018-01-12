package com.ozme;

/**
 * Created by jpec on 12/11/17.
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ImageSwitcher sw;
    TextView name_age;
    TextView desc;
    TextView time;
    ArrayList<Integer> m_imgArray;
    ArrayList<String> m_name_ageArray;
    ArrayList<String> m_descArray;
    ArrayList<String> m_timeArray;
    ArrayList<Long> m_profilesId;

    public CustomAdapter(Context applicationContext, ArrayList<Integer> imgArray, ArrayList<String> name_ageArray, ArrayList<String> descArray, ArrayList<String> timeArray, ArrayList<Long> profilesId) {
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
        sw.setImageResource(m_imgArray.get(i));
        name_age.setText(m_name_ageArray.get(i));
        time.setText(m_timeArray.get(i));
        desc.setText(m_descArray.get(i));

        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Hello you", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ProfilePublic.class);
                intent.putExtra("id", m_profilesId.get(i));
                context.startActivity(intent);
            }
        });


        return view;
    }
}
