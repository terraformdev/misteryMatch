package com.misterymatch.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CSS12 on 31-01-2018.
 */

public class PremiumRes {
    @SerializedName("premiums")
    @Expose
    private List<Premium> premiums = new ArrayList<>();

    @SerializedName("user_premium")
    @Expose
    private UserPremiums userPremiums;
    @SerializedName("expiry_date")
    @Expose
    private ExpiryDate expiryDate;

    public List<Premium> getPremiums() {
        return premiums;
    }

    public void setPremiums(List<Premium> premiums) {
        this.premiums = premiums;
    }

    public UserPremiums getUserPremiums() {
        return userPremiums;
    }

    public void setUserPremiums(UserPremiums userPremiums) {
        this.userPremiums = userPremiums;
    }

    public ExpiryDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(ExpiryDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}
