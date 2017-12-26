package com.ozme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/*
Use menu inflater for all items on right and XML files for those on the left
 */
public class Main2Activity extends AppCompatActivity {
    GridView simpleGrid;
    ImageSwitcher sw;
    int [] arrayList=new int[5];
    int indexArr=0;
    String[] desc;
    String[] name_age;
    int[] time;
    private Toolbar mTopToolbar;
    ImageView account;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Add ToolBar
        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        TextView mTitle = (TextView) mTopToolbar.findViewById(R.id.toolbar_title);        setSupportActionBar(mTopToolbar);
        mTitle.setText(mTopToolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        account=(ImageView)mTopToolbar.findViewById(R.id.account);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "I am working", Toast.LENGTH_SHORT).show();
            }
        });




        arrayList[0]=(R.drawable.goku_training);
        arrayList[1]=(R.drawable.a7x);
        arrayList[2]=R.drawable.papa_mariage_enzo;
        arrayList[3]=R.drawable.a7x;
        arrayList[4]=R.drawable.papa_mariage_enzo;




        simpleGrid = (GridView) findViewById(R.id.grid);
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), arrayList, name_age, desc, time);
        simpleGrid.setAdapter(customAdapter);
        simpleGrid.setSmoothScrollbarEnabled(true);


    }

    //Add the menu (inflate) to the toolbar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            Toast.makeText(getApplicationContext(), "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
