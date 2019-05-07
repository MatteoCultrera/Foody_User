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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setup extends AppCompatActivity {

    private CircleImageView profilePicture;
    private ImageButton save;
    private FloatingActionButton editImage;
    private EditText name, email, address, phoneNumber, bio;
    private TextView errorName;
    private TextView errorMail;
    private TextView errorPhone;
    private TextView errorAddress;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int REQUEST_CAPTURE_IMAGE = 100;
    private final String PROFILE_IMAGE = "ProfileImage.jpg";
    private final String PLACEHOLDER_CAMERA = "PlaceCamera.jpg";
    private String placeholderPath;
    private File storageDir;
    private AlertDialog dialogDism;
    private boolean unchanged, addressCheck, nameCheck, numberCheck, mailCheck;
    private String dialogCode = "ok";
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        firebaseAuth = FirebaseAuth.getInstance();
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
        outState.putString("dialog", dialogCode);
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

        String dialogPrec = savedInstanceState.getString("dialog");

        if (dialogPrec != null && dialogPrec.compareTo("ok") != 0) {
            if (dialogPrec.compareTo("pickImage") == 0) {
                showPickImageDialog();
            } else if (dialogPrec.compareTo("back") == 0) {
                onBackPressed();
            }
        }

        name.clearFocus();
        email.clearFocus();
        address.clearFocus();
        phoneNumber.clearFocus();
        bio.clearFocus();
    }

    protected void onPause(){
        super.onPause();
        if (dialogDism != null){
            dialogDism.dismiss();
        }
    }

    private void updateSave(){

        if(addressCheck && nameCheck && mailCheck && numberCheck){
            save.setImageResource(R.drawable.save_white);
            save.setEnabled(true);
            save.setClickable(true);
        }else{
            save.setImageResource(R.drawable.save_dis);
            save.setEnabled(false);
            save.setClickable(false);
        }

    }

    private void checkName(){
        String username = name.getText().toString();
        String regx = "^[\\p{L} .'-]+$";
        View errorLine = findViewById(R.id.name_error_line);
        Pattern regex = Pattern.compile(regx);
        Matcher matcher = regex.matcher(username);

        if(!matcher.matches()){
            nameCheck = false;
            errorName.setText(getResources().getString(R.string.error_name));
            errorLine.setBackgroundColor(getResources().getColor(R.color.errorColor,this.getTheme()));
            errorLine.setAlpha(1);

        }else{
            nameCheck = true;
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
            numberCheck = false;
            errorPhone.setText(getResources().getString(R.string.error_number));
            errorLine.setBackgroundColor(getResources().getColor(R.color.errorColor,this.getTheme()));
            errorLine.setAlpha(1);
        }else{
            numberCheck = true;
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
            mailCheck = false;
            errorLine.setBackgroundColor(getResources().getColor(R.color.errorColor, this.getTheme()));
            errorLine.setAlpha(1);
        }else{
            mailCheck = true;
            errorMail.setText("");
            errorLine.setAlpha(0.2f);
            errorLine.setBackgroundColor(Color.BLACK);
        }

        updateSave();
    }

    private void checkAddress(){
        View errorLine = findViewById(R.id.address_error_line);
        String regexpAddress = "^(?=\\s*\\S).*$";
        final String addressToCheck = address.getText().toString();

        if(!Pattern.compile(regexpAddress).matcher(addressToCheck).matches()) {
            errorAddress.setText(getResources().getString(R.string.error_address));
            addressCheck = false;
            errorLine.setBackgroundColor(getResources().getColor(R.color.errorColor, this.getTheme()));
            errorLine.setAlpha(1);
        }else{
            addressCheck = true;
            errorAddress.setText("");
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
        mailCheck = true;
        addressCheck = true;
        nameCheck = true;
        numberCheck = true;
        this.profilePicture = findViewById(R.id.profilePicture);
        this.editImage = findViewById(R.id.edit_profile_picture);
        this.name = findViewById(R.id.userName);
        this.email = findViewById(R.id.emailAddress);
        this.address = findViewById(R.id.address);
        this.phoneNumber = findViewById(R.id.phoneNumber);
        this.bio = findViewById(R.id.bio);

        //setup of the Shared Preferences to save value in (key, value) format
        //Shared Preferences definition
        Context context = getApplicationContext();
        sharedPref = context.getSharedPreferences("myPreference", MODE_PRIVATE);
        edit = sharedPref.edit();

        this.errorName = findViewById(R.id.name_error);
        this.errorMail = findViewById(R.id.email_error);
        this.errorPhone = findViewById(R.id.number_error);
        this.errorAddress = findViewById(R.id.address_error);
        TextView errorBio = findViewById(R.id.bio_error);
        //ImageButton back = findViewById(R.id.backButton);
        this.save = findViewById(R.id.saveButton);

        errorName.setText("");
        errorMail.setText("");
        errorPhone.setText("");
        errorAddress.setText("");
        errorBio.setText("");

        File f = new File(storageDir, PROFILE_IMAGE);

        if(f.exists())
            profilePicture.setImageURI(Uri.fromFile(f));

        name.setText(sharedPref.getString("name", ""));
        email.setText(sharedPref.getString("email", ""));
        address.setText(sharedPref.getString("address", ""));
        phoneNumber.setText(sharedPref.getString("phoneNumber", ""));
        bio.setText(sharedPref.getString("bio", ""));
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
                checkAddress();
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String check = sharedPref.getString("address", null);
                if (check != null && check.compareTo(editable.toString()) != 0){
                    unchanged = false;
                }

                for(int i = editable.length(); i > 0; i--) {

                    if(editable.subSequence(i-1, i).toString().equals("\n"))
                        editable.replace(i-1, i, "");
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

                for(int i = editable.length(); i > 0; i--) {

                    if(editable.subSequence(i-1, i).toString().equals("\n"))
                        editable.replace(i-1, i, "");
                }
            }
        });

        updateSave();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
                    }
                    break;

                case  UCrop.REQUEST_CROP:
                    Bitmap bitmap = getBitmapFromFile();

                    if(bitmap != null){
                        profilePicture.setImageBitmap(bitmap);
                        File placeholder = new File(storageDir, PLACEHOLDER_CAMERA);
                        saveBitmap(bitmap, placeholder.getPath());
                        unchanged = false;
                    }
                    break;
            }
        }
    }

    private File createOrReplacePlaceholder(){

        File f = new File(storageDir, PLACEHOLDER_CAMERA);

        if(f.exists())
            this.deleteFile(f.getName());

        f = new File(storageDir, PLACEHOLDER_CAMERA);

        placeholderPath = f.getPath();

       return f;
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
        updateSave();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogCode = "ok";
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
                    dialogCode = "ok";
                    break;
                case 1:
                    pickFromCamera();
                    dialogCode = "ok";
                    break;
                }
            }
        });
        dialogCode = "pickImage";
        dialogDism = builder.show();
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
        options.setHideBottomControls(true);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogCode = "ok";
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogCode = "ok";
                    Setup.super.onBackPressed();
                }
            });
            builder.setTitle(getResources().getString(R.string.alert_dialog_back_title));
            builder.setMessage(getResources().getString(R.string.alert_dialog_back_message));
            builder.setCancelable(false);
            dialogCode = "back";
            dialogDism = builder.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (unchanged){
            super.onBackPressed();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogCode = "ok";
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogCode = "ok";
                    Setup.super.onBackPressed();
                }
            });
            builder.setTitle(getResources().getString(R.string.alert_dialog_back_title));
            builder.setMessage(getResources().getString(R.string.alert_dialog_back_message));
            builder.setCancelable(false);
            dialogCode = "back";
            dialogDism = builder.show();
        }
    }

    public void savedProfile(View view) {

        File f = new File(storageDir, PLACEHOLDER_CAMERA);

        if(f.exists()){
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),bmOptions);
            File profile = new File(storageDir, PROFILE_IMAGE);
            saveBitmap(bitmap, profile.getPath());

            FirebaseStorage storage;
            StorageReference storageReference;
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            StorageReference ref = storageReference.child("images/users/" + firebaseAuth.getCurrentUser().getUid() + ".jpeg");
            ref.putFile(Uri.fromFile(new File(storageDir, PROFILE_IMAGE)))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("SWSW", "success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                .child("endUsers/" + user.getUid());
        HashMap<String, Object> child = new HashMap<>();
        UserInfo info = new UserInfo(name.getText().toString(), email.getText().toString(), address.getText().toString(),
                phoneNumber.getText().toString(), bio.getText().toString());
        child.put("info", info);
        database.updateChildren(child);

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
                    outputStream = new FileOutputStream(path);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
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