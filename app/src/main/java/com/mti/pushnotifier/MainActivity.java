/*
 * Created by Tareq Islam on 6/27/18 1:28 PM
 *
 *  Last modified 6/27/18 1:23 PM
 */

package com.mti.pushnotifier;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{
    private static final String TAG = "MainActivity";

    Calendar mCalendar;
    TimePickerDialog mTimePickerDialog;
    DatePickerDialog mDatePickerDialog;
    TextView mTextView;
    EditText mEmailText;
    Button send;


    private String notifyTitle,notifyCalender,userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mCalendar = Calendar.getInstance();


        initViews();





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH));
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        initDateTimePicker();





    }

    private void initDateTimePicker() {
        mTimePickerDialog = TimePickerDialog.newInstance(
                this,
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE),
                mCalendar.get(Calendar.SECOND),
                false

        );

        mDatePickerDialog = DatePickerDialog.newInstance(
                this,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
        );

        // for Initialize or opening date time picker
        //    mDatePickerDialog.show(getFragmentManager(), "Datepickerdialog");
    }

    private void initViews() {
        mTextView = findViewById(R.id.m_Calender_Label);
        mEmailText=findViewById(R.id.emailText);
        send=findViewById(R.id.send);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Button subscribeButton = findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Subscribing to news topic");
                // [START subscribe_topics]
                FirebaseMessaging.getInstance().subscribeToTopic("news")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = getString(R.string.msg_subscribed);
                                if (!task.isSuccessful()) {
                                    msg = getString(R.string.msg_subscribe_failed);
                                }
                                Log.d(TAG, msg);
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                // [END subscribe_topics]
            }
        });

        Button logTokenButton = findViewById(R.id.logTokenButton);
        logTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get token
                final String[] newToken = new String[1];
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                    newToken[0] = instanceIdResult.getToken();
                        Log.d("newToken", newToken[0]);
                    }});

                // Log and toast
                String msg = getString(R.string.msg_token_fmt, newToken[0]);
                Log.d(TAG, msg);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });





        //Send Notification
        findViewById(R.id.setNotification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setText("");
                mDatePickerDialog.show(getFragmentManager(),"Datepickerdialog");
            }
        });

        //Send Token for register
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToken();
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set(Calendar.YEAR,year);
        mCalendar.set(Calendar.MONTH,monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        mTimePickerDialog.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        mCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        mCalendar.set(Calendar.MINUTE,minute);
        mCalendar.set(Calendar.SECOND,second);

        sendNotification();

    }






    private void sendNotification() {

       //putting Dymmy data change it
       notifyTitle="Dummy Title";
       notifyCalender=retriveTime(mCalendar);
       userEmail=mEmailText.getText().toString();

        new AsyncSetNotification(this).execute(notifyTitle,notifyCalender,userEmail);
    }

    private String retriveTime(Calendar calendar){

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM"); //"yyyy MMM dd HH:mm:ss"

        String formattedDate= sdf.format(calendar.getTime());

        mTextView.setText(formattedDate);

        return formattedDate;



    }



    private void sendToken() {


        final String token = SharedPreference.getInstance(this).getDeviceToken();
        final String email = mEmailText.getText().toString();

        if (token == null) {

            // Get token

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String newToken = instanceIdResult.getToken();
                    Log.d("newToken",newToken);
                    SharedPreference.getInstance(getApplicationContext()).saveDeviceToken(newToken);
                    //    setData(email, newToken);
                    sendData(email,newToken);

                }
            });




            loadData();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
           // return;
        }else {

            sendData(email,token);
            loadData();
        }




    }

    void sendData(String email, String token){
        // Initialize  AsyncLogin() class with email and password
        new AsyncRegisterDevice(this).execute(email,token);
    }



    void loadData(){

    }




}
