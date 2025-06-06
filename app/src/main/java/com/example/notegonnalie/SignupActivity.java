package com.example.notegonnalie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupActivity extends AppCompatActivity {

    Button continueButton2;

    ImageView backButtonToolBar;

    TextView tof_ppText;
    Switch darkmode; // This is your night mode switch
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private static final String NIGHT_MODE_KEY = "night";

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

        darkmode = findViewById(R.id.toolbar3 ); // Make sure R.id.toolbar3 is the ID of your Switch
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean(NIGHT_MODE_KEY, false); // Use the constant key

        if (nightMode) {
            darkmode.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            // Optional: Explicitly set to NO if not nightMode, though default is usually NO
            darkmode.setChecked(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        darkmode.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = sharedPreferences.edit();
                if(darkmode.isChecked()){ // Check the current state of the switch
                    AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_YES );
                    editor.putBoolean( NIGHT_MODE_KEY, true ); // Save true for night mode
                    nightMode = true; // Update the local variable
                } else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean( NIGHT_MODE_KEY, false ); // Save false for day mode
                    nightMode = false; // Update the local variable
                }
                editor.apply();
                recreate(); // Recreate the activity to apply the new theme
            }
        } );


        continueButton2 = (Button) findViewById(R.id.continueButton2);
        continueButton2.setOnClickListener(new View.OnClickListener() {
                                           public void onClick(View v) {
                                               Intent intent = new Intent(SignupActivity.this, HomenotesActivity.class);
                                               startActivity(intent);
                                           }
                                       }
        );

        backButtonToolBar = findViewById(R.id.backButtonToolBar);
        backButtonToolBar.setOnClickListener(new View.OnClickListener() {
                                                 public void onClick(View v) {
                                                     Intent intent = new Intent(SignupActivity.this, WelcomeActivity.class);
                                                     startActivity(intent);
                                                 }
                                             }
        );

        tof_ppText = findViewById(R.id.tof_ppText);
        tof_ppText.setOnClickListener(new View.OnClickListener() {
                                                 public void onClick(View v) {
                                                     Intent intent = new Intent(SignupActivity.this, TOF_PPActivity.class);
                                                     startActivity(intent);
                                                 }
                                             }
        );
    }
}