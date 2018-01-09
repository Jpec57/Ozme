package com.ozme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/*
Use menu inflater for all items on right and XML files for those on the left
 */
public class TimelineActivity extends AppCompatActivity {
    GridView simpleGrid;
    ArrayList<Integer> imgTimeline=new ArrayList<>();
    ArrayList<String> descTimeline= new ArrayList<>();
    ArrayList<String> name_ageTimeline= new ArrayList<>();
    ArrayList<String> timeTimeline = new ArrayList<>();
    CardView fil_actu;

    private Toolbar mTopToolbar;
    ImageView account;
    int currentTimestamp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);

        //Add ToolBar
        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);

        //Toolbar title
        //TextView mTitle = (TextView) mTopToolbar.findViewById(R.id.toolbar_title);
        //mTitle.setText(mTopToolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        account=(ImageView)mTopToolbar.findViewById(R.id.account);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), MyAccount.class);
                startActivity(intent);
            }
        });

        fil_actu=(CardView)findViewById(R.id.fil_actu);





        TimelineFiller();
        recyclerSettings();
        gridSettings();




    }

    private void gridSettings(){
        simpleGrid = (GridView) findViewById(R.id.grid);
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), imgTimeline, name_ageTimeline, descTimeline, timeTimeline);
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
                    if (findViewById(R.id.fil_actu).getVisibility() == View.VISIBLE){
                        //To stop the scrolling
                        view.smoothScrollBy(0, 500);
                        slideToTop(findViewById(R.id.fil_actu));
                        view.smoothScrollToPosition(firstVisibleItem);
                    }
                }

                if (mLastVisibleItem > firstVisibleItem){
                    //Scrolling Up
                    if (findViewById(R.id.fil_actu).getVisibility() == View.GONE){
                        //To Stop the scrolling
                        view.smoothScrollBy(0, 500);
                        slideToBottom(findViewById(R.id.fil_actu));
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


        }
    }

    private void recyclerSettings(){
        RecyclerView recycler = (RecyclerView)findViewById(R.id.recycler);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recycler.setAdapter(new TimelineAdapter(this));
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







    //MENU

    //Add the menu (inflate) to the toolbar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            Toast.makeText(getApplicationContext(), "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
