package com.misterymatch.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MatchResponse {

    @SerializedName("sender_response")
    List<Object> senderResponse;
    @SerializedName("receiver_response")
    List<Object> receiverResponse;
}
