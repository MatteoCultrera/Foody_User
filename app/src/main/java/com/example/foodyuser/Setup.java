package com.example.foodyuser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Debug;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.file.Files;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setup extends AppCompatActivity {

    private CircleImageView profilePicture;
    private FloatingActionButton editImage;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int REQUEST_CAPTURE_IMAGE = 100;
    private final String SAMPLE_CROPPED_IMG_NAME = "ProfileImage";
    private File pictureDirectory;

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

    private  void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,GALLERY_REQUEST_CODE);

    }

    private void pickFromCamera(){
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager())!= null)
            startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);

    }

    private void init(){

        this.profilePicture = findViewById(R.id.profilePicture);
        this.editImage = findViewById(R.id.edit_profile_picture);


        String destinationFileName = SAMPLE_CROPPED_IMG_NAME;
        destinationFileName += ".jpg";

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode){

                case REQUEST_CAPTURE_IMAGE:
                    if(data!= null){
                        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                        setBitmapToFile(SAMPLE_CROPPED_IMG_NAME, imageBitmap);
                        String destinationFileName = SAMPLE_CROPPED_IMG_NAME;
                        destinationFileName += ".jpg";
                        startCrop(Uri.fromFile(new File(getFilesDir(), destinationFileName)));
                    }
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
                    Bitmap bitmap = getBitmapFromFile(SAMPLE_CROPPED_IMG_NAME);
                    if(bitmap != null){
                        profilePicture.setImageBitmap(bitmap);
                    }
                    break;
            }
        }
    }

    private void setBitmapToFile(String fileName, Bitmap bitmap){
        //create a file to write bitmap data
        String destinationFileName = fileName;
        destinationFileName += ".jpg";

        File f = new File(this.getFilesDir(), destinationFileName);

        if(f.exists())
            this.deleteFile(f.getName());

        f = new File(this.getFilesDir(), destinationFileName);

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

        }
    }

    private Bitmap getBitmapFromFile(String fileName){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        String destinationFileName = fileName;
        destinationFileName += ".jpg";
        File dest = new File(this.getFilesDir(), destinationFileName);
        Bitmap bitmap = BitmapFactory.decodeFile(dest.getPath(), options);
        return  bitmap;

    }

    private void showPickImageDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Setup.this);

        builder.setTitle("Select one Option");

        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(Setup.this, android.R.layout.select_dialog_item);

        arrayAdapter.add("Gallery");
        arrayAdapter.add("Camera");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

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

        String destinationFileName = SAMPLE_CROPPED_IMG_NAME;
        destinationFileName += ".jpg";


        File dest = new File(this.getFilesDir(), destinationFileName);

        if(dest.exists())
            this.deleteFile(dest.getName());


        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(this.getFilesDir(), destinationFileName)));


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
        options.setHideBottomControls(true);

        //Colors
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark, getTheme()));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary, getTheme()));


        options.setAllowedGestures(UCropActivity.ALL, UCropActivity.ALL, UCropActivity.ALL);

        options.setCircleDimmedLayer(true);

        options.setToolbarTitle(getResources().getString(R.string.crop_image));

        return options;


    }

}
