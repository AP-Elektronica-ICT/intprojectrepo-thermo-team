package com.app.vinnie.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SaunaList extends AppCompatActivity {
BottomNavigationView mBottomnavigation;

    public void QrCodeScanner(){
        try
        {
            Intent Camera = new Intent();
            Camera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(Camera);
        }
        catch (Exception e){
            e.printStackTrace ();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sauna_list);

        Button button = (Button) findViewById(R.id.button);
        mBottomnavigation = findViewById(R.id.bottom_navigation);
        //perform OnClickListener
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                QrCodeScanner();
            }
        });



        //set home selected
        mBottomnavigation.setSelectedItemId(R.id.saunaList);

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
                        startActivity(new Intent(getApplicationContext(), Settings.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.saunaList:
                        return true;
                }
                return false;
            }
        });
    }
}
