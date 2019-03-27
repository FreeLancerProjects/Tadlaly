package com.semicolon.tadlaly.Fragments;

import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Preferences;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;
import com.semicolon.tadlaly.share.Common;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UpdateProfileItems_Fragment extends Fragment implements UserSingleTone.OnCompleteListener{
    private LinearLayout name_container,email_container,phone_container,country_container,username_container;
    private EditText name,email,username,city;
    private EditText edt_phone;
    private Button updateBtn,cancelBtn;
    private String type;
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private Preferences preferences;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_profile_item,container,false);
        initView(view);
        userSingleTone = UserSingleTone.getInstance();
        userSingleTone.getUser(this);
        preferences = new Preferences(getActivity());
        return view;
    }



    private void initView(View view) {
        name_container = view.findViewById(R.id.name_container);
        email_container = view.findViewById(R.id.email_container);
        phone_container = view.findViewById(R.id.phone_container);
        country_container = view.findViewById(R.id.country_container);
        username_container = view.findViewById(R.id.username_container);
        updateBtn = view.findViewById(R.id.updateBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        edt_phone = view.findViewById(R.id.edt_phone);
        username = view.findViewById(R.id.username);
        city = view.findViewById(R.id.country);
        cancelBtn.setOnClickListener(view1 -> getActivity().finish());

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            type = bundle.getString("type");
            if (type!=null && !TextUtils.isEmpty(type))
            {
                if (type.equals(Tags.update_name))
                {
                    name_container.setVisibility(View.VISIBLE);

                }else if (type.equals(Tags.update_email))
                {
                    email_container.setVisibility(View.VISIBLE);

                }
                else if (type.equals(Tags.update_phone))
                {
                    phone_container.setVisibility(View.VISIBLE);

                }
                else if (type.equals(Tags.update_country))
                {
                    country_container.setVisibility(View.VISIBLE);

                }
                else if (type.equals(Tags.update_username))
                {
                    username_container.setVisibility(View.VISIBLE);

                }
            }
        }

        updateBtn.setOnClickListener(view1 ->
        {

            if (type!=null && !TextUtils.isEmpty(type))
            {
                if (type.equals(Tags.update_name))
                {
                    String m_name = name.getText().toString();
                    if (TextUtils.isEmpty(m_name))
                    {
                        name.setError(getString(R.string.enter_name));
                    }else
                        {
                            name.setError(null);
                            Common.CloseKeyBoard(getActivity(),name);

                            UpdateName(m_name);

                        }

                }else if (type.equals(Tags.update_email))
                {
                    String m_email = email.getText().toString();
                    if (TextUtils.isEmpty(m_email))
                    {
                        email.setError(getString(R.string.enter_email));
                    }else if (!Patterns.EMAIL_ADDRESS.matcher(m_email).matches())
                    {
                        email.setError(getString(R.string.inv_email));

                    }
                    else
                    {
                        email.setError(null);
                        Common.CloseKeyBoard(getActivity(),email);

                        UpdateEmail(m_email);

                    }
                }
                else if (type.equals(Tags.update_phone))
                {
                    String m_phone = edt_phone.getText().toString().trim();
                    if (TextUtils.isEmpty(m_phone))
                    {
                        edt_phone.setError(getString(R.string.field_req));
                    }else if (!Patterns.PHONE.matcher(m_phone).matches()||m_phone.length()<6||m_phone.length()>=13)
                    {
                        edt_phone.setError(getString(R.string.inv_phone));
                    }else
                        {
                            edt_phone.setError(null);
                            Common.CloseKeyBoard(getActivity(),edt_phone);
                            UpdatePhone(m_phone);

                        }
                }
                else if (type.equals(Tags.update_country))
                {
                    String m_city = city.getText().toString();
                    if (TextUtils.isEmpty(m_city))
                    {
                        city.setError(getString(R.string.enter_city));
                    }else
                    {
                        city.setError(null);
                        Common.CloseKeyBoard(getActivity(),city);

                        UpdateCountry(m_city);

                    }
                }
                else if (type.equals(Tags.update_username))
                {
                    String m_username = username.getText().toString();
                    if (TextUtils.isEmpty(m_username))
                    {
                        username.setError(getString(R.string.enter_un));
                    }else
                    {
                        username.setError(null);
                        Common.CloseKeyBoard(getActivity(),username);

                        UpdateUsername(m_username);

                    }
                }
            }
        });

    }
    private void CreateProgress_dialog(String msg) {
        ProgressBar bar = new ProgressBar(getActivity());
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(getActivity(),R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(msg);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIndeterminateDrawable(drawable);
    }
    private void UpdateName(String name)
    {
        Log.e("id",userModel.getUser_id());
        Log.e("name",name);

        CreateProgress_dialog(getString(R.string.upd_name));
        dialog.show();
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<UserModel> call = retrofit.create(Services.class).updateName(userModel.getUser_id(), name);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful())
                {
                    UpdateProfileItems_Fragment.this.userModel = response.body();
                    userSingleTone.setUserModel(response.body());
                    preferences.UpdatePref(response.body());
                    Toast.makeText(getActivity(), R.string.name_upd_succ, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log.e("Error",t.getMessage());
                dialog.dismiss();
            }
        });

    }
    private void UpdateEmail(String email)
    {
        CreateProgress_dialog(getString(R.string.upd_name));
        dialog.show();

        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        retrofit.create(Services.class)
                .updateEmail(userModel.getUser_id(),email)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        if (response.isSuccessful())
                        {
                            UpdateProfileItems_Fragment.this.userModel = userModel;
                            userSingleTone.setUserModel(userModel);
                            preferences.UpdatePref(userModel);

                            Toast.makeText(getActivity(), R.string.email_upd_succ, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                    }
                });



    }
    private void UpdatePhone(String phone)
    {

        CreateProgress_dialog(getString(R.string.upd_name));
        dialog.show();
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<UserModel> call = retrofit.create(Services.class).updatePhone(userModel.getUser_id(), phone);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful())
                {
                    UpdateProfileItems_Fragment.this.userModel = response.body();
                    userSingleTone.setUserModel(response.body());
                    preferences.UpdatePref(response.body());
                    Toast.makeText(getActivity(), R.string.phone_succ_update, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log.e("Error",t.getMessage());
                dialog.dismiss();
            }
        });

    }
    private void UpdateCountry(String city)
    {
        CreateProgress_dialog(getString(R.string.upd_name));
        dialog.show();
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<UserModel> call = retrofit.create(Services.class).updateCity(userModel.getUser_id(), city);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful())
                {
                    UpdateProfileItems_Fragment.this.userModel = response.body();
                    userSingleTone.setUserModel(response.body());
                    preferences.UpdatePref(response.body());
                    Toast.makeText(getActivity(), R.string.city_updated, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log.e("Error",t.getMessage());
                dialog.dismiss();
            }
        });

    }
    private void UpdateUsername(String username)
    {
        CreateProgress_dialog(getString(R.string.upd_username));
        dialog.show();
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<UserModel> call = retrofit.create(Services.class).updateUser_name(userModel.getUser_id(), username);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful())
                {
                    UpdateProfileItems_Fragment.this.userModel = userModel;
                    userSingleTone.setUserModel(userModel);
                    preferences.UpdatePref(userModel);
                    Toast.makeText(getActivity(), R.string.un_updated, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    getActivity().finish();
                    Log.e("dddd",userModel.getUser_name());
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log.e("Error",t.getMessage());
                dialog.dismiss();
            }
        });

    }



    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;
    }
}
