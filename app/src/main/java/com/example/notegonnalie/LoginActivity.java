package com.example.notegonnalie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    Button continueButton1;
    View forgotpasswordView;

    TextView forgotpassTextView, tof_ppText;

    ImageView backButtonToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        continueButton1 = (Button) findViewById(R.id.continueButton1);
        continueButton1.setOnClickListener(new View.OnClickListener() {
                                           public void onClick(View v) {
                                               Intent intent = new Intent(LoginActivity.this, HomenotesActivity.class);
                                               startActivity(intent);
                                           }
                                       }
        );

        forgotpasswordView = findViewById(R.id.forgotpasswordView);
        forgotpasswordView.setOnClickListener(new View.OnClickListener() {
                                                  public void onClick(View v) {
                                                      Intent intent = new Intent(LoginActivity.this, ForgotpassActivity.class);
                                                      startActivity(intent);
                                                  }
                                              }
        );

        forgotpassTextView = findViewById(R.id.forgotpassTextView);
        forgotpassTextView.setOnClickListener(new View.OnClickListener() {
                                                  public void onClick(View v) {
                                                      Intent intent = new Intent(LoginActivity.this, ForgotpassActivity.class);
                                                      startActivity(intent);
                                                  }
                                              }
        );

        backButtonToolBar = findViewById(R.id.backButtonToolBar);
        backButtonToolBar.setOnClickListener(new View.OnClickListener() {
                                                  public void onClick(View v) {
                                                      Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                                                      startActivity(intent);
                                                  }
                                              }
        );
        tof_ppText = findViewById(R.id.tof_ppText);
        tof_ppText.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {
                                              Intent intent = new Intent(LoginActivity.this, TOF_PPActivity.class);
                                              startActivity(intent);
                                          }
                                      }
        );
    }
}