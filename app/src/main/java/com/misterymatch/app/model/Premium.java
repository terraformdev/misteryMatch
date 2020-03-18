package com.misterymatch.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by CSS12 on 31-01-2018.
 */

public class Premium {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("plan_name")
    @Expose
    private String planName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("period")
    @Expose
    private Integer period;
    @SerializedName("gem")
    @Expose
    private Integer gem;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("superlike")
    @Expose
    private Integer superlike;
    @SerializedName("video_call")
    @Expose
    private Integer videoCall;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getGem() {
        return gem;
    }

    public void setGem(Integer gem) {
        this.gem = gem;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getSuperlike() {
        return superlike;
    }

    public void setSuperlike(Integer superlike) {
        this.superlike = superlike;
    }

    public Integer getVideoCall() {
        return videoCall;
    }

    public void setVideoCall(Integer videoCall) {
        this.videoCall = videoCall;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
