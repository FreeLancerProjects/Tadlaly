package com.semicolon.tadlaly.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.semicolon.tadlaly.R;

public class GpsActivity extends AppCompatActivity {

    private LocationManager manager;
    private AlertDialog dialog;
    private final int gps_req=1558;
    private final int per_req=12235;
    private final String FineLoc = Manifest.permission.ACCESS_FINE_LOCATION;
    private String[] permissions = {FineLoc};
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        CreateAlertDialog();
        checkPermission();




    }

    private void CreateAlertDialog()
    {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_gps_dialog,null);
        Button button = view.findViewById(R.id.openBtn);
        button.setOnClickListener(view1 -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent,gps_req);
            dialog.dismiss();
        });
        dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setView(view)
                .create();
        dialog.setCanceledOnTouchOutside(false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==gps_req)
        {
            if (resultCode==RESULT_CANCELED)
            {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    navigateToLogin();

                }else
                {
                    dialog.show();
                }
            }
        }
    }

    private void navigateToLogin()
    {
        Intent intent = new Intent(this,SplashActivity.class);
        startActivity(intent);
        finish();
    }
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), FineLoc) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, per_req);
        } else {
            Log.e("9", "a");

            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                navigateToLogin();

            }else
            {
                dialog.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==per_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {

                    if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    {
                        navigateToLogin();

                    }else
                    {
                        dialog.show();
                    }
                }




            }
        }

    }
}
