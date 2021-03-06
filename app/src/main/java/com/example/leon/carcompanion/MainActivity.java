package com.example.leon.carcompanion;

        import java.util.ArrayList;
        import java.util.Locale;

        import android.Manifest;
        import android.content.ActivityNotFoundException;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.os.Build;
        import android.os.Bundle;
        import android.speech.RecognizerIntent;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.TextView;
        import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String answer;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private ImageButton btnCalls;
    private ImageButton btnSpotify;
    private ImageButton btnNotes;
    private ImageButton btnSMS;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnCalls = (ImageButton) findViewById(R.id.call_btn);
        btnSpotify = (ImageButton) findViewById(R.id.spotify_btn);
        btnNotes = (ImageButton) findViewById(R.id.note_btn);
        btnSMS = (ImageButton) findViewById(R.id.sms_btn);

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        btnCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToCalls();
            }
        });

        btnSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToSpotify();
            }
        });

        btnNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToNotes();
            }
        });

        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToSMS();
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                // permission is not granted, ask for permission:
                requestPermissions(new String[] { Manifest.permission.RECORD_AUDIO},
                        PERMISSION_REQUEST);
            }
            if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                // permission is not granted, ask for permission:
                requestPermissions(new String[] { Manifest.permission.CALL_PHONE},
                        PERMISSION_REQUEST);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // permission is not granted, ask for permission:
                requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST);
            }
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // permission is not granted, ask for permission:
                requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST);
            }
        }

        //Permission-Check Read_Contacts
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                // permission is not granted, ask for permission:
                requestPermissions(new String[] { Manifest.permission.READ_CONTACTS},
                        PERMISSION_REQUEST);
            }
        }

        //Permission-Check Send SMS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                // permission is not granted, ask for permission:
                requestPermissions(new String[] { Manifest.permission.SEND_SMS},
                        PERMISSION_REQUEST);
            }
        }
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED){
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
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if(data == null){
                    txtSpeechInput.setText("No Speech input recognized");
                }

                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    answer = result.get(0);

                    for (String s:result) {
                        Log.d("MAIN", s);
                    }

                    boolean found = false; //Prüfvariable

                    /*
                    Die ArrayList @result hat mehrere Antworten. Diese werden jeweils in die
                    einzelnen Worte aufgeteilt und dann auf definierte Schlagwörter geprüft
                     */
                    for (String s:result) { //Aufteilung in "Interpretationen"
                        String[] split = s.split(" ");
                        for (String wort:split) { //Aufteilung in die einzelnen Wörter

                            Log.d("MAIN", wort);
                            switch (wort.toUpperCase()){
                                case "NOTIZEN":
                                    found = true;
                                    changeToNotes();
                                    break;
                                case "ANRUFEN":
                                    found = true;
                                    changeToCalls();
                                    break;
                                case "SMS":
                                    found = true;
                                    changeToSMS();
                                    break;
                                case "SPOTIFY":
                                    found = true;
                                    changeToSpotify();
                                    break;
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

    public void changeToNotes (){
            Intent intent = new Intent(this, NoteActivity.class);
            startActivity(intent);
        }

    public void changeToCalls(){
        Intent intent = new Intent(this, CallActivity.class);
        startActivity(intent);
    }

    public void changeToSMS(){
        Intent intent = new Intent(this, SMSActivity.class);
        startActivity(intent);
    }

    public void changeToSpotify(){
        Intent intent = new Intent (this, SpotifyActivity.class);
        startActivity(intent);
    }

}
