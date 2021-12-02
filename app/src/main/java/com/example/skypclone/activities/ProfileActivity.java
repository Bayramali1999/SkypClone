package com.example.skypclone.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class ProfileActivity extends AppCompatActivity {

    private String recieverID, userName, userImage;
    private ImageView imageView;
    private TextView userText;
    private Button addFriend, cancelFriend;

    private FirebaseAuth mAuth;
    private String currentUserId;
    private String userState = "new";

    private DatabaseReference friendRef, contactRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        friendRef = FirebaseDatabase.getInstance().getReference().child("Friend Req");
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        recieverID = getIntent().getExtras().get("visit_user_id").toString();
        userName = getIntent().getExtras().get("visit_user_name").toString();
        userImage = getIntent().getExtras().get("visit_user_image").toString();


        imageView = findViewById(R.id.profile_image);
        userText = findViewById(R.id.profile_user_name);
        addFriend = findViewById(R.id.profile_add_friend);
        cancelFriend = findViewById(R.id.profile_remove_friend);


        Glide.with(this)
                .load(userImage)
                .into(imageView);

        userText.setText(userName);


        manageSentRequest();

        cancelFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactRef.child(currentUserId)
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    contactRef.child(recieverID)
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    cancelFriend.setVisibility(View.GONE);
                                                    addFriend.setVisibility(View.VISIBLE);
                                                    addFriend.setText("Add Friend");
                                                    userState = "new";
                                                }
                                            });

                                }
                            }
                        });
            }
        });
    }

    private void manageSentRequest() {

        friendRef.child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(recieverID)) {
                            String reqType = snapshot.child(recieverID).child("req_type").getValue().toString();

                            if (reqType.equals("sent")) {
                                userState = "req_sent";
                                cancelFriend.setVisibility(View.GONE);
                                addFriend.setText("Remove Request");

                            }
                            if (reqType.equals("receive")) {
                                cancelFriend.setVisibility(View.GONE);
                                addFriend.setText("Accept Request");
                                userState = "req_res";

                            } else {

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        contactRef.child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            userState = "friend";
                            cancelFriend.setVisibility(View.VISIBLE);
                            addFriend.setVisibility(View.GONE);
//;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        if (recieverID.equals(currentUserId)) {
            addFriend.setVisibility(View.GONE);
            cancelFriend.setVisibility(View.GONE);
        } else {
            addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (userState.equals("new")) {
                        cancelFriend.setVisibility(View.GONE);
                        addFriend.setVisibility(View.VISIBLE);
                        addFriend.setText("Send Request");
                        sentReqUser();
                    }
                    if (userState.equals("req_sent")) {
                        cancelFriend.setVisibility(View.GONE);
                        addFriend.setVisibility(View.VISIBLE);
                        removeReqFromSender();
                    }
                    if (userState.equals("req_res")) {
                        addFriend.setText("Accept Request");
                        cancelFriend.setVisibility(View.GONE);
                        acceptReqFromReceiver();
                    }
                    if (userState.equals("friend")) {
//                        removeReqFromSender();
                    }
                }
            });
        }
    }

    private void acceptReqFromReceiver() {

        contactRef.child(currentUserId)
                .child(recieverID)
                .child("contact")
                .setValue("Save")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        contactRef.child(recieverID)
                                .child(currentUserId)
                                .child("contact")
                                .setValue("Save")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        friendRef.child(currentUserId)
                                                .child(recieverID)
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            friendRef.child(recieverID)
                                                                    .child(currentUserId)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                addFriend.setVisibility(View.GONE);
                                                                                cancelFriend.setVisibility(View.VISIBLE);
                                                                                userState = "friend";
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });

                                    }
                                });
                    }
                });
    }

    private void removeReqFromSender() {
        friendRef.child(currentUserId)
                .child(recieverID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendRef.child(recieverID)
                                    .child(currentUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                userState = "new";
                                                addFriend.setText("Add Friend");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void sentReqUser() {
        friendRef.child(currentUserId)
                .child(recieverID)
                .child("req_type")
                .setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            friendRef.child(recieverID)
                                    .child(currentUserId)
                                    .child("req_type")
                                    .setValue("receive")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            userState = "req_sent";
                                            addFriend.setText("Remove Request");
                                            cancelFriend.setVisibility(View.GONE);
                                        }
                                    });


                        }
                    }
                });
    }
}