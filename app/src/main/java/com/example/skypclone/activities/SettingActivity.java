package com.example.skypclone.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.skypclone.ContactActivity;
import com.example.skypclone.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {

    private EditText etName, etStatus;
    private ImageView ivProfile;
    private Button saveBtn;

    private Uri uri;
    private StorageReference userRef;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private String downloadUrl;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        progressDialog = new ProgressDialog(this);
        etName = findViewById(R.id.setting_user_name);
        etStatus = findViewById(R.id.setting_user_status);
        ivProfile = findViewById(R.id.setting_profile_image);
        saveBtn = findViewById(R.id.setting_user_button);
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseStorage.getInstance().getReference().child("Profile Image");
        rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, 321);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = etName.getText().toString();
                final String userStatus = etStatus.getText().toString();

                if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userStatus)) {
                    progressDialog.setTitle("Save Date");
                    progressDialog.setMessage("Please wait data is saving");
                    progressDialog.show();
                    if (uri == null) {
                        rootRef.child(mAuth.getCurrentUser().getUid())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            if (snapshot.hasChild("image")) {
                                                saveDataWithoutImage();
                                            } else {
                                                Toast.makeText(SettingActivity.this, "Please select your image", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        progressDialog.dismiss();
                                    }
                                });
                    } else {
                        final StorageReference filePath = userRef.child(mAuth.getCurrentUser().getUid());
                        UploadTask uploadTask = filePath.putFile(uri);
                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SettingActivity.this, "Task does not uploaded", Toast.LENGTH_SHORT).show();
                                }
                                downloadUrl = filePath.getDownloadUrl().toString();
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    downloadUrl = task.getResult().toString();
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("uid", mAuth.getCurrentUser().getUid());
                                    hashMap.put("image", downloadUrl);
                                    hashMap.put("name", userName);
                                    hashMap.put("status", userStatus);

                                    rootRef.child(mAuth.getCurrentUser().getUid())
                                            .updateChildren(hashMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        Intent contact = new Intent(SettingActivity.this, ContactActivity.class);
                                                        startActivity(contact);
                                                        finish();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(SettingActivity.this, "Check your input", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        retrieveData();
    }

    private void retrieveData() {
        rootRef.child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.hasChild("image")) {
                                String imageUrl = snapshot.child("image").getValue().toString();
                                String name = snapshot.child("name").getValue().toString();
                                String status = snapshot.child("status").getValue().toString();
                                etName.setText(name);
                                etStatus.setText(status);
                                Glide.with(getApplicationContext())
                                        .load(imageUrl)
                                        .into(ivProfile);

                            } else {
                                String name = snapshot.child("name").getValue().toString();
                                String status = snapshot.child("status").getValue().toString();
                                etName.setText(name);
                                etStatus.setText(status);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void saveDataWithoutImage() {
        final String userName = etName.getText().toString();
        final String userStatus = etStatus.getText().toString();

        if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(userStatus)) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid", mAuth.getCurrentUser().getUid());
            hashMap.put("name", userName);
            hashMap.put("status", userStatus);

            rootRef.child(mAuth.getCurrentUser().getUid())
                    .updateChildren(hashMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Intent contact = new Intent(SettingActivity.this, ContactActivity.class);
                                startActivity(contact);
                                finish();
                            }
                        }
                    });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 321 && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            ivProfile.setImageURI(uri);
        }
    }
}