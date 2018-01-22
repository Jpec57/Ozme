package com.ozme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChallengeChoiceActivity extends AppCompatActivity {
    Context context;
    EditText editText;
    String beforeChanges;
    String fb_name;
    String birthday;
    String gender;
    String profile_pic;
    String work;
    TextView test;
    TextView next;
    ProfilePictureView profilePictureView;
    static String[] forbiddenWords = {"suicider", "veine", "suicide"};
    String forbiddenOne;
    static AccessToken accessToken= AccessToken.getCurrentAccessToken();
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    JSONArray age_range;
    SharedPreferences sharedPreferences;

    List<String> categoriesTitles= new ArrayList<String>();
    List<List<String>> categoriesKeywords = new ArrayList<List<String>>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_challenge);
        context=this;
        database= FirebaseDatabase.getInstance();

        setCategoriesBubbles();


        test = new TextView(getApplicationContext());
        next =(TextView)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForForbiddenWords()){
                    sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("challenge", editText.getText().toString());
                    editor.putString("first_name", fb_name);
                    editor.putString("birthday", birthday);
                    editor.putString("profil_pic", profile_pic);
                    editor.putString("work", work);
                    editor.apply();
                    //Initialize user
                    firstConnection(sharedPreferences.getBoolean("first_visit", true));
                }else{
                    //Modal present to inform the user
                    // 1. Instantiate an AlertDialog.Builder with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage("Vous ne pouvez pas utiliser le mot \""+forbiddenOne+"\"")
                            .setTitle("Mot interdit")
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    // 3. Get the AlertDialog from create()
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });
        profilePictureView=(ProfilePictureView)findViewById(R.id.friendProfilePicture);
        getFbInfo();
        editText=(EditText)findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (editText.getText().toString().length() == 50){
                    beforeChanges=editText.getText().toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                test.setText(editText.getText().toString());


            }
        });

    }


    public void getFbInfo(){
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        setProfileToView(object);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,birthday,work, gender,age_range");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private boolean checkForForbiddenWords(){
        if (test.getText().toString().equals("")){
            return false;
        }
        for (String forbiddenWord : forbiddenWords) {
            if (test.getText().toString().toLowerCase().contains(forbiddenWord)) {
                forbiddenOne=forbiddenWord;
                return false;
            }

        }
        return true;
    }



    private void setProfileToView(JSONObject jsonObject) {
        //We adapt to the device's width and height
        Display display = getWindowManager().getDefaultDisplay();
        //We must use the LayoutParams coming from the parent layout (ie a relative layout here)
        RelativeLayout.LayoutParams newLayoutParams = new RelativeLayout.LayoutParams(display.getWidth(), display.getWidth());
        profilePictureView.setLayoutParams(newLayoutParams);
        //Have to be custom to be freely set
        profilePictureView.setPresetSize(ProfilePictureView.CUSTOM);
        //Pink filter
        RelativeLayout pink = (RelativeLayout)findViewById(R.id.pink);
        RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(display.getWidth(), display.getWidth()/3);
        newParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        newParams.setMargins(0,0,0,-2);
        pink.setLayoutParams(newParams);
        test.setText("trouver un bon défi");
        test.setTextSize(18);
        RelativeLayout ose=(RelativeLayout)findViewById(R.id.ose);
        RelativeLayout.LayoutParams layoutToBeCentered = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutToBeCentered.addRule(RelativeLayout.BELOW,R.id.ose_logo);
        layoutToBeCentered.addRule(RelativeLayout.CENTER_HORIZONTAL);
        ose.setLayoutParams(layoutToBeCentered);
        ose.addView(test);
        test.setTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            test.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        test.setShadowLayer(2, 1, 1, Color.BLACK);
        try{
            profilePictureView.setProfileId(jsonObject.getString("id"));
        }catch (Exception e){
            Intent intent=new Intent(this, ChallengeChoiceActivity.class);
            startActivity(intent);
        }

        //Get the user's name
        try {
            profile_pic = jsonObject.getString("id");
            fb_name = jsonObject.getString("first_name");
            birthday = jsonObject.getString("birthday");
            gender=jsonObject.getString("gender");
            age_range = jsonObject.getJSONArray("age_range");

            //AgeAuthToKeepGoing();

            try{
                work=jsonObject.getJSONArray("work").toString();



            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void AgeAuthToKeepGoing(int age){
        if (age<18){
            Intent intent = new Intent(this, LoginActivity.class);
            Profile.setCurrentProfile(null);
            startActivity(intent);
        }
    }

    private void firstConnection(boolean isFirstConnection){
        databaseReference=database.getReference("data/users/"+Profile.getCurrentProfile().getId());
        if (isFirstConnection){
            UsersInfo.Users newUser = new UsersInfo.Users();
            UsersInfo.Filter newFilter = new UsersInfo.Filter();
            Profile profile = Profile.getCurrentProfile();
            ArrayList<Integer> hobbies = new ArrayList<Integer>();
            hobbies.add(2);
            hobbies.add(4);
            newUser.setHobbies(hobbies);
            newUser.setUsername(profile.getFirstName());
            newUser.setGender(gender);
            newUser.setJob(work);
            newUser.setChallengeTitle(test.getText().toString());
            newUser.setFilter(newFilter);
            databaseReference.setValue(newUser);
        }else{
            databaseReference.child("challengeTitle").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try{
                        dataSnapshot.getRef().setValue(test.getText().toString());
                        Intent intent = new Intent(getApplicationContext(), ProfilPerso.class);
                        startActivity(intent);

                    }catch (Exception e){
                        Log.e("HELPPPPPPP : ", e.getLocalizedMessage());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            /*
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try{
                        UsersInfo.Users users=dataSnapshot.getValue(UsersInfo.Users.class);
                        users.setChallengeTitle(test.getText().toString());
                        databaseReference.setValue(users);
                        Log.e("OZME", users.getChallengeTitle());
                        Intent intent = new Intent(getApplicationContext(), ProfilPerso.class);
                        startActivity(intent);

                    }catch (Exception e){
                        Log.e("HELPPPPPPP : ", e.getLocalizedMessage());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/


        }
    }

    private void setCategoriesBubbles(){
        DatabaseReference categoriesRef = database.getReference("data/categories/");
        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot category : dataSnapshot.getChildren()){
                    categoriesTitles.add(category.child("image").getValue(String.class));
                    GenericTypeIndicator<List<String>> gti = new GenericTypeIndicator<List<String>>() {};
                    categoriesKeywords.add(category.child("keywords").getValue(gti));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
