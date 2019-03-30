package com.example.foodyuser;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    private File storageDir;
    private boolean unchanged, checkString = true;

    //Shared Preferences definition
    Context context;
    SharedPreferences sharedPref;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        init();

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickImageDialog();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("name", name.getText().toString());
        outState.putString("email", email.getText().toString());
        outState.putString("address", address.getText().toString());
        outState.putString("phoneNumber", phoneNumber.getText().toString());
        outState.putString("bio", bio.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        File f = new File(storageDir, PLACEHOLDER_CAMERA);

        if(f.exists())
            profilePicture.setImageURI(Uri.fromFile(f));

        name.setText(savedInstanceState.getString("name", getResources().getString(R.string.name_hint)));
        email.setText(savedInstanceState.getString("email", getResources().getString(R.string.email_hint)));
        address.setText(savedInstanceState.getString("address", getResources().getString(R.string.address_hint)));
        phoneNumber.setText(savedInstanceState.getString("phoneNumber", getResources().getString(R.string.phone_hint)));
        bio.setText(savedInstanceState.getString("bio", getResources().getString(R.string.bio_hint)));

        name.clearFocus();
        email.clearFocus();
        address.clearFocus();
        phoneNumber.clearFocus();
        bio.clearFocus();
    }

    protected void onPause(){
        super.onPause();

    }

    private void updateSave(){

        if(!checkString){
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
            checkString = false;
            errorName.setText(getResources().getString(R.string.error_name));
            errorLine.setBackgroundColor(getResources().getColor(R.color.errorColor,this.getTheme()));
            errorLine.setAlpha(1);

        }else{
            checkString = true;
            errorName.setText("");
            errorLine.setAlpha(0.2f);
            errorLine.setBackgroundColor(Color.BLACK);
        }

        updateSave();
    }

    private void checkNumber(){
        String regexpPhone = "^(([+]|00)39)?(3[1-6][0-9])(\\d{7})$";
        final String userNumber = phoneNumber.getText().toString();

        View errorLine = findViewById(R.id.number_error_line);

        if(!Pattern.compile(regexpPhone).matcher(userNumber).matches()){
            checkString = false;
            errorPhone.setText(getResources().getString(R.string.error_number));
            errorLine.setBackgroundColor(getResources().getColor(R.color.errorColor,this.getTheme()));
            errorLine.setAlpha(1);
        }else{
            checkString = true;
            errorPhone.setText("");
            errorLine.setAlpha(0.2f);
            errorLine.setBackgroundColor(Color.BLACK);
        }

        updateSave();
    }

    private void checkMail(){
        View errorLine = findViewById(R.id.email_error_line);
        String regexpEmail = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        final String emailToCheck = email.getText().toString();

        if(!Pattern.compile(regexpEmail).matcher(emailToCheck).matches()) {
            errorMail.setText(getResources().getString(R.string.error_email));
            checkString = false;
            errorLine.setBackgroundColor(getResources().getColor(R.color.errorColor, this.getTheme()));
            errorLine.setAlpha(1);
        }else{
            checkString = true;
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

        File f = new File(storageDir, PROFILE_IMAGE);

        if(f.exists())
            profilePicture.setImageURI(Uri.fromFile(f));


        name.setText(sharedPref.getString("name", getResources().getString(R.string.name_Walter)));
        email.setText(sharedPref.getString("email", getResources().getString(R.string.mail_Walter)));
        address.setText(sharedPref.getString("address", getResources().getString(R.string.address_Walter)));
        phoneNumber.setText(sharedPref.getString("phoneNumber", getResources().getString(R.string.phone_Walter)));
        bio.setText(sharedPref.getString("bio", getResources().getString(R.string.bio_Walter)));
        edit.apply();


        //onTextChange to notify the user that there are fields that are not saved
        this.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkName();
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
                checkMail();
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
                checkNumber();
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
                        File placeholder = new File(storageDir, PLACEHOLDER_CAMERA);
                        saveBitmap(bitmap, placeholder.getPath());
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
        File dest = new File(storageDir, PLACEHOLDER_CAMERA);
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
            @NonNull
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ImageView iv = v.findViewById(R.id.iv1);
                iv.setImageDrawable(getDrawable(items[position].getIcon()));
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
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(storageDir, PLACEHOLDER_CAMERA)));
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

    protected void backToProfile(View view) {
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

    protected void savedProfile(View view) {

        File f = new File(storageDir, PLACEHOLDER_CAMERA);

        if(f.exists()){
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),bmOptions);
            File profile = new File(storageDir, PROFILE_IMAGE);
            saveBitmap(bitmap, profile.getPath());

        }

        edit.putString("name", name.getText().toString());
        edit.putString("email", email.getText().toString());
        edit.putString("address", address.getText().toString());
        edit.putString("phoneNumber", phoneNumber.getText().toString());
        edit.putString("bio", bio.getText().toString());
        edit.apply();
        finish();
    }

    private void saveBitmap(Bitmap bitmap,String path){
        if(bitmap!=null){
            try {
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(path); //here is set your file path where you want to save or also here you can set file object directly

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}