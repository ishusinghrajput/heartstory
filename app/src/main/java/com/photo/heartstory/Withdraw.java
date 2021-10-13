package com.photo.heartstory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.photo.heartstory.ui.home.HomeFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Withdraw extends AppCompatActivity {

    ImageView back;
    EditText accountnoedittext , confirmaccountnoedittext , ifsccodeedittext , acholdernameedittext , amountedittext;
    Button withdrawbtn;
    TextView balancetext;

    FirebaseAuth mauth;
    FirebaseUser firebaseUser;

    SimpleDateFormat dateFormat;
    Date date;

    double balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        back = findViewById(R.id.back);
        accountnoedittext = findViewById(R.id.accountnoedittext);
        confirmaccountnoedittext = findViewById(R.id.confirmaccountnoedittext);
        ifsccodeedittext = findViewById(R.id.ifsccodeedittext);
        acholdernameedittext = findViewById(R.id.acholdernameedittext);
        amountedittext = findViewById(R.id.amountedittext);
        withdrawbtn = findViewById(R.id.withdrawbtn);
        balancetext = findViewById(R.id.balancetext);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        date = new Date();


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

                if (TextUtils.isEmpty(accountnoedittext.getText().toString())){
                    accountnoedittext.setError("Account no. can't be empty !");
                }
                else if (TextUtils.isEmpty(confirmaccountnoedittext.getText().toString())){
                    confirmaccountnoedittext.setError("Account no. can't be empty !");
                }
                else if (TextUtils.isEmpty(ifsccodeedittext.getText().toString())){
                    ifsccodeedittext.setError("IFSC Code can't be empty !");
                }
                else if (TextUtils.isEmpty(acholdernameedittext.getText().toString())){
                    acholdernameedittext.setError("Account holder name can't be empty !");
                }
                else if (TextUtils.isEmpty(amountedittext.getText().toString())){
                    amountedittext.setError("Amount can't be empty !");
                }
                else if (!accountnoedittext.getText().toString().equals(confirmaccountnoedittext.getText().toString())){
                    confirmaccountnoedittext.setError("Account No. didn't match !");
                }
                else{

                    Dialog withdrawprocessingdialog = new Dialog(Withdraw.this);
                    withdrawprocessingdialog.setContentView(R.layout.withdrawprocessingdialog);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        withdrawprocessingdialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbg));
                    }
                    withdrawprocessingdialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                    withdrawprocessingdialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    withdrawprocessingdialog.setCancelable(false);
                    withdrawprocessingdialog.show();

                    fetchbalance();

                    int amount = Integer.parseInt(amountedittext.getText().toString());

                    if (!(balance >= amount)){
                        withdrawprocessingdialog.dismiss();
                        AlertDialog alertDialog = new AlertDialog.Builder(Withdraw.this).create();
                        alertDialog.setTitle("You don't have enough balance !");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE , "Got It !" ,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        alertDialog.show();

                    }
                    else if (!(amount >= 3)){
                        withdrawprocessingdialog.dismiss();
                        AlertDialog alertDialog = new AlertDialog.Builder(Withdraw.this).create();
                        alertDialog.setTitle("Minimum amount to withdraw is $3 !");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE , "Got It !" ,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                    else if (amount > 1000){
                        withdrawprocessingdialog.dismiss();
                        AlertDialog alertDialog = new AlertDialog.Builder(Withdraw.this).create();
                        alertDialog.setTitle("Maximum amount to withdraw is $1000 !");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE , "Got It !" ,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                    else{

                        //Uploading Withdraw Amount
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("totalwithdrawn").child(firebaseUser.getUid());
                        databaseReference.setValue(amount * 1000);

                        //Uploading Withdraw Data
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("withdraws").child(firebaseUser.getUid());

                        HashMap<String , Object> withdrawdata = new HashMap<>();
                        withdrawdata.put("name" , acholdernameedittext.getText().toString());
                        withdrawdata.put("accountnumber" , accountnoedittext.getText().toString());
                        withdrawdata.put("ifsccode" , ifsccodeedittext.getText().toString());
                        withdrawdata.put("amount" , amount);
                        withdrawdata.put("date" , dateFormat.format(date));

                        reference.push().setValue(withdrawdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                //Dismiss Withdraw processing dialog
                                withdrawprocessingdialog.dismiss();

                                //Showing Withdraw Completed Dialog
                                Dialog withdrawcompleteddialog = new Dialog(Withdraw.this);
                                withdrawcompleteddialog.setContentView(R.layout.withdrawlcompleteddialog);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    withdrawcompleteddialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbg));
                                }
                                withdrawcompleteddialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                                withdrawcompleteddialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                withdrawcompleteddialog.setCancelable(false);
                                withdrawcompleteddialog.show();
                                Button donebutton = withdrawcompleteddialog.findViewById(R.id.donebutton);
                                donebutton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Withdraw.this , Monetization.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                            }
                        });
                    }
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

                                // Fetching Total Withdrawn
                                DatabaseReference withdrawnReference = FirebaseDatabase.getInstance().getReference("totalwithdrawn").child(firebaseUser.getUid());
                                withdrawnReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int withdrawnviews = 0;
                                        if (snapshot.exists())
                                            withdrawnviews = Integer.parseInt(String.valueOf(snapshot.getValue()));

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