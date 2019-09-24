/*
 * Created by Tareq Islam on 6/27/18 1:28 PM
 *
 *  Last modified 6/27/18 1:23 PM
 */

package com.mti.pushnotifier;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.mti.pushnotifier.api.ApiClient;
import com.mti.pushnotifier.api.UserApiServices;
import com.mti.pushnotifier.model.DeviceRegistrationModel;
import com.mti.pushnotifier.model.UserModel;
import com.mti.pushnotifier.model.UserRegisterModel;
import com.mti.pushnotifier.model.WishMessageModel;
import com.orhanobut.hawk.Hawk;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{
    private static final String TAG = "MainActivity";
    private  String newToken =null;
    Calendar mCalendar;
    TimePickerDialog mTimePickerDialog;
    DatePickerDialog mDatePickerDialog;
    TextView mTextView;
    EditText mEmailText;
    Button send;



    private UserApiServices userApiServices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Hawk.init(getApplicationContext()).build();

        mCalendar = Calendar.getInstance();


        initViews();

        //Create Retrofit service Client
       userApiServices = ApiClient.getClient().create(UserApiServices.class);




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


        //ToDO: Add these Line and functions on Login  Activity

        //for Sign up
        registerUserCall(userApiServices,"Tareq","mti.tareq2@gmail.com","01628399899"); //for only Single time


        //for sign in
        getUserCall(userApiServices,"mti.tareq2@gmail.com");// it will register device with token and Unique ID And will Save Sender id,name,email & phone in Shared prefs


        //Todo: user ID,Name,email,phone  & device Token should be save in shared pref


        //Todo: If shared prefs has the value of userId , user email &  deviceToken ,then we will start the MainActivity






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

                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                    newToken = instanceIdResult.getToken();
                        Log.d("newToken", newToken);
                    }});

                // Log and toast
                String msg = getString(R.string.msg_token_fmt, newToken);
                Log.d(TAG, msg);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });





        //Send Notification
        findViewById(R.id.setNotification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setText("");
                mDatePickerDialog.show(getSupportFragmentManager(),"Datepickerdialog");
            }
        });

        //Send Token for register
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set(Calendar.YEAR,year);
        mCalendar.set(Calendar.MONTH,monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        mTimePickerDialog.show(getSupportFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        mCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        mCalendar.set(Calendar.MINUTE,minute);
        mCalendar.set(Calendar.SECOND,second);

        sendNotification();

    }

    private void sendNotification() {

        setWishMessageCall(userApiServices
                            ,"title"
                            ,"body"
                            ,"01-10-2018"
                            ,"13-09-2018"
                            ,2
                            ,1
                            ,1
                            ,"C_title",
                            "location_id");
    }


    private String retriveTime(Calendar calendar){

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); //"yyyy MMM dd HH:mm:ss"

        String formattedDate= sdf.format(calendar.getTime());

        mTextView.setText(formattedDate);

        return formattedDate;



    }


    private void registerUserCall(UserApiServices userApiServices, String userName, String userEmail,String userPhone) {

        Call<UserRegisterModel> registrationCall=userApiServices.userRegisterResponse(userName,userEmail,userPhone); //passing the paremeter value

        registrationCall.enqueue(new Callback<UserRegisterModel>() {
                                     @Override
                                     public void onResponse(Call<UserRegisterModel> call, Response<UserRegisterModel> response) {
                                         Toast.makeText(MainActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                     }

                                     @Override
                                     public void onFailure(Call<UserRegisterModel> call, Throwable t) {
                                        Log.d("" +getClass().getName(), "User register failed"+ t.getMessage());
                                     }
                                 }


        );

    }




    private void setWishMessageCall(UserApiServices userApiServices,
                                    String wm_title,
                                    String wm_body,
                                    String wm_recieving_date,
                                    String wm_sending_date,
                                    int wm_reciever_id,
                                    int wm_sender_id,
                                    int wm_category_id,
                                    String wm_category_title,
                                    String wm_location_id) {

        Call<WishMessageModel> setWishMsgCall= userApiServices.wishMessageResponse(      wm_title,
                                                                                           wm_body,
                                                                                           wm_recieving_date,
                                                                                           wm_sending_date,
                                                                                           wm_reciever_id,
                                                                                           wm_sender_id,
                                                                                           wm_category_id,
                                                                                           wm_category_title,
                                                                                           wm_location_id);
         //passing the paremeter value

        setWishMsgCall.enqueue(new Callback<WishMessageModel>() {
            @Override
            public void onResponse(Call<WishMessageModel> call, Response<WishMessageModel> response) {
                retriveSetWishMsg(response.body().getError().toString(),  response.body().getMessage().toString());
              }

            @Override
            public void onFailure(Call<WishMessageModel> call, Throwable t) {
                Log.d("" +getClass().getName(), " Set Wish msg failed");
            }
        });

    }

    private void retriveSetWishMsg(String error, String message) {
        Log.d("" +getClass().getName(), "Set Wish"+ message);

        Toast.makeText(MainActivity.this, error + ""+message, Toast.LENGTH_LONG).show();
    }


    private void  getUserCall(UserApiServices userApiServices, String _email){


        Call<UserModel> getUserModelCall=userApiServices.getUserResponse(_email);

        getUserModelCall.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                retriveUser(response.body().getMessage().getUId().toString()
                            ,response.body().getMessage().getUName()
                            ,response.body().getMessage().getUEmail()
                            ,response.body().getMessage().getUPhone()
                        );
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
        Log.d("" +getClass().getName(), " get user failed");
            }
        });

    }

  /*  //Todo Delete the static field and use shared pref instead
    private static int userID = -1;
    private static String userName;
    private static String userEmail;
    private static String userPhone;*/

    void retriveUser(String _userID,String _userName,String _userEmail,String _userPhone){
   //     userID=Integer.parseInt(_userID);
       Hawk.put(Hawk_Keys.user_id.toString(), Integer.parseInt(_userID));
     //   userName=_userName;
        Hawk.put(Hawk_Keys.user_name.toString(),_userName);
       // userEmail=_userEmail;
        Hawk.put(Hawk_Keys.user_email.toString(),_userEmail);
       // userPhone=_userPhone;
        Hawk.put(Hawk_Keys.user_phone.toString(),_userPhone);


        //Todo: On Sign in Success we will register the device
        registerDevice(userApiServices, Hawk.get(Hawk_Keys.user_id.toString(),-1));
    }
    private void registerDevice(final UserApiServices mUserApiServices, final int _userId) {

        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);



        String token= Hawk.get(Hawk_Keys.device_token.toString());
        if (token == null) {

            // Get token

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String newToken = instanceIdResult.getToken();
                    Log.d("newToken",newToken);
                    Hawk.put(Hawk_Keys.device_token.toString(),newToken);

                    registerDevice(mUserApiServices,_userId); //Doing recursion this will help to made the code moduler

                }
            });

        }else {

            Toast.makeText(this, "Token found", Toast.LENGTH_LONG).show();

            //Regiser Devices
            registerDeviceCall(mUserApiServices, androidId, token, _userId);
        }

    }




    private void registerDeviceCall(UserApiServices userApiServices, String d_uniqueID, String token,int userID) {

        Call<DeviceRegistrationModel> registrationCall=userApiServices.deviceRegisterResponse(d_uniqueID,token,userID); //passing the paremeter value

        registrationCall.enqueue(new Callback<DeviceRegistrationModel>() {

            @Override
            public void onResponse(Call<DeviceRegistrationModel> call, Response<DeviceRegistrationModel> response) {

                //Todo: On Success Taks should be right there

                try {
                    Log.d("Tareq", response.body().getError().toString());
                    Log.d("Tareq", response.body().getMessage().toString());
                    Toast.makeText(MainActivity.this, "Error: " + response.body().getError().toString()
                            + "\n msg: " + response.body().getMessage().toString(), Toast.LENGTH_SHORT).show();

                } catch (Exception e){

                }

            }

            @Override
            public void onFailure(Call<DeviceRegistrationModel> call, Throwable t) {

            }

        });

    }

}
