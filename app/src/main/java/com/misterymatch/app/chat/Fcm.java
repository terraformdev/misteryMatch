package com.misterymatch.app.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by santhosh@appoets.com on 03-03-2018.
 */

public class Fcm {

    @SerializedName("to")
    @Expose
    private String to;
    @SerializedName("notification")
    @Expose
    private FcmNotification fcmNotification;
    @SerializedName("data")
    @Expose
    private FcmData fcmData;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public FcmNotification getFcmNotification() {
        return fcmNotification;
    }

    public void setFcmNotification(FcmNotification fcmNotification) {
        this.fcmNotification = fcmNotification;
    }

    public FcmData getFcmData() {
        return fcmData;
    }

    public void setFcmData(FcmData fcmData) {
        this.fcmData = fcmData;
    }

}


class FcmData {

    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("content_available")
    @Expose
    private Boolean contentAvailable;
    @SerializedName("priority")
    @Expose
    private String priority;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getContentAvailable() {
        return contentAvailable;
    }

    public void setContentAvailable(Boolean contentAvailable) {
        this.contentAvailable = contentAvailable;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}

class FcmNotification {

    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("content_available")
    @Expose
    private Boolean contentAvailable;
    @SerializedName("priority")
    @Expose
    private String priority;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getContentAvailable() {
        return contentAvailable;
    }

    public void setContentAvailable(Boolean contentAvailable) {
        this.contentAvailable = contentAvailable;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}