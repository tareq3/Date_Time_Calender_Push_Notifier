/*
 * Created by Tareq Islam on 8/16/18 2:47 AM
 *
 *  Last modified 8/15/18 10:27 PM
 */

package com.mti.pushnotifier.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL="http://192.168.1.4/firebaseCloud_to_Android_using_mysql_PHP/v1/";

    private static Retrofit retrofit=null;

    public static Retrofit getClient(){
        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
