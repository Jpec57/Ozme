package com.ozme;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/*
DOCS : https://developer.android.com/training/implementing-navigation/lateral.html#horizontal-paging
-------------------BEST---------------
http://www.truiton.com/2013/05/android-fragmentpageradapter-example/
 */
public class MainTimelineFragment extends FragmentActivity {
    ImageView account;
    ImageView message;
    ImageView ozme;
    ViewPager viewPager;
    TimelinePagerAdapter timelineSwiperAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline2);
        viewPager=(ViewPager)findViewById(R.id.view_pager);
        timelineSwiperAdapter= new TimelinePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(timelineSwiperAdapter);
        viewPager.setCurrentItem(getIntent().getIntExtra("FRAGMENT_ID", 1));



        account=(ImageView)findViewById(R.id.account);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);

            }
        });

        message=(ImageView)findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
            }
        });

        ozme=(ImageView)findViewById(R.id.ozme);
        ozme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() != 1){
                    viewPager.setCurrentItem(1);
                }
            }
        });


    }

/*

FragmentPagerAdapter
    This is best when navigating between sibling screens representing a fixed, small number of pages.
FragmentStatePagerAdapter
    This is best for paging across a collection of objects for which the number of pages is undetermined.
     It destroys fragments as the user navigates to other pages, minimizing memory usage.
 */
    public class TimelinePagerAdapter extends FragmentPagerAdapter{

        TimelinePagerAdapter(FragmentManager fm) {
            super(fm);
        }

    @Override
    public Fragment getItem(int position) {
            switch (position){
                case 0 :
                    return MyAccountFragment.init(position);
                case 1:
                    return TimelineFragment.init(position);
                case 2 :
                    return MessageFragment.init(position);
            }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
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
