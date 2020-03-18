package com.misterymatch.app.model;

import com.google.gson.annotations.SerializedName;

public class ReceiverResponseModel {

    private Integer id;
    @SerializedName("request_id")
    private Integer requestId;
    @SerializedName("receiver_id")
    private Integer receiverId;
    private Integer status;
    private RequestModel request;
    private UserModel receiver;

    public Integer getId() {
        return id;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public Integer getStatus() {
        return status;
    }

    public RequestModel getRequest() {
        return request;
    }

    public UserModel getReceiver() {
        return receiver;
    }
}
