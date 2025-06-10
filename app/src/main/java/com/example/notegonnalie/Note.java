package com.example.notegonnalie;

import java.sql.Timestamp;

public class Note {

    String noteTitleEditText;
    String noteContentEditText;
    Timestamp timestamp;

    public Note(String noteTitleEditText) {
        this.noteTitleEditText = noteTitleEditText;
    }

    public String getNoteContentEditText() {
        return noteContentEditText;
    }

    public void setNoteContentEditText(String noteContentEditText) {
        this.noteContentEditText = noteContentEditText;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getNoteTitleEditText() {
        return noteTitleEditText;
    }

    public void setNoteTitleEditText(String noteTitleEditText) {
        this.noteTitleEditText = noteTitleEditText;
    }
}
