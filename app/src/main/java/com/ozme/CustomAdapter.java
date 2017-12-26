package com.ozme;

/**
 * Created by jpec on 12/11/17.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class CustomAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ImageSwitcher sw;
    TextView name_age;
    TextView desc;
    TextView time;
    int[] imgs;
    int index=0;

    public CustomAdapter(Context applicationContext, int[] imgResources, String[] name_age, String [] desc, int[] time) {
        this.context = applicationContext;
        inflater = (LayoutInflater.from(applicationContext));
        this.imgs=imgResources;


    }
    @Override
    public int getCount() {
        return 5;
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.timeline_profile, null); // inflate the layout
        sw = (ImageSwitcher) view.findViewById(R.id.imageSwitcher1);
        name_age = (TextView)view.findViewById(R.id.name_age);
        time=(TextView)view.findViewById(R.id.time);
        desc=(TextView)view.findViewById(R.id.desc);

        final View finalView = view;
        sw.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(finalView.getContext());
                myView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                myView.setLayoutParams(new
                        ImageSwitcher.LayoutParams(Gallery.LayoutParams.WRAP_CONTENT,
                        Gallery.LayoutParams.WRAP_CONTENT));
                return myView;
            }
        });
        sw.setImageResource(imgs[i%4]);

        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index=i;
                index++;
                sw.setImageResource(imgs[index%5]);
            }
        });


        return view;
    }
}
