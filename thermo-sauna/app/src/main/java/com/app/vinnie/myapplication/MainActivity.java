package com.app.vinnie.myapplication;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
import com.google.firebase.firestore.Query;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements LocationListener {
    LineGraphSeries<DataPoint> series;
    BottomNavigationView mBottomnavigation;
    TextView AVG1, temp, tempAVG, status;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference noteRef = db.document("Saunas/Sauna1");
    double tempddata;
    int dagen = 36;
    boolean Status;
    Button Month;
    Button Week;
    TextView AVGTIMES, Locatie;
    List DataTemp;

    private TextView latituteField;
    private TextView mDateTime, msaunanameText;
    private TextView addressField; //Add a new TextView to your activity_main to display the address
    private LocationManager locationManager;
    private String provider, mnamesauna;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temp = findViewById(R.id.tempdata);
        status = findViewById(R.id.status);
        tempAVG = findViewById(R.id.TempAVG);
        AVG1 = findViewById(R.id.AVG1);

        //locatie
        Locatie = findViewById(R.id.locatie);
        AVGTIMES = findViewById(R.id.AVGTIMES);
        Month = findViewById(R.id.buttonmonth);
        Week = findViewById(R.id.buttonWeek);

        //user


        addressField =  findViewById(R.id.locatie); //Make sure you add this to activity_main

        //calendar
        mDateTime = findViewById(R.id.DateTime);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);

            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);



        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        }

            //calender
        Calendar calender = Calendar.getInstance(TimeZone.getDefault());
        int year = calender.get(Calendar.YEAR);
        int month = calender.get(Calendar.MONTH);
        int day = calender.get(Calendar.DAY_OF_MONTH);
        String finalyear = Integer.toString(year);
        String finalmonth = Integer.toString(month +1);
        String finalday = Integer.toString(day);

        mDateTime.setText(finalday + "/" + finalmonth + "/" + finalyear);
        Toast.makeText(getApplicationContext(), Integer.toString(year), Toast.LENGTH_SHORT).show();


        Month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AVGTIMES.setText("Month AVG");
                dagen = 31;
            }
        });
        Week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AVGTIMES.setText("Week AVG");
                dagen = 7;
            }
        });

        mBottomnavigation = findViewById(R.id.bottom_navigation);
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

    @Override
    protected void onStart() {
        super.onStart();
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, "Error while loading!", Toast.LENGTH_SHORT);
                    return;
                }
                if (documentSnapshot.exists()) {
                    tempddata = documentSnapshot.getDouble("Temp");
                    Status = documentSnapshot.getBoolean("Status");
                    List<Long> temperaturen = new ArrayList<>();
                    temperaturen = (List<Long>) documentSnapshot.get("TempData");

                    GraphView graph = (GraphView) findViewById(R.id.grafiek);
                    DataPoint[] dp = new DataPoint[37];
                    for (int i = 0; i <= dagen; i++) {
                        String temp1 = Long.toString(temperaturen.get(i));
                        dp[i] = new DataPoint(i, Double.parseDouble(temp1));
                    }
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);
                    graph.addSeries(series);
                    double total = 0;
                    for (double totallengt : temperaturen) {
                        total += totallengt;
                    }
                    String input = AVGTIMES.getText().toString();
                    if (input.equals("Month AVG")) {
                        dagen = 31;
                    }
                    if (input.equals("Week AVG")) {
                        dagen = 7;
                    }
                    double avgtotal = (int) Math.round(total / 37);
                    double avg = (int) Math.round((avgtotal / 37) * dagen);

                    String temperatureAVG = Double.toString(avg);
                    tempAVG.setText(temperatureAVG);

                    String temp1 = Double.toString(avgtotal);
                    AVG1.setText(temp1);

                    String Temperature = Double.toString(tempddata);
                    temp.setText(Temperature);
                    String aanUIT = Boolean.toString(Status);
                    status.setText(aanUIT);



                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);

    }


    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        //You had this as int. It is advised to have Lat/Loing as double.
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(lat, lng, 1);
            int maxLines = address.get(0).getMaxAddressLineIndex();
            String addressStr;
            String country;

            addressStr = address.get(0).getLocality();
            country = address.get(0).getCountryName();
            builder.append(addressStr);
            builder.append(country);

            String fnialAddress = builder.toString(); //
            Toast.makeText(MainActivity.this, fnialAddress, Toast.LENGTH_SHORT).show();
            addressField.setText(country + ", " + addressStr); //This will display the final address.




        } catch (IOException e) {
            // Handle IOException
        } catch (NullPointerException e) {
            // Handle NullPointerException
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {


    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();


    }
}