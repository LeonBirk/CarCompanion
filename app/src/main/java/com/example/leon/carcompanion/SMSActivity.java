package com.example.leon.carcompanion;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
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

    ImageButton btnSpeakSend;
    EditText phoneNumber;
    EditText smsMessage;
    private String answer;
    private String text = "";        //Text for Empfänger or Message
    private static final int PERMISSION_REQUEST = 1;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    //SmsReceiver receiver = new SmsReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

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

        btnSpeakSend = (ImageButton) findViewById(R.id.btnSpeakSend);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        smsMessage = (EditText) findViewById(R.id.smsMessage);

        btnSpeakSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                    phoneNumber.setText(text);
                                    break;
                                case "NACHRICHT":
                                    found = true;
                                    text = "";
                                    text = s.substring(10, s.length());
                                    smsMessage.setText(text);
                                    break;
                                case "SENDEN":
                                    found = true;

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            // permission is not granted, ask for permission:
                                            requestPermissions(new String[] { Manifest.permission.READ_CONTACTS},
                                                    PERMISSION_REQUEST);
                                        }
                                    }

                                    if(phoneNumber!=null && smsMessage!=null){
                                        //test: phoneNumber or Contact-Name?
                                        if(phoneNumber.getText().toString().matches(".*\\d+.*")){
                                            String phone = phoneNumber.getText().toString();
                                            String message = smsMessage.getText().toString();
                                            sendSMS(phone, message);
                                        }else{
                                            String phone = readContacts(phoneNumber.getText().toString());
                                            String message = smsMessage.getText().toString();
                                            sendSMS(phone, message);
                                        }
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

    /**
     * Method to read the phone-number from a contact
     */
    private String readContacts(String contactName){

        String phoneNumberContact="";
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            if(name.equals(contactName)){
                phoneNumberContact = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                break;
            }
        }
        phones.close();
        Log.d("PHONENUMBER", phoneNumberContact);
        return phoneNumberContact;
    }
}
