package com.ozme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

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
  Profile.getCurrentProfile().
 */

public class LoginActivity extends AppCompatActivity {
    //handle connection requests for facebook
    CallbackManager callbackManager;
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        callbackManager = CallbackManager.Factory.create();


        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        /*
        // If using in a fragment
        loginButton.setFragment(this);
        */

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
                        // loginResult will contain the AccessToken
                        Toast.makeText(getApplicationContext(), "You are a genius, dude", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), HomePage.class);
                        startActivity(intent);
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
    }

/*
DOCS :

https://developers.facebook.com/docs/facebook-login/android/?sdk=maven


 */