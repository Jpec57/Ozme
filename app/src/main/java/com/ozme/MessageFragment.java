package com.ozme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.Profile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jpec on 09/01/18.
 */

public class MessageFragment extends Fragment {
    int fragVal;
    ListView listView;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    List<Long> conversationIds;


    static MessageFragment init (int val){
        MessageFragment messageFragment =  new MessageFragment();
        Bundle args = new Bundle();
        args.putInt("val", val);
        messageFragment.setArguments(args);
        return messageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View layoutView = inflater.inflate(R.layout.fragment_message, container,
                false);
        listView=(ListView)layoutView.findViewById(R.id.listView);
        database= FirebaseDatabase.getInstance();
        databaseReference=database.getReference("data/users/"+ Profile.getCurrentProfile().getId()+"/messagers");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<Long>> genericTypeIndicator = new GenericTypeIndicator<List<Long>>() {};
                conversationIds=dataSnapshot.getValue(genericTypeIndicator);
                listView.setAdapter(new MessageViewAdapter(layoutView.getContext(), conversationIds));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return layoutView;
    }
}
