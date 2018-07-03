package com.semicolon.tadlaly.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DepartmentsModel implements Serializable {
    private String main_department_id;
    private String main_department_name;
    private String main_department_image;
    @SerializedName("sub_depart")
    private List<SubdepartObject> subdepartObjectList;


    public String getMain_department_id() {
        return main_department_id;
    }

    public String getMain_department_name() {
        return main_department_name;
    }

    public String getMain_department_image() {
        return main_department_image;
    }

    public List<SubdepartObject> getSubdepartObjectList() {
        return subdepartObjectList;
    }
    public class SubdepartObject implements Serializable
    {
        private String sub_department_id;
        private String sub_department_name;
        private String sub_department_image;

        public String getSub_department_id() {
            return sub_department_id;
        }

        public String getSub_department_name() {
            return sub_department_name;
        }

        public String getSub_department_image() {
            return sub_department_image;
        }
    }
}
