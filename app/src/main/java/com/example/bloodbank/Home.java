package com.example.bloodbank;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

public class Home extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText inputSearch;
    FirebaseRecyclerOptions<Model> options;
    FirebaseRecyclerAdapter<Model, MyViewHolder> adapter;
    DatabaseReference dataref;
    ProgressBar p1;
    String phone2;
    String c, d;
    String textmessage = "Hey Can You give me Blood to the Nearest Blood Bank? It's Urgent";
    private static final int PERMS_REQUEST_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dataref = FirebaseDatabase.getInstance().getReference().child("UploadDBs");
        recyclerView = findViewById(R.id.recyclerview);
        p1 = findViewById(R.id.progressbar);
        inputSearch = findViewById(R.id.inputSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        p1.setVisibility(View.VISIBLE);

        LoadData("");

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString() != null) {
                    LoadData(editable.toString());
                } else {
                    LoadData("");
                }

            }
        });


    }

    private void LoadData(String data) {

        Query query = dataref.orderByChild("pin").startAt(data).endAt(data + "\uf8ff");

        options = new FirebaseRecyclerOptions.Builder<Model>().setQuery(query, Model.class).build();
        adapter = new FirebaseRecyclerAdapter<Model, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MyViewHolder holder, final int position, @NonNull Model model) {
                holder.username.setText(model.getName());
                holder.useraddress.setText("Address: " + model.getAddress());
                holder.userstate.setText("State: " + model.getState());
                holder.userpin.setText("Pin: " + model.getPin());
                holder.userphone.setText(model.getPhoneno());
                holder.userbloodgroup.setText("Bloodgroup: " + model.getBloodgroup());
                holder.usercountry.setText("Country: "+ model.getCountry());

                final String phone = holder.userphone.getText().toString();
                phone2 = phone;

                holder.callbutt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Dexter.withActivity(Home.this)
                                    .withPermission(Manifest.permission.CALL_PHONE)
                                    .withListener(new PermissionListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.M)
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse response) {
                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            callIntent.setData(Uri.parse("tel:" + holder.userphone.getText().toString()));
                                            startActivity(callIntent);
                                        }
                                        @Override
                                        public void onPermissionDenied(PermissionDeniedResponse response)
                                        {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                                            intent.setData(uri);
                                            startActivity(intent);
                                        }
                                        @Override
                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                            token.continuePermissionRequest();
                                        }
                                    }).check();
                        } catch (Exception e){
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                holder.messagebutt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Dialog dialog = new Dialog(Home.this);


                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.singledialogsms);
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp.gravity = Gravity.CENTER;

                        dialog.getWindow().setAttributes(lp);
                        //dialog.setTitle("Call Donor");
                       final EditText e1 = (EditText)dialog.findViewById(R.id.sendnumber);
                       final EditText e2 = (EditText)dialog.findViewById(R.id.sendsmsmessage);
                        Button b1 = (Button)dialog.findViewById(R.id.sendsms);

                        e1.setText(holder.userphone.getText().toString());
                        e2.setText(textmessage);
                        dialog.show();
                        String a = e1.getText().toString().trim();
                        String b = e2.getText().toString().trim();
                        c = a;
                        d = b;

                        b1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                try {
                                    Dexter.withActivity(Home.this)
                                            .withPermission(Manifest.permission.SEND_SMS)
                                            .withListener(new PermissionListener() {
                                                @RequiresApi(api = Build.VERSION_CODES.M)
                                                @Override
                                                public void onPermissionGranted(PermissionGrantedResponse response) {
                                                    String userphoneno = e1.getText().toString().trim();
                                                    String usermessage = e2.getText().toString().trim();

                                                    SmsManager smsManager = SmsManager.getDefault();
                                                    smsManager.sendTextMessage(userphoneno, null, usermessage, null, null );
                                                    Toast.makeText(Home.this, "Message Sent", Toast.LENGTH_LONG).show();

                                                }
                                                @Override
                                                public void onPermissionDenied(PermissionDeniedResponse response)
                                                {
                                                    Intent intent = new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                                    intent.setData(uri);
                                                    startActivity(intent);
                                                }
                                                @Override
                                                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                                    token.continuePermissionRequest();
                                                }
                                            }).check();
                                } catch (Exception e){
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                        });




                    }
                });


                Picasso.get().load(model.getImageUrl()).into(holder.imageView);

                p1.setVisibility(View.GONE);

            }


            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view, parent, false);


                return new MyViewHolder(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }











}
