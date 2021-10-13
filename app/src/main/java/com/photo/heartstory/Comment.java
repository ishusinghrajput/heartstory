package com.photo.heartstory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.photo.heartstory.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Comment extends AppCompatActivity {

    ImageView back,send;
    EditText addcomment;
    FirebaseAuth mauth;
    FirebaseUser firebaseUser;
    String publisher,postid;
    TextView viewpost;

    private RecyclerView recyclerView;
    private Commentadapter commentadapter;
    private List<Commentitem> commentitemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        back=findViewById(R.id.back);
        send=findViewById(R.id.send);
        recyclerView=findViewById(R.id.allcomments);
        addcomment=findViewById(R.id.addcomment);
        viewpost=findViewById(R.id.viewpost);

        mauth=FirebaseAuth.getInstance();
        firebaseUser=mauth.getCurrentUser();

        SharedPreferences prefs=getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        publisher=prefs.getString("publisher", "none");

        SharedPreferences prefs2=getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postid=prefs2.getString("postid", "none");

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentitemList=new ArrayList<>();
        commentadapter=new Commentadapter(this, commentitemList, postid);
        recyclerView.setAdapter(commentadapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment.super.onBackPressed();
            }
        });

        viewpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Comment.this, Fullarticle.class);
                startActivity(intent);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addcomment.getText().toString().equals("")){
                    addcomment.setError("Comment can't be empty");
                }
                else {
                    sendcomment();
                }
            }
        });

        readcomments();

    }

    public void sendcomment(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("comments").child(postid);

        String commentid=reference.push().getKey();

        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("comment", addcomment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        hashMap.put("commentid", commentid);

        reference.child(commentid).setValue(hashMap);

        addnotifications(firebaseUser.getUid(), postid);

        addcomment.setText("");
        Toast.makeText(this, "Posted", Toast.LENGTH_SHORT).show();
    }

    private void readcomments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentitemList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Commentitem commentitem=dataSnapshot.getValue(Commentitem.class);
                    commentitemList.add(commentitem);
                }
                Collections.reverse(commentitemList);
                commentadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addnotifications(String userid, String postid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("notifications").child(publisher);

        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("userid", userid);
        hashMap.put("text", "commented on your post");
        hashMap.put("postid", postid);
        hashMap.put("iscomment", true);

        reference.push().setValue(hashMap);
    }

}