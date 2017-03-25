package com.example.leon.carcompanion;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class NoteActivity extends RootActivity {

    private ListView notesList;
    private Scanner inFile1;
    public static String noteDir = "Notizen";
    private BufferedReader reader;
    private StringBuilder sb;
    private String eol = System.getProperty("line.separator");
    List<String> notesListItems = new ArrayList<String>();

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
        List<String> fileNameList = Arrays.asList(dir.list(
                new FilenameFilter() {
                    @Override public boolean accept(File dir, String name) {
                        return name.endsWith(".txt");
                    }
                }
        ));

        // Reads the content of all .txt files in the specified folder and adds it to an ArrayList
        for (int i = 0; i < fileNameList.size(); i++){
            try{
                File noteFile =  new File(fileNameList.get(i));
                System.out.println("Now reading: " + fileNameList.get(i));
                reader = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory() + "/Documents/Notizen/" + noteFile));
                String line;

                sb = new StringBuilder();

                while ((line = reader.readLine()) != null  ){
                    sb.append(line).append(eol);
                }
                System.out.println(sb.toString());
                notesListItems.add(sb.toString());

            } catch (FileNotFoundException e){
                System.out.println("File not found!");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // ArrayAdapter preparing the ArrayList data for display in the ListView
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                notesListItems);

        // Actual ListView of Note items to be displayed
        notesList = (ListView) findViewById(R.id.notesListView);

        notesList.setAdapter(arrayAdapter);
        }

    public void changeToCreateNote(View view){
        Intent intent = new Intent (this, CreateNoteActivity.class);
        startActivity(intent);
    }

}
