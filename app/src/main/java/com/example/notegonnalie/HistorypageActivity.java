package com.example.notegonnalie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class HistorypageActivity extends AppCompatActivity {

    ImageView logoToolBar;
    LinearLayout recentlyDeletedLayout;
    SharedPreferences sharedPreferences;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    JSONObject selectedNote = null;
    FirebaseFirestore db;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historypage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        logoToolBar = findViewById(R.id.logoToolBar);
        logoToolBar.setOnClickListener(v -> {
            startActivity(new Intent(this, HomenotesActivity.class));
            finish();
        });

        recentlyDeletedLayout = findViewById(R.id.linearLayout9);
        sharedPreferences = getSharedPreferences("notes", Context.MODE_PRIVATE);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDeletedNotes();
        loadFirestoreHistory();
    }

    private void loadDeletedNotes() {
        recentlyDeletedLayout.removeAllViews();
        selectedNote = null;

        try {
            JSONArray allNotes = new JSONArray(sharedPreferences.getString("all_notes", "[]"));
            ArrayList<JSONObject> deletedNotes = new ArrayList<>();

            for (int i = 0; i < allNotes.length(); i++) {
                JSONObject note = allNotes.getJSONObject(i);
                if (note.optBoolean("isDeleted", false)) {
                    deletedNotes.add(note);
                }
            }

            Collections.sort(deletedNotes, (a, b) -> {
                try {
                    return Long.compare(b.getLong("timestamp"), a.getLong("timestamp"));
                } catch (JSONException e) {
                    return 0;
                }
            });

            for (JSONObject note : deletedNotes) {
                String title = note.optString("title", "Untitled");
                String content = note.optString("content", "");
                long timestamp = note.optLong("timestamp", 0);

                View noteView = createNoteView(note, title, timestamp, content);
                recentlyDeletedLayout.addView(noteView);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadFirestoreHistory() {
        if (currentUser == null) return;

        db.collection("users")
                .document(currentUser.getUid())
                .collection("history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot) {
                        String type = doc.getString("type");
                        String title = doc.getString("title");
                        String content = doc.getString("content");
                        long timestamp = doc.getLong("timestamp") != null ? doc.getLong("timestamp") : 0;

                        View noteView = createNoteView(null, type + ": " + title, timestamp, content);
                        recentlyDeletedLayout.addView(noteView);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load history.", Toast.LENGTH_SHORT).show();
                });
    }

    private View createNoteView(JSONObject noteObj, String title, long timestamp, String content) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(25, 10, 25, 10);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.post_it);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(120, 120);
        imageView.setLayoutParams(imageParams);

        TextView titleView = new TextView(this);
        String dateStr = dateFormat.format(new Date(timestamp));
        titleView.setText(title + "\n" + dateStr);
        titleView.setTextSize(16);
        titleView.setPadding(30, 15, 0, 0);

        layout.addView(imageView);
        layout.addView(titleView);

        layout.setOnClickListener(v -> {
            Intent intent = new Intent(HistorypageActivity.this, ViewNoteActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            intent.putExtra("timestamp", timestamp);
            startActivity(intent);
        });

        layout.setOnLongClickListener(v -> {
            if (noteObj != null) {
                selectedNote = noteObj;
                Toast.makeText(this, "Note selected", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        return layout;
    }

    private void saveUpdatedNotes(JSONObject updatedNote) {
        try {
            JSONArray allNotes = new JSONArray(sharedPreferences.getString("all_notes", "[]"));
            JSONArray newArray = new JSONArray();

            for (int i = 0; i < allNotes.length(); i++) {
                JSONObject note = allNotes.getJSONObject(i);
                if (note.getLong("timestamp") == updatedNote.getLong("timestamp")) {
                    newArray.put(updatedNote);
                } else {
                    newArray.put(note);
                }
            }

            sharedPreferences.edit().putString("all_notes", newArray.toString()).apply();
            loadDeletedNotes();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteNotePermanently(JSONObject targetNote) {
        try {
            JSONArray allNotes = new JSONArray(sharedPreferences.getString("all_notes", "[]"));
            JSONArray newArray = new JSONArray();

            for (int i = 0; i < allNotes.length(); i++) {
                JSONObject note = allNotes.getJSONObject(i);
                if (note.getLong("timestamp") != targetNote.getLong("timestamp")) {
                    newArray.put(note);
                }
            }

            sharedPreferences.edit().putString("all_notes", newArray.toString()).apply();
            loadDeletedNotes();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.Recover) {
            if (selectedNote != null) {
                try {
                    selectedNote.put("isDeleted", false);
                    saveUpdatedNotes(selectedNote);
                    Toast.makeText(this, "Note Recovered", Toast.LENGTH_SHORT).show();
                    selectedNote = null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "No note selected", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (id == R.id.Permanentlydelete) {
            if (selectedNote != null) {
                deleteNotePermanently(selectedNote);
                Toast.makeText(this, "Note Deleted Permanently", Toast.LENGTH_SHORT).show();
                selectedNote = null;
            } else {
                Toast.makeText(this, "No note selected", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
