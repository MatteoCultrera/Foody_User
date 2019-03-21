package com.example.foodyuser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.yalantis.ucrop.UCrop;
import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;


public class Setup extends AppCompatActivity {
    //object definition
    private EditText name;
    private EditText emailAddress;
    private EditText address;
    private EditText phoneNumber;

    //Shared Preferences definition
    Context context;
    SharedPreferences sharedPref;
    SharedPreferences.Editor edit;

    private CircleImageView profilePicture;
    private FloatingActionButton editImage;
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "ProfileImage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Log.d("MAD", "SETUP::onCreate(Bundle savedInstanceState)");
        init();

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent()
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), CODE_IMG_GALLERY);
            }
        });
    }

    private void init(){
        this.profilePicture = findViewById(R.id.profilePicture);
        this.editImage = findViewById(R.id.edit_profile_picture);

        //inizialize object of the view
        this.name = findViewById(R.id.userName);
        this.emailAddress = findViewById(R.id.emailAddress);
        this.address = findViewById(R.id.address);
        this.phoneNumber = findViewById(R.id.phoneNumber);

        //inizialization of shared preferences
        context = getApplicationContext();
        sharedPref = context.getSharedPreferences("myPreference", MODE_PRIVATE);
        edit = sharedPref.edit();
        edit.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CODE_IMG_GALLERY && resultCode == RESULT_OK){

            //From Gallery
            Uri imageUri = data.getData();
            if(imageUri!=null){
                startCrop(imageUri);
            }

        }else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){

            Uri imageUriResultCrop = UCrop.getOutput(data);

            if(imageUriResultCrop != null){
                profilePicture.setImageURI(imageUriResultCrop);
            }
        }
    }

    private void startCrop(@NonNull Uri uri){
        String destinationFileName = SAMPLE_CROPPED_IMG_NAME;
        destinationFileName += ".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(),destinationFileName)));

        uCrop.withAspectRatio(1,1);

        uCrop.withMaxResultSize(450,450);

        uCrop.withOptions(getCropOptions());

        uCrop.start(Setup.this);
    }

    private UCrop.Options getCropOptions(){
        UCrop.Options options= new UCrop.Options();

        options.setCompressionQuality(70);

        //Compress Type
        //options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        //options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        //UI
        options.setHideBottomControls(false);

        //Colors
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));


        options.setToolbarTitle(getResources().getString(R.string.crop_image));

        return options;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d("MAD", "onSaveInstanceState(Bundle outState)");

        //save all the string for screen orientation change
        outState.putString("name", name.getText().toString());
        outState.putString("emailAddress", emailAddress.getText().toString());
        outState.putString("address", address.getText().toString());
        outState.putString("phoneNumber", phoneNumber.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d("MAD", "onRestoreInstanceState(Bundle savedInstanceState)");

        //restore all the string after screen orientation change
        name.setText(savedInstanceState.getString("name"));
        emailAddress.setText(savedInstanceState.getString("emailAddress"));
        address.setText(savedInstanceState.getString("address"));
        phoneNumber.setText(savedInstanceState.getString("phoneNumber"));
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("MAD", "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();

        //retrieve all the shared Preferences
        //this is only useful when back button is pressed
        // as the destroy() method is invoked and we lose everything
        name.setText(sharedPref.getString("Name", ""));
        emailAddress.setText(sharedPref.getString("Email", ""));
        address.setText(sharedPref.getString("Address", ""));
        phoneNumber.setText(sharedPref.getString("Phone", ""));

        Log.d("MAD", "onResume() "+getIntent().getExtras());

        getIntent().getExtras();
        Log.d("MAD", "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();

        //save all the shared Preferences
        edit.putString("Name", name.getText().toString());
        edit.putString("Email", emailAddress.getText().toString());
        edit.putString("Address", address.getText().toString());
        edit.putString("Phone", phoneNumber.getText().toString());
        edit.apply();

        //this method is call basically every times the app goes in background
        Log.d("MAD", "onPause()");


    }

    @Override
    protected void onStop() {
        super.onStop();

        getIntent().putExtra("name", name.getText().toString());
        getIntent().putExtra("emailAddress", emailAddress.getText().toString());
        getIntent().putExtra("address", address.getText().toString());
        getIntent().putExtra("phoneNumber", this.phoneNumber.getText().toString());

        Log.d("MAD", "onSTOP() "+getIntent().getExtras());
        Log.d("MAD", "onStop()");
    }
}
