package com.example.skypclone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.skypclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CallingActivity extends AppCompatActivity {

    private ImageView userImageBg, makeCallBtn, cancelCallBtn;
    private TextView tvName;

    private DatabaseReference userRef;

    private String senderName = "", senderImageUrl = "",
            senderUserId, receiverName = "", receiverImageUrl = "",
            receiverUserId, callBy = "",
            cheker = "", callingId = "", ringingID = "";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        receiverUserId = getIntent().getExtras().get("calling_user_id").toString();

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
        userImageBg = findViewById(R.id.calling_receiver_img);
        makeCallBtn = findViewById(R.id.make_call);
        cancelCallBtn = findViewById(R.id.cancel_call);
        tvName = findViewById(R.id.calling_user_name);


        getAndSetProfileInfo();


        cancelCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cheker = "clicked";

                userRef.child(senderUserId)
                        .child("Calling")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists() && snapshot.hasChild("calling")) {
                                    callingId = snapshot.child("calling").getValue().toString();
                                    userRef.child(callingId)
                                            .child("Ringing")
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    userRef.child(senderUserId).
                                                            child("Calling")
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    startActivity(new Intent(CallingActivity.this, RegisterActivity.class));

                                                                    finish();
                                                                }
                                                            });
                                                }
                                            });
                                } else {
                                    startActivity(new Intent(CallingActivity.this, RegisterActivity.class));

                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                userRef.child(senderUserId)
                        .child("Ringing")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists() && snapshot.hasChild("ringing")) {
                                    ringingID = snapshot.child("ringing").getValue().toString();
                                    userRef.child(ringingID)
                                            .child("Calling")
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    userRef.child(senderUserId).
                                                            child("Ringing")
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                   startActivity(new Intent(CallingActivity.this, RegisterActivity.class));
                                                                    finish();
                                                                }
                                                            });
                                                }
                                            });
                                } else {
                                    startActivity(new Intent(CallingActivity.this, RegisterActivity.class));

                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


            }
        });

    }

    private void getAndSetProfileInfo() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(receiverUserId)) {
                    receiverImageUrl = snapshot.child(receiverUserId).child("image").getValue().toString();
                    Glide.with(getApplicationContext())
                            .load(receiverImageUrl)
                            .into(userImageBg);
                    receiverName = snapshot.child(receiverUserId).child("name").getValue().toString();
                    tvName.setText(receiverName);
                }
                if (snapshot.hasChild(senderUserId)) {
                    senderImageUrl = snapshot.child(senderUserId).child("image").getValue().toString();
                    senderName = snapshot.child(senderUserId).child("name").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        userRef.child(receiverUserId)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!cheker.equals("clicked") && !snapshot.hasChild("Calling") && !snapshot.hasChild("Ringing")) {

                                    Log.e("TAG", "onDataChange: Ringing");

                                    final HashMap<String, Object> callingMap = new HashMap<>();

                                    callingMap.put("calling", receiverUserId);

                                    userRef.child(senderUserId)
                                            .child("Calling")
                                            .updateChildren(callingMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        HashMap<String, Object> ringingMap = new HashMap<>();
                                                        ringingMap.put("ringing", senderUserId);
                                                        userRef.child(receiverUserId)
                                                                .child("Ringing")
                                                                .updateChildren(ringingMap);

                                                    }
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(senderUserId).hasChild("Ringing") && !snapshot.child(senderUserId).hasChild("Calling")) {
                    makeCallBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}