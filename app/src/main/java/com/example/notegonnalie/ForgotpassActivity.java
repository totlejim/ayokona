package com.example.notegonnalie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotpassActivity extends AppCompatActivity {

    Button sendcodeButton1;
    Button backloginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView( R.layout.activity_forgotpass);



        sendcodeButton1 = (Button) findViewById(R.id.sendcodeButton1);
        sendcodeButton1.setOnClickListener(new View.OnClickListener() {
                                           public void onClick(View v) {
                                               Intent intent = new Intent(ForgotpassActivity.this, VerificationActivity.class);
                                               startActivity(intent);
                                           }
                                       }
        );

        backloginButton = (Button) findViewById(R.id.backloginButton);
        backloginButton.setOnClickListener(new View.OnClickListener() {
                                           public void onClick(View v) {
                                               Intent intent = new Intent(ForgotpassActivity.this, LoginActivity.class);
                                               startActivity(intent);
                                           }
                                       }
        );
    }
}