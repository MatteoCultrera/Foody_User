package com.example.foodyuser;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private EditText email, password;
    private TextView errorMail, errorPassword;
    private FirebaseAuth firebaseAuth;
    private MaterialButton login, register;
    private boolean correctness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        correctness = true;

        /*
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
        else {
            email = findViewById(R.id.emailLogin);
            errorMail = findViewById(R.id.email_error);
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
            password = findViewById(R.id.password);
            errorPassword = findViewById(R.id.password_error);
            password.addTextChangedListener(new TextWatcher() {
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
            login = findViewById(R.id.login);
            register = findViewById(R.id.register);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
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
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), R.string.auth_success, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Login.this, MainActivity.class);
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
    }

    private void checkMail(){
        View errorLine = findViewById(R.id.email_error_line);
        String regexpEmail = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        final String emailToCheck = email.getText().toString();

        if(!Pattern.compile(regexpEmail).matcher(emailToCheck).matches()) {
            errorMail.setText(getResources().getString(R.string.error_email));
            correctness = false;
            errorLine.setBackgroundColor(getResources().getColor(R.color.errorColor, this.getTheme()));
            errorLine.setAlpha(1);
        }else{
            correctness = true;
            errorMail.setText("");
            errorLine.setAlpha(0.2f);
            errorLine.setBackgroundColor(Color.BLACK);
        }
        updateSave();
    }

    private void checkPassword(){
        View errorLine = findViewById(R.id.password_error_line);
        String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        final String passwordToCheck = password.getText().toString();

        if(!Pattern.compile(regexPassword).matcher(passwordToCheck).matches()) {
            errorPassword.setText(getResources().getString(R.string.error_password));
            correctness = false;
            errorLine.setBackgroundColor(getResources().getColor(R.color.errorColor, this.getTheme()));
            errorLine.setAlpha(1);
        }else{
            correctness = true;
            errorPassword.setText("");
            errorLine.setAlpha(0.2f);
            errorLine.setBackgroundColor(Color.BLACK);
        }
        updateSave();
        */
    }

    private void updateSave(){
        if(!correctness){
            login.setClickable(false);
        }else{
            login.setClickable(true);
        }

    }
}
