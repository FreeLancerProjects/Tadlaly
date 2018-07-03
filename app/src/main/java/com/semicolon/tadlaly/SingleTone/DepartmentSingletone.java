package com.semicolon.tadlaly.SingleTone;

import com.semicolon.tadlaly.Models.DepartmentsModel;

import java.util.List;

public class DepartmentSingletone {
    private static DepartmentSingletone instance=null;
    private List<DepartmentsModel> departmentsModelList;
    private DepartmentSingletone() {
    }

    public static synchronized DepartmentSingletone getInstansce()
    {
        if (instance==null)
        {
            instance = new DepartmentSingletone();
        }
        return instance;
    }

    public interface onCompleteListener
    {
        void onSuccess(List<DepartmentsModel> departmentsModelList);
    }
    public void setDpartmentData(List<DepartmentsModel> departmentsModelList)
    {
        this.departmentsModelList = departmentsModelList;
    }
    public void getDepartmentData(onCompleteListener listener)
    {
        listener.onSuccess(this.departmentsModelList);

    }
}
