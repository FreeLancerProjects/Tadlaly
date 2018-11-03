package com.semicolon.tadlaly.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.semicolon.tadlaly.Models.UserModel;

import java.util.ArrayList;
import java.util.List;

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

    public void setIsSelectedLang(boolean isSelectedLang)
    {
        SharedPreferences preferences =context.getSharedPreferences("Lang",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isSelected",isSelectedLang);
        editor.apply();
    }

    public boolean isLanguageSelected()
    {
        SharedPreferences preferences =context.getSharedPreferences("Lang",Context.MODE_PRIVATE);
        boolean isSelected = preferences.getBoolean("isSelected",false);
        return isSelected;
    }


    public void setSuggestions (String suggestion)
    {
        SharedPreferences preferences = context.getSharedPreferences("suggestion",Context.MODE_PRIVATE);
        String s = preferences.getString("suggestion_list","");
        Gson gson = new Gson();

        if (TextUtils.isEmpty(s))
        {
            List<String> stringList = new ArrayList<>();
            stringList.add(suggestion);

            String gson_data =gson.toJson(stringList);

            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("suggestion_list",gson_data);
            edit.apply();

        }else
            {
                List<String> suggestion_list = gson.fromJson(s,new TypeToken<List<String>>(){}.getType());

                Log.e("bbb","bb");
                if (!suggestion_list.contains(suggestion))
                {
                    Log.e("ccc","ccc");

                    suggestion_list.add(suggestion);

                    String gson_data =gson.toJson(suggestion_list);

                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putString("suggestion_list",gson_data);
                    edit.apply();
                }



            }

    }

    public String[] getSuggestionList()
    {
        SharedPreferences preferences = context.getSharedPreferences("suggestion",Context.MODE_PRIVATE);
        String s = preferences.getString("suggestion_list","");
        Gson gson = new Gson();

        Log.e("data",s+"_____________");
        String [] sug;

        if (!TextUtils.isEmpty(s))
        {
            List<String> stringList = gson.fromJson(s,new TypeToken<List<String>>(){}.getType());
            if (stringList.size()>0)
            {
                sug = stringList.toArray(new String[stringList.size()]);

            }else
            {
                sug = new String[]{};

            }
            Log.e("suuuu",sug[0]);
        }else
            {
                sug = new String[]{};

            }

        return sug;

    }

}
