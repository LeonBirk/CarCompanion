package com.example.leon.carcompanion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class NoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
    }

    public void changeToCreateNote(View view){
        Intent intent = new Intent (this, CreateNoteActivity.class);
        startActivity(intent);
    }
}
