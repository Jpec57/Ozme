package com.ozme;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfilePublic extends AppCompatActivity {

    ArrayList<String> think= new ArrayList<>();
    private SeekBar seekBar1;
    private SeekBar seekBar2;
    private SeekBar seekBar3;
    private long id;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_public);
        setProfilePic();
        setThink();
        setProfileInfo();
    }

    private void setProfileInfo(){
        database= FirebaseDatabase.getInstance();
        final TextView name_age = (TextView)findViewById(R.id.name_age);
        final TextView work= (TextView)findViewById(R.id.work);
        final TextView active = (TextView) findViewById(R.id.active);


        Button ozme = (Button)findViewById(R.id.ozme);
        ozme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePublic.this, CameraActivity.class);
                startActivity(intent);
            }
        });
        //Get user's info from database
        DatabaseReference userReference=database.getReference("data/users/"+id);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                //Place them into their corresponding places
                String nameAndAge= dataSnapshot.child("username").getValue(String.class)+", "+dataSnapshot.child("filter").child("age").getValue(Integer.class);
                name_age.setText(nameAndAge);
                //TODO Location
                final Geocoder geocoder = new Geocoder(ProfilePublic.this);
                List<Address> addresses = null;

                DatabaseReference geoRef = database.getReference("geodata");
                GeoFire geoFire = new GeoFire(geoRef);
                geoFire.getLocation(id+"", new LocationCallback() {
                    @Override
                    public void onLocationResult(String key, GeoLocation location) {
                        String cityName="Non renseignée";
                        if (location != null){
                            List<Address> addresses = null;
                            try {
                                Log.e("JPEC", location.latitude+" LAT\n"+location.longitude);
                                addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (addresses.size() > 0) {
                                cityName=addresses.get(0).getLocality();
                            }
                        }
                        work.setText(dataSnapshot.child("job").getValue(String.class)+" - "+cityName);

                        try {
                            String availability = "";
                            if (dataSnapshot.child("gender").getValue(String.class).equals("male")) {
                                availability = "Actif il y a ";
                            } else {
                                availability = "Active il y a ";
                            }
                            availability += "30 minutes";
                            active.setText(availability);
                        }catch (Exception e){

                        }
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

        setHobbies();


    }

    private void setHobbies(){
        RecyclerView recycler = (RecyclerView)findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutFrozen(true);
        recycler.setLayoutManager(new LinearLayoutManager(ProfilePublic.this, LinearLayoutManager.HORIZONTAL, false));
        //recycler.setAdapter(new RealHobbiesAdapter(this));

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
        id = intent.getLongExtra("id", 0);
        if (id ==0){
            Toast.makeText(getApplicationContext(), "Problem", Toast.LENGTH_SHORT).show();
            Intent intent1= new Intent(this, MainTimelineFragment.class);
            startActivity(intent1);
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
