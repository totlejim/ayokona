package com.example.notegonnalie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewNoteActivity extends AppCompatActivity {

    EditText noteTitleEdit, noteContentEdit;
    ImageView backButtonViewNote;
    Button editButton, saveButton;
    SharedPreferences prefs;
    long noteTimestamp = -1;
    String font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.view_notes);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewNoteMainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        noteTitleEdit = findViewById(R.id.noteTitleEdit);
        noteContentEdit = findViewById(R.id.noteContentEdit);
        backButtonViewNote = findViewById(R.id.backButtonViewNote);
        editButton = findViewById(R.id.editButton);
        saveButton = findViewById(R.id.saveButtonViewNote);

        prefs = getSharedPreferences("notesPrefs", MODE_PRIVATE);

        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            font = intent.getStringExtra("font");

            // Get timestamp by matching title
            for (String key : prefs.getAll().keySet()) {
                if (key.startsWith("note_") && key.endsWith("_title")) {
                    if (prefs.getString(key, "").equals(title)) {
                        noteTimestamp = Long.parseLong(key.split("_")[1]);
                        break;
                    }
                }
            }

            noteTitleEdit.setText(title);
            noteContentEdit.setText(fromHtml(content));

            if (font != null && !font.equals("default")) {
                int fontResId = getResources().getIdentifier(font, "font", getPackageName());
                if (fontResId != 0) {
                    Typeface typeface = ResourcesCompat.getFont(this, fontResId);
                    noteTitleEdit.setTypeface(typeface);
                    noteContentEdit.setTypeface(typeface);
                }
            }
        }

        backButtonViewNote.setOnClickListener(v -> finish());

        editButton.setOnClickListener(v -> {
            noteTitleEdit.setEnabled(true);
            noteContentEdit.setEnabled(true);
            editButton.setVisibility( View.GONE);
            saveButton.setVisibility(View.VISIBLE);
        });

        saveButton.setOnClickListener(v -> {
            String newTitle = noteTitleEdit.getText().toString().trim();
            String newContent = Html.toHtml(noteContentEdit.getText(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);

            if (noteTimestamp != -1) {
                prefs.edit()
                        .putString("note_" + noteTimestamp + "_title", newTitle)
                        .putString("note_" + noteTimestamp + "_content", newContent)
                        .apply();

                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
                noteTitleEdit.setEnabled(false);
                noteContentEdit.setEnabled(false);
                saveButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Error: Note not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Spanned fromHtml(String html) {
        return TextUtils.isEmpty(html)
                ? Html.fromHtml("", Html.FROM_HTML_MODE_LEGACY)
                : Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
    }
}
