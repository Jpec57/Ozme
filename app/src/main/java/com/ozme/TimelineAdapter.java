package com.ozme;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    private Context m_context;


    TimelineAdapter(Context context){
        m_context=context;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView desc;
        public TextView type;
        public TextView time;
        public ImageView img;
        public View layout;


        public ViewHolder(View v) {
            super(v);
            layout = v;
            desc = (TextView) v.findViewById(R.id.desc);
            time = (TextView)v.findViewById(R.id.time);
            type=(TextView)v.findViewById(R.id.type);
            img =(ImageView)v.findViewById(R.id.img);
        }
    }


    @Override
    public TimelineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.fil_actu, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final TimelineAdapter.ViewHolder holder, final int position) {
        /*holder.activity_desc.setText(activity);
        holder.circleImageView.setImageResource(m_id_activities[position]);


        */
    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
