package com.photo.heartstory.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.photo.heartstory.Blog;
import com.photo.heartstory.Blogadapter;
import com.photo.heartstory.Postadaptar;
import com.photo.heartstory.Profile;
import com.photo.heartstory.R;
import com.photo.heartstory.ui.home.HomeFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    ConstraintLayout layout;

    private RecyclerView mbloglist;
    private DatabaseReference mdatabase;
    EditText searchView;
    StorageReference storageReference;
    FirebaseUser user;
    FirebaseAuth mauth;
    LottieAnimationView load;
    String filter="article";
    Button userbyname,userbyusername,searcharticle;
    LottieAnimationView searchsomething;
    TextView textsearchsomething;
    String activityname="search";

    private Postadaptar postadaptar;
    private List<HomeFragment.postitem> postlists;

    List<Blog> userlist;
    Blogadapter blogadapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);


        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });


        layout=root.findViewById(R.id.layout);

        searchsomething=root.findViewById(R.id.searchsomething);
        textsearchsomething=root.findViewById(R.id.textsearchsomething);

        load=root.findViewById(R.id.load);
        load.setVisibility(View.INVISIBLE);
        userbyname=root.findViewById(R.id.userbyname);
        userbyusername=root.findViewById(R.id.userbyusername);
        searcharticle=root.findViewById(R.id.searcharticle);
        mdatabase= FirebaseDatabase.getInstance().getReference().child("users");
        mdatabase.keepSynced(true);


        mbloglist=(RecyclerView)root.findViewById(R.id.myrecyclerview);
        mbloglist.setHasFixedSize(true);
        mbloglist.setLayoutManager(new LinearLayoutManager(getContext()));

        postlists=new ArrayList<>();
        postadaptar=new Postadaptar(getContext(), postlists, layout, activityname);

        userlist = new ArrayList<>();
        blogadapter=new Blogadapter(getContext(), userlist);


        searchView=root.findViewById(R.id.search);

        userbyname.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                filter="userbyname";
                userbyname.setBackgroundResource(R.drawable.filterbtnbg);
                userbyname.setTextColor(Color.WHITE);
                userbyusername.setBackgroundResource(R.drawable.filterbtnbgbefore);
                userbyusername.setTextColor(Color.BLACK);
                searcharticle.setBackgroundResource(R.drawable.filterbtnbgbefore);
                searcharticle.setTextColor(Color.BLACK);
                String searchtext=searchView.getText().toString().toLowerCase();
                if (!TextUtils.isEmpty(searchtext)){
                    namesearch(searchtext);
                    load.setVisibility(View.VISIBLE);
                }
            }
        });

        userbyusername.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                filter="userbyusername";
                userbyusername.setBackgroundResource(R.drawable.filterbtnbg);
                userbyusername.setTextColor(Color.WHITE);
                userbyname.setBackgroundResource(R.drawable.filterbtnbgbefore);
                userbyname.setTextColor(Color.BLACK);
                searcharticle.setBackgroundResource(R.drawable.filterbtnbgbefore);
                searcharticle.setTextColor(Color.BLACK);
                String searchtext=searchView.getText().toString().toLowerCase();
                if (!TextUtils.isEmpty(searchtext)){
                    usernamesearch(searchtext);
                    load.setVisibility(View.VISIBLE);
                }
            }
        });

        searcharticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searcharticle.setBackgroundResource(R.drawable.filterbtnbg);
                searcharticle.setTextColor(Color.WHITE);
                userbyname.setBackgroundResource(R.drawable.filterbtnbgbefore);
                userbyname.setTextColor(Color.BLACK);
                userbyusername.setBackgroundResource(R.drawable.filterbtnbgbefore);
                userbyusername.setTextColor(Color.BLACK);
                filter="article";
                String searchtext=searchView.getText().toString().toLowerCase();
                if (!TextUtils.isEmpty(searchtext)){
                    readposts(searchtext);
                    load.setVisibility(View.VISIBLE);
                }
            }
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchsomething.setVisibility(View.GONE);
                textsearchsomething.setVisibility(View.GONE);
                if (filter.equals("userbyname")){
                    String searchtext=searchView.getText().toString().toLowerCase();
                    load.setVisibility(View.VISIBLE);
                    namesearch(searchtext);
                }
                else if (filter.equals("userbyusername")){
                    String searchtext=searchView.getText().toString().toLowerCase();
                    load.setVisibility(View.VISIBLE);
                    usernamesearch(searchtext);
                }
                else {
                    String searchtext=searchView.getText().toString().toLowerCase();
                    load.setVisibility(View.VISIBLE);
                    readposts(searchtext);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchsomething.setVisibility(View.GONE);
                textsearchsomething.setVisibility(View.GONE);
                if (filter.equals("userbyname")){
                    String searchtext=searchView.getText().toString().toLowerCase();
                    load.setVisibility(View.VISIBLE);
                    namesearch(searchtext);
                }
                else if (filter.equals("userbyusername")){
                    String searchtext=searchView.getText().toString().toLowerCase();
                    load.setVisibility(View.VISIBLE);
                    usernamesearch(searchtext);
                }
                else {
                    String searchtext=searchView.getText().toString().toLowerCase();
                    load.setVisibility(View.VISIBLE);
                    readposts(searchtext);
                }
            }
        });


        return root;
    }

    private void  namesearch(final String searchText) {

        Query firebaseSearchQuery=mdatabase;

        firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userlist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Blog blog = dataSnapshot.getValue(Blog.class);
                    if (blog.getNamelower().contains(searchText)){
                        userlist.add(blog);
                    }
                }
                Collections.reverse(userlist);
                blogadapter.notifyDataSetChanged();
                load.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }

        });
        mbloglist.setAdapter(blogadapter);


        /*FirebaseRecyclerAdapter<Blog, DashboardFragment.BlogViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Blog, DashboardFragment.BlogViewHolder>(
                        Blog.class,
                        R.layout.blog,
                        DashboardFragment.BlogViewHolder.class,
                        firebaseSearchQuery
                ) {

                    @Override
                    protected void populateViewHolder(final DashboardFragment.BlogViewHolder blogViewHolder, final Blog blog, int i) {

                        blogViewHolder.setUsername(blog.getUsername());
                        blogViewHolder.setId(blog.getId());
                        blogViewHolder.Username.setText(blog.getName());

                        mauth=FirebaseAuth.getInstance();
                        user=mauth.getCurrentUser();
                        final String searchuserid=blogViewHolder.searchuser.getText().toString();
                        isfollowing(searchuserid, blogViewHolder.followbtn);

                        if (searchuserid.equals(user.getUid())){
                            blogViewHolder.followbtn.setVisibility(View.INVISIBLE);
                        }

                        blogViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPreferences.Editor editor=getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                                editor.putString("profileid", searchuserid);
                                editor.apply();

                                Intent intent=new Intent(getContext(), Profile.class);
                                startActivity(intent);

                            }
                        });

                        blogViewHolder.followbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (blogViewHolder.followbtn.getText().toString().equals("Follow")){
                                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getUid())
                                            .child("following").child(searchuserid).setValue(true);

                                    FirebaseDatabase.getInstance().getReference().child("follow").child(searchuserid)
                                            .child("followers").child(user.getUid()).setValue(true);

                                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("notifications").child(searchuserid);

                                    HashMap<String, Object> hashMap=new HashMap<>();
                                    hashMap.put("userid", user.getUid());
                                    hashMap.put("text", "started following you.");
                                    hashMap.put("iscomment", false);

                                    reference.push().setValue(hashMap);

                                }
                                else{
                                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getUid())
                                            .child("following").child(searchuserid).removeValue();

                                    FirebaseDatabase.getInstance().getReference().child("follow").child(searchuserid)
                                            .child("followers").child(user.getUid()).removeValue();
                                }
                            }
                        });

                        storageReference = FirebaseStorage.getInstance().getReference();
                        final StorageReference profilepic=storageReference.child(searchuserid);

                        profilepic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.with(getContext()).load(uri).into(blogViewHolder.profileimageview);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Picasso.with(getContext()).load(R.drawable.profilepicture2).into(blogViewHolder.profileimageview);
                            }
                        });
                        load.setVisibility(View.GONE);

                    }
                };
        mbloglist.setAdapter(firebaseRecyclerAdapter);*/
    }

    private void  usernamesearch(String searchText) {
        Query firebaseSearchQuery=mdatabase.orderByChild("username").startAt(searchText).endAt(searchText+"\uf8ff");

        firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userlist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Blog blog = dataSnapshot.getValue(Blog.class);
                    userlist.add(blog);
                }
                Collections.reverse(userlist);
                blogadapter.notifyDataSetChanged();
                load.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }

        });
        mbloglist.setAdapter(blogadapter);

        /*FirebaseRecyclerAdapter<Blog, DashboardFragment.BlogViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Blog, DashboardFragment.BlogViewHolder>(
                        Blog.class,
                        R.layout.blog,
                        DashboardFragment.BlogViewHolder.class,
                        firebaseSearchQuery
                ) {
                    @Override
                    protected void populateViewHolder(final DashboardFragment.BlogViewHolder blogViewHolder, final Blog blog, int i) {

                        blogViewHolder.setUsername(blog.getUsername());
                        blogViewHolder.setId(blog.getId());

                        mauth=FirebaseAuth.getInstance();
                        user=mauth.getCurrentUser();
                        blogViewHolder.Username.setText(blog.getUsername());
                        final String searchuserid=blogViewHolder.searchuser.getText().toString();
                        isfollowing(searchuserid, blogViewHolder.followbtn);

                        if (searchuserid.equals(user.getUid())){
                            blogViewHolder.followbtn.setVisibility(View.INVISIBLE);
                        }

                        blogViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPreferences.Editor editor=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                                editor.putString("profileid", searchuserid);
                                editor.apply();

                                Intent intent=new Intent(getContext(), Profile.class);
                                startActivity(intent);

                            }
                        });

                        blogViewHolder.followbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (blogViewHolder.followbtn.getText().toString().equals("Follow")){
                                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getUid())
                                            .child("following").child(searchuserid).setValue(true);

                                    FirebaseDatabase.getInstance().getReference().child("follow").child(searchuserid)
                                            .child("followers").child(user.getUid()).setValue(true);

                                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("notifications").child(searchuserid);

                                    HashMap<String, Object> hashMap=new HashMap<>();
                                    hashMap.put("userid", user.getUid());
                                    hashMap.put("text", "started following you.");
                                    hashMap.put("iscomment", false);

                                    reference.push().setValue(hashMap);

                                }
                                else{
                                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getUid())
                                            .child("following").child(searchuserid).removeValue();

                                    FirebaseDatabase.getInstance().getReference().child("follow").child(searchuserid)
                                            .child("followers").child(user.getUid()).removeValue();
                                }
                            }
                        });

                        storageReference = FirebaseStorage.getInstance().getReference();
                        final StorageReference profilepic=storageReference.child(searchuserid);

                        profilepic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.with(getContext()).load(uri).into(blogViewHolder.profileimageview);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Picasso.with(getContext()).load(R.drawable.profilepicture2).into(blogViewHolder.profileimageview);
                            }
                        });
                        load.setVisibility(View.GONE);

                    }
                };

        mbloglist.setAdapter(firebaseRecyclerAdapter);*/
    }

    private void readposts(final String searchtext){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        //Query firebaseSearchQuery=reference.orderByChild("title").startAt(searchtext).endAt(searchtext+"\uf8ff");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postlists.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HomeFragment.postitem postitem=dataSnapshot.getValue(HomeFragment.postitem.class);
                    if (postitem.getTitle().toLowerCase().contains(searchtext)){
                        postlists.add(postitem);
                    }
                    load.setVisibility(View.GONE);
                }
                Collections.reverse(postlists);
                postadaptar.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mbloglist.setAdapter(postadaptar);
    }



    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Button followbtn;
        TextView searchuser,Username;
        ImageView profileimageview;
        FirebaseDatabase firebaseDatabase;
        StorageReference storageReference;
        FirebaseAuth mauth;
        FirebaseUser user;
        DatabaseReference databaseReference;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            searchuser=mView.findViewById(R.id.searchuser);
            followbtn=itemView.findViewById(R.id.followbtn);
            Username=mView.findViewById(R.id.username);
            profileimageview=itemView.findViewById(R.id.profileimageview);
            mauth=FirebaseAuth.getInstance();
            user=mauth.getCurrentUser();
            String userid=mauth.getCurrentUser().getUid();
            firebaseDatabase=FirebaseDatabase.getInstance();
            databaseReference=firebaseDatabase.getReference("users");

        }
        public void setUsername(String username){

            Username.setText(username);

        }

        public void setId(String id) {
            searchuser.setText(id);
        }
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

}