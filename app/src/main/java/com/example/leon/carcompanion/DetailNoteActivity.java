package com.example.leon.carcompanion;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class DetailNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_note);

        ImageButton deleteNote = (ImageButton) findViewById(R.id.deleteNoteButton);
        TextView detailTitle = (TextView) findViewById(R.id.noteDetailTextView_Title);
        TextView detailContent = (TextView) findViewById(R.id.noteDetailTextView_Content);
        String text ="";

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            text = (String) extras.get("Text");
        }

        String[] itemText = new String[2];
        try {
            itemText = text.split("\\n");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        final String finalText = text;
        final String finalTextTitle = itemText[0];

        deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                intent.putExtra("ItemToDelete", finalText);
                intent.putExtra("FileToDelete", Environment.getExternalStorageDirectory() + "/Documents/Notizen/" + finalTextTitle + ".txt");
                startActivity(intent);
            }
        });




        detailTitle.setText(itemText[0]);
        detailContent.setText(itemText[1]);

    }
}
