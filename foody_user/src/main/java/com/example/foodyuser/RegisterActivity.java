package com.example.foodyuser;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, password1, password2, email;
    private boolean checkUser, checkPass, checkEqual, checkEmail;
    private FirebaseAuth firebaseAuth;
    private ConstraintLayout login;
    private FloatingActionButton register;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser = false;
        checkEmail = false;
        checkEqual = false;
        checkPass = false;
        username = findViewById(R.id.username_register);
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
                    username.setError(getResources().getString(R.string.empty_password));
                    if (email.getText().toString().equals("")) {
                        email.setError(getResources().getString(R.string.empty_email));
                    }
                    if (password1.getText().toString().equals("")){
                        password1.setError(getResources().getString(R.string.empty_password));
                    }
                    if (password2.getText().toString().equals("")){
                        password2.setError(getResources().getString(R.string.empty_password));
                    }
                    return;
                }
                if (email.getText().toString().equals("")) {
                    email.setError(getResources().getString(R.string.empty_email));
                    if (password1.getText().toString().equals("")){
                        password1.setError(getResources().getString(R.string.empty_password));
                    }
                    if (password2.getText().toString().equals("")){
                        password2.setError(getResources().getString(R.string.empty_password));
                    }
                    return;
                }
                if (password1.getText().toString().equals("")){
                    password1.setError(getResources().getString(R.string.empty_password));
                    if (password2.getText().toString().equals("")){
                        password2.setError(getResources().getString(R.string.empty_password));
                    }
                    return;
                }
                if (password2.getText().toString().equals("")){
                    password2.setError(getResources().getString(R.string.empty_password));
                    return;
                }
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password1.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    savedInstanceState.putString(username.getText().toString(), "name");
                                    savedInstanceState.putString(email.getText().toString(), "email");
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
            password2.setError(null);
        }
        else{
            checkEqual = false;
            password2.setError(getResources().getString(R.string.error_equals));
        }
        updateSave();
    }

    private void checkName(){
        String name = username.getText().toString();
        String regx = "^[\\p{L} .'-]+$";
        Pattern regex = Pattern.compile(regx);
        Matcher matcher = regex.matcher(name);

        if(!matcher.matches()){
            checkUser = false;
            username.setError(getResources().getString(R.string.error_name));

        }else{
            checkUser = true;
            username.setError(null);
        }

        updateSave();
    }

    private void checkPassword(){
        String regexPassword = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
        final String passwordToCheck = password1.getText().toString();

        if(!Pattern.compile(regexPassword).matcher(passwordToCheck).matches()) {
            password1.setError(getResources().getString(R.string.error_password));
            checkPass = false;
        }else{
            checkPass = true;
            password1.setError(null);
        }
        updateSave();
    }

    private void checkMail(){
        String regexpEmail = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        final String emailToCheck = email.getText().toString();

        if(!Pattern.compile(regexpEmail).matcher(emailToCheck).matches()) {
            email.setError(getResources().getString(R.string.error_email));
            checkEmail = false;
        }else{
            checkEmail = true;
            email.setError(null);
        }
        updateSave();
    }

    private void updateSave(){
        Log.d("SWSW", ""+checkEqual+checkEmail+checkUser+checkPass);
        if(checkEmail && checkPass && checkUser && checkEqual){
            register.setClickable(true);
        }else{
            register.setClickable(false);
        }
    }
}
