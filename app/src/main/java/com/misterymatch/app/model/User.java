package com.misterymatch.app.model;

/**
 * Created by santhosh@appoets.com on 30-11-2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

// missing dob, age

public class User {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("device_token")
    @Expose
    private String deviceToken;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("device_type")
    @Expose
    private String deviceType;
    @SerializedName("login_by")
    @Expose
    private String loginBy;
    @SerializedName("social_unique_id")
    @Expose
    private String socialUniqueId;
    @SerializedName("wallet_balance")
    @Expose
    private Integer walletBalance;
    @SerializedName("interest")
    @Expose
    private String interest;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("bio_video")
    @Expose
    private String bioVideo;
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("work")
    @Expose
    private String work;
    @SerializedName("show_age")
    @Expose
    private Integer showAge;
    @SerializedName("show_distance")
    @Expose
    private Integer showDistance;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("distance")
    @Expose
    private Double distance;

    @SerializedName("stripe_cust_id")
    @Expose
    private String stripeCustId;

    @SerializedName("invite_code")
    @Expose
    private String inviteCode;

    @SerializedName("user_preferences")
    @Expose
    private Object userPreferences;
    @SerializedName("user_images")
    @Expose
    private List<UserImage> userImages = new ArrayList<>();

    @SerializedName("user_interest")
    @Expose
    private List<UserInterest> userInterest = new ArrayList<>();

    @SerializedName("user_premium")
    private UserPremiums userPremium;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User withUsername(String username) {
        this.username = username;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public User withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public User withGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User withEmail(String email) {
        this.email = email;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public User withMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public User withCountry(String country) {
        this.country = country;
        return this;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public User withDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public User withDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public User withLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public User withLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public User withDeviceType(String deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    public String getLoginBy() {
        return loginBy;
    }

    public void setLoginBy(String loginBy) {
        this.loginBy = loginBy;
    }

    public User withLoginBy(String loginBy) {
        this.loginBy = loginBy;
        return this;
    }

    public String getSocialUniqueId() {
        return socialUniqueId;
    }

    public void setSocialUniqueId(String socialUniqueId) {
        this.socialUniqueId = socialUniqueId;
    }

    public Integer getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Integer walletBalance) {
        this.walletBalance = walletBalance;
    }

    public User withWalletBalance(Integer walletBalance) {
        this.walletBalance = walletBalance;
        return this;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getStripeCustId() {
        return stripeCustId;
    }

    public void setStripeCustId(String stripeCustId) {
        this.stripeCustId = stripeCustId;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public User withPicture(String picture) {
        this.picture = picture;
        return this;
    }

    public String getBioVideo() {
        return bioVideo;
    }

    public void setBioVideo(String bioVideo) {
        this.bioVideo = bioVideo;
    }

    public User withBioVideo(String bioVideo) {
        this.bioVideo = bioVideo;
        return this;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public User withAbout(String about) {
        this.about = about;
        return this;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public User withWork(String work) {
        this.work = work;
        return this;
    }

    public Integer getShowAge() {
        return showAge;
    }

    public void setShowAge(Integer showAge) {
        this.showAge = showAge;
    }

    public User withShowAge(Integer showAge) {
        this.showAge = showAge;
        return this;
    }

    public Integer getShowDistance() {
        return showDistance;
    }

    public void setShowDistance(Integer showDistance) {
        this.showDistance = showDistance;
    }

    public User withShowDistance(Integer showDistance) {
        this.showDistance = showDistance;
        return this;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User withUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public User withDistance(Double distance) {
        this.distance = distance;
        return this;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Object getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(Object userPreferences) {
        this.userPreferences = userPreferences;
    }

    public User withUserPreferences(Object userPreferences) {
        this.userPreferences = userPreferences;
        return this;
    }

    public List<UserImage> getUserImages() {
        return userImages;
    }

    public void setUserImages(List<UserImage> userImages) {
        this.userImages = userImages;
    }

    public User withUserImages(List<UserImage> userImages) {
        this.userImages = userImages;
        return this;
    }

    public List<UserInterest> getUserInterest() {
        return userInterest;
    }

    public void setUserInterest(List<UserInterest> userInterest) {
        this.userInterest = userInterest;
    }

    public UserPremiums getUserPremium() {
        return userPremium;
    }
}
