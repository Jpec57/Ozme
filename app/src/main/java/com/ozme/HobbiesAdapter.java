package com.ozme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * Created by jpec on 22/01/18.
 */

public class HobbiesAdapter extends RecyclerView.Adapter<HobbiesAdapter.ViewHolder> {
    private Context m_context;


    HobbiesAdapter(Context context){
        m_context=context;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView img;
        public View layout;



        public ViewHolder(View v) {
            super(v);
            layout = v;
            img =(ImageView)v.findViewById(R.id.img);
        }
    }
    @Override
    public HobbiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.fil_actu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HobbiesAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
