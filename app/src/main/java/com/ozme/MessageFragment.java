package com.ozme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jpec on 09/01/18.
 */

public class MessageFragment extends Fragment {
    int fragVal;

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
        View layoutView = inflater.inflate(R.layout.fragment_message, container,
                false);
        //TextView tv = (TextView)layoutView.findViewById(R.id.text);
        return layoutView;
    }
}
