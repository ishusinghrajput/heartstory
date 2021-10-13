package com.photo.heartstory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    TextView create,forgot;
    EditText password;
    EditText email;
    Button login;
    private FirebaseAuth mAuth;
    LottieAnimationView progressBar;
    String emailpattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        create=findViewById(R.id.create);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.loginbtn);
        forgot=findViewById(R.id.forgot);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        mAuth=FirebaseAuth.getInstance();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this, Signup.class);
                startActivity(intent);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email=email.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(email.getText().toString())){
                    email.setError("Email can't be empty");
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else if (TextUtils.isEmpty(password.getText().toString())){
                    password.setError("Email can't be empty");
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else if (!Email.matches(emailpattern)){
                    email.setError("Invalid Email");
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    loginuser();
                }
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,Resetpassword.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private void loginuser(){
        String e_mail= email.getText().toString();
        final String pass_word=password.getText().toString();
        mAuth.signInWithEmailAndPassword(e_mail, pass_word)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            checkemailverification();

                        }
                        else {
                           email.setError("Invalid Email");
                           password.setError("Invalid Password");
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void checkemailverification(){
        FirebaseUser user=mAuth.getCurrentUser();
        Boolean emailflag=user.isEmailVerified();

        if (emailflag){
            Intent intent=new Intent(Login.this,Bottom.class);
            startActivity(intent);
            progressBar.setVisibility(View.INVISIBLE);
            finish();
        }
        else{
            sendemailverification();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void sendemailverification(){
        final FirebaseUser user=mAuth.getCurrentUser();
        if (user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(getApplicationContext(), Verification.class);
                        startActivity(intent);
                        finish();
                        mAuth.signOut();
                    }
                    else {
                        Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }
            });
        }
    }
}
