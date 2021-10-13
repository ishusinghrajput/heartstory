package com.photo.heartstory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class About extends AppCompatActivity {

    ImageView back, circlelogo;
    TextView version , privacypolicy , terms;
    String versionname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        back=findViewById(R.id.back);
        version=findViewById(R.id.version);
        privacypolicy=findViewById(R.id.privacypolicy);
        terms=findViewById(R.id.terms);
        circlelogo=findViewById(R.id.imageView2);

        circlelogo.setImageResource(R.drawable.heartstorylogo);

        versionname=BuildConfig.VERSION_NAME;
        version.setText("Version: " + versionname);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(About.this , PrivacyPolicy.class);
                startActivity(intent);
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(About.this , Terms.class);
                startActivity(intent);
            }
        });


    }
}