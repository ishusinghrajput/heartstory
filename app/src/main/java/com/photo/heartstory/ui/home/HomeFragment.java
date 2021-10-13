package com.photo.heartstory.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.photo.heartstory.Login;
import com.photo.heartstory.MainActivity;
import com.photo.heartstory.Postadaptar;
import com.photo.heartstory.Profile;
import com.photo.heartstory.R;
import com.photo.heartstory.Signup;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    CircleImageView gotoprofile;
    FirebaseAuth auth;
    FirebaseUser user;
    LottieAnimationView loading;
    ConstraintLayout layout;
    NestedScrollView nestedScrollView;
    Button foryoubtn,followingbtn;
    String activityname="homefragment";
    String postsshowing="allposts";

    int poststoload=5;
    int followingpoststoload=5;


    private RecyclerView recyclerView, recyclerView2;
    private Postadaptar postadaptar,postadaptar2;
    private List<postitem> postlists, postlists2;

    private List<String> followinglist;
    private List<String> viewedpostslist;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.textView);
        layout=root.findViewById(R.id.layout);

        recyclerView=root.findViewById(R.id.recyclerview);
        nestedScrollView=root.findViewById(R.id.nestedscrollview);
        foryoubtn=root.findViewById(R.id.foryoubtn);
        followingbtn=root.findViewById(R.id.followingbtn);
        recyclerView.setHasFixedSize(true);
        recyclerView2=root.findViewById(R.id.recyclerview2);
        recyclerView2.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        LinearLayoutManager linearLayoutManager2=new LinearLayoutManager(getContext());
        linearLayoutManager2.setReverseLayout(true);
        linearLayoutManager2.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView2.setLayoutManager(linearLayoutManager2);
        postlists=new ArrayList<>();
        postlists2=new ArrayList<>();
        postadaptar=new Postadaptar(getActivity(),postlists, layout, activityname);
        postadaptar2=new Postadaptar(getActivity(), postlists2, layout, activityname);
        recyclerView.setAdapter(postadaptar);
        recyclerView2.setAdapter(postadaptar2);

        gotoprofile= root.findViewById(R.id.gotoprofile);
        loading=root.findViewById(R.id.loading);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        recyclerView.setVisibility(View.GONE);

        //Check if user is signed in and verified or not
        Boolean emailflag=user.isEmailVerified();

        if (user==null || emailflag==false){
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(getContext(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
        }

        //Profile Picture Loading

        StorageReference profilefic= FirebaseStorage.getInstance().getReference(user.getUid());
        profilefic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext()).load(uri).apply(new RequestOptions().placeholder(R.drawable.accountcircleic)).into(gotoprofile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Glide.with(getContext()).load(R.drawable.accountcircleic).into(gotoprofile);
            }
        });

        //OnClickListeners

        foryoubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postsshowing="allposts";
                foryoubtn.setBackgroundResource(R.drawable.filterbtnbg);
                foryoubtn.setTextColor(Color.WHITE);
                followingbtn.setBackgroundResource(R.drawable.filterbtnbgbefore);
                followingbtn.setTextColor(Color.BLACK);
                recyclerView.setVisibility(View.GONE);
                recyclerView2.setVisibility(View.VISIBLE);
                readallposts();
            }
        });

        followingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postsshowing="followingposts";
                followingbtn.setBackgroundResource(R.drawable.filterbtnbg);
                followingbtn.setTextColor(Color.WHITE);
                foryoubtn.setBackgroundResource(R.drawable.filterbtnbgbefore);
                foryoubtn.setTextColor(Color.BLACK);
                recyclerView2.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                checkfollowing();
            }
        });

        gotoprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user==null){
                    Toast.makeText(getContext(), "Login Expired. Retstart the app.", Toast.LENGTH_SHORT).show();
                }
                else {
                    SharedPreferences.Editor editor=getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getUid());
                    editor.apply();

                    Intent intent=new Intent(getContext(), Profile.class);
                    startActivity(intent);
                }
            }
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!nestedScrollView.canScrollVertically(1)){

                    if (postsshowing.equals("allposts")){
                        poststoload=poststoload+5;
                        readallposts();
                    }
                    else {
                        followingpoststoload=followingpoststoload+5;
                        checkfollowing();
                    }

                }
            }
        });

        checkfollowing();
        checkviewed();
        readallposts();


        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }

    private  void checkfollowing(){
        followinglist=new ArrayList<>();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followinglist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    followinglist.add(dataSnapshot.getKey());
                }

                readposts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void checkviewed(){
        viewedpostslist=new ArrayList<>();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("partiallyviewed")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                viewedpostslist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    viewedpostslist.add(dataSnapshot.getKey());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readposts(){

        Query reference = FirebaseDatabase.getInstance().getReference("posts").limitToLast(followingpoststoload);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    postitem postitem=dataSnapshot.getValue(HomeFragment.postitem.class);

                    if (!viewedpostslist.contains(postitem.getPostid())){

                        for (String id : followinglist) {
                            if (postitem.getPublisher().equals(id)) {
                                postlists.add(0, postitem);
                                loading.setVisibility(View.INVISIBLE);

                            }
                        }

                    }
                }

                postadaptar.notifyDataSetChanged();

                if (postadaptar.getItemCount()<=2){
                    followingpoststoload=followingpoststoload+5;
                    readposts();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readallposts(){

        Query reference = FirebaseDatabase.getInstance().getReference("posts").limitToLast(poststoload);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {

                for (DataSnapshot dataSnapshot2 : snapshot2.getChildren()){
                    postitem postitem2=dataSnapshot2.getValue(HomeFragment.postitem.class);

                    if (!viewedpostslist.contains(postitem2.getPostid())){
                        postlists2.add(0,postitem2);
                        loading.setVisibility(View.INVISIBLE);
                    }

                }

                postadaptar2.notifyDataSetChanged();

                if (postadaptar2.getItemCount()<=2){
                    poststoload=poststoload+5;
                    readallposts();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    public static class postitem {
        private String date;
        private String postid;
        private String postimage;
        private String title;
        private String article;
        private String article1;
        private String article2;
        private String article3;
        private String article4;
        private String article5;
        private String article6;
        private String article7;
        private String article8;
        private String article9;
        private String article10;
        private String publisher;

        public postitem(String date, String postid, String postimage, String title, String article, String article1, String article2, String article3, String article4, String article5, String article6, String article7, String article8, String article9, String article10, String publisher) {
            this.date=date;
            this.postid = postid;
            this.postimage = postimage;
            this.title = title;
            this.article = article;
            this.article1 = article1;
            this.article2 = article2;
            this.article3 = article3;
            this.article4 = article4;
            this.article5 = article5;
            this.article6 = article6;
            this.article7 = article7;
            this.article8 = article8;
            this.article9 = article9;
            this.article10 = article10;
            this.publisher = publisher;

        }

        public postitem(){

        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getPostid() {
            return postid;
        }

        public void setPostid(String postid) {
            this.postid = postid;
        }

        public String getPostimage() {
            return postimage;
        }

        public void setPostimage(String postimage) {
            this.postimage = postimage;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        public String getArticle() {
            return article;
        }

        public void setArticle(String article) {
            this.article = article;
        }

        public String getArticle1() {
            return article1;
        }

        public void setArticle1(String article1) {
            this.article1 = article1;
        }

        public String getArticle2() {
            return article2;
        }

        public void setArticle2(String article2) {
            this.article2 = article2;
        }

        public String getArticle3() {
            return article3;
        }

        public void setArticle3(String article3) {
            this.article3 = article3;
        }

        public String getArticle4() {
            return article4;
        }

        public void setArticle4(String article4) {
            this.article4 = article4;
        }

        public String getArticle5() {
            return article5;
        }

        public void setArticle5(String article5) {
            this.article5 = article5;
        }

        public String getArticle6() {
            return article6;
        }

        public void setArticle6(String article6) {
            this.article6 = article6;
        }

        public String getArticle7() {
            return article7;
        }

        public void setArticle7(String article7) {
            this.article7 = article7;
        }

        public String getArticle8() {
            return article8;
        }

        public void setArticle8(String article8) {
            this.article8 = article8;
        }

        public String getArticle9() {
            return article9;
        }

        public void setArticle9(String article9) {
            this.article9 = article9;
        }

        public String getArticle10() {
            return article10;
        }

        public void setArticle10(String article10) {
            this.article10 = article10;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}