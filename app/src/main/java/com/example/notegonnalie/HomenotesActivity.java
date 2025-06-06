package com.example.notegonnalie;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomenotesActivity extends AppCompatActivity {

    LinearLayout linearLayout3, linearLayout4;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homenotes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        linearLayout3 = findViewById(R.id.linearLayout3);
        linearLayout3.setOnClickListener(new View.OnClickListener() {
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(HomenotesActivity.this, newnotesActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
        );

        linearLayout4 = findViewById(R.id.linearLayout4);
        linearLayout4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomenotesActivity.this, FolderspageActivity.class);
                startActivity(intent);
            }
        }
        );

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.notehistoryItem){
            Toast.makeText(this, "View Note History", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.createfolderItem){
            Toast.makeText(this, "Create Folder", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.selectItem){
            Toast.makeText(this, "Select Item", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.searchItem){
            Toast.makeText(this, "Search Item", Toast.LENGTH_SHORT).show();
        }

        return true;
    }
}