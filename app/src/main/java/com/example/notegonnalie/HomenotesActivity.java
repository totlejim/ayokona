package com.example.notegonnalie;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

public class HomenotesActivity extends AppCompatActivity {

    LinearLayout linearLayout3, linearLayout4;

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

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        linearLayout3 = findViewById(R.id.linearLayout3);
        linearLayout3.setOnClickListener(v -> {
            Intent intent = new Intent(HomenotesActivity.this, newnotesActivity.class);
            startActivity(intent);
        });

        linearLayout4 = findViewById(R.id.linearLayout4);
        linearLayout4.setOnClickListener(v -> {
            Intent intent = new Intent(HomenotesActivity.this, FolderspageActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.notehistoryItem) {
            Toast.makeText(this, "View Note History", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.createfolderItem) {
            Toast.makeText(this, "Create Folder", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.selectItem) {
            Toast.makeText(this, "Select Item", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.searchItem) {
            Toast.makeText(this, "Search Item", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.signOut) {
            FirebaseAuth.getInstance().signOut();

            getSharedPreferences("loginPrefs", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
