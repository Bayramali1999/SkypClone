package com.example.skypclone.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skypclone.R;
import com.example.skypclone.model.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationActivity extends AppCompatActivity {

    private DatabaseReference friendRef, contactRef, userRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    private RecyclerView notificationRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        friendRef = FirebaseDatabase.getInstance().getReference().child("Friend Req");
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        notificationRv = findViewById(R.id.notification_rv);
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(friendRef.child(currentUserId), Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, NotificationVH> adapter = new FirebaseRecyclerAdapter<Contacts, NotificationVH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotificationVH holder, int position, @NonNull Contacts model) {
                holder.acceptBtn.setVisibility(View.VISIBLE);

                final String listUserKey = getRef(position).getKey();
                DatabaseReference reqRef = getRef(position).child("req_type").getRef();
                reqRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.getValue().toString().equals("sent")) {

                                holder.cancelBtn.setVisibility(View.VISIBLE);
                                holder.acceptBtn.setVisibility(View.GONE);

                                userRef.child(listUserKey)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    if (snapshot.hasChild("image")) {
                                                        String image = snapshot.child("image").getValue().toString();
                                                        Glide.with(getApplicationContext())
                                                                .load(image)
                                                                .into(holder.imageProfile);
                                                    }
                                                    String name = snapshot.child("name").getValue().toString();
                                                    holder.userName.setText(name);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                            } else {

                                holder.cancelBtn.setVisibility(View.VISIBLE);
                                holder.acceptBtn.setVisibility(View.VISIBLE);
                                userRef.child(listUserKey)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    if (snapshot.hasChild("image")) {
                                                        String image = snapshot.child("image").getValue().toString();
                                                        Glide.with(getApplicationContext())
                                                                .load(image)
                                                                .into(holder.imageProfile);
                                                    }
                                                    String name = snapshot.child("name").getValue().toString();
                                                    holder.userName.setText(name);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        acceptReqFromReceiver(listUserKey);
                                    }
                                });

                            }
                            holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    removeReqFromSender(listUserKey);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public NotificationVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(NotificationActivity.this).inflate(R.layout.find_friend_design, parent, false);
                return new NotificationVH(v);
            }
        };
        notificationRv.setAdapter(adapter);
        adapter.startListening();
    }

    private void acceptReqFromReceiver(String recieverID) {

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

    private void removeReqFromSender(String recieverID) {
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
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    class NotificationVH extends RecyclerView.ViewHolder {

        private ImageView imageProfile;
        private TextView userName;
        private Button acceptBtn, cancelBtn;
        private RelativeLayout cardView;

        public NotificationVH(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.find_fired_img);
            userName = itemView.findViewById(R.id.find_fired_name);
            acceptBtn = itemView.findViewById(R.id.find_fired_accept_btn);
            cancelBtn = itemView.findViewById(R.id.find_fired_cancel_btn);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}