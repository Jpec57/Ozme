package com.ozme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by jpec on 09/01/18.
 */
/*
DOCS : http://www.truiton.com/2013/05/android-fragmentpageradapter-example/
 */

public class TimelineFragment extends Fragment {
    int fragNum;
    GridView simpleGrid;
    ArrayList<String> imgTimeline=new ArrayList<>();
    ArrayList<String> descTimeline= new ArrayList<>();
    ArrayList<String> name_ageTimeline= new ArrayList<>();
    ArrayList<String> timeTimeline = new ArrayList<>();
    ArrayList<Long> profilesId =  new ArrayList<>();
    CardView fil_actu;
    View layoutView;
    int currentTimestamp;

    static TimelineFragment init (int val){
        TimelineFragment timelineFragment= new TimelineFragment();
        Bundle args = new Bundle();
        args.putInt("val", val);
        timelineFragment.setArguments(args);
        return new TimelineFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragNum = getArguments() != null ? getArguments().getInt("val") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_timeline, container,
                false);

        fil_actu=(CardView)layoutView.findViewById(R.id.fil_actu);
        TimelineFiller();
        recyclerSettings();
        gridSettings();
        return layoutView;
    }
    private void gridSettings(){
        simpleGrid = (GridView) layoutView.findViewById(R.id.grid);
        CustomAdapter customAdapter = new CustomAdapter(layoutView.getContext(), imgTimeline, name_ageTimeline, descTimeline, timeTimeline, profilesId);
        simpleGrid.setAdapter(customAdapter);
        simpleGrid.setSmoothScrollbarEnabled(true);
        simpleGrid.setFastScrollEnabled(true);
        simpleGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mLastVisibleItem < firstVisibleItem){
                    //Scrolling Down
                    //Toast.makeText(getApplicationContext(), "Down", Toast.LENGTH_SHORT).show();
                    if (layoutView.findViewById(R.id.fil_actu).getVisibility() == View.VISIBLE){
                        //To stop the scrolling
                        view.smoothScrollBy(0, 500);
                        slideToTop(layoutView.findViewById(R.id.fil_actu));
                        view.smoothScrollToPosition(firstVisibleItem);
                    }
                }

                if (mLastVisibleItem > firstVisibleItem){
                    //Scrolling Up
                    if (layoutView.findViewById(R.id.fil_actu).getVisibility() == View.GONE){
                        //To Stop the scrolling
                        view.smoothScrollBy(0, 500);
                        slideToBottom(layoutView.findViewById(R.id.fil_actu));
                        view.smoothScrollToPosition(firstVisibleItem);
                    }
                }
                mLastVisibleItem=firstVisibleItem;
            }
        });
    }

    // To animate view slide out from bottom to top
    public void slideToTop(View view){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,-view.getHeight());
        animate.setDuration(200);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    public void slideToBottom(final View view){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight()/3);
        animate.setDuration(500);
        animate.setFillAfter(false);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);


    }



    private void TimelineFiller(){
        //AFTER

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("/data/users/");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                profilesId.add(Long.parseLong(dataSnapshot.getKey()));
                imgTimeline.add(dataSnapshot.child("photos").child("0").getValue(String.class));
                if (dataSnapshot.child("description").exists()){
                    descTimeline.add(dataSnapshot.child("description").getValue(String.class));
                }else{
                    descTimeline.add("Sans description");
                }
                try{
                    name_ageTimeline.add(dataSnapshot.child("username").getValue(String.class)+", "+dataSnapshot.child("filter").child("age").getValue(Integer.class));
                }catch(Exception e){
                    name_ageTimeline.add("Oups, 42");
                }


                Long tsLong = System.currentTimeMillis()/1000;
                //TimeStamp in seconds
                currentTimestamp = tsLong.intValue();
                int timestamp = tsLong.intValue()-784;
                timeTimeline.add(timestampDifference(timestamp));
                CustomAdapter customAdapter = new CustomAdapter(layoutView.getContext(), imgTimeline, name_ageTimeline, descTimeline, timeTimeline, profilesId);
                simpleGrid.setAdapter(customAdapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                profilesId.add(Long.parseLong(dataSnapshot.getKey()));
                imgTimeline.add(dataSnapshot.child("photos").child("0").getValue(String.class));
                Long tsLong = System.currentTimeMillis()/1000;
                //TimeStamp in seconds
                currentTimestamp = tsLong.intValue();
                int timestamp = tsLong.intValue()-784;
                timeTimeline.add(timestampDifference(timestamp));
                name_ageTimeline.add("Jean-Paul, 21");
                descTimeline.add("Best band ever");

                CustomAdapter customAdapter = new CustomAdapter(layoutView.getContext(), imgTimeline, name_ageTimeline, descTimeline, timeTimeline, profilesId);
                simpleGrid.setAdapter(customAdapter);



            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



/*

        //BEFORE
        Long tsLong = System.currentTimeMillis()/1000;
        //TimeStamp in seconds
        currentTimestamp = tsLong.intValue();
        int timestamp = tsLong.intValue();

        for (int k=0; k < 5; k++){
            imgTimeline.add(R.drawable.goku_training);
            imgTimeline.add(R.drawable.a7x);
            imgTimeline.add(R.drawable.papa_mariage_enzo);

            descTimeline.add("A little monkey boy");
            descTimeline.add("Best band ever");
            descTimeline.add("Familly");

            timestamp-=60;
            timeTimeline.add(timestampDifference(timestamp));
            timestamp-=160;
            timeTimeline.add(timestampDifference(timestamp));
            timestamp-=(60*60*2);
            timeTimeline.add(timestampDifference(timestamp));

            name_ageTimeline.add("Jean-Paul, 21");
            name_ageTimeline.add("Aline, 18");
            name_ageTimeline.add("Maxime, 28");

            profilesId.add((long) 1151181962);
            profilesId.add(269506396723510L);
            profilesId.add(1122624547763740L);



        }
        */
    }

    private void recyclerSettings(){
        RecyclerView recycler = (RecyclerView)layoutView.findViewById(R.id.recycler);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(layoutView.getContext(), LinearLayoutManager.HORIZONTAL, false));

        recycler.setAdapter(new TimelineAdapter(layoutView.getContext()));
    }

    private String timestampDifference(int time_profile){
        //Difference in seconds
        int diff= currentTimestamp-time_profile;
        //Convert into minutes or hours
        int hours=diff/3600;
        int minutes = (diff%3600)/60;
        //int seconds=(diff%3600)%60;
        if (hours == 0){
            return "Vu il y a "+minutes+" minutes";
        }else{
            if (minutes < 10){
                return "Vu il y a "+hours+"h 0"+minutes;
            }
        }
        return "Vu il y a "+hours+"h "+minutes;
    }

}
