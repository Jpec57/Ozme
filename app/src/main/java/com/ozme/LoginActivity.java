package com.ozme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/*
 Le SDK Facebook enregistre ces données dans les préférences communes et se charge
 de les paramétrer au début de la session. Vous pouvez déterminer si quelqu’un est
 actuellement connecté en vérifiant AccessToken.getCurrentAccessToken() et
  ProfilePublic.getCurrentProfile().
 */

public class LoginActivity extends AppCompatActivity {
    //handle connection requests for facebook
    CallbackManager callbackManager;
    LoginButton loginButton;
    SharedPreferences sharedPreferences;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //Video management
        VideoView videoview = (VideoView) findViewById(R.id.video_view);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.accueil);
        videoview.setVideoURI(uri);
        videoview.start();
        //End


        //Facebook login

        //First, we have to check if the user isn't currently sign in
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                if (currentAccessToken != null){
                    /*
                    Intent intent = new Intent(getApplicationContext(), MainTimelineFragment.class);
                    startActivity(intent);*/
                }
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
        boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
/*
        if (!loggedIn){
            Intent intent = new Intent(getApplicationContext(), MainTimelineFragment.class);
            startActivity(intent);
        }*/

        //END

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        //Ask for more permissions
        loginButton.setReadPermissions(Arrays.asList("email", "user_birthday","user_work_history", "user_photos", "user_location", "public_profile", "user_about_me", "user_friends" ));
        sharedPreferences= getSharedPreferences("user", MODE_PRIVATE);


        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            //See LoginBehavior for more options...
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        callbackManager = CallbackManager.Factory.create();

        //Handle connection answers
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        //Test if it is the first connexion
                        if (sharedPreferences.getBoolean("first_visit", true)){
                            //First connexion
                            // loginResult will contain the AccessToken
                            Intent intent = new Intent(getApplicationContext(), ChallengeChoiceActivity.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(getApplicationContext(), MainTimelineFragment.class);
                            startActivity(intent);
                        }





                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
        }




    //Transfer the connection result to the LoginManager
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }
    public static class Filter{
        int age;
        int ageMax;
        int ageMin;
        boolean femme;
        boolean homme;
        boolean flash;
        boolean match;
        int maxDistance;
        boolean messages;
        String sexuality;
        int timelineMode;

        Filter(int age, int ageMax, int ageMin, boolean femme, boolean homme, boolean flash, boolean match, int maxDistance,
               boolean messages, String sexuality, int timelineMode){
            this.age=age;
            this.ageMax=ageMax;
            this.ageMin=ageMin;
            this.femme=femme;
            this.homme=homme;
            this.flash=flash;
            this.match=match;
            this.maxDistance=maxDistance;
            this.messages=messages;
            this.sexuality=sexuality;
            this.timelineMode=timelineMode;

        }

        Filter(){

        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getAgeMax() {
            return ageMax;
        }

        public void setAgeMax(int ageMax) {
            this.ageMax = ageMax;
        }

        public int getAgeMin() {
            return ageMin;
        }

        public void setAgeMin(int ageMin) {
            this.ageMin = ageMin;
        }

        public boolean isFemme() {
            return femme;
        }

        public void setFemme(boolean femme) {
            this.femme = femme;
        }

        public boolean isHomme() {
            return homme;
        }

        public void setHomme(boolean homme) {
            this.homme = homme;
        }

        public boolean isFlash() {
            return flash;
        }

        public void setFlash(boolean flash) {
            this.flash = flash;
        }

        public boolean isMatch() {
            return match;
        }

        public void setMatch(boolean match) {
            this.match = match;
        }

        public int getMaxDistance() {
            return maxDistance;
        }

        public void setMaxDistance(int maxDistance) {
            this.maxDistance = maxDistance;
        }

        public boolean isMessages() {
            return messages;
        }

        public void setMessages(boolean messages) {
            this.messages = messages;
        }

        public String getSexuality() {
            return sexuality;
        }

        public void setSexuality(String sexuality) {
            this.sexuality = sexuality;
        }

        public int getTimelineMode() {
            return timelineMode;
        }

        public void setTimelineMode(int timelineMode) {
            this.timelineMode = timelineMode;
        }
    }

    public static class Users{
        String birthday;
        String challengeTitle;
        String code;
        String description;
        Filter filter;
        String gender;
        List<Integer> hobbies;
        String ithink;
        String job;
        JSONObject location;
        List<String> photos;
        JSONObject preference1;
        JSONObject preference2;
        JSONObject preference3;
        JSONObject stats;
        String username;
        List<Integer> messagers;
        

        public Users(String birthday, String challengeTitle, String code, String description, Filter filter, String gender, List<Integer> hobbies, String ithink, String job, JSONObject location, List<String> photos, JSONObject preference1, JSONObject preference2, JSONObject preference3, JSONObject stats, String username, List<Integer> messagers) {
            this.birthday = birthday;
            this.challengeTitle = challengeTitle;
            this.code = code;
            this.description = description;
            this.filter = filter;
            this.gender = gender;
            this.hobbies = hobbies;
            this.ithink = ithink;
            this.job = job;
            this.location = location;
            this.photos = photos;
            this.preference1 = preference1;
            this.preference2 = preference2;
            this.preference3 = preference3;
            this.stats = stats;
            this.username = username;
            this.messagers = messagers;
        }


        Users(){

        }

        public void setPhotos(List<String> photos) {
            this.photos = photos;
        }

        public List<Integer> getMessagers() {
            return messagers;
        }

        public void setMessagers(List<Integer> messagers) {
            this.messagers = messagers;
        }

        public Filter getFilter() {
            return filter;
        }

        public void setFilter(Filter filter) {
            this.filter = filter;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getChallengeTitle() {
            return challengeTitle;
        }

        public void setChallengeTitle(String challengeTitle) {
            this.challengeTitle = challengeTitle;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public List<Integer> getHobbies() {
            return hobbies;
        }

        public void setHobbies(List<Integer> hobbies) {
            this.hobbies = hobbies;
        }

        public String getIthink() {
            return ithink;
        }

        public void setIthink(String ithink) {
            this.ithink = ithink;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public JSONObject getLocation() {
            return location;
        }

        public void setLocation(JSONObject location) {
            this.location = location;
        }

        public List<String> getPhotos() {
            return photos;
        }

        public void setPhotos(ArrayList<String> photos) {
            this.photos = photos;
        }

        public JSONObject getPreference1() {
            return preference1;
        }

        public void setPreference1(JSONObject preference1) {
            this.preference1 = preference1;
        }

        public JSONObject getPreference2() {
            return preference2;
        }

        public void setPreference2(JSONObject preference2) {
            this.preference2 = preference2;
        }

        public JSONObject getPreference3() {
            return preference3;
        }

        public void setPreference3(JSONObject preference3) {
            this.preference3 = preference3;
        }

        public JSONObject getStats() {
            return stats;
        }

        public void setStats(JSONObject stats) {
            this.stats = stats;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }


}

/*
DOCS :

https://developers.facebook.com/docs/facebook-login/android/?sdk=maven


 */