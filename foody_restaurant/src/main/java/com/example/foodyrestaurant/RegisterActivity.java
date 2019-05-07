package com.example.foodyrestaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText username, password1, password2, email;
    private TextInputLayout usernameL, password1L, password2L, emailL;
    private boolean checkUser, checkPass, checkEqual, checkEmail;
    private FirebaseAuth firebaseAuth;
    private ConstraintLayout login;
    private FloatingActionButton register;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor edit;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        sharedPref = this.getSharedPreferences("myPreference", MODE_PRIVATE);
        edit = sharedPref.edit();
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser = false;
        checkEmail = false;
        checkEqual = false;
        checkPass = false;
        username = findViewById(R.id.username_register);
        usernameL = findViewById(R.id.username_register_outer);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkName();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        password1 = findViewById(R.id.password_register);
        password1L = findViewById(R.id.password_register_outer);
        password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkPassword();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        password2 = findViewById(R.id.password_repeat_register);
        password2L = findViewById(R.id.password_repeat_register_outer);
        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkPasswordEqual();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        email = findViewById(R.id.email_register);
        emailL = findViewById(R.id.email_register_outer);
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
        login = findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, Login.class);
                startActivity(intent);
            }
        });
        register = findViewById(R.id.FAB_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().equals("")){
                    usernameL.setError(getResources().getString(R.string.empty_password));
                    if (email.getText().toString().equals("")) {
                        emailL.setError(getResources().getString(R.string.empty_email));
                    }
                    if (password1.getText().toString().equals("")){
                        password1L.setError(getResources().getString(R.string.empty_password));
                    }
                    if (password2.getText().toString().equals("")){
                        password2L.setError(getResources().getString(R.string.empty_password));
                    }
                    return;
                }
                if (email.getText().toString().equals("")) {
                    emailL.setError(getResources().getString(R.string.empty_email));
                    if (password1.getText().toString().equals("")){
                        password1L.setError(getResources().getString(R.string.empty_password));
                    }
                    if (password2.getText().toString().equals("")){
                        password2L.setError(getResources().getString(R.string.empty_password));
                    }
                    return;
                }
                if (password1.getText().toString().equals("")){
                    password1L.setError(getResources().getString(R.string.empty_password));
                    if (password2.getText().toString().equals("")){
                        password2L.setError(getResources().getString(R.string.empty_password));
                    }
                    return;
                }
                if (password2.getText().toString().equals("")){
                    password2L.setError(getResources().getString(R.string.empty_password));
                    return;
                }
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password1.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<String> days = new ArrayList<>();
                                    days.add(getResources().getString(R.string.Closed));
                                    days.add(getResources().getString(R.string.Closed));
                                    days.add(getResources().getString(R.string.Closed));
                                    days.add(getResources().getString(R.string.Closed));
                                    days.add(getResources().getString(R.string.Closed));
                                    days.add(getResources().getString(R.string.Closed));
                                    days.add(getResources().getString(R.string.Closed));
                                    RestaurantInfo info = new RestaurantInfo(username.getText().toString(), email.getText().toString(), days,  5);
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                                            .child("restaurantsInfo/" + user.getUid());
                                    HashMap<String, Object> child = new HashMap<>();
                                    child.put("info", info);
                                    database.updateChildren(child);
                                    edit.apply();
                                    Toast.makeText(getApplicationContext(), R.string.auth_success, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.auth_failure, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void checkPasswordEqual() {
        if(password1.getText().toString().equals(password2.getText().toString())){
            checkEqual = true;
            password2L.setError(null);
        }
        else{
            checkEqual = false;
            password2L.setError(getResources().getString(R.string.error_equals));
        }
        updateSave();
    }

    private void checkName(){
        String name = username.getText().toString();
        String regx = "^[\\p{L} .'-]+$";

        if(!Pattern.compile(regx).matcher(name).matches()) {
            checkUser = false;
            usernameL.setError(getResources().getString(R.string.error_name));
        }else{
            checkUser = true;
            usernameL.setError(null);
        }
        updateSave();
    }

    private void checkPassword(){
        String regexPassword = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
        final String passwordToCheck = password1.getText().toString();

        if(!Pattern.compile(regexPassword).matcher(passwordToCheck).matches()) {
            password1L.setError(getResources().getString(R.string.error_password));
            checkPass = false;
        }else{
            checkPass = true;
            password1L.setError(null);
        }
        updateSave();
    }

    private void checkMail(){
        String regexpEmail = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        final String emailToCheck = email.getText().toString();

        if(!Pattern.compile(regexpEmail).matcher(emailToCheck).matches()) {
            emailL.setError(getResources().getString(R.string.error_email));
            checkEmail = false;
        }else{
            checkEmail = true;
            emailL.setError(null);
        }
        updateSave();
    }

    private void updateSave(){
        if(checkEmail && checkPass && checkUser && checkEqual){
            register.setClickable(true);
        }else{
            register.setClickable(false);
        }
    }
}
