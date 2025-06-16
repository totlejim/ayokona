package com.example.notegonnalie;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class HomenotesActivity extends AppCompatActivity {

    private static final int IMPORT_NOTE_REQUEST_CODE = 999;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    View linearLayout3, linearLayout4;
    GridLayout gridLayoutPinnedNotes, gridLayoutRecentNotes, gridLayoutOtherNotes;
    TextView recentNotesTextView, pinnedNotesTextView;
    MenuItem selectItem;
    ImageView importButton;

    boolean isSelectMode = false;
    boolean selectNoteMode = false;
    Set<String> selectedDocIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homenotes);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not signed in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        linearLayout3 = findViewById(R.id.linearLayout3);
        linearLayout4 = findViewById(R.id.linearLayout4);
        gridLayoutPinnedNotes = findViewById(R.id.gridLayoutPinnedNotes);
        gridLayoutRecentNotes = findViewById(R.id.gridLayoutRecentNotes);
        gridLayoutOtherNotes = findViewById(R.id.gridLayoutOtherNotes);
        recentNotesTextView = findViewById(R.id.recentnotesTextView);
        pinnedNotesTextView = findViewById(R.id.pinnedNotesTextView);
        importButton = findViewById(R.id.importButton);

        linearLayout3.setOnClickListener(v -> startActivity(new Intent(this, newnotesActivity.class)));
        linearLayout4.setOnClickListener(v -> startActivity(new Intent(this, FolderspageActivity.class)));

        importButton.setOnClickListener(v -> openFileChooser());

        selectNoteMode = getIntent().getBooleanExtra("select_note_mode", false);
        loadNotes("");
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("text/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, IMPORT_NOTE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMPORT_NOTE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                importNoteFromUri(uri);
            }
        }
    }

    private void importNoteFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder contentBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }

            reader.close();

            String fileName = "Imported Note";
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    fileName = cursor.getString(nameIndex);
                }
                cursor.close();
            }


            Map<String, Object> note = new HashMap<>();
            note.put("title", fileName);
            note.put("content", contentBuilder.toString());
            note.put("timestamp", System.currentTimeMillis());

            db.collection("users").document(currentUser.getUid())
                    .collection("notes").add(note)
                    .addOnSuccessListener(doc -> {
                        Toast.makeText(this, "Note imported", Toast.LENGTH_SHORT).show();
                        loadNotes("");
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Import failed", Toast.LENGTH_SHORT).show());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to import note", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNotes(String query) {
        gridLayoutPinnedNotes.removeAllViews();
        gridLayoutRecentNotes.removeAllViews();
        gridLayoutOtherNotes.removeAllViews();

        loadPinnedNotes(query);

        db.collection("users")
                .document(currentUser.getUid())
                .collection("notes")
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<DocumentSnapshot> notes = new ArrayList<>();

                    for (DocumentSnapshot doc : snapshots) {
                        if (doc.getBoolean("isDeleted") != null && doc.getBoolean("isDeleted")) continue;

                        String title = doc.getString("title");
                        String content = doc.getString("content");

                        if (!query.isEmpty() && !title.toLowerCase().contains(query.toLowerCase()) &&
                                !content.toLowerCase().contains(query.toLowerCase())) continue;

                        if (!Boolean.TRUE.equals(doc.getBoolean("isPinned"))) {
                            notes.add(doc);
                        }
                    }

                    notes.sort((a, b) -> Long.compare(
                            b.getLong("timestamp") != null ? b.getLong("timestamp") : 0,
                            a.getLong("timestamp") != null ? a.getLong("timestamp") : 0
                    ));

                    for (int i = 0; i < Math.min(10, notes.size()); i++) {
                        addNoteToLayout(notes.get(i), gridLayoutOtherNotes);
                    }

                    loadRecentNotes(query);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load notes", Toast.LENGTH_SHORT).show());
    }

    private void loadPinnedNotes(String query) {
        db.collection("users")
                .document(currentUser.getUid())
                .collection("pinned")
                .get()
                .addOnSuccessListener(snapshots -> {
                    for (DocumentSnapshot doc : snapshots) {
                        String title = doc.getString("title");
                        String content = doc.getString("content");

                        if (!query.isEmpty() && !title.toLowerCase().contains(query.toLowerCase()) &&
                                !content.toLowerCase().contains(query.toLowerCase())) continue;

                        addNoteToLayout(doc, gridLayoutPinnedNotes);
                    }
                });
    }

    private void loadRecentNotes(String query) {
        db.collection("users")
                .document(currentUser.getUid())
                .collection("recent")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        String title = doc.getString("title");
                        String content = doc.getString("content");

                        if (!query.isEmpty() && !title.toLowerCase().contains(query.toLowerCase()) &&
                                !content.toLowerCase().contains(query.toLowerCase())) continue;

                        addNoteToLayout(doc, gridLayoutRecentNotes);
                    }
                });
    }

    private void addNoteToLayout(DocumentSnapshot doc, GridLayout layout) {
        String title = doc.getString("title");
        String content = doc.getString("content");
        String font = doc.getString("font");
        String docId = doc.getId();

        String noteHtml = "<b>" + title + "</b><br>" + content;

        TextView noteView = new TextView(this);
        noteView.setText(Html.fromHtml(noteHtml, Html.FROM_HTML_MODE_LEGACY));
        noteView.setPadding(32, 32, 32, 32);
        noteView.setTextSize(16);
        noteView.setBackgroundResource(R.drawable.custom_edittext);
        noteView.setTag(docId);
        noteView.setClickable(true);
        noteView.setLongClickable(true);

        if (font != null) {
            int fontResId = getResources().getIdentifier(font, "font", getPackageName());
            if (fontResId != 0) {
                noteView.setTypeface(ResourcesCompat.getFont(this, fontResId));
            }
        }

        if (selectNoteMode) {
            noteView.setOnClickListener(v -> {
                Intent result = new Intent();
                result.putExtra("selected_note_html", noteHtml);
                setResult(RESULT_OK, result);
                finish();
            });
        } else {
            noteView.setOnClickListener(v -> {
                if (isSelectMode) toggleSelection(noteView, docId);
                else {
                    Intent intent = new Intent(this, ViewNoteActivity.class);
                    intent.putExtra("title", title);
                    intent.putExtra("content", content);
                    intent.putExtra("font", font);
                    intent.putExtra("timestamp", doc.getLong("timestamp"));
                    startActivity(intent);
                }
            });

            noteView.setOnLongClickListener(v -> {
                if (!isSelectMode) enterSelectMode();
                toggleSelection(noteView, docId);
                return true;
            });
        }

        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.setMargins(16, 16, 16, 16);
        noteView.setLayoutParams(param);
        layout.addView(noteView);
    }

    private void toggleSelection(TextView view, String docId) {
        if (selectedDocIds.contains(docId)) {
            selectedDocIds.remove(docId);
            view.setBackgroundResource(R.drawable.custom_edittext);
        } else {
            selectedDocIds.add(docId);
            view.setBackgroundResource(R.drawable.custom_edittext_selected);
        }

        if (selectedDocIds.isEmpty()) exitSelectMode();
    }

    private void enterSelectMode() {
        isSelectMode = true;
        if (selectItem != null) selectItem.setTitle("Actions");
        showActionDialog();
    }

    private void exitSelectMode() {
        isSelectMode = false;
        selectedDocIds.clear();
        if (selectItem != null) selectItem.setTitle("Select");
        loadNotes("");
    }

    private void showActionDialog() {
        String[] actions = {"Pin", "Delete", "Cancel"};
        new AlertDialog.Builder(this)
                .setTitle("Choose action")
                .setItems(actions, (dialog, which) -> {
                    if (actions[which].equals("Pin")) {
                        List<Task<Void>> pinTasks = new ArrayList<>();

                        for (String docId : selectedDocIds) {
                            DocumentReference sourceRef = db.collection("users")
                                    .document(currentUser.getUid())
                                    .collection("notes")
                                    .document(docId);

                            Task<Void> task = sourceRef.get().continueWithTask(task1 -> {
                                DocumentSnapshot noteDoc = task1.getResult();
                                if (noteDoc.exists()) {
                                    Map<String, Object> noteData = new HashMap<>(noteDoc.getData());
                                    noteData.put("isPinned", true);
                                    sourceRef.update("isPinned", true);

                                    return db.collection("users")
                                            .document(currentUser.getUid())
                                            .collection("pinned")
                                            .document(docId)
                                            .set(noteData);
                                }
                                return Tasks.forResult(null);
                            });

                            pinTasks.add(task);
                        }

                        Tasks.whenAllComplete(pinTasks).addOnSuccessListener(results -> {
                            Toast.makeText(this, "Notes pinned", Toast.LENGTH_SHORT).show();
                            exitSelectMode();
                        });
                    } else if (actions[which].equals("Delete")) {
                        WriteBatch batch = db.batch();
                        List<String> deletedIds = new ArrayList<>();

                        for (String docId : selectedDocIds) {
                            DocumentReference docRef = db.collection("users")
                                    .document(currentUser.getUid())
                                    .collection("notes").document(docId);
                            batch.update(docRef, "isDeleted", true);
                            deletedIds.add(docId);
                        }

                        batch.commit().addOnSuccessListener(unused -> {
                            for (String docId : deletedIds) {
                                db.collection("users")
                                        .document(currentUser.getUid())
                                        .collection("notes")
                                        .document(docId)
                                        .get()
                                        .addOnSuccessListener(doc -> {
                                            if (doc.exists()) {
                                                Map<String, Object> historyEntry = new HashMap<>();
                                                historyEntry.put("type", "Deleted");
                                                historyEntry.put("title", doc.getString("title"));
                                                historyEntry.put("content", doc.getString("content"));
                                                historyEntry.put("timestamp", System.currentTimeMillis());

                                                db.collection("users")
                                                        .document(currentUser.getUid())
                                                        .collection("history")
                                                        .add(historyEntry);
                                            }
                                        });
                            }
                            Toast.makeText(this, "Notes deleted", Toast.LENGTH_SHORT).show();
                            exitSelectMode();
                        });
                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.searchItem);
        selectItem = menu.findItem(R.id.selectItem);

        menu.findItem(R.id.notehistoryItem).setOnMenuItemClickListener(item -> {
            startActivity(new Intent(this, HistorypageActivity.class));
            return true;
        });

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search notes...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                loadNotes(query); return true;
            }

            @Override public boolean onQueryTextChange(String newText) {
                loadNotes(newText); return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.selectItem) {
            if (isSelectMode) exitSelectMode();
            else enterSelectMode();
            return true;
        } else if (item.getItemId() == R.id.signOut) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes("");
    }
}
