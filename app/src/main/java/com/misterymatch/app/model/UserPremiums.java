package com.misterymatch.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by CSS12 on 31-01-2018.
 */

public class UserPremiums {
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("premia_id")
    @Expose
    private String premiaId;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("premium")
    @Expose
    private UserPremium userPremium;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPremiaId() {
        return premiaId;
    }

    public void setPremiaId(String premiaId) {
        this.premiaId = premiaId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserPremium getUserPremium() {
        return userPremium;
    }

    public void setUserPremium(UserPremium userPremium) {
        this.userPremium = userPremium;
    }
}
