package com.semicolon.tadlaly.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PlacesDistanceModel implements Serializable {

    @SerializedName("rows")
    List<ElementObject> rows;

    public List<ElementObject> getRows() {
        return rows;
    }

    public class ElementObject implements Serializable
    {
        @SerializedName("elements")
        List<AllData> elements;

        public List<AllData> getElements() {
            return elements;
        }
    }

    public class AllData implements Serializable
    {
        @SerializedName("distance")
        DistanceObject distanceObject;
        @SerializedName("duration")
        DurationObject durationObject;

        public DistanceObject getDistanceObject() {
            return distanceObject;
        }

        public DurationObject getDurationObject() {
            return durationObject;
        }
    }

    public class DistanceObject implements Serializable
    {
        private String text;
        private int value;

        public String getText() {
            return text;
        }

        public int getValue() {
            return value;
        }
    }

    public class DurationObject implements Serializable
    {
        private String text;

        public String getText() {
            return text;
        }
    }
}
