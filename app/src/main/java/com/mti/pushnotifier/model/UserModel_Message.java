
package com.mti.pushnotifier.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class UserModel_Message {

    @SerializedName("u_email")
    private String mUEmail;
    @SerializedName("u_id")
    private Long mUId;
    @SerializedName("u_name")
    private String mUName;
    @SerializedName("u_phone")
    private String mUPhone;

    public String getUEmail() {
        return mUEmail;
    }

    public void setUEmail(String uEmail) {
        mUEmail = uEmail;
    }

    public Long getUId() {
        return mUId;
    }

    public void setUId(Long uId) {
        mUId = uId;
    }

    public String getUName() {
        return mUName;
    }

    public void setUName(String uName) {
        mUName = uName;
    }

    public String getUPhone() {
        return mUPhone;
    }

    public void setUPhone(String uPhone) {
        mUPhone = uPhone;
    }

}
