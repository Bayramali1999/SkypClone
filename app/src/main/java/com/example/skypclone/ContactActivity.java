package com.example.skypclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.skypclone.activities.FindFriedActivity;
import com.example.skypclone.activities.NotificationActivity;
import com.example.skypclone.activities.RegisterActivity;
import com.example.skypclone.activities.SettingActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skypclone.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ContactActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BottomNavigationView navView;

    private FirebaseAuth mAuth;

    private ImageView findPeopleBtn;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(listener);

        findPeopleBtn = findViewById(R.id.contacts_toolbar_image);
        recyclerView = findViewById(R.id.contacts_rv);
        mAuth = FirebaseAuth.getInstance();

        findPeopleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(ContactActivity.this, FindFriedActivity.class);
                startActivity(settingIntent);

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
}