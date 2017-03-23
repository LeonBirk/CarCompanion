package com.example.leon.carcompanion;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SMSActivity extends AppCompatActivity {

    Button sendSMS;
    EditText phoneNumber;
    EditText smsMessage;
    public static final int PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        sendSMS = (Button) findViewById(R.id.sendSMS);
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
}
