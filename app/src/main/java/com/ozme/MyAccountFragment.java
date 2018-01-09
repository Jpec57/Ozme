package com.ozme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by jpec on 10/01/18.
 */

public class MyAccountFragment extends Fragment {
    int fragVal;
    View layoutView;
    private ImageView profile;
    RoundImage roundImage;
    TextView challenge_text;
    ImageView settings;
    ImageView add;
    ImageView help;

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
        layoutView = inflater.inflate(R.layout.my_account, container,
                false);
        settings=(ImageView)layoutView.findViewById(R.id.settings);
        add=(ImageView)layoutView.findViewById(R.id.add);
        help=(ImageView)layoutView.findViewById(R.id.help);
        //Setting onClickListener
        settings.setOnClickListener(onClickListener);
        add.setOnClickListener(onClickListener);
        help.setOnClickListener(onClickListener);

        //Adapter for notifications
        MyAccountNotificationAdapter adapter = new MyAccountNotificationAdapter(layoutView.getContext());
        ListView listView = (ListView)layoutView.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        profile=(ImageView)layoutView.findViewById(R.id.profile);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.papa_mariage_enzo);
        roundImage = new RoundImage(bm);
        profile.setImageDrawable(roundImage);

        //Display display = getActivity().getWindowManager().getDefaultDisplay();
        challenge_text=(TextView)layoutView.findViewById(R.id.challenge_text);
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
