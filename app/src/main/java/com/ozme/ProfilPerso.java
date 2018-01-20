package com.ozme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfilPerso extends AppCompatActivity {
    TextView challenge_text;
    TextView fb_name_age;
    ImageView photo;
    Button next;
    ProfilePictureView profilePictureView;
    SeekBar seekBar1;
    SeekBar seekBar2;
    SeekBar seekBar3;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferences2;
    Display display;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_perso);

        database= FirebaseDatabase.getInstance();


        pageLayout();

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        sharedPreferences2=getSharedPreferences("activities", MODE_PRIVATE);
        challenge_text=(TextView)findViewById(R.id.challenge_text);
        LinearLayout challenge=(LinearLayout)findViewById(R.id.challenge);
        challenge.setOnClickListener(onClickListener);
        fb_name_age=(TextView)findViewById(R.id.fb_name_age);
        next=(Button)findViewById(R.id.next);
        next.setOnClickListener(onClickListener);
        profilePictureView=(ProfilePictureView)findViewById(R.id.test);
        photo=(ImageView)findViewById(R.id.photo);
        photo.setOnClickListener(onClickListener);

        fbProfilePic();
        bubbleSettings();
        setSeekBar();

        activityRecycler();





    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.photo:
                    Intent intent = new Intent(getApplicationContext(), MyPhotosActivity.class);
                    startActivity(intent);
                break;
                case R.id.challenge_text:
                    Intent intent1 = new Intent(getApplicationContext(), ChallengeChoiceActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.next:
                    //We will not see the first page anymore
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("first_visit", false);
                    editor.apply();
                    Intent intent2 = new Intent(getApplicationContext(), MainTimelineFragment.class);
                    startActivity(intent2);
                    break;

                case R.id.challenge:
                    Intent intent3= new Intent(getApplicationContext(), ChallengeChoiceActivity.class);
                    startActivity(intent3);
                    break;

                default:
                    break;
            }
        }
    };

    private void userInfoQuery(){
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Insert your code here
                        try {
                            object.getString("birthday");
                            object.getString("work");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,birthday,work,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void activityRecycler(){
        RecyclerView recycler = (RecyclerView)findViewById(R.id.recycler);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<String> desc = Arrays.asList("Musique", "Voyage", "Cinéma", "Cuisine", "Voyage", "Avenged");
        int[] id = {R.drawable.travel, R.drawable.a7x, R.drawable.papa_mariage_enzo, R.drawable.gourmandise, R.drawable.travel, R.drawable.a7x};
        //Construction of the array shades
        boolean[] shades = {true, false, false, true, true, true};
        databaseReference=database.getReference("data/users");
        for (int k=0; k<desc.size(); k++){
            shades[k]=sharedPreferences2.getBoolean(k+"", true);
        }
        final List<Integer> hobby = new ArrayList<>();
        for (boolean shade : shades) {
            if (shade) {
                hobby.add(shade ? 1 : 0);
            }
        }
        //TODO
        /*
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(getApplicationContext(), dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                UsersInfo.Users users=dataSnapshot.child(Profile.getCurrentProfile().getId()).getValue(UsersInfo.Users.class);
                try{
                    users.setHobbies(hobby);
                    databaseReference.child(Profile.getCurrentProfile().getId()).setValue(users);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/


        recycler.setAdapter(new ProfileWhatILikeAdapter(this, desc, id, shades));
    }

    private void saveSeekBars(int seekbar, int progress){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(seekbar+"", progress);
        editor.apply();
    }
    private void setSeekBar(){
        seekBar1=(SeekBar)findViewById(R.id.seekBar1);
        seekBar2=(SeekBar)findViewById(R.id.seekBar2);
        seekBar3=(SeekBar)findViewById(R.id.seekBar3);
        seekBar1.setProgress(sharedPreferences.getInt(seekBar1.getId()+"", 0));
        seekBar2.setProgress(sharedPreferences.getInt(seekBar2.getId()+"", 0));
        seekBar3.setProgress(sharedPreferences.getInt(seekBar3.getId()+"", 0));



        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Save now
                saveSeekBars(seekBar.getId(), seekBar.getProgress());
            }
        };
        seekBar1.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBar2.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBar3.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    private void bubbleSettings(){
        //Challenge bubble-Settings
        String challenge = " "+sharedPreferences.getString("challenge", "me faire des lasagnes");
        challenge_text.setText(challenge);
        challenge_text.setOnClickListener(onClickListener);
        DatabaseReference reference = database.getReference("/data/users/"+ Profile.getCurrentProfile().getId()+"/filter/age");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name_age = sharedPreferences.getString("first_name", "Julie")+", "+dataSnapshot.getValue().toString();
                //name_age=name_age+", "+sharedPreferences.getString("birthday", "28");
                fb_name_age.setText(name_age);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //END
    }

    private void pageLayout(){
        RelativeLayout helpMe=(RelativeLayout)findViewById(R.id.helpme);
        display = getWindowManager().getDefaultDisplay();
        RelativeLayout.LayoutParams help = new RelativeLayout.LayoutParams(display.getWidth(), display.getHeight()/2);
        helpMe.setLayoutParams(help);
    }

    private void fbProfilePic(){
        //Facebook profile pic - Settings
        profilePictureView.setProfileId(sharedPreferences.getString("profil_pic", null));
        //We must use the LayoutParams coming from the parent layout (ie a relative layout here)
        RelativeLayout.LayoutParams newLayoutParams = new RelativeLayout.LayoutParams(display.getWidth(), display.getWidth());
        profilePictureView.setLayoutParams(newLayoutParams);
        //Have to be custom to be freely set
        profilePictureView.setPresetSize(ProfilePictureView.CUSTOM);
        //END
    }
}
