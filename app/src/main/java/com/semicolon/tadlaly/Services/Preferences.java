package com.semicolon.tadlaly.Services;

import android.content.Context;
import android.content.SharedPreferences;

import com.semicolon.tadlaly.Models.UserModel;

public class Preferences {
    Context context;

    public Preferences(Context context) {
        this.context = context;
    }

    public void CreateSharedPref(UserModel userModel)
    {
        SharedPreferences pref = context.getSharedPreferences("User",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id",userModel.getUser_id());
        editor.putString("user_name",userModel.getUser_name());
        editor.putString("name",userModel.getUser_full_name());
        editor.putString("email",userModel.getUser_email());
        editor.putString("phone",userModel.getUser_phone());
        editor.putString("password",userModel.getUser_pass());
        editor.putString("photo",userModel.getUser_photo());
        editor.putString("city",userModel.getUser_city());
        editor.putString("token_id",userModel.getUser_token_id());
        editor.putString("lat",userModel.getUser_google_lat());
        editor.putString("lng",userModel.getUser_google_long());
        editor.putString("type",userModel.getUser_type());
        editor.apply();
        CreateSession(Tags.login_session);
    }
    public void UpdatePref(UserModel userModel)
    {
        SharedPreferences pref = context.getSharedPreferences("User",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id",userModel.getUser_id());
        editor.putString("user_name",userModel.getUser_name());
        editor.putString("name",userModel.getUser_full_name());
        editor.putString("email",userModel.getUser_email());
        editor.putString("phone",userModel.getUser_phone());
        editor.putString("password",userModel.getUser_pass());
        editor.putString("photo",userModel.getUser_photo());
        editor.putString("city",userModel.getUser_city());
        editor.putString("token_id",userModel.getUser_token_id());
        editor.putString("lat",userModel.getUser_google_lat());
        editor.putString("lng",userModel.getUser_google_long());
        editor.putString("type",userModel.getUser_type());
        editor.apply();
    }

    private void CreateSession(String session)
    {
        SharedPreferences pref = context.getSharedPreferences("User",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("session",session);
        editor.apply();

    }

    public void ClearSharedPref()
    {
        SharedPreferences pref = context.getSharedPreferences("User",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        CreateSession(Tags.logout_session);
        editor.apply();
    }
    public String getSession()
    {
        SharedPreferences pref = context.getSharedPreferences("User",Context.MODE_PRIVATE);
        String session = pref.getString("session","");
        return session;
    }

    public UserModel getUserData()
    {

        SharedPreferences pref = context.getSharedPreferences("User",Context.MODE_PRIVATE);
        String id=pref.getString("id","");
        String name=pref.getString("name","");
        String username=pref.getString("user_name","");
        String email=pref.getString("email","");
        String phone=pref.getString("phone","");
        String password=pref.getString("password","");
        String photo=pref.getString("photo","");
        String city=pref.getString("city","");
        String token_id=pref.getString("token_id","");
        String lat=pref.getString("lat","");
        String lng=pref.getString("lng","");
        String type=pref.getString("type","");

        UserModel userModel = new UserModel(id,username,password,type,name,phone,email,photo,city,token_id,lat,lng);

        return userModel;
    }
}
