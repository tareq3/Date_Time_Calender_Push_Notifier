
/*
 * Created by Tareq Islam on 8/16/18 2:47 AM
 *
 *  Last modified 8/15/18 10:16 PM
 */

package com.mti.pushnotifier.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class DeviceRegistrationModel {

    @SerializedName("error")
    private Boolean mError;
    @SerializedName("message")
    private String mMessage;

    public Boolean getError() {
        return mError;
    }

    public void setError(Boolean error) {
        mError = error;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

}
