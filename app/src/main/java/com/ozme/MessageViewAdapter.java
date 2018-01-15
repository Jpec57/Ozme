package com.ozme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jpec on 11/01/18.
 */

public class MessageViewAdapter extends BaseAdapter {
    LayoutInflater inflater;
    List<Long> conversationIds;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    static class ViewHolder {
        CircleImageView profile;
        ImageView notif;
        TextView date;
        TextView name_age;
        TextView desc;
    }

    public MessageViewAdapter(Context c, List<Long> conversationIds){
        inflater = (LayoutInflater.from(c));
        this.conversationIds=conversationIds;
        database=FirebaseDatabase.getInstance();
    }

    @Override
    public int getCount() {
        return conversationIds.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder = new ViewHolder();
        convertView = inflater.inflate(R.layout.message_view, null);
        holder.profile = (CircleImageView)convertView.findViewById(R.id.circleProfile);
        holder.date=(TextView)convertView.findViewById(R.id.date);
        holder.name_age=(TextView)convertView.findViewById(R.id.name_age);
        holder.desc=(TextView)convertView.findViewById(R.id.bref);
        holder.notif=(ImageView)convertView.findViewById(R.id.notif);
        final View finalConvertView = convertView;


        databaseReference=database.getReference("data/users/"+conversationIds.get(position));

        final String[] photo = {""};
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UsersInfo.Users users = dataSnapshot.getValue(UsersInfo.Users.class);
                List<String> photos = null;
                if (users != null) {
                    photos = users.getPhotos();
                    photo[0] =photos.get(0);
                    holder.name_age.setText(users.getUsername());
                    holder.profile.setImageDrawable(decodeFromBase64ToDrawable(photos.get(0), finalConvertView.getContext()));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        long persoId = Long.parseLong(Profile.getCurrentProfile().getId());
        Query lastQuery;
        if (persoId < conversationIds.get(position)){
            lastQuery=database.getReference("data/conversations/"+persoId+"/"+conversationIds.get(position)).limitToLast(1);
        }else{
            lastQuery=database.getReference("data/conversations/"+conversationIds.get(position)+"/"+persoId).limitToLast(1);
        }
        lastQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ConversationActivity.Message message = dataSnapshot.getValue(ConversationActivity.Message.class);
                    if (message != null) {
                        holder.desc.setText(message.getText());
                        //Test if the message has been already read
                        //Is it the receiver's message ? If no, continue
                        if (message.getSender() != Long.parseLong(Profile.getCurrentProfile().getId())){
                            //What is the current status ? If not read, show the "pastille rose"
                            if (!message.isRead()){
                                holder.notif.setVisibility(View.VISIBLE);
                            }else{
                                holder.notif.setVisibility(View.INVISIBLE);
                            }
                        }
                    } else {
                        holder.desc.setText("Error");
                    }
                    holder.date.setText(new Date(Long.parseLong(dataSnapshot.getKey())).toString());
                    return;

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ConversationActivity.Message message = dataSnapshot.getValue(ConversationActivity.Message.class);
                if (message != null && message.isRead()) {
                    holder.notif.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
/*
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator(); iterator.hasNext(); ) {
                    DataSnapshot dataSnapshot1 = iterator.next();
                    ConversationActivity.Message message = dataSnapshot1.getValue(ConversationActivity.Message.class);
                    if (message != null) {
                        holder.desc.setText(message.getText());
                    } else {
                        holder.desc.setText("Error");
                    }
                    holder.date.setText(new Date(Long.parseLong(dataSnapshot1.getKey())).toString());
                    return;
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/




        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(finalConvertView.getContext(), ConversationActivity.class);
                intent.putExtra("conversationId", conversationIds.get(position));
                intent.putExtra("circlePhoto", photo[0]);
                finalConvertView.getContext().startActivity(intent);
            }
        });
        return convertView;
    }
    private Drawable decodeFromBase64ToDrawable(String encodedImage, Context context)
    {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
    }
}
