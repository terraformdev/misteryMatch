package com.misterymatch.app.model.dto.response;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by CSS03 on 10-01-2018.
 */

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class SettingsResponse extends BaseResponse {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("age_limit")
    @Expose
    private String ageLimit;
    @SerializedName("distance")
    @Expose
    private Integer distance;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("notification_message")
    @Expose
    private Integer notificationMessage;
    @SerializedName("notification_match")
    @Expose
    private Integer notificationMatch;
    @SerializedName("dnd")
    @Expose
    private Integer dnd;
    @SerializedName("category")
    @Expose
    private Integer category;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAgeLimit() {
        return ageLimit;
    }

    public void setAgeLimit(String ageLimit) {
        this.ageLimit = ageLimit;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(Integer notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public Integer getNotificationMatch() {
        return notificationMatch;
    }

    public void setNotificationMatch(Integer notificationMatch) {
        this.notificationMatch = notificationMatch;
    }

    public Integer getDnd() {
        return dnd;
    }

    public void setDnd(Integer dnd) {
        this.dnd = dnd;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
