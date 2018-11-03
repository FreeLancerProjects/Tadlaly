package com.semicolon.tadlaly.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.semicolon.tadlaly.Models.ResponseModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.language.LanguageHelper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText user_name,email;
    private Button resetBtn;
    private ProgressDialog dialog;
    private ImageView back;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);

        super.attachBaseContext(LanguageHelper.onAttach(newBase, Paper.book().read("language",Locale.getDefault().getLanguage())));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initView();
        CreateProgress_dialog();
    }

    private void initView() {
        user_name = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);
        resetBtn = findViewById(R.id.reset_btn);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        resetBtn.setOnClickListener(view -> ResetPassword());
    }
    private void CreateProgress_dialog() {
        ProgressBar bar = new ProgressBar(this);
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.logging));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIndeterminateDrawable(drawable);
    }
    private void ResetPassword() {
        String m_username = user_name.getText().toString();
        String m_email = email.getText().toString();

        if (TextUtils.isEmpty(m_username))
        {
            user_name.setError(getString(R.string.enter_un));
        }else if (TextUtils.isEmpty(m_email))
        {
            user_name.setError(null);
            email.setError(getString(R.string.enter_email));
        }
        else if (Patterns.EMAIL_ADDRESS.matcher(m_email).matches())
        {
            user_name.setError(null);
            email.setError(getString(R.string.inv_email));
        }else
            {
                dialog.show();
                Map<String,String> map = new HashMap<>();
                map.put("user_name",m_username);
                map.put("user_email",m_email);
                Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
                Call<ResponseModel> call = retrofit.create(Services.class).ResetPassword(map);

                call.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful())
                        {
                            if (response.body().getSuccess()==1)
                            {
                                dialog.dismiss();
                                user_name.setText(null);
                                email.setText(null);
                                Toast.makeText(ForgetPasswordActivity.this, R.string.resetmsg_success, Toast.LENGTH_SHORT).show();
                            }else
                            {
                                dialog.dismiss();
                                Toast.makeText(ForgetPasswordActivity.this, R.string.u_e_incorrect, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        dialog.dismiss();

                        Toast.makeText(ForgetPasswordActivity.this,R.string.something, Toast.LENGTH_LONG).show();
                        Log.e("Error",t.getMessage());
                    }
                });
            }
    }
}
