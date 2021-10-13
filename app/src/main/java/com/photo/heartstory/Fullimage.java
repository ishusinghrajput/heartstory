package com.photo.heartstory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Fullimage extends AppCompatActivity {
    String profileid;
    ImageView image;
    FirebaseAuth mauth;
    StorageReference storageReference;
    LottieAnimationView load;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullimage);

        back=findViewById(R.id.back);

        image=findViewById(R.id.image);
        load=findViewById(R.id.progressBar2);

        SharedPreferences prefs=getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid=prefs.getString("profileid", "none");

        mauth=FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        StorageReference profilepic=storageReference.child(profileid);

        profilepic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(Fullimage.this).load(uri).into(image);
                load.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Picasso.with(Fullimage.this).load(R.drawable.profilepicture2).into(image);
                load.setVisibility(View.GONE);
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Fullimage.super.onBackPressed();
            }
        });


    }
}