package com.example.notegonnalie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class newnotesActivity extends AppCompatActivity {

    EditText titleEditText, editText;
    ImageButton boldformatbutton, italicformatButton, underlineformatButton, redoButton, undoButton;
    Button saveButton;
    ImageView backButtonToolBar;

    private final Stack<String> undoStack = new Stack<>();
    private final Stack<String> redoStack = new Stack<>();
    private boolean isProgrammaticChange = false;
    private String selectedFont = "default";
    private boolean isNoteLocked = false;
    private String notePassword = "";

    String[] fontNames = {"arimoregular", "comicsans", "ubuntubold"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_newnotes);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleEditText = findViewById(R.id.notetitleEditText);
        editText = findViewById(R.id.notecontentEditText);
        boldformatbutton = findViewById(R.id.boldformatButton);
        italicformatButton = findViewById(R.id.italicformatButton);
        underlineformatButton = findViewById(R.id.underlineformatButton);
        undoButton = findViewById(R.id.undoButton);
        redoButton = findViewById(R.id.redoButton);
        saveButton = findViewById(R.id.saveButton);
        ImageButton fontButton = findViewById(R.id.fontstyleButton);
        ImageButton lockButton = findViewById(R.id.Lock);
        ImageButton deleteButton = findViewById(R.id.Delete);
        backButtonToolBar = findViewById(R.id.backButtonToolBar);

        backButtonToolBar.setOnClickListener(v -> {
            if (!titleEditText.getText().toString().isEmpty() || !editText.getText().toString().isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("Discard changes?")
                        .setMessage("You have unsaved changes. Are you sure you want to leave?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            startActivity(new Intent(this, HomenotesActivity.class));
                            finish();
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                startActivity(new Intent(this, HomenotesActivity.class));
                finish();
            }
        });

        boldformatbutton.setOnClickListener(v -> applyStyle(Typeface.BOLD));
        italicformatButton.setOnClickListener(v -> applyStyle(Typeface.ITALIC));
        underlineformatButton.setOnClickListener(v -> applyUnderline());

        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isProgrammaticChange) {
                    undoStack.push(Html.toHtml(new SpannableStringBuilder(s), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL));
                }
            }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (!isProgrammaticChange) redoStack.clear();
            }
        });

        undoButton.setOnClickListener(v -> {
            if (!undoStack.isEmpty()) {
                redoStack.push(Html.toHtml(editText.getText(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL));
                setTextProgrammatically(undoStack.pop());
            }
        });

        redoButton.setOnClickListener(v -> {
            if (!redoStack.isEmpty()) {
                undoStack.push(Html.toHtml(editText.getText(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL));
                setTextProgrammatically(redoStack.pop());
            }
        });

        fontButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose a Font");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fontNames);
            builder.setAdapter(adapter, (dialog, which) -> {
                selectedFont = fontNames[which];
                int fontResId = getResources().getIdentifier(selectedFont, "font", getPackageName());
                Typeface font = ResourcesCompat.getFont(this, fontResId);
                titleEditText.setTypeface(font);
                editText.setTypeface(font);
            });
            builder.show();
        });

        lockButton.setOnClickListener(v -> showLockDialog());

        deleteButton.setOnClickListener(v -> {
            titleEditText.setText("");
            editText.setText("");
            Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
        });

        saveButton.setOnClickListener(v -> {
            String yourTitle = titleEditText.getText().toString().trim();
            Spannable yourSpannedContent = editText.getText();
            String yourStyledHtmlText = Html.toHtml(yourSpannedContent, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);

            if (yourTitle.isEmpty() && yourStyledHtmlText.trim().isEmpty()) {
                Toast.makeText(this, "Note is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            long timestamp = System.currentTimeMillis();
            SharedPreferences sharedPreferences = getSharedPreferences("notes", Context.MODE_PRIVATE);
            try {
                JSONObject note = new JSONObject();
                note.put("title", yourTitle);
                note.put("content", yourStyledHtmlText);
                note.put("timestamp", timestamp);
                note.put("isDeleted", false);
                note.put("isPinned", false);
                note.put("font", selectedFont);
                note.put("locked", isNoteLocked);
                note.put("password", notePassword);

                JSONArray allNotes = new JSONArray(sharedPreferences.getString("all_notes", "[]"));
                allNotes.put(note);
                sharedPreferences.edit().putString("all_notes", allNotes.toString()).apply();

                String folder = getIntent().getStringExtra("folder_name");
                if (folder != null && !folder.isEmpty()) {
                    JSONArray folderNotes = new JSONArray(sharedPreferences.getString(folder + "_notes", "[]"));
                    folderNotes.put(note);
                    sharedPreferences.edit().putString(folder + "_notes", folderNotes.toString()).apply();
                }

                JSONArray historyNotes = new JSONArray(sharedPreferences.getString("history_notes", "[]"));
                JSONObject historyNote = new JSONObject();
                historyNote.put("title", yourTitle);
                historyNote.put("content", yourStyledHtmlText);
                historyNote.put("timestamp", timestamp);
                historyNote.put("type", "added");
                historyNotes.put(historyNote);
                sharedPreferences.edit().putString("history_notes", historyNotes.toString()).apply();

                saveToHistory(yourTitle, yourStyledHtmlText, timestamp, "added");

                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
                finish();

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyStyle(int style) {
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start < end) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(editText.getText());
            ssb.setSpan(new StyleSpan(style), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            editText.setText(ssb);
            editText.setSelection(end);
        }
    }

    private void applyUnderline() {
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start < end) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(editText.getText());
            ssb.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            editText.setText(ssb);
            editText.setSelection(end);
        }
    }

    private void setTextProgrammatically(String htmlText) {
        isProgrammaticChange = true;
        editText.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));
        editText.setSelection(editText.getText().length());
        isProgrammaticChange = false;
    }

    private void showLockDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Note Password");
        View view = getLayoutInflater().inflate(R.layout.dialog_set_password, null);
        EditText passwordInput = view.findViewById(R.id.passwordEditText);
        Button confirmButton = view.findViewById(R.id.confirmButton);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        confirmButton.setOnClickListener(v -> {
            String password = passwordInput.getText().toString();
            if (!password.isEmpty()) {
                isNoteLocked = true;
                notePassword = password;
                Toast.makeText(this, "Note Locked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu2, menu);
        return true;
    }

    private void saveToHistory(String title, String content, long timestamp, String type) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("title", title);
        noteData.put("content", content);
        noteData.put("timestamp", timestamp);
        noteData.put("font", selectedFont);
        noteData.put("locked", isNoteLocked);
        noteData.put("password", notePassword);
        noteData.put("isDeleted", false);
        noteData.put("isPinned", type.equals("pinned")); // true only when pinning

        // Save to Firestore history
        Map<String, Object> historyEntry = new HashMap<>(noteData);
        historyEntry.put("type", type);

        db.collection("users")
                .document(currentUser.getUid())
                .collection("history")
                .add(historyEntry);

        // Save to notes collection
        if (type.equals("added") || type.equals("pinned")) {
            db.collection("users")
                    .document(currentUser.getUid())
                    .collection("notes")
                    .add(noteData);

            db.collection("users")
                    .document(currentUser.getUid())
                    .collection("recent")
                    .add(noteData);
        }

        // Save to pinned collection
        if (type.equals("pinned")) {
            db.collection("users")
                    .document(currentUser.getUid())
                    .collection("pinned")
                    .add(noteData);
        }
    }


}
