package com.photo.heartstory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Commentadapter extends RecyclerView.Adapter<Commentadapter.ViewHolder> {

    public Context mcontext;
    public List<Commentitem> mcomment;
    public String postid;

    private FirebaseUser firebaseUser;

    public Commentadapter(Context mcontext, List<Commentitem> mcomment, String postid){
        this.mcontext=mcontext;
        this.mcomment=mcomment;
        this.postid=postid;
    }

    @NonNull
    @Override
    public Commentadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.commentitem, parent, false);
        return new Commentadapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Commentadapter.ViewHolder holder, int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final Commentitem commentitem=mcomment.get(position);

        holder.commenttext.setText(commentitem.getComment());
        getuserinfo(holder.profilepicture, holder.nametext, commentitem.getPublisher());

        holder.profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", commentitem.getPublisher());
                editor.apply();

                Intent intent=new Intent(mcontext, Profile.class);
                mcontext.startActivity(intent);
            }
        });

        holder.nametext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid" , commentitem.getPublisher());
                editor.apply();

                Intent intent=new Intent(mcontext, Profile.class);
                mcontext.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (commentitem.getPublisher().equals(firebaseUser.getUid())){
                    AlertDialog alertDialog=new AlertDialog.Builder(mcontext).create();
                    alertDialog.setTitle("Do you want to delete comment?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference("comments").child(postid).child(commentitem.getCommentid()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(mcontext, "Comment Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    dialog.dismiss();

                                }
                            });
                    alertDialog.show();
                }

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mcomment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView profilepicture;
        public TextView nametext, commenttext;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profilepicture=itemView.findViewById(R.id.profilepicture);
            nametext=itemView.findViewById(R.id.nametext);
            commenttext=itemView.findViewById(R.id.commenttext);

        }
    }

    private void getuserinfo(final CircleImageView profilepicture, final TextView nametext, String publisherid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(publisherid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Blog blog=snapshot.getValue(Blog.class);
                nametext.setText(blog.getName());

                StorageReference storageReference= FirebaseStorage.getInstance().getReference();
                final StorageReference profilepic=storageReference.child(blog.getId());

                profilepic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(mcontext).load(uri).into(profilepicture);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Glide.with(mcontext).load(R.drawable.accountcircleic).into(profilepicture);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
