package com.ozme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by jpec on 09/01/18.
 */
/*
DOCS : http://www.truiton.com/2013/05/android-fragmentpageradapter-example/
 */

public class TimelineFragment extends Fragment {
    FirebaseDatabase database;
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
        database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("/geodata");
        final GeoFire geoFire = new GeoFire(ref);

        //TODO regarder ici
        geoFire.setLocation(11111111L+"", new GeoLocation(38, -122.4056973), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });
        geoFire.setLocation(1155490200L+"", new GeoLocation(49.0482965, 6.89372949), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });
        geoFire.setLocation(213996785709121L+"", new GeoLocation(60.7853890, -122.4056973), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });





        database.getReference("/data/users/"+Profile.getCurrentProfile().getId()+"/filter").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("JPEC", "Personal data");
                final int ageMin=dataSnapshot.child("ageMin").getValue(Integer.class);
                final int ageMax=dataSnapshot.child("ageMax").getValue(Integer.class);
                final boolean homme = dataSnapshot.child("homme").getValue(Boolean.class);
                final boolean femme= dataSnapshot.child("femme").getValue(Boolean.class);
                geoFire.getLocation(Profile.getCurrentProfile().getId(), new LocationCallback() {
                    @Override
                    public void onLocationResult(String key, GeoLocation location) {
                        GeoQuery geoQuery;
                        if (location != null){
                            geoQuery = geoFire.queryAtLocation(location, 10);
                        }else{
                            geoQuery = geoFire.queryAtLocation(new GeoLocation(37.7853889, -122.4056973), 10);
                        }

                        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                            @Override
                            public void onKeyEntered(String key, GeoLocation location) {
                                Log.e("JPEC", "geo");
                                DatabaseReference sortedProfile = database.getReference("data/users/"+key);
                                sortedProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.e("JPEC", dataSnapshot.child("username").getValue(String.class));
                                        DataSnapshot filter = dataSnapshot.child("filter");
                                        if (homme && femme){
                                            Log.e("JPEC", "sexe ok");
                                            try{
                                            if (ageMin <= filter.child("age").getValue(Integer.class) && ageMax >= filter.child("age").getValue(Integer.class)){
                                                Log.e("JPEC", "age ok");
                                                filler(dataSnapshot);
                                            }}catch (Exception e){
                                                Log.e("JPEC", "age not specified");
                                                filler(dataSnapshot);
                                            }
                                        }else if (homme && filter.child("gender").equals("male")){
                                            filler(dataSnapshot);
                                        }else{
                                            filler(dataSnapshot);
                                        }



                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onKeyExited(String key) {

                            }

                            @Override
                            public void onKeyMoved(String key, GeoLocation location) {

                            }

                            @Override
                            public void onGeoQueryReady() {

                            }

                            @Override
                            public void onGeoQueryError(DatabaseError error) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void timelineFilter(final boolean homme, final boolean femme, final int ageMin, final int ageMax){
        final DatabaseReference reference = database.getReference("/data/users/");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int age=18;
                try{
                    age = dataSnapshot.child("filter").child("age").getValue(Integer.class);
                }catch (Exception e){

                }
                if (ageMax != 0 || ageMin != 0){
                    if (age >= ageMin && age <= ageMax ){
                        fillerAfterAge(homme, femme, dataSnapshot);
                    }
                }else{
                    fillerAfterAge(homme, femme, dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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
    }

    private void fillerAfterAge(boolean homme, boolean femme, DataSnapshot dataSnapshot){
        if (homme && femme){
            filler(dataSnapshot);
        }else if (homme){
            if (dataSnapshot.child("gender").getValue(String.class).equals("male")){
                filler(dataSnapshot);
            }
        }else{
            if (!dataSnapshot.child("gender").getValue(String.class).equals("male")){
                filler(dataSnapshot);
            }
        }
    }

    private void filler(DataSnapshot dataSnapshot){

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
