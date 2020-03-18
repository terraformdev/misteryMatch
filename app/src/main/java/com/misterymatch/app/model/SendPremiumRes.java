package com.misterymatch.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by CSS12 on 31-01-2018.
 */

public class SendPremiumRes {
    @SerializedName("user_premiums")
    @Expose
    private UserPremiums userPremiums;
    @SerializedName("message")
    @Expose
    private String message;

    public UserPremiums getUserPremiums() {
        return userPremiums;
    }

    public void setUserPremiums(UserPremiums userPremiums) {
        this.userPremiums = userPremiums;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
