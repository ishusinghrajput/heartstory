package com.photo.heartstory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.Lottie;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Editprofile extends AppCompatActivity {

    CircleImageView profile;
    ImageButton close;
    TextView change;
    Button save;
    EditText name,username;
    LottieAnimationView progressBar;

    FirebaseUser firebaseUser;
    private Uri imageuri;
    private StorageTask uploadtask;
    StorageReference storageReference;
    String myusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        profile=findViewById(R.id.profile);
        close=findViewById(R.id.close);
        change=findViewById(R.id.change);
        save=findViewById(R.id.save);
        name=findViewById(R.id.name);
        username=findViewById(R.id.username);
        progressBar=findViewById(R.id.progressBar);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference();
        final StorageReference profilepic=storageReference.child(firebaseUser.getUid());

        profilepic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri).into(profile);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Picasso.with(getApplicationContext()).load(R.drawable.accountcircleic).into(profile);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });


        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Blog blog=snapshot.getValue(Blog.class);
                name.setText(blog.getName());
                username.setText(blog.getUsername());
                myusername=""+blog.getUsername();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editprofile.super.onBackPressed();
                finish();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(Editprofile.this);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(Editprofile.this);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd=new ProgressDialog(Editprofile.this);
                pd.setMessage("Updating Profile");
                pd.show();

                if (TextUtils.isEmpty(name.getText().toString())){
                    name.setError("Name can't be empty");
                    pd.dismiss();
                }
                else if (TextUtils.isEmpty(username.getText().toString())){
                    username.setError("Username can't be empty");
                    pd.dismiss();
                }
                else if (username.length()>20){
                    pd.dismiss();
                    username.setError("Username should be between 1-20 characters");
                }
                else if (name.length()>30){
                    name.setError("Username should be between 1-30 characters");
                    pd.dismiss();
                }
                else {
                    Query usernamequery=FirebaseDatabase.getInstance().getReference().child("users").orderByChild("username").equalTo(username.getText().toString());
                    usernamequery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String writtenusername=username.getText().toString();
                            if (writtenusername.equals(myusername)){
                                updateprofile(name.getText().toString(),
                                        username.getText().toString(), pd);

                                if (imageuri!=null){
                                    updateimage(imageuri);
                                }
                            }
                            else {
                                if (snapshot.getChildrenCount() > 0) {
                                    pd.dismiss();
                                    username.setError("Username already taken");
                                } else {
                                    updateprofile(name.getText().toString(),
                                            username.getText().toString(), pd);

                                    if (imageuri != null) {
                                        updateimage(imageuri);
                                    }

                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }

            }
        });

    }

    private void updateprofile(String name, String username, ProgressDialog pd) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("username", username);

        reference.updateChildren(hashMap);

        pd.dismiss();

        Intent intent=new Intent(Editprofile.this, Profile.class);
        startActivity(intent);
        finish();
    }

    private void updateimage(Uri imageuri) {
        String userid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference imgref=storageReference.child(userid);
        imgref.putFile(imageuri);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageuri=result.getUri();

            profile.setImageURI(imageuri);
        }else{
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }

    }
}