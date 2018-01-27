package com.ozme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jpec on 25/01/18.
 */

public class RealHobbiesAdapter extends RecyclerView.Adapter<RealHobbiesAdapter.ViewHolder> {
    private Context m_context;

    RealHobbiesAdapter(Context context){
        m_context=context;

    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView img;
        public View layout;
        public TextView text;



        public ViewHolder(View v) {
            super(v);
            layout = v;
            img =(ImageView)v.findViewById(R.id.img);
            text=(TextView)v.findViewById(R.id.text);

        }
    }

    @Override
    public RealHobbiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.hobbies_template, parent, false);

        return new RealHobbiesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RealHobbiesAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
