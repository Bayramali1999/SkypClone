package com.example.skypclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.skypclone.R;
import com.google.firebase.FirebaseOptions;

public class FindFriedActivity extends AppCompatActivity {

    private RecyclerView findFiendsList;
    private EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_fried);

        findFiendsList = findViewById(R.id.find_fired_rv);
        searchText = findViewById(R.id.find_fired_search_text);
    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    class FriendVH extends RecyclerView.ViewHolder {

        private ImageView imageProfile;
        private TextView userName;
        private Button acceptBtn, cancelBtn;
        private RelativeLayout cardView;

        public FriendVH(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.find_fired_img);
            userName = itemView.findViewById(R.id.find_fired_name);
            acceptBtn = itemView.findViewById(R.id.find_fired_accept_btn);
            cancelBtn = itemView.findViewById(R.id.find_fired_cancel_btn);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}