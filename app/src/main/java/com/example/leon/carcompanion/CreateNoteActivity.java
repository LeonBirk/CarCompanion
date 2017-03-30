package com.example.leon.carcompanion;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CreateNoteActivity extends RootActivity {
    FileOutputStream stream;

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

                if(isExternalStorageWritable())
                {
                    System.out.println("Permission to writing Files is granted.");

                    String note_title = edit_note_title.getText().toString();
                    String note_content = edit_note_content.getText().toString();

                    if(!note_title.isEmpty() && !note_content.isEmpty()){
                        System.out.println("Title is ---> " + note_title );
                        System.out.println("Content is ---> " + note_content);

                        File output = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOCUMENTS) + "/" + NoteActivity.noteDir + "/", note_title + ".txt");
                        System.out.println("Path to file:" + output.getPath());

                        try {
                            stream = new FileOutputStream(output);
                            stream.write((note_title + "\n" + note_content).getBytes());
                            stream.close();

                            changeToNotes();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    } else if(note_title.isEmpty()) {
                            edit_note_title.setText(R.string.note_title_empty_message);
                    } if(note_content.isEmpty()){
                            edit_note_content.setText(R.string.note_content_empty_content);
                }

            }else {
                    System.out.println("Writing to external storage not possible. Please Check App permissions.");}
            }
        });

    }

    protected void changeToNotes(){
        finish();
        Intent intent = new Intent (this, NoteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
