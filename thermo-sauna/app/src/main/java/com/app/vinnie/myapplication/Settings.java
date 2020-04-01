package com.app.vinnie.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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


    //Text and buttons
    Switch nightModeSw, notifySw;
    EditText desTempEdit;

    //database ref
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private DocumentReference noteRef = db.document("Users/Dries");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //allocate usable buttons
        mBottomnavigation = findViewById(R.id.bottom_navigation);
        desTempEdit=findViewById(R.id.DesiredTemp);
        notifySw=findViewById(R.id.Notifications);
        nightModeSw=findViewById(R.id.NightMode);

        //set listeners for switches
        notifySw.setOnCheckedChangeListener(this);
        nightModeSw.setOnCheckedChangeListener(this);

        //load settings at start
        //loadSettings();



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
    }

    /*public void loadSettings(){
        ref= FirebaseDatabase.getInstance().getReference().child("Users").child("Dries");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //get data on change
                String notifStr=dataSnapshot.child("Notifications").getValue().toString();
                notify=Boolean.parseBoolean(notifStr);

                String nightStr=dataSnapshot.child("Nightmode").getValue().toString();
                nightMode=Boolean.parseBoolean(nightStr);

                String tempStr=dataSnapshot.child("Desired_Temp").getValue().toString();
                desiredTemp=Integer.parseInt(tempStr);

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

                desTempEdit.setText(desiredTemp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
*/

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (notifySw.isChecked()){
            notify=true;
        }
        else{
            notify=false;
        }
        if (nightModeSw.isChecked()){
            nightMode=true;
        }
        else{
            nightMode=false;
        }


    }
}
