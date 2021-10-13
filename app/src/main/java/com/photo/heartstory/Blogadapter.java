package com.photo.heartstory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Blogadapter extends RecyclerView.Adapter<Blogadapter.Viewholder> {

    public Context mcontext;
    public List<Blog> muser;

    private FirebaseUser firebaseUser;
    StorageReference storageReference;

    public Blogadapter(Context mcontext, List<Blog> muser){
        this.mcontext=mcontext;
        this.muser=muser;
    }

    @NonNull
    @Override
    public Blogadapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.blog, parent, false);
        return new Blogadapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Blogadapter.Viewholder holder, int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        final Blog blog=muser.get(position);

        holder.searchuser.setText(blog.getId());
        holder.textView.setText(blog.getUsername());

        final String searchuserid=holder.searchuser.getText().toString();
        if (searchuserid.equals(firebaseUser.getUid())){
            holder.followbtn.setVisibility(View.INVISIBLE);
        }

        isfollowing(searchuserid, holder.followbtn);

        holder.mview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", searchuserid);
                editor.apply();

                Intent intent=new Intent(mcontext, Profile.class);
                mcontext.startActivity(intent);

            }
        });

        holder.followbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.followbtn.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid())
                            .child("following").child(searchuserid).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("follow").child(searchuserid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("notifications").child(searchuserid);

                    HashMap<String, Object> hashMap=new HashMap<>();
                    hashMap.put("userid", firebaseUser.getUid());
                    hashMap.put("text", "started following you.");
                    hashMap.put("iscomment", false);

                    reference.push().setValue(hashMap);

                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid())
                            .child("following").child(searchuserid).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("follow").child(searchuserid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profilepic=storageReference.child(searchuserid);

        profilepic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(mcontext).load(uri).into(holder.imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Picasso.with(mcontext).load(R.drawable.profilepicture2).into(holder.imageView);
            }
        });

    }

    @Override
    public int getItemCount() {
        return muser.size();
    }


    public class Viewholder extends RecyclerView.ViewHolder {

        public CircleImageView imageView;
        public TextView textView;
        public TextView searchuser;
        public Button followbtn;
        public View mview;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;

            imageView=itemView.findViewById(R.id.profileimageview);
            textView=itemView.findViewById(R.id.username);
            followbtn=itemView.findViewById(R.id.followbtn);
            searchuser=itemView.findViewById(R.id.searchuser);

        }
    }

    public void isfollowing(final String userid, final Button button){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("follow").child(firebaseUser.getUid()).child("following");
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

}
