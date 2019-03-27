package com.semicolon.tadlaly.Services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.semicolon.tadlaly.Models.AccuracyModel;
import com.semicolon.tadlaly.Models.LocationModel;

import org.greenrobot.eventbus.EventBus;

public class UpdateLatLng extends Service implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Handler handler;
    private Runnable runnable;
    private int accuracy;


    protected synchronized void BuildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        StartLocationUpdate();
    }

    @SuppressLint("MissingPermission")
    private void StartLocationUpdate() {

        initLocationRequest(accuracy);

        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest,new LocationCallback()
                {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                }, Looper.myLooper());
    }

    private void initLocationRequest(int accuracy)
    {
        Log.e("aaccc",accuracy+"_");
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(accuracy);
        locationRequest.setNumUpdates(1);

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new AccuracyModel(accuracy));
                handler.removeCallbacks(runnable);
            }
        };
        handler.postDelayed(runnable,5000);
    }




    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient!=null)
        {
            googleApiClient.connect();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.e("laaaaaaaaaaaat",location.getLatitude()+"_____");
        Log.e("loooooong",location.getLongitude()+"_____");

        LocationModel locationModel = new LocationModel(location.getLatitude(),location.getLongitude());
        EventBus.getDefault().post(locationModel);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (googleApiClient!=null&&googleApiClient.isConnected())
        {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(new LocationCallback());
            googleApiClient.disconnect();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        accuracy = intent.getIntExtra("accuracy",LocationRequest.PRIORITY_HIGH_ACCURACY);
        BuildGoogleApiClient();

        return START_STICKY;
    }
}
