package com.ozme;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.DragEvent;
import android.view.View;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

/*
DOCS :

https://github.com/ArthurHub/Android-Image-Cropper
 */

public class MyPhotosActivity extends AppCompatActivity {
    View small1;
    View small2;
    View small3;
    View small4;
    View small5;
    View big;
    ImageView add_delete1;
    ImageView add_delete2;
    ImageView add_delete3;
    ImageView add_delete4;
    ImageView add_delete5;
    ImageView add_delete6;
    RelativeLayout bigLayout;
    RelativeLayout layout1;
    String stockFile;
    ImageView view;
    ArrayList<ImageView> imgViews = new ArrayList<>();
    ArrayList<ImageView> addDelete = new ArrayList<>();
    SharedPreferences sharedPreferences;
    ArrayList<String> imgsArray;
    int index = 0;
    String help="nothing";
    int[] state={0,0,0,0,0,0};
    int zoneId=0;
    int draggedId=0;
    JSONObject userInfos;
    DatabaseReference databaseReference;
    FirebaseDatabase database;
    UsersInfo.Users user;
    List<String> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_photos_activity);

        //Initialization
        bigLayout = (RelativeLayout) findViewById(R.id.bigLayout);
        big = findViewById(R.id.big);
        small1 = findViewById(R.id.small1);
        small2 = findViewById(R.id.small2);
        small3 = findViewById(R.id.small3);
        small4 = findViewById(R.id.small4);
        small5 = findViewById(R.id.small5);
        small1.setOnClickListener(onClickListener);
        small2.setOnClickListener(onClickListener);
        small3.setOnClickListener(onClickListener);
        small4.setOnClickListener(onClickListener);
        small5.setOnClickListener(onClickListener);
        big.setOnClickListener(onClickListener);
        imgViews.add((ImageView) small1);
        imgViews.add((ImageView) small2);
        imgViews.add((ImageView) small3);
        imgViews.add((ImageView) small4);
        imgViews.add((ImageView) small5);
        imgViews.add((ImageView) big);
        imgsArray = new ArrayList<>();
        imgsArray.add("img1.jpg");
        imgsArray.add("img2.jpg");
        imgsArray.add("img3.jpg");
        imgsArray.add("img4.jpg");
        imgsArray.add("img5.jpg");
        imgsArray.add("img6.jpg");
        add_delete1=(ImageView)findViewById(R.id.add_delete1);
        add_delete2=(ImageView)findViewById(R.id.add_delete2);
        add_delete3=(ImageView)findViewById(R.id.add_delete3);
        add_delete4=(ImageView)findViewById(R.id.add_delete4);
        add_delete5=(ImageView)findViewById(R.id.add_delete5);
        add_delete6=(ImageView)findViewById(R.id.add_delete6);
        add_delete1.setOnClickListener(onClickListener2);
        add_delete2.setOnClickListener(onClickListener2);
        add_delete3.setOnClickListener(onClickListener2);
        add_delete4.setOnClickListener(onClickListener2);
        add_delete5.setOnClickListener(onClickListener2);
        add_delete6.setOnClickListener(onClickListener2);
        addDelete.add(add_delete1);
        addDelete.add(add_delete2);
        addDelete.add(add_delete3);
        addDelete.add(add_delete4);
        addDelete.add(add_delete5);
        addDelete.add(add_delete6);
        layout1 = (RelativeLayout)findViewById(R.id.layout1);

        database= FirebaseDatabase.getInstance();
        databaseReference=database.getReference("data/users/"+ Profile.getCurrentProfile().getId()+"/photos");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().setValue(photos);
                //We save it to firebase
                imgLoader();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Test for big image
        Bitmap yourBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.papa_mariage_enzo);
        //Profile pic
        saveToInternalStorage(yourBitmap, "test.jpg", null);
        imgLoader();
        imgDragAndDrop();
        userInfoQuery();






    }


    private void userInfoQuery(){
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Insert your code here
                        userInfos=object;

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,birthday,work,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyPhotosActivity.this, ProfilPerso.class);
        startActivity(intent);
    }

    private void imgLoader() {
        photos= new ArrayList<String>();
        for (int k = 0; k < imgsArray.size(); k++) {
            try {
                //Same path as saveInternal
                File f = new File(stockFile, imgsArray.get(k));
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                view = imgViews.get(k);
                view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Bitmap yourBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.photo_empty);
                view.setImageBitmap(yourBitmap);
                //State is used to know if there is an image or not
                if (b != null){
                    view.setImageBitmap(b);
                    //There is something
                    state[k]=1;
                    photos.add(getEncoded64ImageStringFromBitmap(b));
                    addDelete.get(k).setImageDrawable(getResources().getDrawable(R.drawable.photo_delete));
                }else{
                    //There isn't
                    state[k]=0;
                    addDelete.get(k).setImageDrawable(getResources().getDrawable(R.drawable.photo_add));
                }
            } catch (Exception e) {
                //No img --> cannot load
            }
        }



    }

    private String saveToInternalStorage(Bitmap bitmapImage, String path, Uri crop) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/com.ozme/app_data/Images
        File directory = cw.getDir("Images", Context.MODE_PRIVATE);
        // Create imageDir only if doesn't exist already
        File mypath = new File(directory, path);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            //if crop isn't null, we have to get the img from the cache to save it
            if (crop != null){
                bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), crop);
            }
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stockFile = directory.getAbsolutePath();
        return mypath.getAbsolutePath();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            String res = "Not saved";
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    imgViews.get(index).setImageURI(resultUri);
                    addDelete.get(index).setImageDrawable(getResources().getDrawable(R.drawable.photo_delete));
                    state[index]=1;
                    help = saveToInternalStorage(result.getBitmap(), "img" + (index+1) + ".jpg", resultUri);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),res+" ERROR", Toast.LENGTH_LONG).show();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                //Toast.makeText(getApplicationContext(),res+" "+error.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void imgDragAndDrop(){
        for (int i=0; i<imgViews.size(); i++){
            imgViews.get(i).setTag("img"+i);

            imgViews.get(i).setOnLongClickListener(onLongClickListener);
            imgViews.get(i).setOnDragListener(onDragListener);
        }
    }

    View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                    v);
            v.startDrag(data, shadowBuilder, v, 0);
            return false;
        }
    };

    View.OnDragListener onDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View dropZone, DragEvent event) {
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Start listening
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //If we get in a zone covered by a drag event
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //If we get out of the zone covered by drag event
                    break;
                case DragEvent.ACTION_DROP:
                    zoneId=0;
                    draggedId=0;
                    //dropZone represents the view we are in
                    //draggedView represents the view we are dragging
                    View draggedView = (View) event.getLocalState();
                    for (int i=0; i<imgViews.size(); i++){
                        if (dropZone.getId() == imgViews.get(i).getId()){
                            zoneId=i;
                        }
                        if (draggedView.getId() == imgViews.get(i).getId()){
                            draggedId=i;
                        }
                    }
                    //If the zone doesn't have an img, we must not exchange
                    if ((draggedId == 5)&&(state[zoneId]==0)){
                        toastMaker("Permutation impossible : il n'y a pas d'image en position "+(zoneId+2));
                        break;
                    }

                    //We try to drag an empty image
                    if (state[draggedId]==0){
                        toastMaker("Permutation impossible : vous devez mettre une image sur l'emplacement "+(draggedId+2)+" d'abord");
                        break;
                    }

                    if (state[zoneId]==0){
                        //There won't be an image after the drag anymore, change the state
                        state[draggedId]=0;
                        //Change the icons
                        addDelete.get(zoneId).setImageDrawable(getResources().getDrawable(R.drawable.photo_delete));
                        addDelete.get(draggedId).setImageDrawable(getResources().getDrawable(R.drawable.photo_add));
                        //Delete the file
                        File f = new File(stockFile, imgsArray.get(draggedId));
                        f.delete();

                    }

                    state[zoneId]=1;

                    //We exchange the drawables
                    Drawable target = imgViews.get(zoneId).getDrawable();
                    imgViews.get(zoneId).setImageDrawable(imgViews.get(draggedId).getDrawable());
                    imgViews.get(draggedId).setImageDrawable(target);

                    //We save the change in the internal storage
                    new asyncChange().execute();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //Stop listening to event
                    return false;
                default:
                    break;
            }
            return true;
        }
    };


    private void imgCrop(ImageView v, String uri) {
        view = v;
        Uri imageUri = Uri.parse(uri);
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
// start cropping activity for pre-acquired image saved on the device
        CropImage.activity(imageUri)
                .start(this);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.small1:
                    index=0;
                    break;
                case R.id.small2:
                    index=1;
                    break;
                case R.id.small3:
                    index=2;
                    break;
                case R.id.small4:
                    index=3;
                    break;
                case R.id.small5:
                    index=4;
                    break;
                case R.id.big:
                    index=5;
                    break;

            }
            imgCrop(imgViews.get(index), stockFile+"/img"+(index+1)+".jpg");

        }
    };

    View.OnClickListener onClickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int indexAddDelete=0;
            switch (v.getId()){
                case R.id.add_delete1:
                    break;
                case R.id.add_delete2:
                    indexAddDelete=1;
                    break;
                case R.id.add_delete3:
                    indexAddDelete=2;
                    break;
                case R.id.add_delete4:
                    //Number Four on screen
                    indexAddDelete=3;
                    break;
                case R.id.add_delete5:
                    indexAddDelete=4;
                    break;
                case R.id.add_delete6:
                    indexAddDelete=5;
                    break;



            }
            if (indexAddDelete != 5){
                addDelete(indexAddDelete);
            }
        }
    };



    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }



    private void addDelete(int index){
            if (state[index] == 0) {
                //There is nothing in it
                imgCrop(imgViews.get(index), stockFile+"/"+imgsArray.get(index));

            } else {
                //There already is an image
                File f = new File(stockFile, imgsArray.get(index));
                //We delete the file containing the img
                if (f.exists()){
                    if (f.delete()){
                        Toast.makeText(this, "Delete completed "+imgsArray.get(index), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    state[index]=0;
                    toastMaker("Not existing file : "+stockFile+"/"+imgsArray.get(index));
                }
                finish();
                Intent intent = getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }


    }

    public void toastMaker(String string){
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("StaticFieldLeak")
    class asyncChange extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... arg0)
        {
            Drawable target = imgViews.get(zoneId).getDrawable();
            saveToInternalStorage(drawableToBitmap(target), imgsArray.get(zoneId), null);
            if (state[zoneId]!=0) {
                saveToInternalStorage(drawableToBitmap(imgViews.get(draggedId).getDrawable()), imgsArray.get(draggedId), null);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result)
        {
            Toast.makeText(getApplicationContext(), "Fini", Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);

        }
    }
//TODO register in online database all of the changes
    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);
    }




}
