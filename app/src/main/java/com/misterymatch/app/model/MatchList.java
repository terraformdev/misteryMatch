package com.misterymatch.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by santhosh@appoets.com on 19-01-2018.
 */

public class MatchList {
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
    @SerializedName("last_message")
    @Expose
    private String lastMessage;
    @SerializedName("timestamp")
    @Expose
    private long timestamp = -1;

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
        if (user != null)
            return user;
        else
            return userLike;
    }

    public void setUserLike(UserLike userLike) {

        this.userLike = userLike;
    }


    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


}
