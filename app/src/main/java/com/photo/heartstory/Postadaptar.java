package com.photo.heartstory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.photo.heartstory.ui.home.HomeFragment;

import java.util.List;

import static android.media.CamcorderProfile.get;

public class Postadaptar extends RecyclerView.Adapter<Postadaptar.Viewholder> {

    public Context mcontext;
    public List<HomeFragment.postitem> mpost;
    public ConstraintLayout layout;
    public String activityname;

    private FirebaseUser firebaseUser;

    public Postadaptar(Context mcontext, List<HomeFragment.postitem> mpost, ConstraintLayout layout, String activityname){
        this.mcontext=mcontext;
        this.mpost=mpost;
        this.layout=layout;
        this.activityname=activityname;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.activity_postitem, parent, false);
        return new Postadaptar.Viewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        HomeFragment.postitem postitem=mpost.get(position);

        Glide.with(mcontext).load(postitem.getPostimage()).into(holder.postimage);
        holder.posttitle.setText(postitem.getTitle());
        holder.postid=postitem.getPostid();


        publisherinfo(holder.postprofile, holder.postname, holder.publisher, postitem.getPublisher());

        issaved(holder.postid, holder.save);


    }


    private void issaved(final String postid, final ImageView save) {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("saves").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()){
                    save.setImageResource(R.drawable.filledsaveic);
                    save.setTag("saved");
                }
                else {
                    save.setImageResource(R.drawable.saveicon);
                    save.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return mpost.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        public ImageView postprofile,postimage,save;
        public TextView posttitle,postname,publisher;
        public String postid;
        public ImageView comments;

        public Viewholder(@NonNull final View itemView) {
            super(itemView);

            postprofile=itemView.findViewById(R.id.postprofile);
            postimage=itemView.findViewById(R.id.postimage);
            save=itemView.findViewById(R.id.saveicon);
            posttitle=itemView.findViewById(R.id.posttitle);
            postname=itemView.findViewById(R.id.postname);
            publisher=itemView.findViewById(R.id.publisher);
            comments=itemView.findViewById(R.id.comments);
            comments.setImageResource(R.drawable.commentic);


            postname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", publisher.getText().toString());
                    editor.apply();

                    Intent intent=new Intent(mcontext, Profile.class);
                    mcontext.startActivity(intent);
                }
            });

            comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("publisher", publisher.getText().toString());
                    editor.apply();

                    SharedPreferences.Editor editor2=mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor2.putString("postid", postid);
                    editor2.apply();

                    Intent intent=new Intent(mcontext, Comment.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mcontext.startActivity(intent);
                }
            });

            postprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", publisher.getText().toString());
                    editor.apply();

                    Intent intent=new Intent(mcontext, Profile.class);
                    mcontext.startActivity(intent);
                }
            });

            postimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", publisher.getText().toString());
                    editor.apply();

                    SharedPreferences.Editor editor2=mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor2.putString("postid", postid);
                    editor2.apply();

                    Intent intent=new Intent(mcontext, Fullarticle.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mcontext.startActivity(intent);
                }
            });

            posttitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor=mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", publisher.getText().toString());
                    editor.apply();

                    SharedPreferences.Editor editor2=mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor2.putString("postid", postid);
                    editor2.apply();

                    Intent intent=new Intent(mcontext, Fullarticle.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mcontext.startActivity(intent);
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (save.getTag().equals("save")){
                        FirebaseDatabase.getInstance().getReference().child("saves").child(firebaseUser.getUid()).child(postid).setValue(true);
                    }
                    else {
                        FirebaseDatabase.getInstance().getReference().child("saves").child(firebaseUser.getUid()).child(postid).removeValue();
                    }
                }
            });


        }


    }

    private void publisherinfo(final ImageView postprofile, final TextView postname, final TextView publisher, final String userid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Blog blog=snapshot.getValue(Blog.class);
                if (blog != null) {
                    postname.setText(blog.getName());
                    publisher.setText(blog.getId());


                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    final StorageReference profilepic = storageReference.child(blog.getId());

                    profilepic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(mcontext).load(uri).apply(new RequestOptions().placeholder(R.drawable.accountcircleic)).into(postprofile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Glide.with(mcontext).load(R.drawable.accountcircleic).into(postprofile);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull Postadaptar.Viewholder holder) {
        super.onViewAttachedToWindow(holder);

        if (holder.getItemViewType()==0){
            if (activityname.equals("homefragment")){
                FirebaseDatabase.getInstance().getReference().child("partiallyviewed").child(firebaseUser.getUid()).child(holder.postid).setValue(true);
            }

        }
    }

}
