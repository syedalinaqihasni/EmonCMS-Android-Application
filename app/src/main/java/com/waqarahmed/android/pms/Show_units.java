package com.waqarahmed.android.pms;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.widget.Toast.LENGTH_SHORT;

public class Show_units extends AppCompatActivity {

    private static final String LOGTAG = "LOGTAG";
    public static final String EDIT = "EDIT";
    public static final String UNIT = "UNIT";
    public static final String UPDATE = "UPDATE";
    public static final String AMOUNT = "AMOUNT";
    public static final String BILL = "BILL";
    //ProgressBar spinner;
    Button button;
    String Api_Key;
  //  String JSON;
    String value="0.0";
    Intent intent,intent1;
//    JSONArray json_Array;
    SharedPreferences sharedprefrences;
    SharedPreferences.Editor editor;
    TextView tvMessage;
    TextView time_update;
    TextView amount;
    TextView bill;
    String currentDateTimeString;
    Bill_Calulator calulator;

    BroadcastReceiver receiver=null;
    IntentFilter filter;
    static String Sp_num;
    String json_string;
    JSONArray json_Array;
    String smsContent="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_units);
        tvMessage= (TextView) findViewById(R.id.message);
        time_update= (TextView) findViewById(R.id.update);

        sharedprefrences=getSharedPreferences(welcome_post.MY_WELCOME_PREF,MODE_PRIVATE);

        settingViews();

        filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arr0, Intent arr1) {
                Log.d(LOGTAG,"RECEIVEd");

                try {
                    processReceiver(arr0,arr1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        registerReceiver(receiver,filter);
    }

    public void processReceiver(Context context, Intent intent) throws JSONException {
        Toast.makeText(context,"Data Received",Toast.LENGTH_LONG).show();

        Bundle bundle = intent.getExtras();
        Object[] objArr  = (Object[]) bundle.get("pdus");
        smsContent="";

        for(int i=0; i<objArr.length; i++){
            SmsMessage smsMsg = SmsMessage.createFromPdu((byte[])objArr[i]);
            String smsBody = smsMsg.getMessageBody();
            String senderNumber = smsMsg.getDisplayOriginatingAddress();
            smsContent +=smsBody;
            Sp_num = senderNumber;
        }
        if(Sp_num.equals("+923353305880")|| Sp_num.equals("03353305880")) {
            String message = smsContent;
            tvMessage.setText(smsContent);
//            json_parsing();
//            settingViews();
//            savingData();
        }
    }

    public void json_parsing() throws JSONException {
        json_string = smsContent;
        json_Array = new JSONArray(json_string);
        int count = 0;

        while (count < json_Array.length()) {
            JSONObject JO = json_Array.getJSONObject(count);
            value = JO.getString("ENERGY");
            count++;
        }
        Log.i("value",value);
    }

    public void settingViews() {
        time_update.setText(sharedprefrences.getString(UPDATE,"No update received"));
    }

    public String getTime(){
        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        return currentDateTimeString;
    }

    public void savingData() {
        editor=sharedprefrences.edit();
        editor.putString(UNIT,value);
        editor.putString(UPDATE,getTime());
        editor.putString(AMOUNT,calulator.getAmount(value));
        editor.putString(BILL,calulator.getBill(calulator.getAmount(value)));
        editor.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void edit_profile(View view) {
        intent=new Intent(this,Form.class);
        intent.putExtra(EDIT,true);
        startActivity(intent);

    }

}
