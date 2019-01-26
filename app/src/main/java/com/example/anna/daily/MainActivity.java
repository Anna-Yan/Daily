package com.example.anna.daily;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.anna.daily.adapter.DealNameRecyclerViewAdapter;
import com.example.anna.daily.model.Deal;
import com.mikhaellopez.circularimageview.CircularImageView;
import static com.example.anna.daily.adapter.DealNameRecyclerViewAdapter.ViewHolder.taskAdapter;
import static com.example.anna.daily.adapter.DealNameRecyclerViewAdapter.deal_row_index;
import static com.example.anna.daily.adapter.TaskRecyclerViewAdapter.task_row_index;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private DBhelper dBhelper;
    private List<Deal> dealList;

    private Button addDealBttn;
    private Button saveDealBttn;
    private Button updateDealBttn;
    private Button addTaskBttn;

    private LinearLayout bottomWhiteLinLayout;
    private LinearLayout bottomBlueLinLayout;
    private ImageButton pathButton;
    private ImageButton photoButton;
    private ImageButton voiceButton;

    private ImageButton editButton;
    private ImageButton deleteButton;
    private EditText dealNameEditText;
    private CircularImageView imageView;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager verticallinLayoutManager;
    private RecyclerView.LayoutManager horizontalLinLayoutManager;
    public static DealNameRecyclerViewAdapter mAdapter;

    private Context mContext;
    private Toolbar toolbar;
    private EditText toolbarSearchET;
    private ImageButton toolbarSearchBttn;
    private ImageButton toolbarCloseBttn;
    private RelativeLayout toolbarBttnLayout;

    private static final int REQUEST_READ_STORAGE_PERMISSION = 100;
    private static final int REQUEST_WRITE_STORAGE_PERMISSION = 101;
    private static final int REQUEST_RECORD_PERMISSION = 102;
    private final int GALLERY_REQUEST_CODE = 103;
    private final int CAMERA_REQUEST_CODE = 104;
    private final int SPEECH_REQUEST_CODE = 105;
    private Uri selectedImage;

    private Bitmap decodedBitmap;
    public  static String base64Image = "";
    public static boolean isInserting;
    private Animation slideDownAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;

        toolbar =  findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        addDealBttn = findViewById(R.id.addDealButton);
        saveDealBttn = findViewById(R.id.saveDealButton);
        updateDealBttn = findViewById(R.id.updateDealButton);
        addTaskBttn = findViewById(R.id.addTaskButton);

        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        addDealBttn.setOnClickListener(this);
        saveDealBttn.setOnClickListener(this);

        bottomWhiteLinLayout = findViewById(R.id.bottomWhiteLinLayout);
        bottomBlueLinLayout= findViewById(R.id.bottomBlueLinLayout);
        pathButton = findViewById(R.id.pathButton);
        photoButton = findViewById(R.id.photoButton);
        voiceButton = findViewById(R.id.voiceButton);
        pathButton.setOnClickListener(this);
        photoButton.setOnClickListener(this);
        voiceButton.setOnClickListener(this);

        dealNameEditText = findViewById(R.id.dealNameEditText);
        imageView = findViewById(R.id.imageView);

        toolbarSearchET = findViewById(R.id.toolbarSearchEditText);
        toolbarSearchBttn = findViewById(R.id.toolbarSearchBttn);
        toolbarCloseBttn= findViewById(R.id.toolbarCloseBttn);
        toolbarBttnLayout= findViewById(R.id.toolbaBttnLayout);
        toolbarSearchBttn.setOnClickListener(this);
        toolbarCloseBttn.setOnClickListener(this);
        toolbarBttnLayout.setOnClickListener(this);

        toolbarSearchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(toolbarSearchET.getText().toString().matches("")){
                    updateData();
                }
                toolbarSearchBttn.setVisibility(View.VISIBLE);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });



        // Set up RecyclerView Layout
        mRecyclerView = findViewById(R.id.dealRecyclerView);
        verticallinLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false);
        horizontalLinLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(verticallinLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //set Animation to recyclerview item
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(3000);
        itemAnimator.setRemoveDuration(3000);
        mRecyclerView.setItemAnimator(itemAnimator);

        //Set up adapter
        dBhelper = new DBhelper(this);

        //For adding current Deal at the first of recView
        dealList = dBhelper.getAllDeals();
        Collections.reverse(dealList);

        mAdapter = new DealNameRecyclerViewAdapter(mContext,dealList);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onBackPressed() {

        //Bring everything to original look
       if(bottomBlueLinLayout.isShown() || bottomWhiteLinLayout.isShown()){
           hideKeyboard();

           dealNameEditText.setText("");
           addDealBttn.setVisibility(View.VISIBLE);
           saveDealBttn.setVisibility(View.VISIBLE);
           updateDealBttn.setVisibility(View.VISIBLE);
           addTaskBttn.setVisibility(View.VISIBLE);
           editButton.setVisibility(View.VISIBLE);
           deleteButton.setVisibility(View.VISIBLE);

           bottomWhiteLinLayout.setVisibility(View.GONE);
           hideBlueLayout();

           deal_row_index = -1;
           task_row_index = -1;
           mAdapter.notifyDataSetChanged();
           if(taskAdapter != null){
               taskAdapter.notifyDataSetChanged();
           }

       }else
           super.onBackPressed();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.addDealButton:

                addDealBttn.setVisibility(View.INVISIBLE);
                bottomWhiteLinLayout.setVisibility(View.VISIBLE);
                dealNameEditText.setHint("Deal name");
                showKeyboard();
                isInserting = true;

                break;

            case R.id.saveDealButton:

                addDealBttn.setVisibility(View.VISIBLE);
                bottomWhiteLinLayout.setVisibility(View.GONE);
                hideKeyboard();

                addDeal();

                break;

            case R.id.pathButton:

                requestImagePermission();
                break;

            case R.id.photoButton:

                requestMakePhotoPermission();
                break;

            case R.id.voiceButton:

                requestRecordPermission();
                break;

            case R.id.toolbaBttnLayout:
            case R.id.toolbarSearchBttn:
            case R.id.toolbarCloseBttn:

                if (toolbarSearchBttn.isShown()) {

                    String searchText = toolbarSearchET.getText().toString();

                    if(!searchText.matches("")){

                        toolbarSearchBttn.setVisibility(View.INVISIBLE);
                        dealList = dBhelper.searchByDealName(searchText);
                        mAdapter = new DealNameRecyclerViewAdapter(this, dealList);
                        mRecyclerView.setAdapter(mAdapter);
                    }
               }else {

                    toolbarSearchBttn.setVisibility(View.VISIBLE);
                    dealList = dBhelper.getAllDeals();
                    updateData();
                    toolbarSearchET.setText("");
                }
                dBhelper.closeDB();
                break;

        }
    }

    @Override
    protected void onDestroy() {

        Log.i("hreshtak", "onDestroy, row index="+deal_row_index);
        deal_row_index = -5;
        super.onDestroy();
    }

    public void hideKeyboard(){

        InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm2.hideSoftInputFromWindow(dealNameEditText.getWindowToken(), 0);
    }

    public void showKeyboard(){

        dealNameEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(dealNameEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void makeCameraPicture() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    private void makeSpeech() {

        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak please...");
        speechIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);

        try {
            startActivityForResult(speechIntent,SPEECH_REQUEST_CODE);
        }
        catch (ActivityNotFoundException e){
            Toast.makeText(mContext, "Sorry, your device doesn't support speech language", Toast.LENGTH_SHORT).show();
        }
    }

    private void addDeal(){

        Deal deal = new Deal();
        deal.setName(dealNameEditText.getText().toString());
        deal.setImagePath(base64Image);
        deal.setExpanded(0);

       if(dBhelper.insertDeal(deal)) {

           updateData();

           dealNameEditText.setText("");
           base64Image = "";
           dBhelper.closeDB();

       }else
           Toast.makeText(mContext, "Deal not added", Toast.LENGTH_SHORT).show();
    }


    public void updateData(){

        dealList = dBhelper.getAllDeals();
        Collections.reverse(dealList);

        mAdapter = new DealNameRecyclerViewAdapter(this, dealList);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void requestRecordPermission(){

        if(PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            //Permission not Granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_PERMISSION);
            }
        }
        else {
            //Permission Granted
           makeSpeech();
        }
    }

    private void requestImagePermission() {

        if(PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            //Permission not Granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READ_STORAGE_PERMISSION);
            }
        }
        else {
            //Permission Granted, lets go pick photo
            pickPhotofromGallery();

        }
    }


    private void hideBlueLayout(){

        slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

            bottomBlueLinLayout.startAnimation(slideDownAnimation);
            bottomBlueLinLayout.setVisibility(View.GONE);
    }
    private void pickPhotofromGallery(){

        Toast.makeText(mContext, "Choose picture", Toast.LENGTH_SHORT).show();
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    private void requestMakePhotoPermission() {

        if(PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            //Permission not Granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE_PERMISSION);
            }
        }
        else {
            //Permission Granted, lets go pick photo
            makeCameraPicture();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GALLERY_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {

                    selectedImage = data.getData();
                    //decode image and convert it to Base64 format
                    base64Image = imageToString(decodeImage(selectedImage));

                    //Saving Deal
                    if(isInserting){

                        addDealBttn.setVisibility(View.VISIBLE);
                        bottomWhiteLinLayout.setVisibility(View.GONE);
                        hideKeyboard();
                        addDeal();
                    }
                }

                break;

            case CAMERA_REQUEST_CODE:
                if(resultCode == RESULT_OK && data != null){

                    hideKeyboard();
                    Bitmap picture = (Bitmap) data.getExtras().get("data");

                    //decode image and convert it to Base64 format
                    base64Image = imageToString(picture);

                    if(isInserting) {

                        addDealBttn.setVisibility(View.VISIBLE);
                        bottomWhiteLinLayout.setVisibility(View.GONE);
                        hideKeyboard();
                        addDeal();
                    }
                }

                break;

            case SPEECH_REQUEST_CODE:

                if(resultCode == RESULT_OK && data != null) {

                    ArrayList<String> recognizedText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    dealNameEditText.setText(recognizedText.get(0));
                    dealNameEditText.setSelection(recognizedText.get(0).length());

                }
                else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                    dealNameEditText.setText("audio error");
                }
                else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                    dealNameEditText.setText("network error");
                }
                else if(resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                    dealNameEditText.setText("No Match");
                }

                    break;


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            int grantResult = grantResults[i];

            //Permission Granted
            switch (requestCode) {
                case REQUEST_RECORD_PERMISSION:

                    //if request is granted go to makeSpeech
                    if (permission.equals(Manifest.permission.RECORD_AUDIO) && grantResult == PackageManager.PERMISSION_GRANTED) {
                        requestRecordPermission();
                    }
                    break;

                //if request is granted go to pick photo
                case REQUEST_READ_STORAGE_PERMISSION:
                    if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResult == PackageManager.PERMISSION_GRANTED) {
                        requestImagePermission();
                    }
                    break;
                //if request is granted go to pick photo
                case REQUEST_WRITE_STORAGE_PERMISSION:
                    if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResult == PackageManager.PERMISSION_GRANTED) {
                        requestImagePermission();
                    }
                    break;
            }
        }


}



    //This is for encoding image to Base64 String
    private String imageToString(Bitmap bitmap) {

        decodedBitmap = bitmap;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        decodedBitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream);

        byte [] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte , Base64.DEFAULT);

    }

    //This is for decoding any user selected image to 100x100 size
    private Bitmap decodeImage(Uri uri) {

        // The new size we want to scale to
        int reqHeight = 300;
        int reqWidth = 300;

        // Decode image size
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        try {
            BitmapFactory.decodeStream(getContentResolver().openInputStream(uri),null, options);

            // Decode with inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;

            // Decode image with sample size
            decodedBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri),null, options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return decodedBitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        Log.i("bitmap","sample size = "+inSampleSize);
        return inSampleSize;
    }


}
