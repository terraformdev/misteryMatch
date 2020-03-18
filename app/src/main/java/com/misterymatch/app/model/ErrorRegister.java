package com.misterymatch.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 30-11-2017.
 */

public class ErrorRegister {
    @SerializedName("device_type")
    @Expose
    private List<String> deviceType = null;
    @SerializedName("device_token")
    @Expose
    private List<String> deviceToken = null;
    @SerializedName("device_id")
    @Expose
    private List<String> deviceId = null;
    @SerializedName("login_by")
    @Expose
    private List<String> loginBy = null;
    @SerializedName("first_name")
    @Expose
    private List<String> firstName = null;
    @SerializedName("last_name")
    @Expose
    private List<String> lastName = null;
    @SerializedName("email")
    @Expose
    private List<String> email = null;
    @SerializedName("mobile")
    @Expose
    private List<String> mobile = null;
    @SerializedName("password")
    @Expose
    private List<String> password = null;

    public List<String> getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(List<String> deviceType) {
        this.deviceType = deviceType;
    }

    public ErrorRegister withDeviceType(List<String> deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    public List<String> getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(List<String> deviceToken) {
        this.deviceToken = deviceToken;
    }

    public ErrorRegister withDeviceToken(List<String> deviceToken) {
        this.deviceToken = deviceToken;
        return this;
    }

    public List<String> getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(List<String> deviceId) {
        this.deviceId = deviceId;
    }

    public ErrorRegister withDeviceId(List<String> deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public List<String> getLoginBy() {
        return loginBy;
    }

    public void setLoginBy(List<String> loginBy) {
        this.loginBy = loginBy;
    }

    public ErrorRegister withLoginBy(List<String> loginBy) {
        this.loginBy = loginBy;
        return this;
    }

    public List<String> getFirstName() {
        return firstName;
    }

    public void setFirstName(List<String> firstName) {
        this.firstName = firstName;
    }

    public ErrorRegister withFirstName(List<String> firstName) {
        this.firstName = firstName;
        return this;
    }

    public List<String> getLastName() {
        return lastName;
    }

    public void setLastName(List<String> lastName) {
        this.lastName = lastName;
    }

    public ErrorRegister withLastName(List<String> lastName) {
        this.lastName = lastName;
        return this;
    }

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public ErrorRegister withEmail(List<String> email) {
        this.email = email;
        return this;
    }

    public List<String> getMobile() {
        return mobile;
    }

    public void setMobile(List<String> mobile) {
        this.mobile = mobile;
    }

    public ErrorRegister withMobile(List<String> mobile) {
        this.mobile = mobile;
        return this;
    }

    public List<String> getPassword() {
        return password;
    }

    public void setPassword(List<String> password) {
        this.password = password;
    }

    public ErrorRegister withPassword(List<String> password) {
        this.password = password;
        return this;
    }

}
