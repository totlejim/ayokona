package com.example.notegonnalie;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotpassActivity extends AppCompatActivity {

    EditText emailEditText;
    Button sendcodeButton1;
    Button backloginButton;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgotpass);

        firebaseAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email); // Make sure you have this EditText in your layout
        sendcodeButton1 = findViewById(R.id.sendcodeButton1);
        backloginButton = findViewById(R.id.backloginButton);

        sendcodeButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgotpassActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotpassActivity.this, "Reset link sent! Check your email.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ForgotpassActivity.this, LoginActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(ForgotpassActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        backloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotpassActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
