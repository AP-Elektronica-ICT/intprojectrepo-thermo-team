package com.app.vinnie.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;

public class Settings extends AppCompatActivity {

    BottomNavigationView mBottomnavigation;

    //variables
    int desiredTemp;


    //Text and buttons
    Switch nightModeSw, notifySw;
    EditText desTempEdit;

    //database ref
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mBottomnavigation = findViewById(R.id.bottom_navigation);

        //set home selected
        mBottomnavigation.setSelectedItemId(R.id.settings);


        //load settings at start
        loadSettings();

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

    public void loadSettings(){
        nightModeSw=findViewById(R.id.NightMode);
        desTempEdit=findViewById(R.id.DesiredTemp);
        notifySw=findViewById(R.id.Notifications);


    }


}
