package com.example.skypclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.skypclone.R;
import com.example.skypclone.model.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FindFriedActivity extends AppCompatActivity {

    private RecyclerView findFiendsList;
    private EditText searchText;
    private DatabaseReference rootRef;
    private String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_fried);
        rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        findFiendsList = findViewById(R.id.find_fired_rv);
        searchText = findViewById(R.id.find_fired_search_text);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchText.getText().toString().equals("")) {
                    Toast.makeText(FindFriedActivity.this, "Please write name", Toast.LENGTH_SHORT).show();
                } else {
                    str = s.toString();
                    onStart();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options = null;

        if (str.equals("")) {
            options = new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(rootRef, Contacts.class)
                    .build();
        } else {
            options = new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(rootRef
                                    .orderByChild("name")
                                    .startAt(str)
                                    .endAt(str + "\uf8ff")
                            , Contacts.class)
                    .build();
        }

        FirebaseRecyclerAdapter<Contacts, FriendVH> adapter = new FirebaseRecyclerAdapter<Contacts, FriendVH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendVH holder, @SuppressLint("RecyclerView") int position, @NonNull Contacts model) {
                holder.userName.setText(model.getName());
                Glide.with(getApplicationContext())
                        .load(model.getImage())
                        .into(holder.imageProfile);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userId = getRef(position).getKey();
                        Intent intent = new Intent(FindFriedActivity.this, ProfileActivity.class);
                        intent.putExtra("visit_user_id", userId);
                        intent.putExtra("visit_user_name", model.getName());
                        intent.putExtra("visit_user_image", model.getImage());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FriendVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_design, parent, false);
                return new FriendVH(v);
            }
        };

        findFiendsList.setAdapter(adapter);
        adapter.startListening();

    }

    class FriendVH extends RecyclerView.ViewHolder {

        private ImageView imageProfile;
        private TextView userName;
        private Button videoCallBtn;
        private RelativeLayout cardView;

        public FriendVH(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.contact_image);
            userName = itemView.findViewById(R.id.contact_name);
            videoCallBtn = itemView.findViewById(R.id.contact_name_accept_btn);
            cardView = itemView.findViewById(R.id.card_view_c);
        }
    }
}