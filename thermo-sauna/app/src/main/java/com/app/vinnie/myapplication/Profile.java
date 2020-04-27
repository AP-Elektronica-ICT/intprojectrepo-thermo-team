package com.app.vinnie.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import io.alterac.blurkit.BlurLayout;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class Profile extends AppCompatActivity {
BottomNavigationView mBottomnavigation;
Button mdeleteButton;
TextView mUsername, mPhone, mEmail;
ImageView mProfilepic;
FloatingActionButton mfab;
BlurLayout mblurLayoutName, mblurLayoutPhone, mblurLayoutEmail;

ProgressDialog pd;
//farebase
FirebaseUser muser;
FirebaseAuth mAuth;
FirebaseFirestore mStore;
//storage
StorageReference storageReference;
//path where images of user profile will be stored
String storagePath = "Users_Profile_Imgs/";

String userID;

//uri of picked image
Uri image_uri;
//

private static final int CAMERA_REQUEST_CODE = 100;
private static final int STORAGE_REQUEST_CODE = 200;
private static final int IMAGE_PICK_GALLERY_CODE = 300;
private static final int IMAGE_PICK_CAMERA_CODE = 400;

//arrays of permission to be requested
    String cameraPermissions[];
    String storagePermissions[];

//TRY


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mblurLayoutName = findViewById(R.id.blurLayoutName);
        mblurLayoutPhone = findViewById(R.id.blurLayoutPhone);
        mblurLayoutEmail = findViewById(R.id.blurLayoutEmail);
        mblurLayoutName.startBlur();
        mblurLayoutPhone.startBlur();
        mblurLayoutEmail.startBlur();

        //TRY

        //init firebase
        muser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        storageReference = getInstance().getReference(); //fbase stor ref



        //views
        mdeleteButton = findViewById(R.id.ButtonDelete);
        mBottomnavigation = findViewById(R.id.bottom_navigation);
        mUsername = findViewById(R.id.username_Textview);
        mPhone = findViewById(R.id.phonenumber_Textview);
        mEmail = findViewById(R.id.userEmail_Textview);
        mProfilepic = findViewById(R.id.Profilepic);
        mfab = findViewById(R.id.fab);

        //init progress dialog
        pd = new ProgressDialog(getApplication());

        //init arrays of permissons
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};




        //referentie naar de userTest deel en vervolgens adhv USERID de momenteel ingelogde user
        DocumentReference documentReference = mStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                //userinfo uit database halen en in textviews zetten
                mPhone.setText(documentSnapshot.getString("phone"));
                mUsername.setText(documentSnapshot.getString("uname"));
                mEmail.setText(documentSnapshot.getString("email"));
                String image = documentSnapshot.getString("image");

                try {
                    Picasso.get().load(image).rotate(90).into(mProfilepic);
                }
                catch (Exception d){
                    Picasso.get().load(R.drawable.ic_user_name).placeholder(R.drawable.ic_user_name).rotate(90).into(mProfilepic);
                }

            }

        });

        //set profile selected
        mBottomnavigation.setSelectedItemId(R.id.profile);
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

        //fab onclicklist
        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
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

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission(){
        //min api verhogen
        requestPermissions(storagePermissions ,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result && result1;

    }
    //min api verhogen
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission(){
        requestPermissions(cameraPermissions ,CAMERA_REQUEST_CODE);
    }


    private void showEditProfileDialog() {
        //show dialog options
        //edit profile picture, name, phone number
        String options[]= {"Edit profile picture", "Edit name", "Edit phone number"};

        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        //set title
        builder.setTitle("Choose Action");
        // set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                switch (which){
                    case 0:
                        pd.setMessage("Updating profile picture");
                        showImagePicDialog();
                        break;
                    case 1:
                        pd.setMessage("Updating  username");
                        showNamePhoneUpdateDialog("uname");
                        break;
                    case 2:
                        pd.setMessage("Updating phone number");
                        showNamePhoneUpdateDialog("phone");

                        break;

                }

            }
        });
        //create and show dialog
        builder.create();
        builder.show();


    }

    private void showNamePhoneUpdateDialog(final String key) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setTitle("update"+ key);
        //set layout of dialog
        LinearLayout linearLayout = new LinearLayout(getApplication());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);

        //add edit text
        final EditText editText = new EditText(getApplication());
        editText.setHint("Enter"+key); //edit update name or photo
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        builder.setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text from edit text
                String value = editText.getText().toString().trim();

                if (!TextUtils.isEmpty(value)){

                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);
                    DocumentReference documentReference = mStore.collection("Users").document(userID);
                    documentReference.update(key, value);

                }
                else {
                    Toast.makeText(Profile.this, "please enter"+key, Toast.LENGTH_SHORT).show();

                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


            }
        });
        //create and show dialog
        builder.create();
        builder.show();


    }

    private void showImagePicDialog() {
        //show dialog options
        //Camera, choose from gallery
        String options[]= {"Open camera", "Choose from gallery"};

        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        //set title
        builder.setTitle("Pick image from:");
        // set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                switch (which){
                    case 0:

                        if(!checkCameraPermission()){
                            requestCameraPermission();
                        }
                        else {
                            pickFromCamera();
                        }

                        break;
                    case 1:
                        if(!checkStoragePermission()){
                            requestStoragePermission();
                        }
                        else {
                            pickFromGallery();
                        }
                        break;

                }

            }
        });
        //create and show dialog
        builder.create();
        builder.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //deze methode wordt aangeroepen wanneer de user Allow of Deny kiest van de dialog
        //deze keuze wordt hier afgehandeld

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    //checken of we toegang hebben tot camera
                    boolean cameraAccepted =grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }
                }
                else {
                    //toegang geweigerd
                    Toast.makeText(Profile.this, "please enalble camera & storage permission", Toast.LENGTH_SHORT).show();

                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                //van gallerij: eerst checkn of we hiervoor toestemming hebben
                if (grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickFromGallery();
                    }
                }
                else {
                    //toegang geweigerd
                    Toast.makeText(Profile.this, "please enalble  storage permission", Toast.LENGTH_SHORT).show();

                }

            }
            break;
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //deze methode wordt opgeroepne na het nemen van een foto met camera of vanuit gallerij
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //abeelding gekozen vanuit de gallerij --> verkrijgen van uri van de image
                image_uri = data.getData();

                uploadProfileCoverphoto(image_uri);

            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //afbeelding gekozen met camera
                uploadProfileCoverphoto(image_uri);


            }
        }
        super.onActivityResult(requestCode, resultCode, data);


    }

    private void uploadProfileCoverphoto(final Uri uri) {


        //path and name of image t be stored in firebase storage
        String filePathandName = storagePath+ "" + "image" + "_"+ userID;

        StorageReference storageReference2 = storageReference.child(filePathandName);

        storageReference2.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUti = uriTask.getResult();

                        //check if image is dowloaded or not
                        if (uriTask.isSuccessful()){
                            //image upload
                            //add/update url in users database
                            HashMap<String, Object> results = new HashMap<>();
                            results.put("image", downloadUti.toString());
                            DocumentReference documentReference = mStore.collection("Users").document(userID);
                            documentReference.update("image", downloadUti.toString());
                            //picasso lib
                            Picasso.get().load(downloadUti.toString()).rotate(90).into(mProfilepic);


                        }
                        else {
                            //error
                            Toast.makeText(Profile.this, "Some error occured", Toast.LENGTH_SHORT).show();


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });




    }

    private void pickFromCamera() {
        //intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_uri = Profile.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

    }

    //check
    private void pickFromGallery(){
        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);


    }

    //logout voor ap --> terug naar login activity
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        finishAffinity();

        startActivity(new Intent(getApplicationContext(), Startscreen.class));



    }
    public void deleteUser(String userid){
        mStore.collection("Users").document(userid).delete();
        startActivity(new Intent(Profile.this, Startscreen.class));


    }
}
