package com.example.leon.carcompanion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CreateNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        final EditText edit_note_title = (EditText) findViewById(R.id.edit_note_title);
        final EditText edit_note_content = (EditText) findViewById(R.id.edit_note_content);

        Button save_note_btn = (Button) findViewById(R.id.save_note_button);
        save_note_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note_title = edit_note_title.getText().toString();
                String note_content = edit_note_content.getText().toString();
                System.out.println("Title is ---> " + note_title );
                System.out.println("Content is ---> " + note_content);

                BufferedWriter writer;
                try {
                    FileOutputStream openedFile = openFileOutput("CarCompNotes", MODE_WORLD_WRITEABLE);

                    writer = new BufferedWriter(new OutputStreamWriter(openedFile));
                    String eol = System.getProperty("line.separator");
                    writer.write(note_title + eol);
                    writer.write(note_content + eol);
                    writer.close();

                    changeToReadNote();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }

    protected void changeToReadNote(){
        finish();
        Intent intent = new Intent (this, ReadNoteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
