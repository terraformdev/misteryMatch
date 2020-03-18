package com.misterymatch.app.library;


import com.misterymatch.app.model.dto.response.BaseResponse;

/**
 * Created by CSS03 on 10-01-2018.
 */

public class CustomException extends Exception {

    private int code;
    private String exception;

    public CustomException(int code, String exception) {
        this.code = code;
        this.exception = exception;
    }

    public CustomException(int code, Throwable throwable) {
        this.code = code;
        this.exception = throwable.getMessage();
    }

    public CustomException(int code, BaseResponse response){
        this.code = code;
        this.exception = response.getMessage();
    }

    public CustomException(BaseResponse response){
        ExceptionTracker.track(response.getMessage());
        this.exception = response.getMessage();
    }

    public CustomException(){}

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
