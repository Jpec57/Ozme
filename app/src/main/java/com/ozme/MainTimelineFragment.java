package com.ozme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
                }else{
                    Intent intent=new Intent(MainTimelineFragment.this, CameraActivity.class);
                    startActivity(intent);
                }
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        getAge(sharedPreferences.getString("birthday", null));


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

    private void getAge(String birthday) {
        int i=0;
        StringBuilder m= new StringBuilder();
        StringBuilder d= new StringBuilder();
        StringBuilder y= new StringBuilder();

        for (int k =0; k<birthday.length(); k++){
            String loop=birthday.substring(k, k+1);
            if (loop.equals("/")){
                i++;
            }else{
                switch (i){
                    case 0:
                        m.append(loop);
                        break;
                    case 1 :
                        d.append(loop);
                        break;
                    case 2 :
                        y.append(loop);
                        break;
                }
            }

        }
        int birthdayMonth=Integer.parseInt(m.toString());
        int birthdayDay=Integer.parseInt(d.toString());
        int birthdayYear=Integer.parseInt(y.toString());
        Log.e("JPEC", "test : "+birthdayMonth);

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(birthdayYear, birthdayMonth, birthdayDay);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if(birthdayMonth > (today.get(Calendar.MONTH)+1) || (birthdayMonth == (today.get(Calendar.MONTH)) && birthdayDay > today.get(Calendar.DAY_OF_MONTH)+1)) {
            age--;
        }

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("/data/users/"+ Profile.getCurrentProfile().getId()+"/filter/age");
        reference.setValue(age);



    }
}
