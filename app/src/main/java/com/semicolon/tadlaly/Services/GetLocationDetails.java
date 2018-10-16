package com.semicolon.tadlaly.Services;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.semicolon.tadlaly.Activities.RegisterActivity;
import com.semicolon.tadlaly.Activities.UpdateAdsActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetLocationDetails {
    private RegisterActivity registerActivity;
    private UpdateAdsActivity updateAdsActivity;
    private String city;
    public void getLocation(Context context,String type,double lat,double lng)
    {
        Log.e("dfdddddd","bbbbbbb");

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(lat,lng,1);

            if (addressList.size()>0)
            {
                Address address = addressList.get(0);

                if (address!=null)
                {
                    if (address.getLocality()!=null)
                    {
                        city = address.getLocality();

                        if (type.equals("1"))
                        {

                            registerActivity = (RegisterActivity) context;
                            registerActivity.setFullLocation(city);
                        }else if (type.equals("2"))
                        {
                            updateAdsActivity = (UpdateAdsActivity) context;
                            updateAdsActivity.setFullLocation(city);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
