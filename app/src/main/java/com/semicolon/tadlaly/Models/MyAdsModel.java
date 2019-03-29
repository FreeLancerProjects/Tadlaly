package com.semicolon.tadlaly.Models;

import java.io.Serializable;
import java.util.List;

public class MyAdsModel implements Serializable {
    private String id_advertisement;
    //ads_depend on subdepartment
    private String advertisement_user;
    private String main_department_fk;
    private String sub_department_fk;
    private String is_sold;
    /////////////////////////////////
    private String advertisement_title;
    private String advertisement_content;
    private String advertisement_code;
    private String advertisement_price;
    private String advertisement_type;
    private String advertisement_date;
    private String advertisement_owner;
    private String advertisement_owner_image;

    private String google_lat;
    private String google_long;
    private String city;
    private String phone;
    private String show_phone;
    private String view_count;
    private String share_count;
    private double distance;

    private String department_name;
    private String department_image;
    private boolean read_status;

    private boolean readed = false;


    private List<Images> advertisement_image;
    private int success;

    public class Images implements Serializable
    {
        private String photo_name;
        private String id_photo;

        public String getPhoto_name() {
            return photo_name;
        }

        public String getId_photo() {
            return id_photo;
        }
    }


    public String getId_advertisement() {
        return id_advertisement;
    }

    public String getAdvertisement_code() {
        return advertisement_code;
    }

    public String getAdvertisement_price() {
        return advertisement_price;
    }

    public String getAdvertisement_type() {
        return advertisement_type;
    }

    public String getAdvertisement_date() {
        return advertisement_date;
    }

    public String getGoogle_lat() {
        return google_lat;
    }

    public String getGoogle_long() {
        return google_long;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public String getShow_phone() {
        return show_phone;
    }


    public String getView_count() {
        return view_count;
    }

    public String getShare_count() {
        return share_count;
    }

    public String getAdvertisement_title() {
        return advertisement_title;
    }

    public String getAdvertisement_content() {
        return advertisement_content;
    }

    public List<Images> getAdvertisement_image() {
        return advertisement_image;
    }

    public int getSuccess() {
        return success;
    }

    public String getAdvertisement_user() {
        return advertisement_user;
    }

    public String getMain_department_fk() {
        return main_department_fk;
    }

    public String getSub_department_fk() {
        return sub_department_fk;
    }

    public String getIs_sold() {
        return is_sold;
    }

    public double getDistance() {
        return distance;
    }

    public String getAdvertisement_owner() {
        return advertisement_owner;
    }

    public String getAdvertisement_owner_image() {
        return advertisement_owner_image;
    }

    public String getDepartment_name() {
        return department_name;
    }

    public String getDepartment_image() {
        return department_image;
    }

    public boolean isRead_status() {
        return read_status;
    }

    public void setRead_status(boolean read_status) {
        this.read_status = read_status;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }
}
