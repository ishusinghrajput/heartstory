package com.photo.heartstory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Signup extends AppCompatActivity {

    private static final int RC_SIGN_IN =1;
    EditText name,username,email,password;
    Button signup;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    TextView already;
    LottieAnimationView progressBar;
    String emailpattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    CircleImageView setprofile;
    StorageReference storageReference;
    Uri imageuri;
    TextView choosetext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name=findViewById(R.id.name);
        username=findViewById(R.id.username);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        signup=findViewById(R.id.signupbtn);
        already=findViewById(R.id.already);
        choosetext=findViewById(R.id.choosetext);
        progressBar=findViewById(R.id.progressBar);
        setprofile=findViewById(R.id.setprofile);
        progressBar.setVisibility(View.INVISIBLE);

        Glide.with(Signup.this).load(R.drawable.profilepicture2).into(setprofile);

        storageReference= FirebaseStorage.getInstance().getReference();

        mAuth=FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Email=email.getText().toString();
                String Username=username.getText().toString();
                String Password=password.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(name.getText().toString())){
                    progressBar.setVisibility(View.INVISIBLE);
                    name.setError("Name can't be empty");
                }
                else if (TextUtils.isEmpty(username.getText().toString())){
                    progressBar.setVisibility(View.INVISIBLE);
                    username.setError("username can't be empty");
                }
                else if (TextUtils.isEmpty(email.getText().toString())){
                    progressBar.setVisibility(View.INVISIBLE);
                    email.setError("Email can't be empty");
                }
                else if (TextUtils.isEmpty(password.getText().toString())){
                    progressBar.setVisibility(View.INVISIBLE);
                    password.setError("Password can't be empty");
                }
                else if (password.length()<6){
                    progressBar.setVisibility(View.INVISIBLE);
                    password.setError("Password must have length of 6 letters");
                }
                else if (username.length()>20){
                    progressBar.setVisibility(View.INVISIBLE);
                    username.setError("Username should be between 1-20 characters");
                }
                else if (name.length()>30){
                    name.setError("Username should be between 1-30 characters");
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else if (!Email.matches(emailpattern)){
                    email.setError("Invalid Email");
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else if (imageuri==null){
                    Dialog noprofileimagedialog = new Dialog(Signup.this);
                    noprofileimagedialog.setContentView(R.layout.noprofileimagedialog);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        noprofileimagedialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbg));
                    }
                    noprofileimagedialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                    noprofileimagedialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    noprofileimagedialog.setCancelable(false);
                    noprofileimagedialog.show();
                    Button selectimagebutton = noprofileimagedialog.findViewById(R.id.selectimagebtn);
                    selectimagebutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            choosetext.performClick();
                            noprofileimagedialog.dismiss();
                        }
                    });
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    Query usernamequery=FirebaseDatabase.getInstance().getReference().child("users").orderByChild("username").equalTo(Username);
                    usernamequery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount()>0){
                                progressBar.setVisibility(View.INVISIBLE);
                                username.setError("Username not available");
                            }
                            else{
                                createuser();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }
        });


        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Signup.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        setprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(Signup.this);

            }
        });

        choosetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(Signup.this);


            }
        });

    }

    public void createuser(){
        String e_mail= email.getText().toString();
        String pass_word=password.getText().toString();
        mAuth.createUserWithEmailAndPassword(e_mail, pass_word)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            uploaddata();
                            uploadimage(imageuri);
                            sendemailverification();
                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            email.setError("An existing account is associated with this email");
                            Toast.makeText(Signup.this, "Registeration Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                        Toast.makeText(Signup.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void uploaddata(){

        String userid=mAuth.getCurrentUser().getUid();
        DatabaseReference currentuserdata=FirebaseDatabase.getInstance().getReference().child("users").child(userid);

        HashMap<String, Object> newpost=new HashMap<>();
        newpost.put("name", name.getText().toString());
        newpost.put("namelower", name.getText().toString().toLowerCase());
        newpost.put("username", username.getText().toString().toLowerCase());
        newpost.put("email", email.getText().toString());
        newpost.put("id", userid);



        currentuserdata.setValue(newpost);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageuri=result.getUri();

            setprofile.setImageURI(imageuri);
        }else{
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }

    }


    private void uploadimage(Uri imageuri) {
        String userid=mAuth.getCurrentUser().getUid();
        StorageReference imgref=storageReference.child(userid);
        imgref.putFile(imageuri);
    }
}
