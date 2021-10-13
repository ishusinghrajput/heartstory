package com.photo.heartstory.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.photo.heartstory.Bottom;
import com.photo.heartstory.Monetization;
import com.photo.heartstory.Profile;
import com.photo.heartstory.R;
import com.photo.heartstory.Signup;
import com.photo.heartstory.Splashscreen;
import com.photo.heartstory.Withdraw;
import com.photo.heartstory.ui.home.HomeFragment;
import com.photo.heartstory.ui.home.HomeViewModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

import okhttp3.Cookie;


public class Post extends Fragment {

    private HomeViewModel homeViewModel;
    Uri imageuri,addimageuri1,addimageuri2,addimageuri3,addimageuri4,addimageuri5,addimageuri6,addimageuri7,addimageuri8,addimageuri9,addimageuri10;
    String myuri = "";
    String addimage1url,addimage2url,addimage3url,addimage4url,addimage5url,addimage6url,addimage7url,addimage8url,addimage9url,addimage10url;
    String postid;
    StorageTask uploadtask;
    StorageReference storageReference;
    DatabaseReference reference , postsbyuserreference;

    SimpleDateFormat dateFormat;
    Date date;

    ImageView imageadded,imageadded1,imageadded2,imageadded3,imageadded4,imageadded5,imageadded6,imageadded7,imageadded8,imageadded9,imageadded10;
    EditText title, article,article1,article2,article3,article4,article5,article6,article7,article8,article9,article10;
    Button addimage , post;
    TextView choosethumbnailtext;

