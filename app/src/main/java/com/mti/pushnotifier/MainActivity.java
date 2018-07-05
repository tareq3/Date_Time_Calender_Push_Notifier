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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{
    private static final String TAG = "MainActivity";

    Calendar mCalendar;
    TimePickerDialog mTimePickerDialog;
    DatePickerDialog mDatePickerDialog;
    TextView mTextView;
    EditText mEmailText;
    Button send;


    FirebaseFirestore mFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirestore=FirebaseFirestore.getInstance();
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
                String token = FirebaseInstanceId.getInstance().getToken();

                // Log and toast
                String msg = getString(R.string.msg_token_fmt, token);
                Log.d(TAG, msg);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });





        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setText("");
                mDatePickerDialog.show(getFragmentManager(),"Datepickerdialog");
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToken(v);
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
        retriveTime(mCalendar);


    }

    void retriveTime(Calendar calendar){
        mTextView.setText(calendar.toString());
    }

ProgressDialog progressDialog;
    public void sendToken(View view) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering Device...");
        progressDialog.show();

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
                    setData(email, newToken);

                }
            });




            loadData();
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }else {
            setData(email,token);
            loadData();
            progressDialog.dismiss();
        }




    }

    void setData(final String email, String token){
        //String id= UUID.randomUUID().toString();

        Map<String, Object> users=new HashMap<>();
       // users.put("id",id);
        users.put("email",email);
        users.put("token",token);

        mFirestore.collection("users").document(email)
                        .set(users)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("" +getClass().getName(), "User added with id: "+ email);

                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("" +getClass().getName(), "Error adding data to firestore");
                            }
                        });

    }


    void loadData(){
        mFirestore.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                    Log.d("" +getClass().getName(), documentSnapshot.getId()+"=>" + documentSnapshot.getData());
                                }
                            }else{
                                Log.w("" +getClass().getName(), "Error getting data from firestore");
                            }
                        }
                    });
    }
}
