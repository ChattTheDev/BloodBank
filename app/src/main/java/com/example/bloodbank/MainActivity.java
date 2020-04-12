package com.example.bloodbank;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText e1, e2, e3, e4, e5, e6, e7;
    DatabaseReference dataref;
    StorageReference storageReference;
    Button upload, chooseimage;
    CircularImageView i1;
    int REQUEST_CODE_IMAGE = 101;
    ProgressDialog dialog;

    Uri imageUri;
    boolean isImageAdded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        e1 = findViewById(R.id.name);
        e2 = findViewById(R.id.address);
        e3 = findViewById(R.id.phoneno);
        e4 = findViewById(R.id.enterstate);
        e5 = findViewById(R.id.pincode);
        e6 = findViewById(R.id.bloodgroup);
        e7 = findViewById(R.id.country);

        upload = findViewById(R.id.buttonadddb);
        chooseimage = findViewById(R.id.imagechooser);



        i1 = findViewById(R.id.enterimage);
        i1.setShadowEnable(true);
        i1.setShadowRadius(7f);


        dataref = FirebaseDatabase.getInstance().getReference().child("UploadDBs");
        storageReference = FirebaseStorage.getInstance().getReference().child("UserImage");



        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String namefin =  e1.getText().toString();
                final String addfin = e2.getText().toString();
                final String phonefin = e3.getText().toString();
                final String statefin = e4.getText().toString();
                final String pinfin = e5.getText().toString();
                final String bloodfin = e6.getText().toString();
                final String countryfin = e7.getText().toString();
                 dialog = new ProgressDialog(MainActivity.this);
                dialog.setTitle("Uploading...");
                dialog.show();
                dialog.setCancelable(false);


                if(isImageAdded !=false && namefin!=null && addfin!=null && phonefin!=null && statefin!=null && pinfin!=null && bloodfin!=null){

                    uploadImageFull(namefin, addfin, phonefin, statefin, pinfin, bloodfin, countryfin);

                }

            }
        });

        chooseimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();

            }
        });





    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    private void uploadImageFull(final String namefin, final String addfin, final String phonefin, final String statefin, final String pinfin, final String bloodfin, final String countryfin) {

        if (imageUri != null) {



            final String key = dataref.push().getKey();
            storageReference.child(key + ".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap hashMap = new HashMap();
                            hashMap.put("name", namefin);
                            hashMap.put("address", addfin);
                            hashMap.put("phoneno", phonefin);
                            hashMap.put("state", statefin);
                            hashMap.put("pin", pinfin);
                            hashMap.put("imageUrl", uri.toString());
                            hashMap.put("bloodgroup", bloodfin);
                            hashMap.put("country",countryfin );

                            dataref.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(getApplicationContext(), "New Donor Added", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });

                        }
                    });

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    dialog.setMessage("Uploaded " + ((int) progress) + "%...");


                }
            });
            e1.setText("");
            e2.setText("");
            e3.setText("");
            e4.setText("");
            e5.setText("");
            e6.setText("");
            e7.setText("");
        }
        else{
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE_IMAGE && data!=null){
            imageUri = data.getData();
            isImageAdded=true;
            i1.setImageURI(imageUri);
        }
    }
}
