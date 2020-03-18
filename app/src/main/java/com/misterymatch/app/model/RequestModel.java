package com.misterymatch.app.model;

import com.google.gson.annotations.SerializedName;

public class RequestModel {

    private Integer id;
    @SerializedName("sender_id")
    private Integer senderId;
    @SerializedName("receiver_id")
    private Integer receiverId;
    @SerializedName("current_receiver_id")
    private Integer currentReceiverId;
    @SerializedName("video_id")
    private String senderToken;
    @SerializedName("receiver_access")
    private String receiverToken;
    private String status;
    @SerializedName("video_session")
    private Integer isVideoCompleted;
    @SerializedName("sender_rated")
    private Integer senderRated;
    @SerializedName("receiver_rated")
    private Integer receiverRated;
    private UserModel sender;

    public Integer getId() {
        return id;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public Integer getCurrentReceiverId() {
        return currentReceiverId;
    }

    public String getSenderToken() {
        return senderToken;
    }

    public String getReceiverToken() {
        return receiverToken;
    }

    public String getStatus() {
        return status;
    }

    public Integer getIsVideoCompleted() {
        return isVideoCompleted;
    }

    public Integer getSenderRated() {
        return senderRated;
    }

    public Integer getReceiverRated() {
        return receiverRated;
    }

    public UserModel getSender() {
        return sender;
    }
}