    FirebaseAuth mauth;
    FirebaseUser firebaseUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_post, container, false);

        // Inflate the layout for this fragment

        choosethumbnailtext=root.findViewById(R.id.choosethumbnailtext);

        imageadded=root.findViewById(R.id.postimg);
        addimage=root.findViewById(R.id.addimage);
        imageadded1=root.findViewById(R.id.imageadded1);
        article1=root.findViewById(R.id.article1);
        imageadded2=root.findViewById(R.id.imageadded2);
        article2=root.findViewById(R.id.article2);
        imageadded3=root.findViewById(R.id.imageadded3);
        article3=root.findViewById(R.id.article3);
        imageadded4=root.findViewById(R.id.imageadded4);
        article4=root.findViewById(R.id.article4);
        imageadded5=root.findViewById(R.id.imageadded5);
        article5=root.findViewById(R.id.article5);
        imageadded6=root.findViewById(R.id.imageadded6);
        article6=root.findViewById(R.id.article6);
        imageadded7=root.findViewById(R.id.imageadded7);
        article7=root.findViewById(R.id.article7);
        imageadded8=root.findViewById(R.id.imageadded8);
        article8=root.findViewById(R.id.article8);
        imageadded9=root.findViewById(R.id.imageadded9);
        article9=root.findViewById(R.id.article9);
        imageadded10=root.findViewById(R.id.imageadded10);
        article10=root.findViewById(R.id.article10);

        post=root.findViewById(R.id.postbtn);
        title=root.findViewById(R.id.text);
        article=root.findViewById(R.id.article);

        mauth=FirebaseAuth.getInstance();
        firebaseUser=mauth.getCurrentUser();

        dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");
        date=new Date();

        reference= FirebaseDatabase.getInstance().getReference().child("posts");
        postsbyuserreference= FirebaseDatabase.getInstance().getReference().child("postsbyuser").child(firebaseUser.getUid());
        postid=reference.push().getKey();

        storageReference= FirebaseStorage.getInstance().getReference("posts").child(firebaseUser.getUid()).child(postid);

        imageadded1.setVisibility(View.GONE);
        article1.setVisibility(View.GONE);
        imageadded2.setVisibility(View.GONE);
        article2.setVisibility(View.GONE);
        imageadded3.setVisibility(View.GONE);
        article3.setVisibility(View.GONE);
        imageadded4.setVisibility(View.GONE);
        article4.setVisibility(View.GONE);
        imageadded5.setVisibility(View.GONE);
        article5.setVisibility(View.GONE);
        imageadded6.setVisibility(View.GONE);
        article6.setVisibility(View.GONE);
        imageadded7.setVisibility(View.GONE);
        article7.setVisibility(View.GONE);
        imageadded8.setVisibility(View.GONE);
        article8.setVisibility(View.GONE);
        imageadded9.setVisibility(View.GONE);
        article9.setVisibility(View.GONE);
        imageadded10.setVisibility(View.GONE);
        article10.setVisibility(View.GONE);


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(title.getText().toString())){
                    title.setError("Title Can't be Empty");
                }
                else if (TextUtils.isEmpty(article.getText().toString())){
                    article.setError("Article Can't be Empty");
                }
                else {
                    uploadpost();
                }
            }
        });

        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1000);
            }
        });


        imageadded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setAspectRatio(5,3)
                        .start(getContext(), Post.this);

            }
        });

        choosethumbnailtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setAspectRatio(5,3)
                        .start(getContext(), Post.this);

            }
        });

        imageadded1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alertDialog=new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Do you want to change this image?");
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
                                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent,1001);
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
            }
        });

        imageadded2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog=new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Do you want to change this image?");
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
                                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent,1002);
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
            }
        });

        imageadded3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog=new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Do you want to change this image?");
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
                                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent,1003);
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
            }
        });

        imageadded4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog=new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Do you want to change this image?");
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
                                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent,1004);
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
            }
        });

        imageadded5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog=new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Do you want to change this image?");
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
                                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent,1005);
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
            }
        });

        imageadded6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog=new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Do you want to change this image?");
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
                                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent,1006);
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
            }
        });

        imageadded7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog=new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Do you want to change this image?");
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
                                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent,1007);
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
            }
        });

        imageadded8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog=new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Do you want to change this image?");
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
                                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent,1008);
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
            }
        });

        imageadded9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog=new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Do you want to change this image?");
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
                                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent,1009);
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
            }
        });

        imageadded10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog=new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Do you want to change this image?");
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
                                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent,1010);
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
            }
        });


        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        return root;



    }

    private void uploadpost() {
        ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Posting");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (imageuri!=null){
            StorageReference filereference=storageReference.child(System.currentTimeMillis()
            + "."+ getfileextension(imageuri));

            uploadtask=filereference.putFile(imageuri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isComplete()){
                        throw task.getException();
                    }

                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Uri downloaduri= (Uri) task.getResult();
                        myuri=downloaduri.toString();

                        if (addimageuri1!=null){
                            storageReference.child("addimage1").putFile(addimageuri1);
                        }

                        if (addimageuri2!=null){
                            storageReference.child("addimage2").putFile(addimageuri2);
                        }

                        if (addimageuri3!=null){
                            storageReference.child("addimage3").putFile(addimageuri3);
                        }

                        if (addimageuri4!=null){
                            storageReference.child("addimage4").putFile(addimageuri4);
                        }

                        if (addimageuri5!=null){
                            storageReference.child("addimage5").putFile(addimageuri5);                        }

                        if (addimageuri6!=null){
                            storageReference.child("addimage6").putFile(addimageuri6);
                        }

                        if (addimageuri7!=null){
                            storageReference.child("addimage7").putFile(addimageuri7);                        }

                        if (addimageuri8!=null){
                            storageReference.child("addimage8").putFile(addimageuri8);
                        }

                        if (addimageuri9!=null){
                            storageReference.child("addimage9").putFile(addimageuri9);
                        }

                        if (addimageuri10!=null){
                            storageReference.child("addimage10").putFile(addimageuri10);
                        }

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("date", dateFormat.format(date));
                        hashMap.put("postid", postid);
                        hashMap.put("postimage", myuri);
                        hashMap.put("title", title.getText().toString());
                        hashMap.put("article", article.getText().toString());
                        if (!TextUtils.isEmpty(article1.getText().toString())){
                            hashMap.put("article1", article1.getText().toString());
                        }
                        if (!TextUtils.isEmpty(article2.getText().toString())){
                            hashMap.put("article2", article2.getText().toString());
                        }
                        if (!TextUtils.isEmpty(article3.getText().toString())){
                            hashMap.put("article3", article3.getText().toString());
                        }
                        if (!TextUtils.isEmpty(article4.getText().toString())){
                            hashMap.put("article4", article4.getText().toString());
                        }
                        if (!TextUtils.isEmpty(article5.getText().toString())){
                            hashMap.put("article5", article5.getText().toString());
                        }
                        if (!TextUtils.isEmpty(article6.getText().toString())){
                            hashMap.put("article6", article6.getText().toString());
                        }
                        if (!TextUtils.isEmpty(article7.getText().toString())){
                            hashMap.put("article7", article7.getText().toString());
                        }
                        if (!TextUtils.isEmpty(article8.getText().toString())){
                            hashMap.put("article8", article8.getText().toString());
                        }
                        if (!TextUtils.isEmpty(article9.getText().toString())){
                            hashMap.put("article9", article9.getText().toString());
                        }
                        if (!TextUtils.isEmpty(article10.getText().toString())){
                            hashMap.put("article10", article10.getText().toString());
                        }
                        hashMap.put("publisher", firebaseUser.getUid());
                        hashMap.put("isdeleted" , false);

                        reference.child(postid).setValue(hashMap);
                        postsbyuserreference.child(postid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Successfully Posted", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), Bottom.class);
                                    startActivity(intent);
                                    getActivity().finish();

                                }
                            }
                        });

                    }
                    else {
                        progressDialog.dismiss();
                        Dialog nothumbnaildialog = new Dialog(getContext());
                        nothumbnaildialog.setContentView(R.layout.nothumbnaildialog);
                        nothumbnaildialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                        nothumbnaildialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        nothumbnaildialog.setCancelable(false);
                        nothumbnaildialog.show();
                        Button selectimagebutton = nothumbnaildialog.findViewById(R.id.selectimagebtn);
                        selectimagebutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                choosethumbnailtext.performClick();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed to Post", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }else {
            progressDialog.dismiss();
            Dialog nothumbnaildialog = new Dialog(getContext());
            nothumbnaildialog.setContentView(R.layout.nothumbnaildialog);
            nothumbnaildialog.getWindow().getAttributes().windowAnimations = R.style.animation;
            nothumbnaildialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            nothumbnaildialog.setCancelable(false);
            nothumbnaildialog.show();
            Button selectimagebutton = nothumbnaildialog.findViewById(R.id.selectimagebtn);
            selectimagebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    choosethumbnailtext.performClick();
                    nothumbnaildialog.dismiss();
                }
            });
        }
    }

    private String getfileextension(Uri uri){
        ContentResolver contentResolver= getContext().getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageuri=result.getUri();

            imageadded.setImageURI(imageuri);
        }
        else if (requestCode==1000){
            if (resultCode== RESULT_OK){
                if (addimageuri1==null){
                    addimageuri1=data.getData();
                    imageadded1.setImageURI(addimageuri1);
                    imageadded1.setVisibility(View.VISIBLE);
                    article1.setVisibility(View.VISIBLE);
                }
                else if (addimageuri2==null){
                    addimageuri2=data.getData();
                    imageadded2.setImageURI(addimageuri2);
                    imageadded2.setVisibility(View.VISIBLE);
                    article2.setVisibility(View.VISIBLE);
                }
                else if (addimageuri3==null){
                    addimageuri3=data.getData();
                    imageadded3.setImageURI(addimageuri3);
                    imageadded3.setVisibility(View.VISIBLE);
                    article3.setVisibility(View.VISIBLE);
                }
                else if (addimageuri4==null){
                    addimageuri4=data.getData();
                    imageadded4.setImageURI(addimageuri4);
                    imageadded4.setVisibility(View.VISIBLE);
                    article4.setVisibility(View.VISIBLE);
                }
                else if (addimageuri5==null){
                    addimageuri5=data.getData();
                    imageadded5.setImageURI(addimageuri5);
                    imageadded5.setVisibility(View.VISIBLE);
                    article5.setVisibility(View.VISIBLE);
                }
                else if (addimageuri6==null){
                    addimageuri6=data.getData();
                    imageadded6.setImageURI(addimageuri6);
                    imageadded6.setVisibility(View.VISIBLE);
                    article6.setVisibility(View.VISIBLE);
                }
                else if (addimageuri7==null){
                    addimageuri7=data.getData();
                    imageadded7.setImageURI(addimageuri7);
                    imageadded7.setVisibility(View.VISIBLE);
                    article7.setVisibility(View.VISIBLE);
                }
                else if (addimageuri8==null){
                    addimageuri8=data.getData();
                    imageadded8.setImageURI(addimageuri8);
                    imageadded8.setVisibility(View.VISIBLE);
                    article8.setVisibility(View.VISIBLE);
                }
                else if (addimageuri9==null){
                    addimageuri9=data.getData();
                    imageadded9.setImageURI(addimageuri9);
                    imageadded9.setVisibility(View.VISIBLE);
                    article9.setVisibility(View.VISIBLE);
                }
                else if (addimageuri10==null){
                    addimageuri10=data.getData();
                    imageadded10.setImageURI(addimageuri10);
                    imageadded10.setVisibility(View.VISIBLE);
                    article10.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(getContext(), "You can add only 10 extra images.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (requestCode==1001) {
            if (resultCode == RESULT_OK) {
                addimageuri1 = data.getData();
                imageadded1.setImageURI(addimageuri1);
            }
        }
        else if (requestCode==1002) {
            if (resultCode == RESULT_OK) {
                addimageuri2 = data.getData();
                imageadded2.setImageURI(addimageuri2);
            }
        }
        else if (requestCode==1003) {
            if (resultCode == RESULT_OK) {
                addimageuri3 = data.getData();
                imageadded3.setImageURI(addimageuri3);
            }
        }
        else if (requestCode==1004) {
            if (resultCode == RESULT_OK) {
                addimageuri4 = data.getData();
                imageadded4.setImageURI(addimageuri4);
            }
        }
        else if (requestCode==1005) {
            if (resultCode == RESULT_OK) {
                addimageuri5 = data.getData();
                imageadded5.setImageURI(addimageuri5);
            }
        }
        else if (requestCode==1006) {
            if (resultCode == RESULT_OK) {
                addimageuri6 = data.getData();
                imageadded6.setImageURI(addimageuri6);
            }
        }
        else if (requestCode==1007) {
            if (resultCode == RESULT_OK) {
                addimageuri7 = data.getData();
                imageadded7.setImageURI(addimageuri7);
            }
        }
        else if (requestCode==1008) {
            if (resultCode == RESULT_OK) {
                addimageuri8 = data.getData();
                imageadded8.setImageURI(addimageuri8);
            }
        }
        else if (requestCode==1009) {
            if (resultCode == RESULT_OK) {
                addimageuri9 = data.getData();
                imageadded9.setImageURI(addimageuri9);
            }
        }
        else if (requestCode==1010) {
            if (resultCode == RESULT_OK) {
                addimageuri10 = data.getData();
                imageadded10.setImageURI(addimageuri10);
            }
        }
        else{
            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }
}