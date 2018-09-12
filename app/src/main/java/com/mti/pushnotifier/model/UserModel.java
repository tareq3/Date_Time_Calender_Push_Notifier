
package com.mti.pushnotifier.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class UserModel {

    @SerializedName("error")
    private Boolean mError;
    @SerializedName("message")
    private UserModel_Message mMessage;

    public Boolean getError() {
        return mError;
    }

    public void setError(Boolean error) {
        mError = error;
    }

    public UserModel_Message getMessage() {
        return mMessage;
    }

    public void setMessage(UserModel_Message message) {
        mMessage = message;
    }

}
