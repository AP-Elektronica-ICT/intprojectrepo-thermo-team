package com.app.vinnie.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {
BottomNavigationView mBottomnavigation;
Button mdeleteButton;
FirebaseUser muser;
FirebaseAuth mAuth;
FirebaseFirestore mStore;
String userID;
TextView mUsername, mPhone, mEmail;
//TRY
DatabaseReference mDatabase;
FirebaseDatabase database;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        //TRY


        muser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        mdeleteButton = findViewById(R.id.ButtonDelete);
        mBottomnavigation = findViewById(R.id.bottom_navigation);
        mUsername = findViewById(R.id.username_Textview);
        mPhone = findViewById(R.id.phonenumber_Textview);
        mEmail = findViewById(R.id.userEmail_Textview);


        //ONDERSTAANDE REFERENTIE NAAR DATABASE VERBETEREN zodat we Onchange Data hebben!!!!!!!!!!!!

        //referentie naar de userTest deel en vervolgens adhv USERID de momenteel ingelogde user
        DocumentReference documentReference = mStore.collection("usersTest").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                //userinfo uit database halen en in textviews zetten
                mPhone.setText(documentSnapshot.getString("phone"));
                mUsername.setText(documentSnapshot.getString("uname"));
                mEmail.setText(documentSnapshot.getString("email"));
            }

        });
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////






        //set profile selected
        mBottomnavigation.setSelectedItemId(R.id.profile);

        //perform itemSelectedListner
        mBottomnavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(), Settings.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.saunaList:
                        startActivity(new Intent(getApplicationContext(), SaunaList.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        mdeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(Profile.this);
                dialog.setTitle("Are you sure you want to delete your account?");
                dialog.setMessage("Deleting your account is permanent and will remove all content including comments, avatars and profile settings. ");
                dialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        muser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                if (task.isSuccessful()){
                                    deleteUser(userID);
                                    Toast.makeText(Profile.this, "Account deleted", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                    finish();
                                }
                                else{
                                    String errormessage = task.getException().getMessage();
                                    Toast.makeText(Profile.this,"error acquired" + errormessage,Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

                dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

    }

    //logout voor ap --> terug naar login activity
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(Profile.this, Login.class));
        this.finish();
    }
    public void deleteUser(String userid){
        mStore.collection("usersTest").document(userid).delete();


    }



}
