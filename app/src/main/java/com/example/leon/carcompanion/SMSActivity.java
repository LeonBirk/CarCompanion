package com.example.leon.carcompanion;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class SMSActivity extends AppCompatActivity {

    Button sendSMS;
    ImageButton btnSpeak;
    EditText phoneNumber;
    EditText smsMessage;
    public static final int PERMISSION_REQUEST = 1;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        sendSMS = (Button) findViewById(R.id.sendSMS);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        smsMessage = (EditText) findViewById(R.id.smsMessage);

        sendSMS.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v){
                String phone = phoneNumber.getText().toString();
                String message = smsMessage.getText().toString();
                sendSMS(phone, message);
            }
        });

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    }

    private void sendSMS(String phone, String message)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                // permission is not granted, ask for permission:
                requestPermissions(new String[] { Manifest.permission.SEND_SMS},
                        PERMISSION_REQUEST);
            }
        }

        Log.println(Log.DEBUG, "Telefonnummer: ", phone);
        Log.println(Log.DEBUG, "Nachricht: ", message);
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, SMSActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null, message, pi, null);
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

                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    phoneNumber.setText(result.get(0));
                    //answer = result.get(0);
                }
                break;
            }

        }
    }
}
