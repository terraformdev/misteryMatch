package com.misterymatch.app.model.dto.response;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by CSS03 on 10-01-2018.
 */

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class BaseResponse {

    private int status;
    private String message;
    private String error;


    public BaseResponse() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
