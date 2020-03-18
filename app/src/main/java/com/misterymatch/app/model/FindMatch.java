package com.misterymatch.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santhosh@appoets.com on 28-12-2017.
 */

public class FindMatch {
    @SerializedName("user")
    @Expose
    private List<User> user = new ArrayList<>();

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }

    public FindMatch withUser(List<User> user) {
        this.user = user;
        return this;
    }
}
