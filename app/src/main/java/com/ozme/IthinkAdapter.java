package com.ozme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jpec on 30/12/17.
 */

public class IthinkAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<String> m_think;
    public IthinkAdapter(Context applicationContext, ArrayList<String> think){
        this.context = applicationContext;
        this.m_think=think;
        inflater = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return m_think.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.what_i_think, null); // inflate the layout
        TextView message = (TextView) convertView.findViewById(R.id.message);
        message.setText(m_think.get(position));
        return convertView;
    }
}
