package com.ozme;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        setData();





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


        final ArrayList<String> imgHobbyList=new ArrayList<String>();
        databaseReference=database.getReference("data/hobbies");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot hobby : dataSnapshot.getChildren()){
                    imgHobbyList.add(hobby.child("image").getValue(String.class));
                    RecyclerView recycler = (RecyclerView)findViewById(R.id.recycler);
                    recycler.setHasFixedSize(true);
                    recycler.setLayoutManager(new LinearLayoutManager(ProfilPerso.this, LinearLayoutManager.HORIZONTAL, false));
                    recycler.setAdapter(new ProfileWhatILikeAdapter(ProfilPerso.this,imgHobbyList));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference = database.getReference("data/users/"+Profile.getCurrentProfile().getId()+"/hobbies");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot index : dataSnapshot.getChildren()){
                    /*
                    try {
                        shaded[Integer.parseInt(index.getKey())] = index.getValue(Boolean.class);
                    }catch (NullPointerException n){

                    }
                    */
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        seekBar1.setTag("1");
        seekBar2.setTag("2");
        seekBar3.setTag("3");

        database.getReference("data/users/"+Profile.getCurrentProfile().getId()+"/preference1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    seekBar1.setProgress(dataSnapshot.getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        database.getReference("data/users/"+Profile.getCurrentProfile().getId()+"/preference2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    seekBar2.setProgress(dataSnapshot.getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        database.getReference("data/users/"+Profile.getCurrentProfile().getId()+"/preference3").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    seekBar3.setProgress(dataSnapshot.getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                //Save now
                database.getReference("data/users/"+Profile.getCurrentProfile().getId()+"/preference"+seekBar.getTag().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().setValue(seekBar.getProgress());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
                String name_age = sharedPreferences.getString("first_name", "None")+", "+dataSnapshot.getValue().toString();
                fb_name_age.setText(name_age);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private void setData(){
        final TextView job = (TextView)findViewById(R.id.job);
        database.getReference("data/users/"+Profile.getCurrentProfile().getId()+"/job").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    job.setText(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        LinearLayout changeJob = (LinearLayout)findViewById(R.id.changeJob);
        changeJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ProfilPerso.this);
                dialog.setTitle("Votre nouveau poste");
                dialog.setContentView(R.layout.dialog_change_job);
                final EditText editJob= (EditText)dialog.findViewById(R.id.editJob);
                Button clickJob=(Button)dialog.findViewById(R.id.clickJob);
                clickJob.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        job.setText(editJob.getText().toString());
                        database.getReference("data/users/"+Profile.getCurrentProfile().getId()+"/job").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                dataSnapshot.getRef().setValue(editJob.getText().toString());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                dialog.show();
            }
        });


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
