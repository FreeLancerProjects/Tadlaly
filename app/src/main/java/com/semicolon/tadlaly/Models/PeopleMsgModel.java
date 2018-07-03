package com.semicolon.tadlaly.Models;

import java.io.Serializable;

public class PeopleMsgModel implements Serializable {
    private String id_message;
    private String from_id;
    private String to_id;
    private String content_message;
    private String time_message;
    private String from_name;
    private String from_phone;
    private String from_image;

    public String getId_message() {
        return id_message;
    }

    public String getFrom_id() {
        return from_id;
    }

    public String getTo_id() {
        return to_id;
    }

    public String getContent_message() {
        return content_message;
    }

    public String getTime_message() {
        return time_message;
    }

    public String getFrom_name() {
        return from_name;
    }

    public String getFrom_phone() {
        return from_phone;
    }

    public String getFrom_image() {
        return from_image;
    }
}
