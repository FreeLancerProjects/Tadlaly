package com.semicolon.tadlaly.Models;

import java.io.Serializable;

public class SlideShowModel implements Serializable{
    private String id;
    private String image_title;
    private String image_content;
    private String image;
    private String show_in_home;
    private String date_s;
    private String publisher;

    public String getId() {
        return id;
    }

    public String getImage_title() {
        return image_title;
    }

    public String getImage_content() {
        return image_content;
    }

    public String getImage() {
        return image;
    }

    public String getShow_in_home() {
        return show_in_home;
    }

    public String getDate_s() {
        return date_s;
    }

    public String getPublisher() {
        return publisher;
    }
}
