package com.ozme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by jpec on 10/01/18.
 */

public class MyAccountFragment extends Fragment {
    int fragVal;
    View layoutView;
    private ImageView profile;
    RoundImage roundImage;
    TextView challenge_text, fb_name_age;
    ImageView settings;
    ImageView add;
    ImageView help;
    LinearLayout challenge;
    RelativeLayout background;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    UsersInfo.Users user;

    static MyAccountFragment init (int val){
        MyAccountFragment myAccountFragment =  new MyAccountFragment();
        Bundle args = new Bundle();
        args.putInt("val", val);
        myAccountFragment.setArguments(args);
        return myAccountFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_my_account, container,
                false);
        settings=(ImageView)layoutView.findViewById(R.id.settings);
        add=(ImageView)layoutView.findViewById(R.id.add);
        help=(ImageView)layoutView.findViewById(R.id.help);
        challenge=(LinearLayout)layoutView.findViewById(R.id.challenge);
        background=(RelativeLayout)layoutView.findViewById(R.id.background);

        //Setting onClickListener
        settings.setOnClickListener(onClickListener);
        add.setOnClickListener(onClickListener);
        help.setOnClickListener(onClickListener);
        challenge.setOnClickListener(onClickListener);
        background.setOnClickListener(onClickListener);

        //Adapter for notifications
        MyAccountNotificationAdapter adapter = new MyAccountNotificationAdapter(layoutView.getContext());
        ListView listView = (ListView)layoutView.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        profile=(ImageView)layoutView.findViewById(R.id.profile);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.papa_mariage_enzo);
        roundImage = new RoundImage(bm);
        profile.setImageDrawable(roundImage);
        ProfilePictureView profilePictureView = (ProfilePictureView)layoutView.findViewById(R.id.circleProfile);
        profilePictureView.setProfileId(Profile.getCurrentProfile().getId());


        challenge_text=(TextView)layoutView.findViewById(R.id.challenge_text);
        fb_name_age=(TextView)layoutView.findViewById(R.id.fb_name_age);

        database= FirebaseDatabase.getInstance();
        databaseReference=database.getReference("data/users/"+ Profile.getCurrentProfile().getId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user= dataSnapshot.getValue(UsersInfo.Users.class);
                if (user != null) {
                    challenge_text.setText(user.getChallengeTitle());
                    UsersInfo.Filter filter = user.getFilter();
                    String name_age=user.getUsername()+", "+filter.getAge();
                    fb_name_age.setText(name_age);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return layoutView;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()){
                case R.id.settings:
                    intent = new Intent(layoutView.getContext(),SettingsActivity.class);
                    break;
                case R.id.challenge:
                    intent= new Intent(layoutView.getContext(), ChallengeChoiceActivity.class);
                    break;
                case R.id.background:
                    intent= new Intent(layoutView.getContext(), ProfilPerso.class);
                    break;
                case R.id.profileIcon:
                    intent= new Intent(layoutView.getContext(), ProfilPerso.class);
                    break;
                default:
                    intent=null;
                    break;


            }
            if (intent != null){
                startActivity(intent);
            }
        }
    };
}
