package com.misterymatch.app.model;

import com.google.gson.annotations.SerializedName;

public class UserModel {

    private int id;
    private String username;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    private String gender;
    private String dob;
    private String age;
    private String email;
    private String mobile;
    private String country;
    private String address;
    private Double latitude;
    private Double longitude;
    private String picture;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getCountry() {
        return country;
    }

    public String getAddress() {
        return address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getPicture() {
        return picture;
    }
}
