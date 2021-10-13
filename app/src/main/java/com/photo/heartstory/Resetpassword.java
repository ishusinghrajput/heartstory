package com.photo.heartstory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Resetpassword extends AppCompatActivity {

    EditText email;
    Button resetbtn;
    LottieAnimationView progressBar;
    String emailpattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        email=findViewById(R.id.email);
        back=findViewById(R.id.back);
        resetbtn=findViewById(R.id.resetbtn);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();

        resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Email=email.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(email.getText().toString())){
                    email.setError("Email can't be empty");
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else if (!Email.matches(emailpattern)){
                    email.setError("Invalid Email");
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    firebaseAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                if (user!=null){
                                    firebaseAuth.signOut();
                                }
                                Intent intent = new Intent(Resetpassword.this, Checkreset.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(Resetpassword.this, "No account exist with this email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resetpassword.super.onBackPressed();
                finish();
            }
        });
    }
}
