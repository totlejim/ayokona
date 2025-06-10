package com.example.notegonnalie;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private Button continueButton2;
    private ImageView backButtonToolBar;
    private TextView tof_ppText;
    private EditText usernameEditText, emailEditText, passwordEditText;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirebaseApp.initializeApp(this); // Initialize Firebase BEFORE using FirebaseAuth
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        firebaseAuth = FirebaseAuth.getInstance();



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // UI references
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        continueButton2 = findViewById(R.id.continueButton2);
        backButtonToolBar = findViewById(R.id.backButtonToolBar);
        tof_ppText = findViewById(R.id.tof_ppText);

        backButtonToolBar.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, WelcomeActivity.class));
            finish();
        });

        tof_ppText.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, TOF_PPActivity.class));
            finish();
        });

        continueButton2.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignupActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 8) {
                Toast.makeText(SignupActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            continueButton2.setEnabled(false); // Prevent double-tap

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignupActivity.this, task -> {
                        continueButton2.setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            sendEmailVerification();
                        } else {
                            Toast.makeText(SignupActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignupActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                        }
                        firebaseAuth.signOut();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        finish();
                    });
        }
    }
}
