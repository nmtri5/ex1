package com.example.tringuyen_sydneyaraujo_comp304_lab6_ex1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.ContactsContract;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    private static final int SMS_RECEIVE_PERMISSION_REQUEST = 1;
    private EditText eText, txtPhone;
    private TextView SMSes;
    private TextView textMessage;
    //
    public static final String SENT = "SMS_SENT";
    public static final String DELIVERED = "SMS_DELIVERED";
    //
    private PendingIntent sentPI, deliveredPI;
    private BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    private IntentFilter intentFilter;
    //
    // receive intents sent by sendBroadcast()
    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //display the SMS received in the TextView
            textMessage = (TextView) findViewById(R.id.textMessage);
            //display the content of the received message in text view
            //SMSes.setText(intent.getExtras().getString("sms"));
            textMessage.setText(textMessage.getText()+"\n"+
                    intent.getExtras().getString("sms"));
        }
    };
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        //request permissions
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_PHONE_STATE},
                SMS_RECEIVE_PERMISSION_REQUEST);
        Bundle extras = getIntent().getExtras();
        String contactName="";
        ArrayList phoneList = new ArrayList();
        if(extras != null)
            contactName = extras.getString("contactName");
        phoneList = extras.getParcelableArrayList("phoneList");
        int a  = extras.getInt("id");
        textMessage = (TextView) findViewById(R.id.textMessage);

        textMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
        TextView tView = (TextView) findViewById(R.id.textView);
        tView.setText(contactName);
        //this.getSupportActionBar().setTitle(contactName);
        ImageView imgView = (ImageView) findViewById(R.id.imageView);
        imgView.setImageResource(R.drawable.contacts);
        //
        eText = (EditText) findViewById(R.id.editText);
        txtPhone = (EditText) findViewById(R.id.txtPhone);
        txtPhone.setText(phoneList.get(a-1).toString());

        //
        //an action to take in the future with same permission
        //as your application
        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0, new
                Intent(DELIVERED), 0);
        //intent to filter the action for SMS messages received
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");
        //---register the receiver---
        registerReceiver(intentReceiver, intentFilter);
        //
    }
    //
    @Override
    public void onResume() {
        super.onResume();
        //---register the receiver---
        //registerReceiver(intentReceiver, intentFilter);
        //---create the BroadcastReceiver when the SMS is sent---
        smsSentReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) //Retrieve the current result code, as set by the previous receiver
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        //---create the BroadcastReceiver when the SMS is delivered---
        smsDeliveredReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
        //---register the two BroadcastReceivers---
        registerReceiver(smsDeliveredReceiver, new
                IntentFilter(DELIVERED));
        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
    }
    @Override
    public void onPause() {
        super.onPause();
        //---unregister the receiver---
        //unregisterReceiver(intentReceiver);
        //---unregister the two BroadcastReceivers---
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //---unregister the receiver---
        unregisterReceiver(intentReceiver);
    }
    //
    public void sendMessage(View v)
    {
        eText = (EditText) findViewById(R.id.editText);
        String phoneNumber = txtPhone.getText().toString();
        String message = eText.getText().toString();
        sendSMS(phoneNumber, message);
        textMessage.setText(textMessage.getText()+"\n"+ eText.getText());
    }
    //sends an SMS message to another device
    private void sendSMS(String phoneNumber, String message)
    {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI,
                deliveredPI);
    }
}
