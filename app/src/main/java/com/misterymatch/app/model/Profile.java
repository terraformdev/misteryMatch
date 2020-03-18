package com.misterymatch.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by CSS03 on 11-01-2018.
 */

public class Profile {
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("interest")
    @Expose
    private List<Interest> interest = null;
    private String currency;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Interest> getInterest() {
        return interest;
    }

    public void setInterest(List<Interest> interest) {
        this.interest = interest;
    }

    public String getCurrency() {
        return currency;
    }
}
