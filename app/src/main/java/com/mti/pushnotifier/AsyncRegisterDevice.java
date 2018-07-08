/*
 * Created by Tareq Islam on 7/7/18 7:52 PM
 *
 *  Last modified 7/7/18 7:52 PM
 */

package com.mti.pushnotifier;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/***
 * Created by Tareq on 07,July,2018.
 */
/*
   *In the AsyncTAsk<X,Y,Z> :

   * the first type is for doInBackground(X x)

   * the second type is for onProgressUpdate(Y y)

   * the third type is for onPostExecute(Z z)
   *
   ----------------------------------------------
   Here : X – The type of the input variables value you want to set to the background process. This can be an array of objects.

          Y – The type of the objects you are going to enter in the onProgressUpdate method.

          Z – The type of the result from the operations you have done in the background process.
   -------------------------------------------------------------
   For calling this:
   if we consider class as MyTask, then:

   *        MyTask myTask = new MyTask();

    *            myTask.execute(x); //now pass the value in execute as x
*/

public class AsyncRegisterDevice extends AsyncTask<String, String, String>
{

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;



    private Context mContext;

    private    ProgressDialog pdLoading;
    private HttpURLConnection conn;
    private URL url = null;
    //Constructor
    public AsyncRegisterDevice(Context context) {
        this.mContext=context;
        pdLoading= new ProgressDialog(context);
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();


        //this method will be running on UI thread
        pdLoading.setMessage("\tLoading...");
        pdLoading.setCancelable(false);
        pdLoading.show();

    }
    @Override
    protected String doInBackground(String... params) {
        try {

            // Enter URL address where your php file resides
            // url = new URL("http://wishermanager.000webhostapp.com/tokens/crud_db.php");
            url = new URL("http://192.168.0.108/FcmExample/RegisterDevice.php");

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "exception";
        }
        try {
            // Setup HttpURLConnection class to send and receive data from php and mysql
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");

            // setDoInput and setDoOutput method depict handling of both send and receive
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Append parameters to URL
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("email", params[0])
                    .appendQueryParameter("token", params[1]);
            String query = builder.build().getEncodedQuery();

            // Open connection for sending data
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return "exception";
        }

        try {

            int response_code = conn.getResponseCode();

            // Check if successful connection made
            if (response_code == HttpURLConnection.HTTP_OK) {

                // Read data sent from server
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Pass data to onPostExecute method
                return(result.toString());

            }else{

                return("unsuccessful");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "exception";
        } finally {
            conn.disconnect();
        }


    }

    @Override
    protected void onPostExecute(String result) {

        //this method will be running on UI thread

        pdLoading.dismiss();

        if(result.equalsIgnoreCase("true")|| result.equals("0") ) {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */


            Toast.makeText(mContext, "Device has been registered successfully", Toast.LENGTH_LONG).show();
        }else if(result.equals("2")){
            Toast.makeText(mContext, "You are already registered !", Toast.LENGTH_SHORT).show();
        }else if (result.equalsIgnoreCase("false") ){

            // If username and password does not match display a error message
            Toast.makeText(mContext, "Invalid email or password", Toast.LENGTH_LONG).show();

        } else if (result.equalsIgnoreCase("exception")|| result.equals("1") || result.equalsIgnoreCase("unsuccessful") ) {

            Toast.makeText(mContext, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

        }
    }

}