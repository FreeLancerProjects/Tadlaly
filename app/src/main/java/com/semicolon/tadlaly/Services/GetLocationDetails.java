package com.semicolon.tadlaly.Services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.semicolon.tadlaly.Activities.RegisterActivity;
import com.semicolon.tadlaly.Activities.UpdateAdsActivity;
import com.semicolon.tadlaly.Models.MyLocation;
import com.semicolon.tadlaly.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetLocationDetails {
    private RegisterActivity registerActivity;
    private UpdateAdsActivity updateAdsActivity;
    private String city;
    public void getLocation(Context context,String type,double lat,double lng)
    {
        Log.e("dfdddddd","bbbbbbb");
        Retrofit retrofit = Api.getRetrofit(Tags.GeoPlaceUrl);
        Call<MyLocation> call = retrofit.create(Services.class).getMyLocationAddress("https://maps.googleapis.com/maps/api/geocode/json?latlng="+lat + "," + lng +"&sensor=true & key="+ context.getString(R.string.Api_key));
        call.enqueue(new Callback<MyLocation>() {
            @Override
            public void onResponse(Call<MyLocation> call, Response<MyLocation> response) {
                if (response.isSuccessful())
                {
                    MyLocation myLocation = response.body();
                    if (myLocation.getResults().size()>0)
                    {
                        MyLocation.PlaceData placeData = myLocation.getResults().get(0);
                        if (placeData.getAddress_components().size()>0)
                        {
                            if (placeData.getAddress_components().size()>=3)
                            {
                                if (type.equals("1"))
                                {
                                    city = placeData.getAddress_components().get(1).getLong_name()+" "+placeData.getAddress_components().get(placeData.getAddress_components().size()-1).getLong_name();

                                    registerActivity = (RegisterActivity) context;
                                    registerActivity.setFullLocation(city);
                                }else if (type.equals("2"))
                                {
                                    city = placeData.getAddress_components().get(1).getLong_name()+" "+placeData.getAddress_components().get(placeData.getAddress_components().size()-1).getLong_name();
                                    updateAdsActivity = (UpdateAdsActivity) context;
                                    updateAdsActivity.setFullLocation(city);
                                }

                            }else if (placeData.getAddress_components().size()==2)
                            { if (type.equals("1"))
                            {
                                city = placeData.getAddress_components().get(0).getLong_name()+" "+placeData.getAddress_components().get(placeData.getAddress_components().size()-1).getLong_name();
                                registerActivity = (RegisterActivity) context;
                                registerActivity.setFullLocation(city);
                            }else if (type.equals("2"))
                            {
                                city = placeData.getAddress_components().get(0).getLong_name()+" "+placeData.getAddress_components().get(placeData.getAddress_components().size()-1).getLong_name();
                                updateAdsActivity = (UpdateAdsActivity) context;
                                updateAdsActivity.setFullLocation(city);
                            }

                            }
                            else if (placeData.getAddress_components().size()>=1)
                            {
                                if (type.equals("1"))
                                {
                                    city = placeData.getAddress_components().get(0).getLong_name();
                                    registerActivity = (RegisterActivity) context;
                                    registerActivity.setFullLocation(city);
                                }else if (type.equals("2"))
                                {
                                    city = placeData.getAddress_components().get(0).getLong_name();
                                    updateAdsActivity = (UpdateAdsActivity) context;
                                    updateAdsActivity.setFullLocation(city);
                                }

                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MyLocation> call, Throwable t) {

                Log.e("error",t.getMessage());
                Toast.makeText(context,R.string.something, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
