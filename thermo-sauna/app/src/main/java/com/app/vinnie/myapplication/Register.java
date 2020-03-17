package com.app.vinnie.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    EditText mUsername, mEmail, mPassword, mRepeatpassword, mPhoneNumber;

    Button mRegisterbtn;
    TextView mLoginbtn;
    private FirebaseAuth mAuth;
    //Progressbar progressbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsername = findViewById(R.id.UnameRegis);
        mEmail = findViewById(R.id.EmailRegister);
        mPassword = findViewById(R.id.PasswordRegister);
        mRepeatpassword = findViewById(R.id.PasswordRepeatRegister);
        mPhoneNumber = findViewById(R.id.PhoneRegister);
        mRegisterbtn = findViewById(R.id.RegisterButton);

        mAuth = FirebaseAuth.getInstance();
        //progressBar = fincViewById(R.id.progressbarRegister

        // voor als ge al ingelogd is --> direct naar mainactivity
        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }

        mRegisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                // valideren van user input --> nog verbeteren
                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mPassword.setError("password is required");
                    return;
                }

                if (password.length() < 8){
                    mPassword.setError(" password must be longer dan 8 characters");
                    return;
                }

                // progessBar.setVisibility(View.VISIBLE);

                //register user in firebase

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Register.this, "USER CREATED.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else {
                            Toast.makeText(Register.this, "USER Failed.", Toast.LENGTH_LONG).show();

                            Toast.makeText(Register.this, "ERROR!" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });


    }

    public void Login(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));

    }
}
