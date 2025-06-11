package com.example.notegonnalie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HomenotesActivity extends AppCompatActivity {

    LinearLayout linearLayout3, linearLayout4, linearLayout6, linearLayout7, linearLayout10;
    LinearLayout pinnedLayout, recentNotesLayout, otherNotesLayout;
    SharedPreferences prefs;

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
        pinnedLayout = findViewById(R.id.linearLayout6);
        recentNotesLayout = findViewById(R.id.linearLayout7);
        otherNotesLayout = findViewById(R.id.linearLayout10);

        prefs = getSharedPreferences("notesPrefs", MODE_PRIVATE);

        linearLayout3 = findViewById(R.id.linearLayout3);
        linearLayout4 = findViewById(R.id.linearLayout4);

        linearLayout3.setOnClickListener(v -> startActivity(new Intent(this, newnotesActivity.class)));
        linearLayout4.setOnClickListener(v -> startActivity(new Intent(this, FolderspageActivity.class)));

        loadNotes("");
    }

    private void loadNotes(String query) {
        pinnedLayout.removeAllViews();
        recentNotesLayout.removeAllViews();
        otherNotesLayout.removeAllViews();

        Map<String, ?> allEntries = prefs.getAll();
        List<Long> timestamps = new ArrayList<>();

        for (String key : allEntries.keySet()) {
            if (key.contains("_title")) {
                try {
                    timestamps.add(Long.parseLong(key.split("_")[1]));
                } catch (NumberFormatException ignored) {}
            }
        }

        Collections.sort(timestamps, Collections.reverseOrder());

        int regularCount = 0;

        for (long ts : timestamps) {
            String title = prefs.getString("note_" + ts + "_title", "");
            String content = prefs.getString("note_" + ts + "_content", "");
            String font = prefs.getString("note_" + ts + "_font", "default");
            boolean isPinned = prefs.getBoolean("note_" + ts + "_pinned", false);

            if (!query.isEmpty() && !title.toLowerCase().contains(query.toLowerCase()) && !content.toLowerCase().contains(query.toLowerCase())) {
                continue;
            }

            TextView noteView = new TextView(this);
            noteView.setText(Html.fromHtml("<b>" + title + "</b><br>" + content));
            noteView.setPadding(32, 32, 32, 32);
            noteView.setTextSize(16);
            noteView.setBackgroundResource(R.drawable.note_background);

            int fontResId = getResources().getIdentifier(font, "font", getPackageName());
            if (fontResId != 0) {
                noteView.setTypeface(ResourcesCompat.getFont(this, fontResId));
            }

            noteView.setOnClickListener(v -> Toast.makeText(this, "Note clicked!", Toast.LENGTH_SHORT).show());

            noteView.setOnLongClickListener(v -> {
                PopupMenu popup = new PopupMenu(this, v);
                popup.getMenu().add(isPinned ? "Unpin" : "Pin");
                popup.setOnMenuItemClickListener(menuItem -> {
                    prefs.edit().putBoolean("note_" + ts + "_pinned", !isPinned).apply();
                    loadNotes(query);
                    return true;
                });
                popup.show();
                return true;
            });

            if (isPinned) {
                pinnedLayout.addView(noteView);
            } else {
                if (regularCount < 2) {
                    recentNotesLayout.addView(noteView);
                } else {
                    otherNotesLayout.addView(noteView);
                }
                regularCount++;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.searchItem);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setQueryHint("Search notes...");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    loadNotes(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    loadNotes(newText);
                    return true;
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signOut) {
            FirebaseAuth.getInstance().signOut();
            getSharedPreferences("loginPrefs", MODE_PRIVATE).edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes("");
    }
}
