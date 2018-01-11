package com.ozme;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.ozme.ChallengeChoiceActivity.accessToken;
/*
NB : We must compare the id to know you will be the first node
 */

public class ConversationActivity extends AppCompatActivity {
    ImageSwitcher profilePictureView;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<Message> conversationMessage;
    ListView listView;
    EditText editText;
    ImageView send;
    Timer timer;
    Date date;
    boolean a;
    LinearLayout focusFight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);

        Intent intent= getIntent();
        database= FirebaseDatabase.getInstance();
        getConversation(intent.getLongExtra("conversationId", 0));

        setPicture();
        setTimerForPic();
        setFocusFight();

        //Detect when EditText is focused to hide the img
        editText=(EditText)findViewById(R.id.messageToSend);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                listView.smoothScrollBy(0,400);
                if (hasFocus){
                    profilePictureView.setVisibility(View.GONE);
                    listView.smoothScrollToPosition(listView.getLastVisiblePosition());
                }else{
                    profilePictureView.setVisibility(View.VISIBLE);
                    InputMethodManager imanager = (InputMethodManager) getApplicationContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imanager != null) {
                        imanager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }

                }
            }
        });
        send=(ImageView)findViewById(R.id.send);
        //has to be after getConv
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=editText.getText().toString();
                if (!text.equals("")){
                    Message message = new Message();
                    message.setText(text);
                    message.setSender(Long.parseLong(Profile.getCurrentProfile().getId()));
                    databaseReference.child(System.currentTimeMillis()+"").setValue(message);
                    editText.setText("");
                }
            }
        });




    }
    private void setFocusFight(){
        focusFight=(LinearLayout)findViewById(R.id.focusFight);
        focusFight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusFight.requestFocus();
            }
        });
    }

    private void setTimerForPic(){
        timer= new Timer();
        date = new Date();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                a=!a;
                if (a){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            profilePictureView.setImageResource(R.drawable.goku_training);
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            profilePictureView.setImageResource(R.drawable.a7x);
                        }
                    });
                }
            }
        },date,   10*1000);
    }

    private void setPicture(){
        profilePictureView= (ImageSwitcher) findViewById(R.id.profile);
        profilePictureView.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new
                        ImageSwitcher.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                return myView;
            }
        });
        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);
        profilePictureView.setInAnimation(in);
        profilePictureView.setOutAnimation(out);
        profilePictureView.setImageResource(R.drawable.a7x);
    }


    private void getConversation (long strangerId){
        listView=(ListView)findViewById(R.id.listView);
        listView.setItemsCanFocus(false);
        listView.setClickable(false);
        long persoId = Long.parseLong(Profile.getCurrentProfile().getId());
        if (persoId < strangerId){
            databaseReference=database.getReference("data/conversations/"+persoId+"/"+strangerId);
        }else{
            databaseReference=database.getReference("data/conversations/"+strangerId+"/"+persoId);
        }

        conversationMessage= new ArrayList<>();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                conversationMessage.add(message);
                listView.setAdapter(new ConversationAdapter(getApplicationContext(), conversationMessage));

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //Cannot be changed
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //Cannot be removed
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //Cannot be moved
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        });



    }

    public void getFbInfo(){
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,birthday,work, gender");
        request.setParameters(parameters);
        request.executeAsync();

    }
    /*
    https://firebase.google.com/docs/database/admin/retrieve-data
    https://firebase.google.com/docs/database/android/read-and-write
     */

    public static class Message{
        public boolean read;
        public String text;
        public long sender;
        public String status;
        public Message(boolean read, long sender, String text, String status){
            this.text=text;
            this.read=read;
            this.sender=sender;
            this.status= status;
        }
        public Message(){

        }

        public boolean isRead() {
            return read;
        }

        public String getText() {
            return text;
        }

        public long getSender() {
            return sender;
        }

        public void setRead(boolean read) {
            this.read = read;
        }

        public void setText(String text) {
            this.text = text;
        }

        public void setSender(long sender) {
            this.sender = sender;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

    }




}