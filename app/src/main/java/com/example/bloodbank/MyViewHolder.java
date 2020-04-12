package com.example.bloodbank;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView username, useraddress, userstate, userpin, userphone, userbloodgroup, usercountry;
    Button callbutt, messagebutt;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.userimage);
        username = itemView.findViewById(R.id.username);
        useraddress = itemView.findViewById(R.id.useraddress);
        userstate = itemView.findViewById(R.id.userstate);
        userpin = itemView.findViewById(R.id.userpin);
        userphone = itemView.findViewById(R.id.userphoneno);
        userbloodgroup = itemView.findViewById(R.id.userblood1);
        usercountry = itemView.findViewById(R.id.usercountry);

        callbutt = itemView.findViewById(R.id.detailsbutt);

        messagebutt = itemView.findViewById(R.id.messagebutt);







    }
}
