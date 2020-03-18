package com.misterymatch.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequestMatchModel {

    private String user;
    @SerializedName("sender_response")
    private List<SenderResponseModel> senderResponse;
    @SerializedName("receiver_response")
    private List<ReceiverResponseModel> receiverResponse;

    public String getUser() {
        return user;
    }

    public List<SenderResponseModel> getSenderResponse() {
        return senderResponse;
    }

    public List<ReceiverResponseModel> getReceiverResponse() {
        return receiverResponse;
    }
}
