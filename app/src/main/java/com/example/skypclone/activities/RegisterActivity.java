package com.example.skypclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.skypclone.ContactActivity;
import com.example.skypclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private Button continueNextButton;
    private EditText phoneText, codeText;
    private CountryCodePicker ccp;
    private RelativeLayout phoneAuth;

    private String phoneNumber = "", cheker = "";

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private PhoneAuthProvider.ForceResendingToken token;
    private String verificationId;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initialComment();

        continueNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (continueNextButton.getText().toString().equals("Submit") || cheker.equals("Code Sent")) {
                    String code = codeText.getText().toString();
                    if (!code.equals("")) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                        signInWithPhoneAuthCredential(credential);
                        loadingBar.setTitle("Code Sent");
                        loadingBar.setMessage("Place wait");
                        loadingBar.show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Please write code", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    phoneNumber = ccp.getFullNumberWithPlus();

                    Toast.makeText(RegisterActivity.this, phoneNumber, Toast.LENGTH_SHORT).show();

                    if (!phoneNumber.equals("")) {
                        loadingBar.setTitle("Phone Sent");
                        loadingBar.setMessage("Place wait");
                        loadingBar.show();

                        PhoneAuthOptions options =
                                PhoneAuthOptions.newBuilder(mAuth)
                                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(RegisterActivity.this)                 // Activity (for callback binding)
                                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                                        .build();

                        PhoneAuthProvider.verifyPhoneNumber(options);

                    } else {
                        Toast.makeText(RegisterActivity.this, "Please write phone number carrectno", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    private void initialComment() {

        mAuth = FirebaseAuth.getInstance();
        ccp = findViewById(R.id.ccp);
        phoneText = findViewById(R.id.phoneText);

        loadingBar = new ProgressDialog(this);
        continueNextButton = findViewById(R.id.continueNextButton);
        codeText = findViewById(R.id.codeText);

        phoneAuth = findViewById(R.id.phoneAuth);
        ccp.registerCarrierNumberEditText(phoneText);


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);
                loadingBar.dismiss();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingBar.dismiss();

                phoneAuth.setVisibility(View.VISIBLE);
                codeText.setVisibility(View.GONE);
                continueNextButton.setText("Continue");

                Toast.makeText(RegisterActivity.this, "onVerificationFailed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                loadingBar.dismiss();
                continueNextButton.setText("Submit");
                phoneAuth.setVisibility(View.GONE);
                codeText.setVisibility(View.VISIBLE);

                cheker = "Code Sent";
                verificationId = s;
                token = forceResendingToken;
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            goToMain();
                        } else {
                            loadingBar.dismiss();
                        }
                    }
                });
    }

    private void goToMain() {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            goToMain();
        }
    }
}