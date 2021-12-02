package com.example.skypclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.skypclone.R;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
    }


    class NotificationVH extends RecyclerView.ViewHolder {

        private ImageView imageProfile;
        private TextView userName;
        private Button videoCallBtn;
        private RelativeLayout cardView;

        public NotificationVH(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.contact_image);
            userName = itemView.findViewById(R.id.contact_name);
            videoCallBtn = itemView.findViewById(R.id.contact_name_accept_btn);
            cardView = itemView.findViewById(R.id.card_view_c);
        }
    }
}