package com.misterymatch.app.model.dto.request;

import com.bluelinelabs.logansquare.annotation.JsonObject;


/**
 * Created by douglas on 7/13/2017.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class BaseRequest {
 private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
