package com.ozme;

import android.content.ClipData;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DragActivity extends AppCompatActivity {

    LinearLayout ll1;
    LinearLayout ll2;
    ImageView img1;
    ImageView img2;
    TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drag);

        img1=(ImageView)findViewById(R.id.img1);
        img2=(ImageView)findViewById(R.id.img2);
        ll1=(LinearLayout)findViewById(R.id.ll1);
        ll2=(LinearLayout)findViewById(R.id.ll2);
        test=(TextView)findViewById(R.id.test);

        img2.setOnLongClickListener(onLongClickListener);
        img1.setOnLongClickListener(onLongClickListener);
        ll2.setTag("ll2");
        img1.setTag("img1");
        img2.setTag("img2");
        ll1.setTag("ll1");

        img1.setOnDragListener(onDragListener);
        img2.setOnDragListener(onDragListener);

        //findViewById(R.id.ll2).setOnDragListener(onDragListener);
        //findViewById(R.id.ll1).setOnDragListener(onDragListener);
        /*
        ll1.setOnDragListener(onDragListener);
        ll2.setOnDragListener(onDragListener);
        */


    }


    View.OnDragListener onDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:

                    Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_SHORT).show();
                    test.setText("Started "+v.getTag().toString());
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    test.setText("Entered");
                    Toast.makeText(getApplicationContext(), "Entered", Toast.LENGTH_SHORT).show();
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    test.setText("Exited");

                    Toast.makeText(getApplicationContext(), "Exited", Toast.LENGTH_SHORT).show();
                    break;
                case DragEvent.ACTION_DROP:
                    test.setText("Dropped");
                    //v represents the view we are in
                    //v.setVisibility(View.INVISIBLE);

                    //view represents the view we are dragging
                    View view = (View) event.getLocalState();
                    view.setVisibility(View.INVISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (event.getResult()) {
                        test.setText("End");
                        Toast.makeText(getApplicationContext(), "The drop was handled.", Toast.LENGTH_LONG).show();

                    } else {
                        test.setText("End2");
                        Toast.makeText(getApplicationContext(), "The drop didn't work.", Toast.LENGTH_LONG).show();
                    }
                    return false;
                default:
                    break;
            }
            return true;
        }
    };

    View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            ClipData data = ClipData.newPlainText("Help", "Help me");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                    v);
            v.startDrag(data, shadowBuilder, v, 0);
            return false;
        }
    };
}
