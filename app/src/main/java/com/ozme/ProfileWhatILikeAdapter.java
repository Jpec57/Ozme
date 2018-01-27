package com.ozme;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileWhatILikeAdapter extends RecyclerView.Adapter<ProfileWhatILikeAdapter.ViewHolder> {
    private Context m_context;
    private List<String> m_hobbyNames;
    SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    ProfileWhatILikeAdapter(Context context, List<String> hobbyNames) {
        m_context = context;
        m_hobbyNames = hobbyNames;
        sharedPreferences = m_context.getSharedPreferences("activities", Context.MODE_PRIVATE);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("data/users/" + Profile.getCurrentProfile().getId()+"/hobbies");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("hobbies");

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
            circleImageView = (CircleImageView) v.findViewById(R.id.activity_img);
            gray_filter = (ImageView) v.findViewById(R.id.gray_filter);
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
    public void onBindViewHolder(final ProfileWhatILikeAdapter.ViewHolder holder, int position) {
        try {
            String activity = "";
            for (int k = 0; k < m_hobbyNames.get(position).length(); k++) {
                if (m_hobbyNames.get(position).charAt(k) == '.') {
                    activity = m_hobbyNames.get(position).substring(0, k);
                }
            }
            holder.activity_desc.setText(activity);

            storageReference.child(m_hobbyNames.get(position)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(m_context).load(uri).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            holder.circleImageView.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                }
            });
            final boolean[] shaded = new boolean[m_hobbyNames.size()];
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot index : dataSnapshot.getChildren()){
                        try {
                            shaded[Integer.parseInt(index.getKey())] = index.getValue(Boolean.class);
                        }catch (NullPointerException n){

                        }
                    }


                    //Test if shaded or not in database
                    try {
                        if (shaded[holder.getAdapterPosition()]) {
                            holder.gray_filter.setVisibility(View.VISIBLE);
                        } else {
                            holder.gray_filter.setVisibility(View.INVISIBLE);
                        }
                    } catch (IndexOutOfBoundsException ioobe) {

                    }

                    holder.circleImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            shaded[holder.getAdapterPosition()]= !shaded[holder.getAdapterPosition()];
                            if (!shaded[holder.getAdapterPosition()]) {
                                holder.gray_filter.setVisibility(View.VISIBLE);
                            } else {
                                holder.gray_filter.setVisibility(View.INVISIBLE);
                            }

                            databaseReference.child(""+holder.getAdapterPosition()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    dataSnapshot.getRef().setValue(shaded[holder.getAdapterPosition()]);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });



                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {

        }

    }

    @Override
    public int getItemCount() {
        return m_hobbyNames.size();
    }
}
