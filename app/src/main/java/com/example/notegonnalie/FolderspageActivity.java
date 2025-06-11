package com.example.notegonnalie;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FolderspageActivity extends AppCompatActivity {

    ImageView logoToolBar;
    RelativeLayout layout2, layout3, layout4;
    int folderCount = 4; // Already 4 folders exist

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_folderspage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        logoToolBar = findViewById(R.id.logoToolBar);
        layout2 = findViewById(R.id.relativeLayout2);
        layout3 = findViewById(R.id.relativeLayout3);
        layout4 = findViewById(R.id.relativeLayout4);

        logoToolBar.setOnClickListener(v -> {
            Intent intent = new Intent(FolderspageActivity.this, HomenotesActivity.class);
            startActivity(intent);
        });

        // Initial folders
        addFolderToLayout(layout3, "Folder 1");
        addFolderToLayout(layout3, "Folder 2");
        addFolderToLayout(layout4, "Folder 3");
        addFolderToLayout(layout4, "Folder 4");

        layout2.setOnClickListener(v -> showCreateFolderDialog());
    }

    private void showCreateFolderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Folder");

        final EditText input = new EditText(this);
        input.setHint("Enter folder name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String folderName = input.getText().toString().trim();
            if (!folderName.isEmpty()) {
                folderCount++;
                if (layout3.getChildCount() < 2) {
                    addFolderToLayout(layout3, folderName);
                } else {
                    addFolderToLayout(layout4, folderName);
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void addFolderToLayout(RelativeLayout parentLayout, String folderName) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View folderView = inflater.inflate(R.layout.folder_item, parentLayout, false);
        TextView folderTextView = folderView.findViewById(R.id.folderName);
        folderTextView.setText(folderName);

        ImageView folderIcon = folderView.findViewById(R.id.folderIcon);
        folderIcon.setImageResource(R.drawable.folder); // Change to your actual icon

        folderView.setOnClickListener(v -> {
            Intent intent = new Intent(FolderspageActivity.this, newnotesActivity.class);
            intent.putExtra("folder_name", folderName);
            startActivity(intent);
        });

        parentLayout.addView(folderView);
    }
}
