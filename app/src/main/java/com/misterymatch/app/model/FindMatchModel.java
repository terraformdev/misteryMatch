package com.misterymatch.app.model;

import com.google.gson.annotations.SerializedName;

public class FindMatchModel {

    private String message;
    @SerializedName("request_id")
    private Integer requestId;

    public String getMessage() {
        return message;
    }

    public Integer getRequestId() {
        return requestId;
    }
}
