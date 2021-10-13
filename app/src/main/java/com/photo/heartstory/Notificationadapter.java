package com.photo.heartstory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Notificationadapter extends RecyclerView.Adapter<Notificationadapter.Viewholder> {

    private Context mcontext;
    private List<Notification> mnotification;

    public Notificationadapter(Context mcontext, List<Notification> mnotification){
        this.mcontext=mcontext;
        this.mnotification=mnotification;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.notificationitem, parent, false);
        return new Notificationadapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        final Notification notification=mnotification.get(position);

        holder.noti.setText(notification.getText());

        userinfo(holder.notiprofile, holder.notiusername, notification.getUserid());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.isIscomment()){
                    SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("publisher", notification.getUserid());
                    editor.apply();

                    SharedPreferences.Editor editor2=mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor2.putString("postid", notification.getPostid());
                    editor2.apply();

                    Intent intent=new Intent(mcontext, Comment.class);
                    mcontext.startActivity(intent);
                }
                else {
                    SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", notification.getUserid());
                    editor.apply();

                    Intent intent=new Intent(mcontext, Profile.class);
                    mcontext.startActivity(intent);
                }
            }
        });

        holder.notiprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", notification.getUserid());
                editor.apply();

                Intent intent=new Intent(mcontext, Profile.class);
                mcontext.startActivity(intent);
            }
        });

        holder.notiusername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", notification.getUserid());
                editor.apply();

                Intent intent=new Intent(mcontext, Profile.class);
                mcontext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mnotification.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        public CircleImageView notiprofile;
        public TextView notiusername, noti;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            notiprofile=itemView.findViewById(R.id.notiprofile);
            notiusername=itemView.findViewById(R.id.notiusername);
            noti=itemView.findViewById(R.id.noti);


        }
    }

    public  void userinfo(final CircleImageView notiprofile, final TextView username, final String publisherid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Blog blog=snapshot.getValue(Blog.class);

                username.setText(blog.getName());

                StorageReference storageReference= FirebaseStorage.getInstance().getReference();
                StorageReference profilepic=storageReference.child(blog.getId());

                profilepic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(mcontext).load(uri).into(notiprofile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Glide.with(mcontext).load(R.drawable.accountcircleic).into(notiprofile);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
