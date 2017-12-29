package com.ozme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.*;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class ChallengeChoiceActivity extends AppCompatActivity {
    Context context;
    EditText editText;
    String beforeChanges;
    TextView test;
    TextView next;
    ProfilePictureView profilePictureView;
    static String[] forbiddenWords = {"suicider", "veine", "suicide"};
    String forbiddenOne;
    static AccessToken accessToken= AccessToken.getCurrentAccessToken();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_challenge);
        context=this;

        test = new TextView(getApplicationContext());
        next =(TextView)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForForbiddenWords()){
                    Intent intent = new Intent(getApplicationContext(), HomePage.class);
                    startActivity(intent);
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

    public static Bitmap getFacebookProfilePicture(String userID) throws IOException {
        URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture");
        return BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
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
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private boolean checkForForbiddenWords(){
        for (String forbiddenWord : forbiddenWords) {
            if (test.getText().toString().toLowerCase().contains(forbiddenWord)) {
                forbiddenOne=forbiddenWord;
                return false;
            }

        }
        return true;
    }


    private void setProfileToView(JSONObject jsonObject) {
        //facebookName.setText(jsonObject.getString("name"));

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
        //layoutToBeCentered.addRule(RelativeLayout.CENTER_IN_PARENT);
        //layoutToBeCentered.addRule(RelativeLayout.RIGHT_OF, R.id.ose_logo);
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

        }
    }


}