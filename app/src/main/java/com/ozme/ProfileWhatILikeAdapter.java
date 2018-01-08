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

public class ProfileWhatILikeAdapter extends RecyclerView.Adapter<ProfileWhatILikeAdapter.ViewHolder> {
    private Context m_context;
    private List<String> m_activities_desc;
    private int[] m_id_activities;
    //If shaded = not liked
    private boolean[] m_shaded;
    SharedPreferences sharedPreferences;

    ProfileWhatILikeAdapter(Context context, List<String> activities_desc, int[] id_activities, boolean[] shaded){
        m_activities_desc=activities_desc;
        m_id_activities=id_activities;
        m_shaded=shaded;
        m_context=context;
        sharedPreferences=m_context.getSharedPreferences("activities", Context.MODE_PRIVATE);



    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView activity_desc;
        public View layout;
        public CircleImageView circleImageView;
        public ImageView gray_filter;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            activity_desc = (TextView) v.findViewById(R.id.activity_desc);
            circleImageView = (CircleImageView)v.findViewById(R.id.activity_img);
            gray_filter=(ImageView)v.findViewById(R.id.gray_filter);
        }
    }


    @Override
    public ProfileWhatILikeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.profile_what_ilike_adapter, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ProfileWhatILikeAdapter.ViewHolder holder, final int position) {
        final String activity=m_activities_desc.get(position);
        holder.activity_desc.setText(activity);
        holder.circleImageView.setImageResource(m_id_activities[position]);
        if(m_shaded[position]){
            holder.gray_filter.setVisibility(View.VISIBLE);
        }else{
            holder.gray_filter.setVisibility(View.INVISIBLE);
        }
        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_shaded[position]= !m_shaded[position];
                if(m_shaded[position]){
                    holder.gray_filter.setVisibility(View.VISIBLE);
                }else{
                    holder.gray_filter.setVisibility(View.INVISIBLE);
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(position+"", m_shaded[position]);
                editor.apply();


            }
        });
    }

    @Override
    public int getItemCount() {
        return m_activities_desc.size();
    }
}
