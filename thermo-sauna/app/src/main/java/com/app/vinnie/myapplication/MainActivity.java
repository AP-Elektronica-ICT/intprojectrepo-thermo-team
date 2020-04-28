package com.app.vinnie.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class MainActivity extends AppCompatActivity{

    BottomNavigationView mBottomnavigation;

    TextView locatie;
    //farebase
    FirebaseUser muser;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String userID;
    private FusedLocationProviderClient fusedLocationClient;
    Button getlocation;
//storage



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mBottomnavigation = findViewById(R.id.bottom_navigation);
        //locatie = findViewById(R.id.location);
        getlocation = findViewById(R.id.getlocation);

        //set home selected
        mBottomnavigation.setSelectedItemId(R.id.home);

        //perform itemSelectedListner

        mBottomnavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(), Settings.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.saunaList:
                        startActivity(new Intent(getApplicationContext(), SaunaList.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });






    }





}
