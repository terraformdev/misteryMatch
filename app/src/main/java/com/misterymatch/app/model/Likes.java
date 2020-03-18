package com.misterymatch.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by CSS03 on 16-01-2018.
 */

public class Likes {

    @SerializedName("like")
    @Expose
    private Like like;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("superlike_count")
    @Expose
    private Integer superlikeCount;

    public Like getLike() {
        return like;
    }

    public void setLike(Like like) {
        this.like = like;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSuperlikeCount() {
        return superlikeCount;
    }

    public void setSuperlikeCount(Integer superlikeCount) {
        this.superlikeCount = superlikeCount;
    }

}
