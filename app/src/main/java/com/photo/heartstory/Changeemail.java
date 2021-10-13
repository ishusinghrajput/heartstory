package com.photo.heartstory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Changeemail extends AppCompatActivity {

    EditText password;
    EditText newemail;
    Button change;
    String Currentemail;
    TextView currentemail;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeemail);

        password=findViewById(R.id.currentpassword);
        newemail=findViewById(R.id.newemail);
        change=findViewById(R.id.changeemail);
        back=findViewById(R.id.back);
        currentemail=findViewById(R.id.currentemail);
        final String emailpattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Blog blog=snapshot.getValue(Blog.class);
                Currentemail=""+blog.getEmail();
                currentemail.setText(Currentemail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Changeemail.super.onBackPressed();
                finish();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                final ProgressDialog load=new ProgressDialog(Changeemail.this);
                load.setMessage("Updating Profile");
                load.show();

                if (TextUtils.isEmpty(password.getText().toString())){
                    password.setError("Please enter password");
                    load.dismiss();
                }
                else if (TextUtils.isEmpty(newemail.getText().toString())){
                    newemail.setError("Please enter your new Email");
                    load.dismiss();
                }
                else if (!newemail.getText().toString().matches(emailpattern)){
                    newemail.setError("Invalid Email");
                    load.dismiss();
                }
                else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(Changeemail.this).create();
                    alertDialog.setTitle("You are changing your email to : " + newemail.getText());
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                    load.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

                                    AuthCredential credential= EmailAuthProvider.getCredential(Currentemail, password.getText().toString());

                                    user.reauthenticate(credential)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){
                                                        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

                                                        firebaseUser.updateEmail(newemail.getText().toString())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){
                                                                            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

                                                                            HashMap<String, Object> hashMap=new HashMap<>();
                                                                            hashMap.put("email", newemail.getText().toString());

                                                                            reference.updateChildren(hashMap);
                                                                            Toast.makeText(Changeemail.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                                                                            sendemailverification();
                                                                            load.dismiss();
                                                                            Intent intent=new Intent(Changeemail.this, Bottom.class);
                                                                            startActivity(intent);
                                                                            finish();

                                                                        }
                                                                        else {
                                                                            newemail.setError("Email is associated with another account");
                                                                            load.dismiss();
                                                                        }
                                                                    }
                                                                });
                                                    }else {
                                                        password.setError("Invalid Password");
                                                        Toast.makeText(Changeemail.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                                                        load.dismiss();
                                                    }
                                                }
                                            });
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
    }
    private void sendemailverification(){
        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(Changeemail.this, "Verify your new Email", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(Changeemail.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}