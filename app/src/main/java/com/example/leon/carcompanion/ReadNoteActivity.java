package com.example.leon.carcompanion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_note);

        BufferedReader reader;

        try{
            FileInputStream noteFile =  openFileInput("CarCompNotes.txt");
            reader = new BufferedReader(new InputStreamReader(noteFile));
            String line;
            String eol = System.getProperty("line.separator");
            StringBuilder sb = new StringBuilder();

            TextView notesView = (TextView) findViewById(R.id.read_note);

            while ((line = reader.readLine()) != null  ){
                sb.append(line).append(eol);
            }

            notesView.setText(sb.toString());

        } catch (FileNotFoundException e){
            System.out.println("File not found!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
