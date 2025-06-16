package com.example.notegonnalie;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;

public class FolderspageActivity extends AppCompatActivity {

    ImageView logoToolBar;
    View layout2;
    GridLayout layout3, layout4;
    SharedPreferences sharedPreferences;
    final String FOLDER_PREF = "folders_data";

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

        sharedPreferences = getSharedPreferences(FOLDER_PREF, MODE_PRIVATE);

        logoToolBar.setOnClickListener(v -> {
            Intent intent = new Intent(FolderspageActivity.this, HomenotesActivity.class);
            startActivity(intent);
        });

        layout2.setOnClickListener(v -> showCreateFolderDialog());

        loadFolders();
    }

    private void showCreateFolderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Folder");

        final EditText input = new EditText(this);
        input.setHint("Enter folder name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String folderName = input.getText().toString().trim();
            if (!TextUtils.isEmpty(folderName)) {
                saveFolder(folderName);
                addFolderToLayout(folderName);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void addFolderToLayout(String folderName) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View folderView = inflater.inflate(R.layout.folder_item, null);

        TextView folderTextView = folderView.findViewById(R.id.folderName);
        folderTextView.setText(folderName);

        ImageView folderIcon = folderView.findViewById(R.id.folderIcon);
        folderIcon.setImageResource(R.drawable.folder);

        folderView.setOnClickListener(v -> {
            Intent intent = new Intent(FolderspageActivity.this, FolderNotesActivity.class);
            intent.putExtra("folder_name", folderName);
            startActivity(intent);
        });

        folderView.setOnLongClickListener(v -> {
            showFolderOptions(folderName);
            return true;
        });

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(8, 8, 8, 8);
        folderView.setLayoutParams(params);

        if (layout3.getChildCount() < 4) {
            layout3.addView(folderView);
        } else {
            layout4.addView(folderView);
        }
    }

    private void showFolderOptions(String folderName) {
        String[] options = {"Rename", "Delete"};

        new AlertDialog.Builder(this)
                .setTitle(folderName)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showRenameDialog(folderName);
                    } else {
                        deleteFolder(folderName);
                    }
                })
                .show();
    }

    private void showRenameDialog(String oldName) {
        EditText input = new EditText(this);
        input.setHint("New folder name");

        new AlertDialog.Builder(this)
                .setTitle("Rename Folder")
                .setView(input)
                .setPositiveButton("Rename", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(newName)) {
                        renameFolder(oldName, newName);
                        recreate(); // refresh UI
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteFolder(String folderName) {
        try {
            JSONArray folders = getFolderArray();
            JSONArray updated = new JSONArray();
            for (int i = 0; i < folders.length(); i++) {
                if (!folders.getString(i).equals(folderName)) {
                    updated.put(folders.getString(i));
                }
            }
            sharedPreferences.edit().putString("folders", updated.toString()).apply();
            recreate(); // refresh UI
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void renameFolder(String oldName, String newName) {
        try {
            JSONArray folders = getFolderArray();
            JSONArray updated = new JSONArray();
            for (int i = 0; i < folders.length(); i++) {
                String current = folders.getString(i);
                updated.put(current.equals(oldName) ? newName : current);
            }
            sharedPreferences.edit().putString("folders", updated.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveFolder(String folderName) {
        try {
            JSONArray folderArray = getFolderArray();
            folderArray.put(folderName);
            sharedPreferences.edit().putString("folders", folderArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONArray getFolderArray() throws JSONException {
        String folders = sharedPreferences.getString("folders", "[]");
        return new JSONArray(folders);
    }

    private void loadFolders() {
        try {
            JSONArray folderArray = getFolderArray();
            for (int i = 0; i < folderArray.length(); i++) {
                addFolderToLayout(folderArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
