package com.example.notegonnalie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button continueButton1;
    TextView forgotpassTextView, tof_ppText;
    ImageView backButtonToolBar;
    EditText usernameEditText, passwordEditText;
    CheckBox rememberMeCheckBox;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameEditText = findViewById(R.id.Email);
        passwordEditText = findViewById(R.id.Password);
        continueButton1 = findViewById(R.id.continueButton1);
        forgotpassTextView = findViewById(R.id.forgotpassTextView);
        tof_ppText = findViewById(R.id.tof_ppText);
        backButtonToolBar = findViewById(R.id.backButtonToolBar);
        rememberMeCheckBox = findViewById(R.id.checkboxRememberMe);
        final ImageView togglePassword = findViewById(R.id.togglePasswordVisibility);

        // Load saved credentials
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean rememberMe = prefs.getBoolean("rememberMe", false);

        if (rememberMe) {
            String savedEmail = prefs.getString("email", "");
            String savedPassword = prefs.getString("password", "");
            usernameEditText.setText(savedEmail);
            passwordEditText.setText(savedPassword);
            rememberMeCheckBox.setChecked(true);

            firebaseAuth.signInWithEmailAndPassword(savedEmail, savedPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                startActivity(new Intent(LoginActivity.this, HomenotesActivity.class));
                                finish();
                            }
                        }
                    });
        }

        final boolean[] isPasswordVisible = {false};
        togglePassword.setOnClickListener(v -> {
            if (isPasswordVisible[0]) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.baseline_remove_red_eye_24);
                isPasswordVisible[0] = false;
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePassword.setImageResource(R.drawable.baseline_visibility_off_24);
                isPasswordVisible[0] = true;
            }
            passwordEditText.setSelection(passwordEditText.length());
        });

        continueButton1.setOnClickListener(v -> {
            String email = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                usernameEditText.setError("Email is required");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                passwordEditText.setError("Password is required");
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                user.reload().addOnCompleteListener(reloadTask -> {
                                    if (user.isEmailVerified()) {
                                        if (rememberMeCheckBox.isChecked()) {
                                            getSharedPreferences("loginPrefs", MODE_PRIVATE)
                                                    .edit()
                                                    .putString("email", email)
                                                    .putString("password", password)
                                                    .putBoolean("rememberMe", true)
                                                    .apply();
                                        } else {
                                            getSharedPreferences("loginPrefs", MODE_PRIVATE)
                                                    .edit()
                                                    .clear()
                                                    .apply();
                                        }
                                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, HomenotesActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Email not verified. Check your inbox.", Toast.LENGTH_LONG).show();
                                        firebaseAuth.signOut();
                                    }
                                });
                            }
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Log.e("AUTH_ERROR", "Login failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Login failed: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        //forgoetpassword
        forgotpassTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotpassActivity.class);
            startActivity(intent);
        });

        backButtonToolBar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });

        tof_ppText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, TOF_PPActivity.class);
            startActivity(intent);
        });
    }
}
