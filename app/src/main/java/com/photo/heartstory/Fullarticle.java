package com.photo.heartstory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

// important library for Google adMob
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class Fullarticle extends AppCompatActivity {

    String profileid, postid, title;
    TextView date,articletitle, views, article, publishername ,article1,article2,article3,article4,article5,article6,article7,article8,article9,article10;
    ImageView back,mainimage, more, saveic, addimage1,addimage2,addimage3, addimage4, addimage5,addimage6,addimage7,addimage8,addimage9,addimage10;
    Button follow;
    CircleImageView publisherdp;
    FirebaseAuth mauth;
    String myuid;
    ImageButton comment;
    StorageReference storageReference;

    //creating Object of AdLoader
    private AdLoader adLoader ,adLoader2, adLoader3 ;

    // simple boolean to check the status of ad
    private boolean adLoaded=false;


    //creating Template View object
    TemplateView template , nativead2, nativead3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullarticle);

        SharedPreferences prefs=getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid=prefs.getString("profileid", "none");

        SharedPreferences prefs2=getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postid=prefs2.getString("postid", "none");

        date=findViewById(R.id.date);
        views=findViewById(R.id.views);
        articletitle=findViewById(R.id.articletitle);
        article=findViewById(R.id.article);
        mainimage=findViewById(R.id.mainimage);
        comment=findViewById(R.id.comment);
        more=findViewById(R.id.more);
        back=findViewById(R.id.back);

        addimage1=findViewById(R.id.addimage1);
        article1=findViewById(R.id.article1);
        addimage2=findViewById(R.id.addimage2);
        article2=findViewById(R.id.article2);
        addimage3=findViewById(R.id.addimage3);
        article3=findViewById(R.id.article3);
        addimage4=findViewById(R.id.addimage4);
        article4=findViewById(R.id.article4);
        addimage5=findViewById(R.id.addimage5);
        article5=findViewById(R.id.article5);
        addimage6=findViewById(R.id.addimage6);
        article6=findViewById(R.id.article6);
        addimage7=findViewById(R.id.addimage7);
        article7=findViewById(R.id.article7);
        addimage8=findViewById(R.id.addimage8);
        article8=findViewById(R.id.article8);
        addimage9=findViewById(R.id.addimage9);
        article9=findViewById(R.id.article9);
        addimage10=findViewById(R.id.addimage10);
        article10=findViewById(R.id.article10);

        addimage1.setVisibility(View.GONE);
        article1.setVisibility(View.GONE);
        addimage2.setVisibility(View.GONE);
        article2.setVisibility(View.GONE);
        addimage3.setVisibility(View.GONE);
        article3.setVisibility(View.GONE);
        addimage4.setVisibility(View.GONE);
        article4.setVisibility(View.GONE);
        addimage5.setVisibility(View.GONE);
        article5.setVisibility(View.GONE);
        addimage6.setVisibility(View.GONE);
        article6.setVisibility(View.GONE);
        addimage7.setVisibility(View.GONE);
        article7.setVisibility(View.GONE);
        addimage8.setVisibility(View.GONE);
        article8.setVisibility(View.GONE);
        addimage9.setVisibility(View.GONE);
        article9.setVisibility(View.GONE);
        addimage10.setVisibility(View.GONE);
        article10.setVisibility(View.GONE);

        publisherdp=findViewById(R.id.publisherdp);
        publishername=findViewById(R.id.publishername);
        follow=findViewById(R.id.follow);
        saveic=findViewById(R.id.saveic);

        more.setVisibility(View.GONE);

        storageReference=FirebaseStorage.getInstance().getReference();

        mauth=FirebaseAuth.getInstance();
        myuid=mauth.getCurrentUser().getUid();

        if (myuid.equals(profileid)){
            follow.setVisibility(View.INVISIBLE);
        }


        if (profileid.equals(myuid)){
            more.setVisibility(View.VISIBLE);
        }

        articleinfo(date,articletitle,article,article1,article2,article3,article4,article5,article6,article7,article8,article9,article10);
        isfollowing();
        issaved(postid, saveic);
        getViews();

        // Initializing the Google Admob SDK
        MobileAds.initialize (this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete( InitializationStatus initializationStatus ) {


            }
        });

        //Initializing the AdLoader   objects

        adLoader = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110").forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            private ColorDrawable background;@Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {

                NativeTemplateStyle styles = new
                        NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();

                template = findViewById(R.id.nativeTemplateView);
                template.setStyles(styles);
                template.setNativeAd(nativeAd);
                adLoaded = true;
            }
        }).build();

        adLoader2 = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110").forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            private ColorDrawable background;@Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {

                NativeTemplateStyle styles = new
                        NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();

                nativead2 = findViewById(R.id.nativead2);
                nativead2.setStyles(styles);
                nativead2.setNativeAd(nativeAd);
            }
        }).build();

        adLoader3 = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110").forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            private ColorDrawable background;@Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {

                NativeTemplateStyle styles = new
                        NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();

                nativead3 = findViewById(R.id.nativead3);
                nativead3.setStyles(styles);
                nativead3.setNativeAd(nativeAd);
            }
        }).build();


        loadNativeAd();
        showNativeAd();
        loadNativeAd2();
        loadNativeAd3();


        publisherdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", profileid);
                editor.apply();

                Intent intent=new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
            }
        });

        publishername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", profileid);
                editor.apply();

                Intent intent=new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Fullarticle.super.onBackPressed();
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(Fullarticle.this, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.delete:

                                AlertDialog alertDialog=new AlertDialog.Builder(Fullarticle.this).create();
                                alertDialog.setTitle("Do you want to delete this post?");
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
                                                ProgressDialog progressDialog=new ProgressDialog(Fullarticle.this);
                                                progressDialog.setMessage("Deleting");
                                                progressDialog.setCancelable(false);
                                                progressDialog.show();

                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("postsbyuser").child(profileid).child(postid);
                                                reference.child("isdeleted").setValue(true);

                                                FirebaseDatabase.getInstance().getReference("posts").child(postid).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(Fullarticle.this, "Post Successfully Deleted", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                dialog.dismiss();

                                            }
                                        });
                                alertDialog.show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.postmenu);
                popupMenu.show();
            }

        });



        DatabaseReference databaseReference2= FirebaseDatabase.getInstance().getReference("users").child(profileid);
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getApplicationContext()==null){
                    return;
                }

                Blog blog=snapshot.getValue(Blog.class);

                if (blog != null) {
                    publishername.setText(blog.getName());

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    final StorageReference profilepic = storageReference.child(blog.getId());

                    profilepic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getApplicationContext()).load(uri).into(publisherdp);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Glide.with(getApplicationContext()).load(R.drawable.accountcircleic).into(publisherdp);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        loadaddimages();

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (follow.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("follow").child(myuid)
                            .child("following").child(profileid).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("follow").child(profileid)
                            .child("followers").child(myuid).setValue(true);

                    addnotifications(myuid);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("follow").child(myuid)
                            .child("following").child(profileid).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("follow").child(profileid)
                            .child("followers").child(myuid).removeValue();
                }
            }
        });

        saveic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveic.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("saves").child(myuid).child(postid).setValue(true);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("saves").child(myuid).child(postid).removeValue();
                }
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("publisher", profileid);
                editor.apply();

                SharedPreferences.Editor editor2=getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor2.putString("postid", postid);
                editor2.apply();

                Intent intent=new Intent(Fullarticle.this, Comment.class);
                startActivity(intent);
            }
        });


    }

    public void isfollowing(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("follow").child(myuid).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileid).exists()){
                    follow.setText("Following");
                }
                else {
                    follow.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    public void addnotifications(String userid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("notifications").child(profileid);

        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("userid", userid);
        hashMap.put("text", "started following you.");
        hashMap.put("iscomment", false);

        reference.push().setValue(hashMap);
    }

    public void loadaddimages(){

        storageReference.child("posts").child(profileid).child(postid).child("addimage1")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(addimage1);
                addimage1.setVisibility(View.VISIBLE);
                nativead2.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addimage1.setVisibility(View.GONE);

            }
        });

        storageReference.child("posts").child(profileid).child(postid).child("addimage2")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(addimage2);
                addimage2.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addimage2.setVisibility(View.GONE);
            }
        });

        storageReference.child("posts").child(profileid).child(postid).child("addimage3")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(addimage3);
                addimage3.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addimage3.setVisibility(View.GONE);
            }
        });

        storageReference.child("posts").child(profileid).child(postid).child("addimage4")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(addimage4);
                addimage4.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addimage4.setVisibility(View.GONE);
            }
        });

        storageReference.child("posts").child(profileid).child(postid).child("addimage5")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(addimage5);
                addimage5.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addimage5.setVisibility(View.GONE);
            }
        });

        storageReference.child("posts").child(profileid).child(postid).child("addimage6")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(addimage6);
                addimage6.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addimage6.setVisibility(View.GONE);
            }
        });

        storageReference.child("posts").child(profileid).child(postid).child("addimage7")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(addimage7);
                addimage7.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addimage7.setVisibility(View.GONE);
            }
        });

        storageReference.child("posts").child(profileid).child(postid).child("addimage8")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(addimage8);
                addimage8.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addimage8.setVisibility(View.GONE);
            }
        });

        storageReference.child("posts").child(profileid).child(postid).child("addimage9")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(addimage9);
                addimage9.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addimage9.setVisibility(View.GONE);
            }
        });

        storageReference.child("posts").child(profileid).child(postid).child("addimage10")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(addimage10);
                addimage10.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addimage10.setVisibility(View.GONE);
            }
        });
    }

    private void articleinfo(final TextView date, final TextView articletitle, final TextView article, final TextView article1,final TextView article2, final TextView article3,final TextView article4, final TextView article5,final TextView article6, final TextView article7,final TextView article8, final TextView article9,final TextView article10){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("posts").child(postid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getApplicationContext()==null){
                    return;
                }

                HomeFragment.postitem postitem=snapshot.getValue(HomeFragment.postitem.class);

                date.setText(postitem.getDate());

                //Mark Post as Viewed
                FirebaseDatabase.getInstance().getReference("postviews").child(postid).child(myuid).setValue(true);

                articletitle.setText(postitem.getTitle());

                article.setText(postitem.getArticle());
                if (postitem.getArticle1()!=null){
                    article1.setText(postitem.getArticle1());
                    article1.setVisibility(View.VISIBLE);
                }
                if (postitem.getArticle2()!=null){
                    article2.setText(postitem.getArticle2());
                    article2.setVisibility(View.VISIBLE);
                }
                if (postitem.getArticle3()!=null){
                    article3.setText(postitem.getArticle3());
                    article3.setVisibility(View.VISIBLE);
                }
                if (postitem.getArticle4()!=null){
                    article4.setText(postitem.getArticle4());
                    article4.setVisibility(View.VISIBLE);
                }
                if (postitem.getArticle5()!=null){
                    article5.setText(postitem.getArticle5());
                    article5.setVisibility(View.VISIBLE);
                }
                if (postitem.getArticle6()!=null){
                    article6.setText(postitem.getArticle6());
                    article6.setVisibility(View.VISIBLE);
                }
                if (postitem.getArticle7()!=null){
                    article7.setText(postitem.getArticle7());
                    article7.setVisibility(View.VISIBLE);
                }
                if (postitem.getArticle8()!=null){
                    article8.setText(postitem.getArticle8());
                    article8.setVisibility(View.VISIBLE);
                }
                if (postitem.getArticle9()!=null){
                    article9.setText(postitem.getArticle9());
                    article9.setVisibility(View.VISIBLE);
                }
                if (postitem.getArticle10()!=null){
                    article10.setText(postitem.getArticle10());
                    article10.setVisibility(View.VISIBLE);
                }

                Glide.with(getApplicationContext()).load(postitem.getPostimage()).into(mainimage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getViews(){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("postviews").child(postid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                views.setText(""+snapshot.getChildrenCount()+" views");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadNativeAd()
    {
        // Creating  an Ad Request
        AdRequest adRequest = new AdRequest.Builder().build() ;

        // load Native Ad with the Request
        adLoader.loadAd(adRequest) ;


    }

    private void loadNativeAd2()
    {
        // Creating  an Ad Request
        AdRequest adRequest2 = new AdRequest.Builder().build() ;

        // load Native Ad with the Request
        adLoader2.loadAd(adRequest2) ;


    }

    private void loadNativeAd3()
    {
        // Creating  an Ad Request
        AdRequest adRequest3 = new AdRequest.Builder().build() ;

        // load Native Ad with the Request
        adLoader3.loadAd(adRequest3) ;


    }


    private void showNativeAd()
    {
        if ( adLoaded )
        {
            template.setVisibility( View.VISIBLE) ;
        }
        else
        {
            //Load the Native ad if it is not loaded
            loadNativeAd() ;

        }
    }

}