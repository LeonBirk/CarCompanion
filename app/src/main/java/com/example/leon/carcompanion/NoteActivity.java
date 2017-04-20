package com.example.leon.carcompanion;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoteActivity extends AppCompatActivity {

    private ListView notesList;
    public static String noteDir = "Notizen";
    private BufferedReader reader;
    private StringBuilder sb;
    private String eol = System.getProperty("line.separator");
    List<String> notesListItems = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Creates Folder 'Documents' if there isn't one yet
        File folder = new File(Environment.getExternalStorageDirectory() + "/Documents/");
        System.out.println(folder.toString());
        if (!folder.exists()) {
            folder.mkdir();
            System.out.println("create Folder: Documents");
        }

        // Creates Folder 'Notizen' if there isn't one yet
        File folder1 = new File(Environment.getExternalStorageDirectory() + "/Documents/" + noteDir);
        System.out.println(folder1.toString());
        if (!folder1.exists()) {
            folder1.mkdir();
            System.out.println("create Folder: " + noteDir);
        }



        // Create Array of file names in 'Notizen' directory

            File dir = new File(Environment.getExternalStorageDirectory() + "/Documents/" + noteDir);

            System.out.println(dir.toString());
            List <String> fileNameList = Arrays.asList(dir.list(
                    new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.endsWith(".txt");
                        }
                    }
            ));
            System.out.print("NotesArray wird erstellt");



        // Reads the content of all .txt files in the specified folder1 and adds it to an ArrayList
        for (int i = 0; i < fileNameList.size(); i++){
            try{
                File noteFile =  new File(fileNameList.get(i));
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

        /**
         * Deleting notes from external Storage
         */
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            // necessary for hiding the note instantly, files are only being read after activity reload
            String toDelete = (String) extras.get("ItemToDelete");
            notesListItems.remove(toDelete);

            String filePath = (String) extras.get("FileToDelete");
            File fileToDelete = new File(filePath);
            boolean deleted = fileToDelete.delete();
            System.out.println(filePath + " has been deleted!");

            String titleDeletedNote = (String) extras.get("ItemToChange_Title");
            Context context = getApplicationContext();
            CharSequence toastText = (titleDeletedNote + ".txt ist gelöscht worden.");
            int duration = Toast.LENGTH_LONG;
            Toast deletedToast = Toast.makeText(context, toastText,duration);
            deletedToast.show();

        }

        // ArrayAdapter preparing the ArrayList data for display in the ListView
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                notesListItems);

        // Actual ListView of Note items to be displayed
        notesList = (ListView) findViewById(R.id.notesListView);

        notesList.setAdapter(arrayAdapter);
        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemText = parent.getItemAtPosition(position).toString();
                Intent intent = new Intent(getApplicationContext(), DetailNoteActivity.class);
                intent.putExtra("Text", itemText);
                startActivity(intent);
            }
        });

        }

    public void changeToCreateNote(View view){
        Intent intent = new Intent (this, CreateNoteActivity.class);
        startActivity(intent);
    }

}
