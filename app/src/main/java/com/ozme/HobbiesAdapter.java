package com.ozme;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by jpec on 22/01/18.
 */

public class HobbiesAdapter extends RecyclerView.Adapter<HobbiesAdapter.ViewHolder> {
    private Context m_context;
    private List<String> m_titles;
    private List<List<String>> m_keywords;
    private FirebaseStorage firebaseStorage;
    private EditText m_editText;


    HobbiesAdapter(Context context, List<String> titles, List<List<String>> keywords, EditText editText){
        m_context=context;
        m_titles=titles;
        m_keywords=keywords;
        firebaseStorage = FirebaseStorage.getInstance();
        m_editText=editText;

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
    public HobbiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.hobbies_template, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final HobbiesAdapter.ViewHolder holder, int position) {
        StorageReference ref= firebaseStorage.getReference("/categories/"+m_titles.get(holder.getAdapterPosition()));
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(m_context).load(uri).into(holder.img);
            }
        });
        String title = m_titles.get(holder.getAdapterPosition());
        for (int k=0; k<title.length(); k++){
            if (title.charAt(k)=='.'){
                holder.text.setText(title.substring(0,k));
                break;
            }
        }
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(m_context);
                dialog.setTitle("Propositions");
                dialog.setContentView(R.layout.list_view_prop);
                ListView listView = (ListView)dialog.findViewById(R.id.listView);
                ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(dialog.getContext(), android.R.layout.simple_list_item_1, m_keywords.get(holder.getAdapterPosition()));
                listView.setAdapter(itemsAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(m_context, "Size : "+m_titles.size(), Toast.LENGTH_SHORT).show();
                        try {
                            m_editText.setText(m_keywords.get(holder.getAdapterPosition()).get(position));
                        }catch (Exception e){
                            Log.e("JPEC", e.getLocalizedMessage());
                        }
                        dialog.dismiss();


                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return m_titles.size();
    }


}
