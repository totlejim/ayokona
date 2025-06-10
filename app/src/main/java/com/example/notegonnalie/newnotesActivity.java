package com.example.notegonnalie;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Timestamp;
import java.util.Stack;

public class newnotesActivity extends AppCompatActivity {

    ImageView backButtonToolBar;
    EditText editText;
    ImageButton boldformatbutton, italicformatButton, underlineformatButton, redoButton, undoButton, theme;
    String[] fontNames = {"arimoregular", "comicsans", "ubuntubold"};

    LinearLayout toolMenu;
    Button saveButton;




    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private String lastText = "";

    private boolean isProgrammaticChange = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        EdgeToEdge.enable( this );
        setContentView( R.layout.activity_newnotes );
        ViewCompat.setOnApplyWindowInsetsListener( findViewById( R.id.main ), (v, insets) -> {
            Insets systemBars = insets.getInsets( WindowInsetsCompat.Type.systemBars() );
            v.setPadding( systemBars.left, systemBars.top, systemBars.right, systemBars.bottom );
            return insets;
        } );




        backButtonToolBar = findViewById( R.id.backButtonToolBar );
        backButtonToolBar.setOnClickListener( new View.OnClickListener() {
                                                  public void onClick(View v) {
                                                      Intent intent = new Intent( newnotesActivity.this, HomenotesActivity.class );
                                                      startActivity( intent );
                                                  }
                                              }
        );






        ImageButton fontButton = findViewById( R.id.fontstyleButton );
        TextView sampleText = findViewById( R.id.notetitleEditText );
        TextView sampleText2 = findViewById( R.id.notecontentEditText );

        editText = findViewById( R.id.notecontentEditText );
        boldformatbutton = findViewById( R.id.boldformatButton );
        italicformatButton = findViewById( R.id.italicformatButton );
        underlineformatButton = findViewById( R.id.underlineformatButton );
        saveButton = findViewById( R.id.saveButton );


        boldformatbutton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyStyle( Typeface.BOLD );
            }
        } );

        italicformatButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyStyle( Typeface.ITALIC );
            }
        } );

        underlineformatButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyUnderline();
            }
        } );
        editText.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isProgrammaticChange) {
                    undoStack.push( s.toString() );
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isProgrammaticChange) {
                    lastText = s.toString();
                    redoStack.clear();
                }
            }
        } );

        undoButton = findViewById( R.id.undoButton );
        redoButton = findViewById( R.id.redoButton );


        // Undo logic
        undoButton.setOnClickListener( v -> {
            if (!undoStack.isEmpty()) {
                String previousText = undoStack.pop();
                redoStack.push( editText.getText().toString() );
                setTextProgrammatically( previousText );
            }
        } );

        // Redo logic
        redoButton.setOnClickListener( v -> {
            if (!redoStack.isEmpty()) {
                String nextText = redoStack.pop();
                undoStack.push( editText.getText().toString() );
                setTextProgrammatically( nextText );
            }
        } );
        fontButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show font selection dialog
                AlertDialog.Builder builder = new AlertDialog.Builder( newnotesActivity.this );
                builder.setTitle( "Choose a Font" );

                ArrayAdapter<String> adapter = new ArrayAdapter<>( newnotesActivity.this,
                        android.R.layout.simple_list_item_1, fontNames );

                builder.setAdapter( adapter, (dialog, which) -> {
                    String selectedFont = fontNames[which];
                    int fontResId = getResources().getIdentifier( selectedFont, "font", getPackageName() );
                    sampleText.setTypeface( ResourcesCompat.getFont( newnotesActivity.this, fontResId ) );
                    sampleText2.setTypeface( ResourcesCompat.getFont( newnotesActivity.this, fontResId ) );
                } );

                builder.show();
            }
        } );



    }

    private void setTextProgrammatically(String text) {
        isProgrammaticChange = true;
        editText.setText(text);
        editText.setSelection(text.length()); // Move cursor to end
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.toolbar_menu2, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.securityItem) {
            Toast.makeText( this, "Security Item", Toast.LENGTH_SHORT ).show();
        }
        if (id == R.id.deleteItem) {
            Toast.makeText( this, "Delete Item", Toast.LENGTH_SHORT ).show();
        }
        if (id == R.id.themeItem) {
            Toast.makeText( this, "Theme Item", Toast.LENGTH_SHORT ).show();
        }

        return true;
    }
}