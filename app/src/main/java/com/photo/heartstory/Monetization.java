package com.photo.heartstory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class Monetization extends AppCompatActivity {

    ImageView back;
    Button withdrawbtn;
    TextView totalviewstext , earned , withdrawn , balancetext;

    FirebaseAuth mauth;
    FirebaseUser firebaseUser;

    double balance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monetization);

        back=findViewById(R.id.back);
        withdrawbtn=findViewById(R.id.withdrawbtn);
        totalviewstext=findViewById(R.id.totalviews);
        earned=findViewById(R.id.totalearnedtext);
        withdrawn=findViewById(R.id.withdrawn);
        balancetext=findViewById(R.id.balancetext);

        mauth=FirebaseAuth.getInstance();
        firebaseUser=mauth.getCurrentUser();

        fetchbalance();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        withdrawbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (balance >= 3){
                    Intent intent = new Intent(Monetization.this , Withdraw.class);
                    startActivity(intent);
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(Monetization.this).create();
                    alertDialog.setTitle("Minimum amount to withdraw is $3");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE , "Got It !" ,
                       new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                           }
                       });
                    alertDialog.show();
                }

            }
        });


    }

    public void fetchbalance(){

        // Fetching Total Views
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("postsbyuser").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int[] totalviews = {0};

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HomeFragment.postitem postitem=dataSnapshot.getValue(HomeFragment.postitem.class);
                    if (postitem != null) {
                        String postid = postitem.getPostid();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("postviews").child(postid);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int currentpostviews = (int) snapshot.getChildrenCount();

                                // Showing Total Views
                                totalviews[0] = totalviews[0] + currentpostviews;
                                totalviewstext.setText(String.valueOf(totalviews[0]));

                                // Showing Total Earned
                                earned.setText("$ " + (totalviews[0] / (double) 1000));

                                // Fetching Total Withdrawn
                                DatabaseReference withdrawnReference = FirebaseDatabase.getInstance().getReference("totalwithdrawn").child(firebaseUser.getUid());
                                withdrawnReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int withdrawnviews = 0;
                                        if (snapshot.exists())
                                        withdrawnviews = Integer.parseInt(String.valueOf(snapshot.getValue()));

                                        // Showing Total Withdrawn
                                        if (withdrawnviews != 0){
                                            withdrawn.setText("$ " + (withdrawnviews / (double) 1000));
                                        }
                                        else {
                                            withdrawn.setText("$ 0");
                                        }

                                        // Showing Balance
                                        int eligibleviews = totalviews[0] - withdrawnviews;
                                        balance = eligibleviews / (double) 1000;
                                        balancetext.setText("$ " + balance);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "Unable To Fetch Views", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Unable to Fetch Total Views", Toast.LENGTH_SHORT).show();
            }
        });
    }

}