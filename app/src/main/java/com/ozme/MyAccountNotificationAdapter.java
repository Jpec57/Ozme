package com.ozme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by jpec on 15/12/17.
 */

public class MyAccountNotificationAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;

    public MyAccountNotificationAdapter(Context c){
        this.context=c;
        inflater = (LayoutInflater.from(c));

    }
    @Override
    public int getCount() {
        return 10;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.my_account_notif, null); // inflate the layout

        return convertView;
    }
}
