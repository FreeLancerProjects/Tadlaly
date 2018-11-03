package com.semicolon.tadlaly.Models;

import java.io.Serializable;

public class ResponseModel implements Serializable{
    private int success;
    private int success_delete;
    public int getSuccess() {
        return success;
    }

    public int getSuccess_delete() {
        return success_delete;
    }
}
