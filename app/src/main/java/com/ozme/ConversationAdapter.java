package com.ozme;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

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
    Drawable drawable;

    public ConversationAdapter(Context applicationContext, ArrayList<ConversationActivity.Message> conversationMessage, Drawable circlePhoto){
        this.m_context = applicationContext;
        this.m_conversationMessage=conversationMessage;
        inflater = (LayoutInflater.from(applicationContext));
        this.drawable=circlePhoto;
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
        final ConversationActivity.Message message= m_conversationMessage.get(position);
        TextView messageText;
        if (message.getSender()== Long.parseLong(Profile.getCurrentProfile().getId())){
            //The receiver is speaking
            convertView = inflater.inflate(R.layout.conversation_receiver, null); // inflate the layout
            messageText= (TextView)convertView.findViewById(R.id.text);

            String type= message.getType();
            if (type != null && !type.equals("") && !type.equals("text")) {
                LinearLayout imgContainer= (LinearLayout) convertView.findViewById(R.id.imgContainer);
                imgContainer.setVisibility(View.VISIBLE);
                ImageView img = (ImageView)convertView.findViewById(R.id.img);
                TextView imgDesc= (TextView)convertView.findViewById(R.id.imgDesc);


                final byte[] rawMedia = Base64.decode(message.getData(),Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(rawMedia, 0, rawMedia.length);
                img.setImageBitmap(bmp);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(message.getType(), rawMedia);
                    }
                });

                messageText.setVisibility(View.GONE);
                imgDesc.setText(message.getText());

            }

        }else{
            //The other one is talking
            convertView = inflater.inflate(R.layout.conversation_sender, null); // inflate the layout
            messageText = (TextView) convertView.findViewById(R.id.text);
            //Have to set the circleImageView
            circleProfile= (CircleImageView)convertView.findViewById(R.id.circleProfile);
            circleProfile.setImageDrawable(drawable);

            String type= message.getType();
            if (type != null && !type.equals("") && !type.equals("text")) {
                LinearLayout imgContainer= (LinearLayout) convertView.findViewById(R.id.imgContainer);
                imgContainer.setVisibility(View.VISIBLE);
                ImageView img = (ImageView)convertView.findViewById(R.id.img);
                TextView imgDesc= (TextView)convertView.findViewById(R.id.imgDesc);

                final byte[] rawMedia = Base64.decode(message.getData(), Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(rawMedia, 0, rawMedia.length);
                img.setImageBitmap(bmp);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(message.getType(), rawMedia);
                    }
                });

                messageText.setVisibility(View.GONE);
                imgDesc.setText(message.getText());

            }
        }
        messageText.setText(message.getText());
        return convertView;
    }

    private void showDialog(String type, byte[] rawMedia){
        Dialog dialog = new Dialog(m_context);
        dialog.setContentView(R.layout.dialog_show_oz);
        dialog.setTitle("Oz - Réponse au défi");

        ImageView imageView = (ImageView)dialog.findViewById(R.id.img);
        if (type.equals("video")){
            VideoView videoView= (VideoView)dialog.findViewById(R.id.video);
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }else{
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(rawMedia, 0, rawMedia.length));
        }

        dialog.show();
    }


}
