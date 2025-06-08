package com.example.notegonnalie;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseApp;

public class SignupActivity extends AppCompatActivity {

    Button continueButton2;
    ImageView backButtonToolBar;
    TextView tof_ppText;

    EditText usernameEditText, emailEditText, passwordEditText;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);

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

            if (TextUtils.isEmpty(username)) {
                usernameEditText.setError("Username is required");
                return;
            }
// Inside continueButton2.setOnClickListener
            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Email is required");
                return;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Please enter a valid email address");
                return;
            }

            continueButton2.setEnabled( false );
            Toast.makeText(SignupActivity.this, "All fields are filled", Toast.LENGTH_SHORT).show();

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    continueButton2.setEnabled( true );
                    if (task.isSuccessful()) {
                        Toast.makeText( SignupActivity.this, "Registration Successful", Toast.LENGTH_SHORT ).show();
                        sendEmailVerification();
                    } else {
                        Toast.makeText( SignupActivity.this, "Registration Failed", Toast.LENGTH_SHORT ).show();
                    }
                }
            });
        });
    }


    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(SignupActivity.this, "Verification Email Has Been Sent", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                }
            });
        } else {
            Toast.makeText(SignupActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
        }
    }
}
