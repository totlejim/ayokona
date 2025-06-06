package com.example.notegonnalie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NewpasssetupActivity extends AppCompatActivity {

    Button backButton2;
    Button updatepassButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_newpasssetup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        updatepassButton = (Button) findViewById(R.id.updatepassButton);
        updatepassButton.setOnClickListener(new View.OnClickListener() {
                                           public void onClick(View v) {
                                               Intent intent = new Intent(NewpasssetupActivity.this, HomenotesActivity.class);
                                               startActivity(intent);
                                           }
                                       }
        );
        backButton2 = (Button) findViewById(R.id.backButton2);
        backButton2.setOnClickListener(new View.OnClickListener() {
                                           public void onClick(View v) {
                                               Intent intent = new Intent(NewpasssetupActivity.this, VerificationActivity.class);
                                               startActivity(intent);
                                           }
                                       }
        );
    }
}