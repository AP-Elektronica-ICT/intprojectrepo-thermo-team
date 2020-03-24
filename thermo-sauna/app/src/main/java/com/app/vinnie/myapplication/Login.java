package com.app.vinnie.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class Login extends AppCompatActivity {

    EditText mUsername, mPassword;
    Button mLoginbtn;
    FirebaseAuth mAuth;
    //ProgressBar progressbar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = findViewById(R.id.UsernameLogin);
        mPassword = findViewById(R.id.PasswordLogin);
        mAuth = FirebaseAuth.getInstance();
        mLoginbtn = findViewById(R.id.Login_button);
        //progressbar = findViewById(R.id.progressBar)

// voor als ge al ingelogd is --> direct naar mainactivity
        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }

        mLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();


                if(TextUtils.isEmpty(username)){
                    mUsername.setError("USERNAME= IS REQUIRED");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    mPassword.setError("PASSWORD IS REQUIRED");
                }


                //progressbar.setVisibility(View.VISIBLE);



                mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Login.this, "Login works!.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else {

                            Toast.makeText(Login.this, "Login failed!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });

    }


    public void Register(View view) {
        startActivity(new Intent(getApplicationContext(), Register.class));

    }

    public void forget(View view) {
        startActivity(new Intent(getApplicationContext(), ForgetPassword.class));

    }
}
