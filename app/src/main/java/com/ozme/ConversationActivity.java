package com.ozme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    long id;
    LinearLayout focusFight;
    List<String> photos;
    String circlePhoto;
    int photosIndex=0;
    LinearLayout listViewContainer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);

        Intent intent= getIntent();
        database= FirebaseDatabase.getInstance();
        id=intent.getLongExtra("conversationId", 0);
        circlePhoto=intent.getStringExtra("circlePhoto");
        getConversation(id);

        setPicture();
        setTimerForPic();
        setFocusFight();

        listViewContainer=(LinearLayout)findViewById(R.id.listViewContainer);


        //Detect when EditText is focused to hide the img
        editText=(EditText)findViewById(R.id.messageToSend);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                listView.smoothScrollBy(0,400);
                if (hasFocus){
                    profilePictureView.setVisibility(View.GONE);
                    listView.setSelection(listView.getAdapter().getCount() - 1);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(0,0,0,200);
                    listViewContainer.setLayoutParams(layoutParams);
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
                photosIndex++;
                if (photos != null) {
                    if (photosIndex >= photos.size()) {
                        photosIndex = 0;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            profilePictureView.setImageDrawable(decodeFromBase64ToDrawable(photos.get(photosIndex)));
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

        //Get img from firebase
        DatabaseReference databaseReference1=database.getReference("data/users/"+id+"/photos");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                GenericTypeIndicator<List<String>> photosType = new GenericTypeIndicator<List<String>>(){};
                photos= dataSnapshot.getValue(photosType);

                if (photos != null) {
                    profilePictureView.setImageDrawable(decodeFromBase64ToDrawable(photos.get(photosIndex)));
                }else{
                    profilePictureView.setImageResource(R.drawable.logo_bright_white);
                    Toast.makeText(ConversationActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
    private Drawable decodeFromBase64ToDrawable(String encodedImage)
    {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
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
                    //We get rid of the notif icon if the user has read the message sent by the sender
                    Message lastMessage= conversationMessage.get(conversationMessage.size()-1);

                    if ( lastMessage.getSender() != Long.parseLong(Profile.getCurrentProfile().getId()) && !lastMessage.isRead() ){
                            lastMessage.setRead(true);
                            databaseReference.child(dataSnapshot.getKey()).setValue(lastMessage);
                    }

                    ConversationAdapter conversationAdapter = new ConversationAdapter(getApplicationContext(), conversationMessage, decodeFromBase64ToDrawable(circlePhoto));
                listView.setAdapter(conversationAdapter);
                scrollMyListViewToBottom(conversationAdapter);
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

    private void scrollMyListViewToBottom(final ConversationAdapter conversationAdapter) {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(conversationAdapter.getCount() - 1);
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
