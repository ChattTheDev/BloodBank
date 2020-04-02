package com.example.bloodbank;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText e1, e2, e3, e4, e5, e6;
    DatabaseReference dataref;
    TextView t1;
    ProgressBar p1;
    StorageReference storageReference;
    Button upload;
    ImageView i1;
    int REQUEST_CODE_IMAGE = 101;

    Uri imageUri;
    boolean isImageAdded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        e1 = findViewById(R.id.name);
        e2 = findViewById(R.id.address);
        e3 = findViewById(R.id.phoneno);
        e4 = findViewById(R.id.enterstate);
        e5 = findViewById(R.id.pincode);
        e6 = findViewById(R.id.bloodgroup);

        upload = findViewById(R.id.buttonadddb);


        i1 = findViewById(R.id.enterimage);

        t1 = findViewById(R.id.uploadvalue);
        p1 = findViewById(R.id.progressbar);

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

                if(isImageAdded !=false && namefin!=null && addfin!=null && phonefin!=null && statefin!=null && pinfin!=null && bloodfin!=null){

                    uploadImageFull(namefin, addfin, phonefin, statefin, pinfin, bloodfin);

                }

            }
        });

        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);

            }
        });



    }

    private void uploadImageFull(final String namefin, final String addfin, final String phonefin, final String statefin, final String pinfin, final String bloodfin) {

        t1.setVisibility(View.VISIBLE);
        p1.setVisibility(View.VISIBLE);

        final String key = dataref.push().getKey();
        storageReference.child(key+".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

                        dataref.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                               startActivity(new Intent(getApplicationContext(), Home.class));
                                Toast.makeText(getApplicationContext(), "New Donor Added", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (taskSnapshot.getBytesTransferred()*100)/taskSnapshot.getTotalByteCount();
                p1.setProgress((int) progress);
                t1.setText(progress + "%");

            }
        });

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
