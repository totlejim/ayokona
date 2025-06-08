package com.example.notegonnalie;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    EditText usernameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        EdgeToEdge.enable( this );
        setContentView( R.layout.activity_login );


        usernameEditText = findViewById( R.id.username );
        passwordEditText = findViewById( R.id.password );
        continueButton1 = findViewById( R.id.continueButton1 );

        continueButton1.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty( username )) {
                    usernameEditText.setError( "Username is required" );
                    return;
                }

                if (TextUtils.isEmpty( password )) {
                    passwordEditText.setError( "Password is required" );
                    return;
                }

                // Both fields are filled
                Toast.makeText( LoginActivity.this, "All fields are filled", Toast.LENGTH_SHORT ).show();

                Intent intent = new Intent( LoginActivity.this, HomenotesActivity.class );
                startActivity( intent );
            }
        } );


        forgotpassTextView = findViewById( R.id.forgotpassTextView );
        forgotpassTextView.setOnClickListener( new View.OnClickListener() {
                                                   public void onClick(View v) {
                                                       Intent intent = new Intent( LoginActivity.this, ForgotpassActivity.class );
                                                       startActivity( intent );
                                                   }
                                               }
        );

        backButtonToolBar = findViewById( R.id.backButtonToolBar );
        backButtonToolBar.setOnClickListener( new View.OnClickListener() {
                                                  public void onClick(View v) {
                                                      Intent intent = new Intent( LoginActivity.this, WelcomeActivity.class );
                                                      startActivity( intent );
                                                      finish();
                                                  }
                                              }
        );
        tof_ppText = findViewById( R.id.tof_ppText );
        tof_ppText.setOnClickListener( new View.OnClickListener() {
                                           public void onClick(View v) {
                                               Intent intent = new Intent( LoginActivity.this, TOF_PPActivity.class );
                                               startActivity( intent );
                                           }
                                       }
        );
    }
}
