package com.example.notegonnalie;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Stack;

public class newnotesActivity extends AppCompatActivity {

    ImageView backButtonToolBar;
    EditText editText;
    ImageButton boldformatbutton, italicformatButton, underlineformatButton, redoButton, undoButton, theme;
    String[] fontNames = {"arimoregular", "comicsans", "ubuntubold"};

    Button saveButton;

    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private String lastText = "";
    private boolean isProgrammaticChange = false;

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

        // Prompt password if already locked
        String savedPassword = getSharedPreferences("NotePrefs", MODE_PRIVATE)
                .getString("note_password", null);
        if (savedPassword != null) {
            promptPassword(savedPassword);
        }

        // Lock Button
        ImageButton lockButton = findViewById(R.id.Lock);
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLockDialog();
            }
        });

        ImageButton deleteButton = findViewById(R.id.Delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmation();
            }
        });


        backButtonToolBar = findViewById(R.id.backButtonToolBar);
        backButtonToolBar.setOnClickListener(v -> {
            Intent intent = new Intent(newnotesActivity.this, HomenotesActivity.class);
            startActivity(intent);
        });

        ImageButton fontButton = findViewById(R.id.fontstyleButton);
        TextView sampleText = findViewById(R.id.notetitleEditText);
        TextView sampleText2 = findViewById(R.id.notecontentEditText);

        editText = findViewById(R.id.notecontentEditText);
        boldformatbutton = findViewById(R.id.boldformatButton);
        italicformatButton = findViewById(R.id.italicformatButton);
        underlineformatButton = findViewById(R.id.underlineformatButton);
        saveButton = findViewById(R.id.saveButton);

        boldformatbutton.setOnClickListener(v -> applyStyle(Typeface.BOLD));
        italicformatButton.setOnClickListener(v -> applyStyle(Typeface.ITALIC));
        underlineformatButton.setOnClickListener(v -> applyUnderline());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isProgrammaticChange) {
                    undoStack.push(s.toString());
                }
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isProgrammaticChange) {
                    lastText = s.toString();
                    redoStack.clear();
                }
            }
        });

        undoButton = findViewById(R.id.undoButton);
        redoButton = findViewById(R.id.redoButton);

        undoButton.setOnClickListener(v -> {
            if (!undoStack.isEmpty()) {
                String previousText = undoStack.pop();
                redoStack.push(editText.getText().toString());
                setTextProgrammatically(previousText);
            }
        });

        redoButton.setOnClickListener(v -> {
            if (!redoStack.isEmpty()) {
                String nextText = redoStack.pop();
                undoStack.push(editText.getText().toString());
                setTextProgrammatically(nextText);
            }
        });

        fontButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(newnotesActivity.this);
            builder.setTitle("Choose a Font");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(newnotesActivity.this,
                    android.R.layout.simple_list_item_1, fontNames);

            builder.setAdapter(adapter, (dialog, which) -> {
                String selectedFont = fontNames[which];
                int fontResId = getResources().getIdentifier(selectedFont, "font", getPackageName());
                sampleText.setTypeface(ResourcesCompat.getFont(newnotesActivity.this, fontResId));
                sampleText2.setTypeface(ResourcesCompat.getFont(newnotesActivity.this, fontResId));
            });

            builder.show();
        });
    }

    private void setTextProgrammatically(String text) {
        isProgrammaticChange = true;
        editText.setText(text);
        editText.setSelection(text.length());
        isProgrammaticChange = false;
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

    // Toolbar menu still inflates, but securityItem is ignored
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.deleteItem) {
            Toast.makeText(this, "Delete Item", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.themeItem) {
            Toast.makeText(this, "Theme Item", Toast.LENGTH_SHORT).show();
        }
        return true;
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
                getSharedPreferences("NotePrefs", MODE_PRIVATE)
                        .edit()
                        .putString("note_password", password)
                        .apply();
                Toast.makeText(this, "Note Locked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void promptPassword(String correctPassword) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Password");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        builder.setCancelable(false);

        builder.setPositiveButton("Unlock", (dialog, which) -> {
            String entered = input.getText().toString();
            if (!entered.equals(correctPassword)) {
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
                finish(); // Exit the activity
            }
        });

        builder.show();
    }
    private void showDeleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Clear the content
                    EditText titleEdit = findViewById(R.id.notetitleEditText);
                    EditText contentEdit = findViewById(R.id.notecontentEditText);
                    titleEdit.setText("");
                    contentEdit.setText("");
                    Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}
