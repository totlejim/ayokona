package com.example.notegonnalie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

public class FolderNotesActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_NOTE = 101;
    private GridLayout folderNotesGrid;
    private SharedPreferences sharedPreferences;
    private String folderName;
    private ImageButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_notes);

        folderNotesGrid = findViewById(R.id.folderNotesGrid);
        addButton = findViewById(R.id.Addstuff);
        sharedPreferences = getSharedPreferences("notes_in_folders", MODE_PRIVATE);

        folderName = getIntent().getStringExtra("folder_name");

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        if (folderName == null || folderName.isEmpty()) {
            finish(); // Exit if no folder name was passed
            return;
        }

        setTitle(folderName);
        loadNotes();

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(FolderNotesActivity.this, HomenotesActivity.class);
            intent.putExtra("select_note_mode", true);
            intent.putExtra("folder_name", folderName);
            startActivityForResult(intent, REQUEST_SELECT_NOTE);
        });
    }

    private void loadNotes() {
        String notesJson = sharedPreferences.getString(folderName, "[]");

        try {
            JSONArray notesArray = new JSONArray(notesJson);
            LayoutInflater inflater = LayoutInflater.from(this);

            folderNotesGrid.removeAllViews(); // Clear before reloading

            for (int i = 0; i < notesArray.length(); i++) {
                String noteHtml = notesArray.getString(i);

                View noteView = inflater.inflate(R.layout.note_item_layout, null);
                TextView noteText = noteView.findViewById(R.id.noteDescription);
                noteText.setText(Html.fromHtml(noteHtml, Html.FROM_HTML_MODE_LEGACY));

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.setMargins(16, 16, 16, 16);
                noteView.setLayoutParams(params);

                folderNotesGrid.addView(noteView);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_NOTE && resultCode == RESULT_OK && data != null) {
            String selectedNoteHtml = data.getStringExtra("selected_note_html");
            if (selectedNoteHtml != null) {
                saveNoteToFolder(selectedNoteHtml);
                loadNotes(); // Refresh UI
            }
        }
    }

    private void saveNoteToFolder(String noteHtml) {
        try {
            JSONArray notesArray = new JSONArray(sharedPreferences.getString(folderName, "[]"));
            notesArray.put(noteHtml);
            sharedPreferences.edit().putString(folderName, notesArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
