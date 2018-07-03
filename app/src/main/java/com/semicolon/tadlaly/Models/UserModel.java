package com.semicolon.tadlaly.Models;

import java.io.Serializable;

public class UserModel implements Serializable{
    private String user_id;
    private String user_name;
    private String user_pass;
    private String user_type;
    private String user_full_name;
    private String user_phone;
    private String user_email;
    private String user_photo;
    private String user_city;
    private String user_token_id;
    private String user_google_lat;
    private String user_google_long;
    private int success;

    public UserModel(String user_id, String user_name, String user_pass, String user_type, String user_full_name, String user_phone, String user_email, String user_photo, String user_city, String user_token_id, String user_google_lat, String user_google_long) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_pass = user_pass;
        this.user_type = user_type;
        this.user_full_name = user_full_name;
        this.user_phone = user_phone;
        this.user_email = user_email;
        this.user_photo = user_photo;
        this.user_city = user_city;
        this.user_token_id = user_token_id;
        this.user_google_lat = user_google_lat;
        this.user_google_long = user_google_long;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_pass() {
        return user_pass;
    }

    public String getUser_type() {
        return user_type;
    }

    public String getUser_full_name() {
        return user_full_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public String getUser_city() {
        return user_city;
    }

    public String getUser_token_id() {
        return user_token_id;
    }

    public String getUser_google_lat() {
        return user_google_lat;
    }

    public String getUser_google_long() {
        return user_google_long;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getSuccess() {
        return success;
    }
}
