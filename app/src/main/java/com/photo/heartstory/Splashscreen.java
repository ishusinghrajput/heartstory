package com.photo.heartstory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.photo.heartstory.ui.home.HomeFragment;

public class Splashscreen extends AppCompatActivity {

    ImageView splashlogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        splashlogo=findViewById(R.id.splashlogo);
        splashlogo.setImageResource(R.drawable.heartstorylogo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser()==null){
                    Intent intent = new Intent(Splashscreen.this, Signup.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(Splashscreen.this, Bottom.class);
                    startActivity(intent);
                    finish();
                }
            }
        },2000);
    }
}
