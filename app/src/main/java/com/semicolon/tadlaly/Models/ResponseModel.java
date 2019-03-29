package com.semicolon.tadlaly.Models;

import java.io.Serializable;

public class ResponseModel implements Serializable{
    private int success;
    private int success_delete;
    private int success_visit;
    private int success_doread;
    private  boolean status_follow;
    private int success_follow;

    public int getSuccess() {
        return success;
    }

    public int getSuccess_delete() {
        return success_delete;
    }

    public int getSuccess_visit() {
        return success_visit;
    }

    public int getSuccess_doread() {
        return success_doread;
    }

    public boolean isStatus_follow() {
        return status_follow;
    }

    public int getSuccess_follow() {
        return success_follow;
    }
}
