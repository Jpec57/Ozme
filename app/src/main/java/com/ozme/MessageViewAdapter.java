package com.ozme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.facebook.Profile;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jpec on 11/01/18.
 */

public class MessageViewAdapter extends BaseAdapter {
    LayoutInflater inflater;
    List<Long> conversationIds;
    static class ViewHolder {
        CircleImageView imageView;
    }

    public MessageViewAdapter(Context c, List<Long> conversationIds){
        inflater = (LayoutInflater.from(c));
        this.conversationIds=conversationIds;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        convertView = inflater.inflate(R.layout.message_view, null);
        holder.imageView = (CircleImageView)convertView.findViewById(R.id.circleProfile);
        //holder.imageView.setImageResource();

        final View finalConvertView = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(finalConvertView.getContext(), ConversationActivity.class);
                intent.putExtra("conversationId", conversationIds.get(position));
                finalConvertView.getContext().startActivity(intent);
            }
        });
        return convertView;
    }
}
