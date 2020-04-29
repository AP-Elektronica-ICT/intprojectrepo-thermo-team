package com.app.vinnie.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

//firebase

public class  SaunaList extends AppCompatActivity {
    BottomNavigationView mBottomnavigation;

    public void QrCodeScanner() {
        try {
            Intent Camera = new Intent();
            Camera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(Camera);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //farebase
    FirebaseUser muser;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String userID;
    DocumentReference noteRef;
    ListView list;
    ArrayAdapter adapter;
    List<String> Sauna_list;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sauna_list);

        list = findViewById(R.id.list);
        //adapter=new ArrayAdapter(this,R.layout.sauna_item_list, Sauna_list);
        //list.setAdapter(adapter);

        //init firebase
        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        muser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mAuth.getCurrentUser().getUid();
        noteRef = mStore.collection("Users").document(userID);
        /*noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e!=null){
                    Toast.makeText(SaunaList.this, "Error while loading!", Toast.LENGTH_SHORT);
                    return;
                }
                if (documentSnapshot.exists()){

                    Sauna_list = (List<String>) documentSnapshot.get("Saunas");
                    adapter=new ArrayAdapter(SaunaList.this,R.layout.sauna_item_list, Sauna_list);
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
            }
        });*/

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(SaunaList.this);
                builder.setMessage("What do you want to do?")
                        .setCancelable(true)
                        .setPositiveButton("Select", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                String save = parent.getItemAtPosition(position).toString();
                                noteRef.update("SelectedSauna",save);
                                adapter.notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                String save = parent.getItemAtPosition(position).toString();
                                noteRef.update("Saunas", FieldValue.arrayRemove(save));
                                adapter.notifyDataSetChanged();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        });


        Button add = (Button) findViewById(R.id.button1);
        mBottomnavigation = findViewById(R.id.bottom_navigation);
        //perform OnClickListener
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //QrCodeScanner();
                qrScan.initiateScan();

                //If qr scanner does not return this will add value as it should
                //noteRef.update("Saunas", FieldValue.arrayUnion("Sauna1"));


            }
        });


        //set home selected
        mBottomnavigation.setSelectedItemId(R.id.saunaList);

        //perform itemSelectedListener

        mBottomnavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
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
                    Toast.makeText(SaunaList.this, "Error while loading!", Toast.LENGTH_SHORT);
                    return;
                }
                if (documentSnapshot.exists()){

                    Sauna_list = (List<String>) documentSnapshot.get("Saunas");
                    adapter=new ArrayAdapter(SaunaList.this,R.layout.sauna_item_list, Sauna_list);
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
            }
        });
    }

    String Sauna_add;
    //qr code scanner object
    IntentIntegrator qrScan = new IntentIntegrator(this);

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            //noteRef.update("Saunas", FieldValue.arrayUnion("Sauna1"));
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    //JSONObject obj = new JSONObject(result.getContents());
                    //setting values to string
                    //Sauna_add = obj.getString("Sauna");
                    String qr = new String(result.getContents());
                    noteRef.update("Saunas",FieldValue.arrayUnion(qr));
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}