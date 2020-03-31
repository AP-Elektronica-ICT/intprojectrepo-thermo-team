package com.app.vinnie.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText mUsername, mEmail, mPassword, mRepeatpassword, mPhoneNumber;

    Button mRegisterbtn;
    TextView mLoginbtn;
    private FirebaseAuth mAuth;
    //Progressbar progressbar
    FirebaseFirestore mStore;
    String userID;

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
        mStore = FirebaseFirestore.getInstance();
        //progressBar = fincViewById(R.id.progressbarRegister

        // voor als ge al ingelogd is --> direct naar mainactivity
        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }

        mRegisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String username = mUsername.getText().toString();
                final String phoneNumber = mPhoneNumber.getText().toString();

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


                //register user in firebase

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Register.this, "USER CREATED.", Toast.LENGTH_SHORT).show();
                            //voor we naar login gaan eerst user profile gegevens wegschrijven naar de database
                            //userID nemen van de registerende user
                            userID = mAuth.getCurrentUser().getUid();
                            //selecteren van de kolom waar je wilt opslagen
                            DocumentReference documentReference = mStore.collection("usersTest").document(userID);

                            //data die we willen wegschrijven
                            Map<String, Object> user = new HashMap<>();
                            user.put("uname", username);
                            user.put("email", email);
                            user.put("phone", phoneNumber);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG","user profile created for " +userID);
                                }
                            });

                            documentReference.set(user).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG","user profile creation in database failed");
                                }
                            });


                            //inloggen
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else {
                            //indien de user niet kon worden aangemaakt
                            Toast.makeText(Register.this, "USER REGISTER FAILED.", Toast.LENGTH_LONG).show();
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
