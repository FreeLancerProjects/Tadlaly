package com.semicolon.tadlaly.SingleTone;

public class LatLngSingleTone {
    private static LatLngSingleTone instance=null;
    private double lat=0.0,lng=0.0;
    private LatLngSingleTone(){}

    public void setLatLng(double lat,double lng)
    {
        this.lat = lat;
        this.lng = lng;
    }
    public static synchronized LatLngSingleTone getInstance()
    {
        if (instance==null)
        {
            instance = new LatLngSingleTone();
        }
        return instance;
    }
    public void getLatLng(onLatLngSuccess onLatLngSuccess)
    {
        onLatLngSuccess.onSuccess(lat,lng);
    }

    public interface onLatLngSuccess
    {
       void onSuccess(double lat,double lng);
    }
}
