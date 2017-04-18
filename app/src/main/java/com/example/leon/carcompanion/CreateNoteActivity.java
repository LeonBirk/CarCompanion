package com.example.leon.carcompanion;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class CreateNoteActivity extends AppCompatActivity {
    FileOutputStream stream;
    private String answer;
    private String text = "";        //Text for Empfänger or Message
    private static final int PERMISSION_REQUEST = 1;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private EditText edit_note_title;
    private EditText edit_note_content;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        edit_note_title = (EditText) findViewById(R.id.edit_note_title);
        edit_note_content = (EditText) findViewById(R.id.edit_note_content);
        saveButton = (Button) findViewById(R.id.save_note_button);

        Bundle extras = getIntent().getExtras();
        if (extras!= null){
            String titleToChange = (String) extras.get("ItemToChange_Title");
            String contentToChange = (String) extras.get("ItemToChange_Content");

            System.out.println("Extras sind: " + titleToChange + " und " + contentToChange);

            edit_note_title.setText(titleToChange);
            edit_note_content.setText(contentToChange);
        } else {
            System.out.println("Extras sind leer!");
        }

        if (!edit_note_title.getText().toString().isEmpty() && !edit_note_content.getText().toString().isEmpty()){
            saveButton.setText("Notiz speichern!");
        }

        /**
         * Saving the note to external storage
         */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String note_title = edit_note_title.getText().toString();
                String note_content = edit_note_content.getText().toString();

                if(edit_note_title.getText().toString().isEmpty()){
                    promptSpeechInput();
                } else if(edit_note_content.getText().toString().isEmpty()){
                    promptSpeechInput();
                }

                if (isExternalStorageWritable()) {
                    System.out.println("Permission to writing Files is granted.");

                    if (!note_title.isEmpty() && !note_content.isEmpty()) {
                        System.out.println("Title is ---> " + note_title);
                        System.out.println("Content is ---> " + note_content);

                        File output = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOCUMENTS) + "/" + NoteActivity.noteDir + "/", note_title + ".txt");
                        System.out.println("Path to file:" + output.getPath());

                        try {
                            stream = new FileOutputStream(output);
                            stream.write((note_title + "\n" + note_content).getBytes());
                            stream.close();

                            changeToNotes();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (note_title.isEmpty()) {
                        edit_note_title.setText(R.string.note_title_empty_message);
                    }


                } else {

                    System.out.println("Writing to external storage not possible. Please Check App permissions.");
                }
            }
        });
        if(edit_note_title.getText().toString().isEmpty() || edit_note_content.getText().toString().isEmpty()){
            promptSpeechInput();
        }
    }


        /**
         * Showing google speech input dialog
         * */

    private void promptSpeechInput() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //txtSpeechInput.setText("Permission Granted");
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (data == null) {
                    //txtSpeechInput.setText("No Speech input recognized");
                }

                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    answer = result.get(0);

                    for (String s : result) {
                        Log.d("MAIN", s);
                    }

                    boolean found = false; //Prüfvariable

                    /*
                    Die ArrayList @result hat mehrere Antworten. Diese werden jeweils in die
                    einzelnen Worte aufgeteilt und dann auf definierte Schlagwörter geprüft
                     */
                    for (String s : result) { //Aufteilung in "Interpretationen"
                        String[] split = s.split(" ");
                        for (String wort : split) { //Aufteilung in die einzelnen Wörter

                            Log.d("MAIN", wort);
                            switch (wort.toUpperCase()) {
                                case "TITEL":
                                    found = true;
                                    text = "";
                                    text = s.substring(6, s.length());
                                    edit_note_title.setText(text);

                                    break;
                                case "NOTIZ":
                                    found = true;
                                    text = "";
                                    text= s.substring(6, s.length());
                                    edit_note_content.setText(text);
                                    saveButton.setText("Speichern");

                                    break;
                                case "SPEICHERN":
                                    found = true;

                            }
                            if (found) break; //Aus der Schleife
                        }
                        if (found) break;
                    }
                }
                break;
            }

        }
    }

    protected void changeToNotes() {
        finish();
        Intent intent = new Intent(this, NoteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        System.out.println("external storage is writeable.");
        return Environment.MEDIA_MOUNTED.equals(state);

    }
}

