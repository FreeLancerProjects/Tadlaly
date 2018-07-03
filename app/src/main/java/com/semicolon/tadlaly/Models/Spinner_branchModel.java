package com.semicolon.tadlaly.Models;

import java.io.Serializable;

public class Spinner_branchModel implements Serializable {
    private String id;
    private String name;

    public Spinner_branchModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
