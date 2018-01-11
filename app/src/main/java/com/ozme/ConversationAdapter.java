package com.ozme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.Profile;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jpec on 10/01/18.
 */
/*
Mettre un boolean pour savoir si c'est l'utilisateur ou non qui parle
 */

public class ConversationAdapter extends BaseAdapter {
    Context m_context;
    LayoutInflater inflater;
    ArrayList<ConversationActivity.Message> m_conversationMessage;
    CircleImageView circleProfile;

    public ConversationAdapter(Context applicationContext, ArrayList<ConversationActivity.Message> conversationMessage){
        this.m_context = applicationContext;
        this.m_conversationMessage=conversationMessage;
        inflater = (LayoutInflater.from(applicationContext));
        //TODO get the img from the "stranger" here
    }
    @Override
    public int getCount() {
        return m_conversationMessage.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ConversationActivity.Message message= m_conversationMessage.get(position);
        if (message.getSender()== Long.parseLong(Profile.getCurrentProfile().getId())){
            //The receiver is speaking
            convertView = inflater.inflate(R.layout.conversation_receiver, null); // inflate the layout
        }else{
            //The other one is talking
            convertView = inflater.inflate(R.layout.conversation_sender, null); // inflate the layout
            //Have to set the circleImageView
            circleProfile= (CircleImageView)convertView.findViewById(R.id.circleProfile);
            circleProfile.setImageResource(R.drawable.goku_training);
        }
        TextView messageText = (TextView) convertView.findViewById(R.id.text);
        messageText.setText(message.getText());
        return convertView;
    }
}
