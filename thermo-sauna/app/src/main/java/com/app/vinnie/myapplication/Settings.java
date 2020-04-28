package com.app.vinnie.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Settings extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    BottomNavigationView mBottomnavigation;

    //variables
    double desiredTemp;
    boolean notify;
    boolean nightMode;


    //farebase
    FirebaseUser muser;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;

    String userID;
    DocumentReference noteRef;

    //Text and buttons
    Switch nightModeSw, notifySw;
    Button saveTemperatuur;
    EditText desTempEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //allocate usable buttons
        mBottomnavigation = findViewById(R.id.bottom_navigation);
        desTempEdit=findViewById(R.id.DesiredTemp);
        notifySw=findViewById(R.id.Notifications);
        nightModeSw=findViewById(R.id.NightMode);
        saveTemperatuur=findViewById(R.id.saveTemp);

        //set listeners for switches
        notifySw.setOnCheckedChangeListener(this);
        nightModeSw.setOnCheckedChangeListener(this);

        //init firebase
        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        muser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mAuth.getCurrentUser().getUid();

        //set home selected
        mBottomnavigation.setSelectedItemId(R.id.settings);

        //perform itemSelectedListener

        mBottomnavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.settings:
                        return true;
                    case R.id.saunaList:
                        startActivity(new Intent(getApplicationContext(), SaunaList.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        noteRef = mStore.collection("Users").document(userID);
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e!=null){
                    Toast.makeText(Settings.this, "Error while loading!", Toast.LENGTH_SHORT);
                    return;
                }

                if (documentSnapshot.exists()){
                    notify =documentSnapshot.getBoolean("Notifications");

                    nightMode =documentSnapshot.getBoolean("Nightmode");


                    desiredTemp =documentSnapshot.getDouble("Desired_Temp");


                    //change data
                    if (notify==true){
                        notifySw.setChecked(true);
                    }
                    if (notify==false){
                        notifySw.setChecked(false);
                    }
                    if (nightMode==true){
                        nightModeSw.setChecked(true);
                    }
                    if (nightMode==false){
                        nightModeSw.setChecked(false);
                    }

                    String desTempText = Double.toString(desiredTemp);
                    desTempEdit.setText(desTempText);
                }
            }
        });

        saveTemperatuur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String saveTempStr = desTempEdit.getText().toString();
                Double tempSaveVal = Double.parseDouble(saveTempStr);
                noteRef.update("Desired_Temp", tempSaveVal);
            }
        });
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (notifySw.isChecked()){
            notify=true;
            noteRef.update("Notifications",true);
        }
        else{
            notify=false;
            noteRef.update("Notifications",false);
        }
        if (nightModeSw.isChecked()){
            nightMode=true;
            noteRef.update("Nightmode",true);
        }
        else{
            nightMode=false;
            noteRef.update("Nightmode",false);
        }

    }
}
