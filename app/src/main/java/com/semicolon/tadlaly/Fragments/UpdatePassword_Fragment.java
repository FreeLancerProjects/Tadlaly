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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Preferences;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UpdatePassword_Fragment extends Fragment implements UserSingleTone.OnCompleteListener {

    private EditText pass,newPass,re_newPass;
    private Button cancel,update;
    private ProgressDialog dialog;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private Preferences preferences;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_password,container,false);
        initView(view);
        CreateProgressDialog();
        userSingleTone = UserSingleTone.getInstance();
        userSingleTone.getUser(this);
        preferences = new Preferences(getActivity());
        return view;

    }

    private void initView(View view) {
        pass = view.findViewById(R.id.curr_pass);
        newPass = view.findViewById(R.id.new_pass);
        re_newPass = view.findViewById(R.id.re_new_pass);
        cancel = view.findViewById(R.id.cancelBtn);
        update = view.findViewById(R.id.updateBtn);

        cancel.setOnClickListener(view1 ->
        getActivity().finish()
        );
        update.setOnClickListener(view1 -> UpdateData());
    }
    private void CreateProgressDialog()
    {
        ProgressBar bar = new ProgressBar(getActivity());
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(getActivity(),R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.updating_pass));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setIndeterminateDrawable(drawable);
    }
    private void UpdateData() {
        String m_Pass = pass.getText().toString();
        String m_newPass = newPass.getText().toString();
        String m_rePass = re_newPass.getText().toString();


        if (TextUtils.isEmpty(m_Pass))
        {
            pass.setError(getString(R.string.enter_curr_pass));
        }else if (TextUtils.isEmpty(m_newPass))
        {
            newPass.setError(getString(R.string.enter_new_pass));

        }else if (TextUtils.isEmpty(m_rePass))
        {
            re_newPass.setError(getString(R.string.re_new_pass));

        }else if (!m_newPass.equals(m_rePass))
        {
            re_newPass.setError(getString(R.string.pass_notmath));

        }
        else
            {
                UpdatePassword(m_Pass,m_newPass);
            }
    }

    private void UpdatePassword(String m_Pass, String pass)
    {
        Log.e("oldpass",userModel.getUser_pass());
        dialog.show();
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<UserModel> call = retrofit.create(Services.class).updatePassword(userModel.getUser_id(), m_Pass, pass);

        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful())
                {
                    UpdatePassword_Fragment.this.userModel = response.body();
                    userSingleTone.setUserModel(response.body());
                    preferences.UpdatePref(response.body());
                    Toast.makeText(getActivity(), R.string.pass_upd_success, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;
    }
}
