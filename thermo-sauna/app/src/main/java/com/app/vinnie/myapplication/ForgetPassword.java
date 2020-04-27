package com.app.vinnie.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    private Button mSendResetButton;
    private EditText mResetEmail;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        //verbinding firebase
        mAuth = FirebaseAuth.getInstance();
        mSendResetButton = findViewById(R.id.ResetPasswordButton);
        mResetEmail = findViewById(R.id.EmailForgetPass);

        mSendResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mResetEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mResetEmail.setError("no password given");
                }
                else{
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(ForgetPassword.this,"RESET MAIL SEND",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), Login.class));

                            }
                            else{
                                String errormessage = task.getException().getMessage();
                                Toast.makeText(ForgetPassword.this,"error acquired" + errormessage,Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            }
        });




    }

    public void GoToRegister(View view) {
        startActivity(new Intent(getApplicationContext(), Register.class));
    }
}
