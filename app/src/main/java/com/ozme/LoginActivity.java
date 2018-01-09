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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
                    Intent intent = new Intent(getApplicationContext(), MainTimelineFragment.class);
                    startActivity(intent);
                }
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
        boolean loggedIn = AccessToken.getCurrentAccessToken() == null;

        if (!loggedIn){
            Intent intent = new Intent(getApplicationContext(), MainTimelineFragment.class);
            startActivity(intent);
        }

        //END

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
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
}

/*
DOCS :

https://developers.facebook.com/docs/facebook-login/android/?sdk=maven


 */