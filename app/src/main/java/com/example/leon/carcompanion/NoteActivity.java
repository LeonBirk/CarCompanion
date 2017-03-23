package com.example.leon.carcompanion;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;


public class NoteActivity extends AppCompatActivity {

    private ListView notesList;
    private BufferedReader reader;
    public static String noteDir = "Notizen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Creates Folder 'Notizen' if there isn't one yet
        File folder = new File(Environment.getExternalStorageDirectory() + "/Documents/" + noteDir);
        if (!folder.exists()) {
            folder.mkdir();
        }

        notesList = (ListView) findViewById(R.id.notesListView);
        }

    public void changeToCreateNote(View view){
        Intent intent = new Intent (this, CreateNoteActivity.class);
        startActivity(intent);
    }

}
