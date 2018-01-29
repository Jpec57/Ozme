package com.ozme;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;

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
    Handler handler;
    Location loc;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline2);
        database = FirebaseDatabase.getInstance();

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        timelineSwiperAdapter = new TimelinePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(timelineSwiperAdapter);
        viewPager.setCurrentItem(getIntent().getIntExtra("FRAGMENT_ID", 1));

        //ADDED


        account = (ImageView) findViewById(R.id.account);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);

            }
        });

        message = (ImageView) findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
            }
        });

        ozme = (ImageView) findViewById(R.id.ozme);
        ozme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() != 1) {
                    viewPager.setCurrentItem(1);
                } else {
                    Intent intent = new Intent(MainTimelineFragment.this, CameraActivity.class);
                    startActivity(intent);
                }
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        getAge(sharedPreferences.getString("birthday", null));

        handler = new Handler();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        updateData.run();

    }

    @SuppressLint("MissingPermission")
    private void setActive() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            database.getReference("data/users/" + Profile.getCurrentProfile().getId() + "/location").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataSnapshot.child("lat").getRef().setValue(loc.getLatitude());
                    dataSnapshot.child("long").getRef().setValue(loc.getLongitude());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){

        }
        DatabaseReference ref = database.getReference("data/users/" + Profile.getCurrentProfile().getId() + "/active");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().setValue(System.currentTimeMillis());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private Runnable updateData = new Runnable() {
        public void run() {
            setActive();
            handler.postDelayed(updateData, 60000);
        }
    };

    /*

    FragmentPagerAdapter
        This is best when navigating between sibling screens representing a fixed, small number of pages.
    FragmentStatePagerAdapter
        This is best for paging across a collection of objects for which the number of pages is undetermined.
         It destroys fragments as the user navigates to other pages, minimizing memory usage.
     */
    public class TimelinePagerAdapter extends FragmentPagerAdapter {

        TimelinePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MyAccountFragment.init(position);
                case 1:
                    return TimelineFragment.init(position);
                case 2:
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
        int i = 0;
        StringBuilder m = new StringBuilder();
        StringBuilder d = new StringBuilder();
        StringBuilder y = new StringBuilder();

        for (int k = 0; k < birthday.length(); k++) {
            String loop = birthday.substring(k, k + 1);
            if (loop.equals("/")) {
                i++;
            } else {
                switch (i) {
                    case 0:
                        m.append(loop);
                        break;
                    case 1:
                        d.append(loop);
                        break;
                    case 2:
                        y.append(loop);
                        break;
                }
            }

        }
        int birthdayMonth = Integer.parseInt(m.toString());
        int birthdayDay = Integer.parseInt(d.toString());
        int birthdayYear = Integer.parseInt(y.toString());

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(birthdayYear, birthdayMonth, birthdayDay);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (birthdayMonth > (today.get(Calendar.MONTH) + 1) || (birthdayMonth == (today.get(Calendar.MONTH)) && birthdayDay > today.get(Calendar.DAY_OF_MONTH) + 1)) {
            age--;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("/data/users/" + Profile.getCurrentProfile().getId() + "/filter/age");
        reference.setValue(age);


    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog;
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Voulez vous vraiment quitter l'application ?");
        alertDialogBuilder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations()) {
            deleteTempFiles(getCacheDir());
        }
    }

    private boolean deleteTempFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteTempFiles(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
        return file.delete();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    database.getReference("data/users/"+Profile.getCurrentProfile().getId()+"/location").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dataSnapshot.child("lat").getRef().setValue(loc.getLatitude());
                            dataSnapshot.child("long").getRef().setValue(loc.getLongitude());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
                break;

        }
    }
}
