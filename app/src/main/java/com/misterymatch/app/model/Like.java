package com.misterymatch.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by santhosh@appoets.com on 23-01-2018.
 */

public class Like {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("like_id")
    @Expose
    private Integer likeId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("super_like")
    @Expose
    private Integer superLike;
    @SerializedName("user_like")
    @Expose
    private UserLike userLike;
    @SerializedName("user")
    @Expose
    private UserLike user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getLikeId() {
        return likeId;
    }

    public void setLikeId(Integer likeId) {
        this.likeId = likeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSuperLike() {
        return superLike;
    }

    public void setSuperLike(Integer superLike) {
        this.superLike = superLike;
    }

    public UserLike getUserLike() {
        if(user != null){
            userLike  = user;
        }
        return userLike;
    }

    public void setUserLike(UserLike userLike) {
        this.userLike = userLike;
    }

    public UserLike getUser() {
        return user;
    }

    public void setUser(UserLike user) {
        if(user != null){
            this.user = user;
        }
    }
}
