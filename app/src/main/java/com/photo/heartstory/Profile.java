package com.photo.heartstory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
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
import com.photo.heartstory.ui.home.HomeFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    ConstraintLayout layout;
    CircleImageView profileimg;
    TextView username,name,followers,following,posts,followerstext,followingtext;
    Button editprofile;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    FirebaseAuth mauth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    LottieAnimationView progressbar;
    NestedScrollView nestedScrollView;
    String profileid;
    ImageButton mypostbtn, savebtn;
    ImageView setting, lineundermypost, lineundersaved, back;
    RecyclerView myposts,savedposts;
    private List<String> mysavedposts;
    String activityname="profile";

    private Postadaptar postadaptar,postadaptar2;
    private List<HomeFragment.postitem> postlists;
    private List<HomeFragment.postitem> postlists2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        layout=findViewById(R.id.layout);
        nestedScrollView=findViewById(R.id.nestedScrollView);

        profileimg=findViewById(R.id.profileimg);
        back=findViewById(R.id.back);
        lineundermypost=findViewById(R.id.lineundermypost);
        lineundersaved=findViewById(R.id.lineundersaved);
        lineundersaved.setVisibility(View.GONE);
        editprofile=findViewById(R.id.editprofile);
        username=findViewById(R.id.username);
        name=findViewById(R.id.name);
        followerstext=findViewById(R.id.followerstext);
        followingtext=findViewById(R.id.followingtext);
        progressbar=findViewById(R.id.progressBar);
        followers=findViewById(R.id.followers);
        following=findViewById(R.id.following);
        setting=findViewById(R.id.setting);
        posts=findViewById(R.id.posts);
        mypostbtn=findViewById(R.id.mypostbtn);
        savebtn=findViewById(R.id.savebtn);
        myposts=findViewById(R.id.myposts);
        myposts.setHasFixedSize(true);

        savedposts=findViewById(R.id.savedposts);
        savedposts.setHasFixedSize(true);

        savedposts.setVisibility(View.INVISIBLE);
        myposts.setVisibility(View.VISIBLE);

        mauth=FirebaseAuth.getInstance();
        user=mauth.getCurrentUser();
        final String userid=mauth.getCurrentUser().getUid();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference profilepic=storageReference.child(userid);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(Profile.this);
        LinearLayoutManager linearLayoutManager2=new LinearLayoutManager(Profile.this);
        //linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        myposts.setLayoutManager(linearLayoutManager);
        savedposts.setLayoutManager(linearLayoutManager2);
        postlists=new ArrayList<>();
        postlists2=new ArrayList<>();
        postadaptar=new Postadaptar(getApplicationContext(), postlists, layout, activityname);
        postadaptar2=new Postadaptar(getApplicationContext(), postlists2, layout, activityname);
        myposts.setAdapter(postadaptar);
        savedposts.setAdapter(postadaptar2);

        SharedPreferences prefs=getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid=prefs.getString("profileid", "none");

        if (!profileid.equals(userid)){
            setting.setVisibility(View.GONE);
        }

        if (profileid.equals(userid)){
            editprofile.setText("Edit Profile");
        }
        else{
            isfollowing(profileid, editprofile);
        }

        userinfo();
        getfollowers();
        getfollowing();
        noofposts();
        readposts();

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editprofile.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getUid())
                            .child("following").child(profileid).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("follow").child(profileid)
                            .child("followers").child(user.getUid()).setValue(true);

                    addnotifications(userid);
                }
                else if (editprofile.getText().toString().equals("Following")){
                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getUid())
                            .child("following").child(profileid).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("follow").child(profileid)
                            .child("followers").child(user.getUid()).removeValue();
                }
                else {
                    Intent intent =new Intent(Profile.this, Editprofile.class);
                    startActivity(intent);
                }
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Profile.this, Followers.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "Followers");
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Profile.this, Followers.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "Following");
                startActivity(intent);
            }
        });

        followingtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Profile.this, Followers.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "Following");
                startActivity(intent);
            }
        });

        followerstext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Profile.this, Followers.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "Followers");
                startActivity(intent);
            }
        });

        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor=getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", profileid);
                editor.apply();

                Intent intent=new Intent(Profile.this,Fullimage.class);
                startActivity(intent);
            }
        });

        mypostbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedposts.setVisibility(View.GONE);
                lineundermypost.setVisibility(View.VISIBLE);
                lineundersaved.setVisibility(View.GONE);
                myposts.setVisibility(View.VISIBLE);
            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myposts.setVisibility(View.GONE);
                lineundermypost.setVisibility(View.GONE);
                lineundersaved.setVisibility(View.VISIBLE);
                savedposts.setVisibility(View.VISIBLE);
                mysavedposts();
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Profile.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile.super.onBackPressed();
            }
        });

    }

    public void userinfo(){

        mauth=FirebaseAuth.getInstance();
        user=mauth.getCurrentUser();
        String userid=mauth.getCurrentUser().getUid();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference profilepic=storageReference.child(profileid);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getApplicationContext()==null){
                    return;
                }

                Blog user=snapshot.getValue(Blog.class);

                username.setText(user.getUsername());
                name.setText(user.getName());


                profilepic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getApplicationContext()).load(uri).into(profileimg);
                        progressbar.setVisibility(View.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Picasso.with(getApplicationContext()).load(R.drawable.accountcircleic).into(profileimg);
                        progressbar.setVisibility(View.INVISIBLE);
                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void isfollowing(final String userid, final Button button){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("follow").child(user.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userid).exists()){
                    button.setText("Following");
                }
                else {
                    button.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getfollowers(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getfollowing(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("follow").child(profileid).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readposts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postlists.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HomeFragment.postitem postitem=dataSnapshot.getValue(HomeFragment.postitem.class);
                        if (postitem.getPublisher().equals(profileid)) {
                            postlists.add(postitem);
                        }
                }
                Collections.reverse(postlists);
                postadaptar.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void noofposts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int noofpost=0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HomeFragment.postitem postitem=dataSnapshot.getValue(HomeFragment.postitem.class);
                    if (postitem.getPublisher().equals(profileid)) {
                        noofpost++;
                    }
                }

                posts.setText(""+noofpost);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void mysavedposts(){
        mysavedposts=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("saves").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    mysavedposts.add(dataSnapshot.getKey());
                }

                readsavedposts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readsavedposts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postlists2.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HomeFragment.postitem postitem=dataSnapshot.getValue(HomeFragment.postitem.class);

                    for (String id : mysavedposts){
                        if (postitem.getPostid().equals(id)) {
                            postlists2.add(postitem);
                        }
                    }
                }
                Collections.reverse(postlists2);
                postadaptar2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addnotifications(String userid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("notifications").child(profileid);

        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("userid", userid);
        hashMap.put("text", "started following you.");
        hashMap.put("iscomment", false);

        reference.push().setValue(hashMap);
    }

}