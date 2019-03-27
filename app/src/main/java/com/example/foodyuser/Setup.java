package com.example.foodyuser;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setup extends AppCompatActivity {

    private CircleImageView profilePicture;
    private ImageButton save, back;
    private FloatingActionButton editImage;
    private EditText name, email, address, phoneNumber, bio;
    private TextView errorName, errorMail, errorAddress, errorPhone, errorBio;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int REQUEST_CAPTURE_IMAGE = 100;
    private final String PROFILE_IMAGE = "ProfileImage.jpg";
    private final String PLACEHOLDER_CAMERA="PlaceCamera.jpg";
    private String placeholderPath;
    private boolean unchanged = true;


    //Shared Preferences definition
    Context context;
    SharedPreferences sharedPref;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        init();

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickImageDialog();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (unchanged){
            super.onBackPressed();
        }
        else {
            Log.d("ALERT", "false");
            AlertDialog.Builder builder = new AlertDialog.Builder(Setup.this);
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Setup.super.onBackPressed();
                }
            });
            builder.setTitle(getResources().getString(R.string.alert_dialog_back_title));
            builder.setMessage(getResources().getString(R.string.alert_dialog_back_message));
            builder.setCancelable(false);
            builder.show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("name", name.getText().toString());
        outState.putString("email", email.getText().toString());
        outState.putString("address", address.getText().toString());
        outState.putString("phone_num", phoneNumber.getText().toString());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        name.setText(savedInstanceState.getString("name", getResources().getString(R.string.name_hint)));
        email.setText(savedInstanceState.getString("email", getResources().getString(R.string.email_hint)));
        address.setText(savedInstanceState.getString("address", getResources().getString(R.string.address_hint)));
        phoneNumber.setText(savedInstanceState.getString("phone_num", getResources().getString(R.string.phone_hint)));

        name.clearFocus();
        email.clearFocus();
        address.clearFocus();
        phoneNumber.clearFocus();
    }

    protected void onPause(){
        super.onPause();

    }

    private void updateSave(){
        String username = name.getText().toString();
        String regx = "^[\\p{L} .'-]+$";
        Pattern regex = Pattern.compile(regx);
        Matcher nameMatcher = regex.matcher(username);

        String userNumber = phoneNumber.getText().toString();

        View errorLine = findViewById(R.id.email_error_line);

        if(!nameMatcher.matches() || !PhoneNumberUtils.isGlobalPhoneNumber(userNumber)
                || userNumber.length() == 0 || !Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()){
            save.setImageResource(R.drawable.save_dis);
            save.setEnabled(false);
            save.setClickable(false);
        }else{
            save.setImageResource(R.drawable.save_white);
            save.setEnabled(true);
            save.setClickable(true);
        }


    }

    private void checkName(){
        String username = name.getText().toString();
        String regx = "^[\\p{L} .'-]+$";
        View errorLine = findViewById(R.id.name_error_line);
        Pattern regex = Pattern.compile(regx);
        Matcher matcher = regex.matcher(username);

        if(!matcher.matches()){
            errorName.setText(getResources().getString(R.string.error_name));
            errorLine.setBackgroundColor(getResources().getColor(R.color.errorColor,this.getTheme()));
            errorLine.setAlpha(1);
        }else{
            errorName.setText("");
            errorLine.setAlpha(0.2f);
            errorLine.setBackgroundColor(Color.BLACK);
        }

        updateSave();

    }

    private void checkNumber(){
        String userNumber = phoneNumber.getText().toString();
        View errorLine = findViewById(R.id.number_error_line);

        if(!PhoneNumberUtils.isGlobalPhoneNumber(userNumber) || userNumber.length() == 0){
            errorPhone.setText(getResources().getString(R.string.error_number));
            errorLine.setBackgroundColor(getResources().getColor(R.color.errorColor,this.getTheme()));
            errorLine.setAlpha(1);
        }else{
            errorPhone.setText("");
            errorLine.setAlpha(0.2f);
            errorLine.setBackgroundColor(Color.BLACK);
        }

        updateSave();

    }

    private void checkMail(){
        View errorLine = findViewById(R.id.email_error_line);

        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()){
            errorMail.setText(getResources().getString(R.string.error_email));
            errorLine.setBackgroundColor(getResources().getColor(R.color.errorColor,this.getTheme()));
            errorLine.setAlpha(1);
        }else{
            errorMail.setText("");
            errorLine.setAlpha(0.2f);
            errorLine.setBackgroundColor(Color.BLACK);
        }
        updateSave();
    }

    private  void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,GALLERY_REQUEST_CODE);

    }

    private void pickFromCamera(){
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager())!= null){

            File photoFile = createOrReplacePlaceholder();

            if(photoFile!=null){
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.foodyuser",
                        photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.d("PICTURE", "Launching Camera");
                startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
            }

        }

    }

    private void init(){
        unchanged = true;
        this.profilePicture = findViewById(R.id.profilePicture);
        this.editImage = findViewById(R.id.edit_profile_picture);
        this.name = findViewById(R.id.userName);
        this.email = findViewById(R.id.emailAddress);
        this.address = findViewById(R.id.address);
        this.phoneNumber = findViewById(R.id.phoneNumber);
        this.bio = findViewById(R.id.bio);

        //setup of the Shared Preferences to save value in (key, value) format
        context = getApplicationContext();
        sharedPref = context.getSharedPreferences("myPreference", MODE_PRIVATE);
        edit = sharedPref.edit();
        edit.apply();

        //onTextChange to notify the user that there are fields that are not saved
        this.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String check = sharedPref.getString("name", null);
                if (check != null && check.compareTo(editable.toString()) != 0){
                    unchanged = false;
                }
            }
        });
        this.email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String check = sharedPref.getString("email", null);
                if (check != null && check.compareTo(editable.toString()) != 0){
                    unchanged = false;
                }
            }
        });
        this.address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String check = sharedPref.getString("address", null);
                if (check != null && check.compareTo(editable.toString()) != 0){
                    unchanged = false;
                }
            }
        });
        this.phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String check = sharedPref.getString("phoneNumber", null);
                if (check != null && check.compareTo(editable.toString()) != 0){
                    unchanged = false;
                }
            }
        });
        this.bio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String check = sharedPref.getString("bio", null);
                if (check != null && check.compareTo(editable.toString()) != 0){
                    unchanged = false;
                }
            }
        });

        this.errorName = findViewById(R.id.name_error);
        this.errorMail = findViewById(R.id.email_error);
        this.errorPhone = findViewById(R.id.number_error);
        this.errorAddress = findViewById(R.id.address_error);
        this.errorBio = findViewById(R.id.bio_error);
        this.back = findViewById(R.id.backButton);
        this.save = findViewById(R.id.saveButton);

        errorName.setText("");
        errorMail.setText("");
        errorPhone.setText("");
        errorAddress.setText("");
        errorBio.setText("");

        Bitmap b = BitmapFactory.decodeFile(this.getFilesDir() + "/" + PROFILE_IMAGE);
        profilePicture.setImageBitmap(b);
        name.setText(sharedPref.getString("name", getResources().getString(R.string.name_hint)));
        email.setText(sharedPref.getString("email", getResources().getString(R.string.email_hint)));
        address.setText(sharedPref.getString("address", getResources().getString(R.string.address_hint)));
        phoneNumber.setText(sharedPref.getString("phoneNumber", getResources().getString(R.string.phone_hint)));
        bio.setText(sharedPref.getString("bio", getResources().getString(R.string.bio_hint)));
        edit.apply();

        Bitmap image = getBitmapFromFile();

        if(image != null)
            profilePicture.setImageBitmap(image);
            name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!name.hasFocus()){
                    checkName();
                }
            }
        });
        phoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!phoneNumber.hasFocus()){
                    checkNumber();
                }
            }
        });
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!phoneNumber.hasFocus()){
                    checkMail();
                }
            }
        });
        updateSave();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("PICTURE", "End Picture");

        if(resultCode == RESULT_OK){
            switch (requestCode){

                case REQUEST_CAPTURE_IMAGE:
                    File f = new File(placeholderPath);
                    Log.d("PICTURE", "Entered Request Capture");
                    startCrop(Uri.fromFile(f));
                    break;

                case GALLERY_REQUEST_CODE:
                    if(data !=null){
                        Uri imageUri = data.getData();

                        if(imageUri != null)
                            startCrop(imageUri);

                      // profilePicture.setImageURI(imageUri);

                    }
                    break;

                case  UCrop.REQUEST_CROP:
                    Bitmap bitmap = getBitmapFromFile();
                    if(bitmap != null){
                        profilePicture.setImageBitmap(bitmap);
                    }
                    break;
            }
        }
    }

    private void setBitmapPlaceholder(Bitmap bitmap){

        File f = new File(this.getFilesDir(), PLACEHOLDER_CAMERA);

        if(f.exists())
            this.deleteFile(f.getName());

        f = new File(this.getFilesDir(), PLACEHOLDER_CAMERA);

//Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file

        try{

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private File createOrReplacePlaceholder(){

        Log.d("PICTURE", "Create or Replace");
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File f = new File(storageDir, PLACEHOLDER_CAMERA);

        if(f.exists())
            this.deleteFile(f.getName());

        f = new File(storageDir, PLACEHOLDER_CAMERA);

        placeholderPath = f.getPath();

       return f;
    }

    private void setBitmapProfile(Bitmap bitmap){

        File f = new File(this.getFilesDir(), PROFILE_IMAGE);

        if(f.exists())
            this.deleteFile(f.getName());

        f = new File(this.getFilesDir(), PROFILE_IMAGE);

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file

        try{

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromFile(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        File dest = new File(this.getFilesDir(), PROFILE_IMAGE);
        if(!dest.exists())
            return null;
        return  BitmapFactory.decodeFile(dest.getPath(), options);

    }

    private void showPickImageDialog(){
        final Item[] items = {
                new Item(getString(R.string.alert_dialog_image_gallery), R.drawable.collections_black),
                new Item(getString(R.string.alert_dialog_image_camera), R.drawable.camera_black)
        };
        ListAdapter arrayAdapter = new ArrayAdapter<Item>(
                this,
                R.layout.alert_dialog_item,
                R.id.tv1,
                items){
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ImageView iv = v.findViewById(R.id.iv1);
                iv.setImageDrawable(getDrawable(items[position].icon));
                return v;
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(Setup.this);
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setTitle(getResources().getString(R.string.alert_dialog_image_title));
        builder.setCancelable(false);
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                case 0:
                    pickFromGallery();
                    break;
                case 1:
                    pickFromCamera();
                    break;
                }
            }
        });
        builder.show();
    }

    private void startCrop(@NonNull Uri uri){
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(this.getFilesDir(), PROFILE_IMAGE)));
        uCrop.withAspectRatio(1,1);
        uCrop.withMaxResultSize(450,450);
        uCrop.withOptions(getCropOptions());
        uCrop.start(Setup.this);
    }

    private UCrop.Options getCropOptions(){
        UCrop.Options options= new UCrop.Options();

        options.setCompressionQuality(100);

        //Compress Type
        //options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        //options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        //UI
        options.setHideBottomControls(true);

        //Colors
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark, getTheme()));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary, getTheme()));
        options.setAllowedGestures(UCropActivity.ALL, UCropActivity.ALL, UCropActivity.ALL);
        options.setCircleDimmedLayer(true);
        options.setToolbarTitle(getResources().getString(R.string.crop_image));
        return options;
    }

    public void backToProfile(View view) {
        if (unchanged){
            super.onBackPressed();
        }
        else {
            Log.d("ALERT", "false");
            AlertDialog.Builder builder = new AlertDialog.Builder(Setup.this);
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Setup.super.onBackPressed();
                }
            });
            builder.setTitle(getResources().getString(R.string.alert_dialog_back_title));
            builder.setMessage(getResources().getString(R.string.alert_dialog_back_message));
            builder.setCancelable(false);
            builder.show();
        }
    }

    public void savedProfile(View view) {
        super.onBackPressed();
        edit.putString("name", name.getText().toString());
        edit.putString("email", email.getText().toString());
        edit.putString("address", address.getText().toString());
        edit.putString("phoneNumber", phoneNumber.getText().toString());
        edit.apply();
        finish();
    }
}
