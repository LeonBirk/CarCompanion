package com.example.leon.carcompanion;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SMSActivity extends AppCompatActivity {

    Button sendSMS;
    EditText phoneNumber;
    EditText smsMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        sendSMS = (Button) findViewById(R.id.sendSMS);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        smsMessage = (EditText) findViewById(R.id.smsMessage);

        sendSMS.setOnClickListener(new View.OnClickListener()
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
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, SMSActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null, message, pi, null);
    }
}
