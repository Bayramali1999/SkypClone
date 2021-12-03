package com.example.skypclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.example.skypclone.activities.CallingActivity;
import com.example.skypclone.activities.FindFriedActivity;
import com.example.skypclone.activities.NotificationActivity;
import com.example.skypclone.activities.RegisterActivity;
import com.example.skypclone.activities.SettingActivity;
import com.example.skypclone.databinding.ActivityMainBinding;
import com.example.skypclone.model.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContactActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BottomNavigationView navView;

    private FirebaseAuth mAuth;
    private String callBy="";
    private ImageView findPeopleBtn;
    private RecyclerView recyclerView;

    private DatabaseReference contactRef, userRef;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(listener);

        findPeopleBtn = findViewById(R.id.contacts_toolbar_image);
        recyclerView = findViewById(R.id.contacts_rv);
        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();

        findPeopleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(ContactActivity.this, FindFriedActivity.class);
                startActivity(settingIntent);

            }
        });

        validateContact();
    }

    private void validateContact() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users")
                .child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Intent intent = new Intent(ContactActivity.this, SettingActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_setting:
                    Intent settingIntent = new Intent(ContactActivity.this, SettingActivity.class);
                    startActivity(settingIntent);
                    break;
                case R.id.navigation_home:
                    Intent homeIntent = new Intent(ContactActivity.this, ContactActivity.class);
                    startActivity(homeIntent);
                    break;
                case R.id.navigation_notifications:
                    Intent nIntent = new Intent(ContactActivity.this, NotificationActivity.class);
                    startActivity(nIntent);
                    break;

                case R.id.navigation_logout:
                    mAuth.signOut();
                    Intent rIntent = new Intent(ContactActivity.this, RegisterActivity.class);
                    startActivity(rIntent);
                    finish();
                    break;

            }
            return false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        checkForReceiving();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactRef.child(currentUserId), Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, ContactVh> adapter = new FirebaseRecyclerAdapter<Contacts, ContactVh>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactVh holder, int position, @NonNull Contacts model) {
                holder.videoCallBtn.setVisibility(View.VISIBLE);
                String itemKey = getRef(position).getKey();
                holder.videoCallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callingIntent = new Intent(ContactActivity.this, CallingActivity.class);
                        callingIntent.putExtra("calling_user_id", itemKey);
                        startActivity(callingIntent);
                    }
                });
                userRef.child(itemKey)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild("image")) {
                                    String imageUrl = snapshot.child("image").getValue().toString();
                                    Glide.with(getApplicationContext())
                                            .load(imageUrl)
                                            .into(holder.imageProfile);
                                }
                                String name = snapshot.child("name").getValue().toString();
                                holder.userName.setText(name);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

            @NonNull
            @Override
            public ContactVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(ContactActivity.this)
                        .inflate(R.layout.contact_design, parent, false);

                return new ContactVh(v);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void checkForReceiving() {
        userRef.child(currentUserId)
                .child("Ringing")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("ringing")) {
                            callBy = snapshot.child("ringing").getValue().toString();
                            Intent callingIntent = new Intent(ContactActivity.this, CallingActivity.class);
                            callingIntent.putExtra("calling_user_id", callBy);
                            startActivity(callingIntent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    class ContactVh extends RecyclerView.ViewHolder {

        private ImageView imageProfile;
        private TextView userName;
        private Button videoCallBtn;
        private RelativeLayout cardView;

        public ContactVh(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.contact_image);
            userName = itemView.findViewById(R.id.contact_name);
            videoCallBtn = itemView.findViewById(R.id.contact_name_accept_btn);
            cardView = itemView.findViewById(R.id.card_view_c);
        }
    }
}