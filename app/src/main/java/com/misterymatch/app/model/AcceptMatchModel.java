package com.misterymatch.app.model;

import com.google.gson.annotations.SerializedName;

public class AcceptMatchModel {

    private Integer id;
    @SerializedName("sender_id")
    private Integer senderId;
    @SerializedName("receiver_id")
    private Integer receiverId;
    @SerializedName("current_receiver_id")
    private Integer currentReceiverId;
    @SerializedName("receiver_access")
    private String receiverToken;
    private String status;

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

    public String getReceiverToken() {
        return receiverToken;
    }

    public String getStatus() {
        return status;
    }
}
