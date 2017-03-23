package com.example.leon.carcompanion;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class NoteActivity extends AppCompatActivity {

    private ListView notesList;
    private BufferedReader reader;
    private Scanner inFile1;
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

        // Create Array of file names in 'Notizen' directory
        File dir = new File(Environment.getExternalStorageDirectory() + "/Documents/" + noteDir);
        List<String> list = Arrays.asList(dir.list(
                new FilenameFilter() {
                    @Override public boolean accept(File dir, String name) {
                        return name.endsWith(".txt");
                    }
                }
        ));
        System.out.println(list.get(0));


        notesList = (ListView) findViewById(R.id.notesListView);
        }

    public void changeToCreateNote(View view){
        Intent intent = new Intent (this, CreateNoteActivity.class);
        startActivity(intent);
    }

}
