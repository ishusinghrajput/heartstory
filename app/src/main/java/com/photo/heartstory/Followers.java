package com.photo.heartstory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Followers extends AppCompatActivity {

    String id;
    String title;
    TextView text;
    LottieAnimationView load;

    List<String> idlist;
    RecyclerView recyclerView;
    List<Blog> userlist;
    Blogadapter blogadapter;

    private DatabaseReference mdatabase;
    StorageReference storageReference;
    FirebaseUser user;
    FirebaseAuth mauth;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        final Intent  intent=getIntent();
        id=intent.getStringExtra("id");
        title=intent.getStringExtra("title");

        text=findViewById(R.id.text);
        back=findViewById(R.id.back);
        text.setText(title);
        recyclerView=findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userlist=new ArrayList<>();

        blogadapter=new Blogadapter(this, userlist);
        recyclerView.setAdapter(blogadapter);

        load=findViewById(R.id.followersload);

        idlist=new ArrayList<>();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Followers.super.onBackPressed();
            }
        });

        switch (title){
            case  "Following":
                getFollowings();
                break;
            case "Followers":
                getFollowers();
                break;

        }
    }

    private void showusers() {
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userlist.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Blog blog=dataSnapshot.getValue(Blog.class);
                    for (String id : idlist){
                        if (blog.getId().equals(id)){
                            userlist.add(blog);
                        }
                    }
                    Collections.reverse(userlist);
                    blogadapter.notifyDataSetChanged();
                    load.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getFollowers() {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("follow").child(id).child("followers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    idlist.add(dataSnapshot.getKey());
                }
                showusers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getFollowings() {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("follow").child(id).child("following");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    idlist.add(dataSnapshot.getKey());
                }
                showusers();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


}