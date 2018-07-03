package com.semicolon.tadlaly.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MyLocation implements Serializable {
    @SerializedName("results")
    private List<PlaceData>results;

    public List<PlaceData> getResults() {
        return results;
    }

    public class PlaceData implements Serializable
    {
        @SerializedName("address_components")
        private List<PlaceComponent> address_components;
        private String formatted_address;

        public List<PlaceComponent> getAddress_components() {
            return address_components;
        }

        public String getFormatted_address() {
            return formatted_address;
        }
    }

    public class PlaceComponent implements Serializable
    {
        private String long_name;
        private String short_name;

        public String getLong_name() {
            return long_name;
        }

        public String getShort_name() {
            return short_name;
        }
    }
}
