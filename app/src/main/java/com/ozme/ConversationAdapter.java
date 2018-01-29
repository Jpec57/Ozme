package com.ozme;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.SimpleMainThreadMediaPlayerListener;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
    public FirebaseStorage firebaseStorage;
    public StorageReference storageReference;
    boolean firstTime=true;


    public ConversationAdapter(Context applicationContext, ArrayList<ConversationActivity.Message> conversationMessage) {
        this.m_context = applicationContext;
        this.m_conversationMessage = conversationMessage;
        inflater = (LayoutInflater.from(applicationContext));
        firebaseStorage = FirebaseStorage.getInstance();
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
        final ConversationActivity.Message message = m_conversationMessage.get(position);
        TextView messageText;
        if (message.getSender() == Long.parseLong(Profile.getCurrentProfile().getId())) {
            //The receiver is speaking
            convertView = inflater.inflate(R.layout.conversation_receiver, null); // inflate the layout
            messageText = (TextView) convertView.findViewById(R.id.text);

            String type = message.getType();
            if (type != null && !type.equals("") && !type.equals("text")) {
                if (type.equals("image")) {
                    isImage(convertView, message, messageText, false, position);
                } else {
                    //Last one immediatly shown
                    if (position == m_conversationMessage.size()-1){
                        showDialog(message.getType(), message.getData(), message.getTime(), position);
                    }else{
                        //Have to click on the picture first
                        isVideo(convertView, message, position);
                    }
                }

            }

        } else {
            //The other one is talking
            convertView = inflater.inflate(R.layout.conversation_sender, null); // inflate the layout
            messageText = (TextView) convertView.findViewById(R.id.text);
            //Have to set the circleImageView
            //First get the drawable from database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference("data/users/"+message.getSender()+"/photos/0").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        drawable = decodeFromBase64ToDrawable(dataSnapshot.getValue(String.class));
                    }catch (NullPointerException n){

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            circleProfile = (CircleImageView) convertView.findViewById(R.id.circleProfile);
            circleProfile.setImageDrawable(drawable);

            String type = message.getType();
            if (type != null && !type.equals("") && !type.equals("text")) {
                if (type.equals("image")) {
                    isImage(convertView, message, messageText, true, position);
                } else {
                    //if (m_conversationMessage.size()-1 == position)
                    //isVideo(convertView, message);
                }


            }
        }
        messageText.setText(message.getText());
        return convertView;
    }
    private Drawable decodeFromBase64ToDrawable(String encodedImage)
    {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return new BitmapDrawable(m_context.getResources(), BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
    }

    private void showDialog(String type, final String data, final int time, final int pos) {
        final Dialog dialog = new Dialog(m_context);
        dialog.setContentView(R.layout.dialog_show_oz);
        dialog.setTitle("Oz - Réponse au défi");

        final ImageView imageView = (ImageView) dialog.findViewById(R.id.img);
        if (type.equals("video")) {
            final VideoView videoView = (VideoView) dialog.findViewById(R.id.video);
            final ProgressBar progressBar=(ProgressBar)dialog.findViewById(R.id.progress_bar);
            imageView.setVisibility(View.GONE);

            storageReference = firebaseStorage.getReferenceFromUrl(data);
            try {
                final File localFile=File.createTempFile("OZME_TEMP", "mp4");
                storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>(){
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.e("JPEC", "file has been downloaded");
                        videoView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                        videoView.setVideoPath(localFile.getPath());
                        videoView.start();
                        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                dialog.dismiss();
                                //m_conversationMessage.remove(mes);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
            /*
                        videoView.setVideoPath(mOutputFile.getPath());
            videoView.start();
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoView.start();
                }
            });
             */

            /*
            storageReference = firebaseStorage.getReferenceFromUrl(data);
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(final Uri uri) {

                    videoView.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(uri);
                    videoView.start();
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            dialog.dismiss();
                            m_conversationMessage.remove(m_conversationMessage.size() - 1);
                            notifyDataSetChanged();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("JPEC", "Failed to load the video");
                }
            });*/

        } else {
            storageReference = firebaseStorage.getReferenceFromUrl(data);
            // Load the image using Glide
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(m_context).load(uri).into(imageView);
                    try {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        dialog.dismiss();
                                        m_conversationMessage.remove(m_conversationMessage.size() - 1);
                                        notifyDataSetChanged();

                                    }
                                },
                                time * 1000);
                    } catch (Exception e) {

                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("JPEC", "Failed to load the img");
                }
            });
        }

        dialog.show();
    }

    private void isVideo(View convertView, final ConversationActivity.Message message, final int pos) {
        Log.e("JPEC", "isVideo");
        //Make the video layout visible
        LinearLayout imgContainer = (LinearLayout) convertView.findViewById(R.id.imgContainer);
        imgContainer.setVisibility(View.VISIBLE);
        ImageView img = (ImageView)convertView.findViewById(R.id.img);
        img.setImageResource(R.drawable.video_white);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(message.getType(), message.getData(), message.getTime(), pos);
            }
        });
    }

    private void isImage(View convertView, final ConversationActivity.Message message, TextView messageText, boolean isStranger, final int pos) {
        LinearLayout imgContainer = (LinearLayout) convertView.findViewById(R.id.imgContainer);
        imgContainer.setVisibility(View.VISIBLE);
        final ImageView img = (ImageView) convertView.findViewById(R.id.img);
        TextView imgDesc = (TextView) convertView.findViewById(R.id.imgDesc);

        storageReference = firebaseStorage.getReferenceFromUrl(message.getData());
        // Load the image using Glide
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(m_context).load(uri).into(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("JPEC", "Failed to load the img");
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(message.getType(), message.getData(), message.getTime(), pos);
            }
        });
        if (isStranger) {
            //Only for the stranger
            try {
                if (message.getTime() != 0 && message.getSender() != Long.parseLong(Profile.getCurrentProfile().getId())) {
                    img.performClick();
                }
            } catch (Exception e) {

            }
        }
        messageText.setVisibility(View.GONE);
        imgDesc.setText(message.getText());
    }

    private class loadVideo extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

}
