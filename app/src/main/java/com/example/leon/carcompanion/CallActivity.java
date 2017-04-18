package com.example.leon.carcompanion;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class CallActivity extends AppCompatActivity {
    Button btnStartCall;
    ImageButton btnCallSpeak;
    EditText editTextCall;
    private String answer;
    private String text = "";        //Text for Empfänger or Message
    private static final int PERMISSION_REQUEST = 1;
    private final int REQ_CODE_SPEECH_INPUT = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        btnStartCall = (Button) findViewById(R.id.btnStartCall);
        btnCallSpeak = (ImageButton) findViewById(R.id.btnCallSpeak);
        editTextCall = (EditText) findViewById(R.id.editTextCall);


        btnStartCall.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                String phone = editTextCall.getText().toString();
                startCall(phone);
            }
        });

        btnCallSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });
    }





    private void startCall(String phone)
    {

        Uri number = Uri.parse("tel:"+phone);
        Intent callIntent = new Intent(Intent.ACTION_CALL, number); //use ACTION_CALL class
        Log.println(Log.DEBUG, "Telefonnummer: ", phone);
        try{
            startActivity(callIntent);  //call activity and make phone call
        }
        catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(getApplicationContext(),"yourActivity is not founded",Toast.LENGTH_SHORT).show();
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
                    //txtSpeechInput.setText("No Speech input recognized");
                }

                /**
                 if (resultCode == RESULT_OK && null != data) {
                 ArrayList<String> result = data
                 .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                 if(result.get(0).toUpperCase().equals("SENDEN")){
                 String phone = phoneNumber.getText().toString();
                 String message = smsMessage.getText().toString();
                 sendSMS(phone, message);
                 }else if(result.get(0).toUpperCase().equals("EMPFÄNGER")){
                 phoneNumber.setText(result.get(0));
                 }else if(result.get(0).toUpperCase().equals("NACHRICHT")){
                 smsMessage.setText(result.get(0));
                 }

                 //answer = result.get(0);
                 } */

                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //txtSpeechInput.setText(result.get(0));
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
                                case "EMPFÄNGER":
                                    found = true;
                                    text = "";
                                    text = s.substring(10, s.length());
                                    editTextCall.setText(text);
                                    break;
                                case "NACHRICHT":
                                    found = true;
                                    text = "";
                                    text = s.substring(10, s.length());
                                    break;
                                case "ANRUFEN":
                                    found = true;
                                    if(editTextCall!=null){
                                        String phone = editTextCall.getText().toString();
                                        startCall(phone);
                                    }
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
}
