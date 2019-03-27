package com.semicolon.tadlaly.Models;

import java.io.Serializable;

public class AccuracyModel implements Serializable {

    private int accuracy;

    public AccuracyModel(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getAccuracy() {
        return accuracy;
    }
}
