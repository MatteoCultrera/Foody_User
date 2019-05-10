package com.example.foodybiker;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private TextInputEditText email, password;
    private TextInputLayout emailL, passwordL;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton login;
    private ConstraintLayout register;
    private boolean correctness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
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
            register = findViewById(R.id.register_button);
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
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), R.string.login_failure, Toast.LENGTH_SHORT).show();
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
