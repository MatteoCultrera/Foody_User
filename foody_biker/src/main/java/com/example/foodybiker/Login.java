package com.example.foodybiker;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private File storage;
    private final String PROFILE = "profileImage";
    private TextInputEditText email, password;
    private TextInputLayout emailL, passwordL;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton login;
    private boolean correctness;
    private Dialog dialog;
    private SharedPreferences prefs;
    private final String MAIN_DIR = "user_utils";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        prefs = this.getSharedPreferences("myPreference", MODE_PRIVATE);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            File root = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            final File directory = new File(root.getPath()+File.separator+MAIN_DIR);
            final File image = new File(directory, firebaseAuth.getCurrentUser().getUid()+".jpg");
            if(!image.exists()) {
                loginAppear();
                fetchData();
            }else {
                Intent intent = new Intent(Login.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        }
        else {
            setContentView(R.layout.login_layout);
            correctness = false;
            email = findViewById(R.id.username_login);
            emailL = findViewById(R.id.username_login_outer);
            email.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    checkMail();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            password = findViewById(R.id.password_login);
            passwordL = findViewById(R.id.password_login_outer);
            login = findViewById(R.id.FAB_login);
            ConstraintLayout register = findViewById(R.id.register_button);
            password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(password.getText().length() > 0)
                        passwordL.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                if (email.getText().toString().equals("")) {
                    emailL.setError(getResources().getString(R.string.empty_email));
                    if (password.getText().toString().equals("")){
                        passwordL.setError(getResources().getString(R.string.empty_password));
                    }
                    return;
                }
                if (password.getText().toString().equals("")){
                    passwordL.setError(getResources().getString(R.string.empty_password));
                    return;
                }
                loginAppear();
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Saving the image of the profile if there would be one
                            final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Bikers");
                            Query query = database.child(firebaseAuth.getCurrentUser().getUid());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                    String imagePath = dataSnapshot.child("info").child("path").getValue(String.class);
                                    if(imagePath != null){
                                        File root = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                        storage = new File(root.getPath()+File.separator+PROFILE);
                                        if(storage.exists()){
                                            for(File f : storage.listFiles())
                                                f.delete();
                                        }else{
                                            storage.mkdirs();
                                        }
                                        File imageProfile = new File(storage.getPath()+
                                                File.separator+firebaseAuth.getCurrentUser().getUid()+System.currentTimeMillis()+".jpg");
                                        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                                        mStorageRef.child(imagePath).getFile(imageProfile)
                                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                storage.delete();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.login_failure, Toast.LENGTH_SHORT).show();
                            loginDisappear();
                        }
                        }
                    });
                }
            });
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Login.this, RegisterActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void loginAppear(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void loginDisappear(){
        dialog.dismiss();
    }

    private void fetchData(){

        File root = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        final File directory = new File(root.getPath()+File.separator+MAIN_DIR);
        if(directory.exists()){
            for(File f : directory.listFiles())
                f.delete();
            directory.delete();
        }
        directory.mkdirs();

        prefs.edit().putString("id", firebaseAuth.getCurrentUser().getUid()).apply();

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Bikers");
        Query query = database.child(firebaseAuth.getCurrentUser().getUid()).child("info");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final BikerInfo info = dataSnapshot.getValue(BikerInfo.class);
                if(info.getUsername() != null){
                    if(info.getUsername().length() > 0){
                        prefs.edit().putString("name", info.getUsername()).apply();
                    }else{
                        prefs.edit().remove("name").apply();
                    }
                }else {
                    prefs.edit().remove("name").apply();
                }

                if(info.getEmail()!=null){
                    if(info.getEmail().length() > 0){
                        prefs.edit().putString("email", info.getEmail()).apply();
                    }else{
                        prefs.edit().remove("email").apply();
                    }
                }else {
                    prefs.edit().remove("email").apply();
                }

                if(info.getAddress()!=null){
                    if(info.getAddress().length() > 0){
                        prefs.edit().putString("address", info.getAddress()).apply();
                    }else{
                        prefs.edit().remove("address").apply();
                    }
                }else {
                    prefs.edit().remove("address").apply();
                }

                if(info.getNumberPhone()!=null){
                    if(info.getNumberPhone().length()>0){
                        prefs.edit().putString("phoneNumber", info.getNumberPhone()).apply();
                    }else {
                        prefs.edit().remove("phoneNumber").apply();
                    }
                }else {
                    prefs.edit().remove("phoneNumber").apply();
                }

                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

                if(info.getPath()!=null){
                    final File image = new File(directory, firebaseAuth.getCurrentUser().getUid()+".jpg");
                    mStorageRef.child(info.getPath()).getFile(image).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            prefs.edit().putString("imgLocale",image.getPath()).apply();
                            prefs.edit().putString("imgRemote",info.getPath()).apply();
                            Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                }else{
                    prefs.edit().remove("imgLocale").apply();
                    prefs.edit().remove("imgRemote").apply();
                    Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), R.string.login_failure, Toast.LENGTH_SHORT).show();
                prefs.edit().putBoolean("allFilesFetched",false).apply();
                loginDisappear();
            }
        });

    }

    private void checkMail(){
        String regexpEmail = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        final String emailToCheck = email.getText().toString();

        if(!Pattern.compile(regexpEmail).matcher(emailToCheck).matches()) {
            emailL.setError(getResources().getString(R.string.error_email));
            correctness = false;
        }else{
            correctness = true;
            emailL.setError(null);
        }
        updateSave();
    }

    private void updateSave(){
        if(!correctness){
            login.setClickable(false);
        }else{
            login.setClickable(true);
        }
    }
}
