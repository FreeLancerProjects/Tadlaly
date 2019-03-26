package com.semicolon.tadlaly.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Preferences;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;
import com.semicolon.tadlaly.language.LocalManager;

import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    private EditText user_name,user_pass;
    private TextView forget_pass,create_account;
    private Button login_btn,skip;
    private ProgressDialog progressDialog;
    private UserSingleTone userSingleTone;
    private Preferences preferences;
    private String lang="";
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);

        super.attachBaseContext(LocalManager.updateResources(newBase,LocalManager.getLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"OYA-Regular.ttf",true);*/
       // LocalManager.setLocality(lang,this);
        userSingleTone = UserSingleTone.getInstance();
        preferences = new Preferences(this);
        initView();
        CreateProgress_dialog();
        String session = preferences.getSession();
        if (!TextUtils.isEmpty(session)|| session!=null)
        {
            if (session.equals(Tags.login_session))
            {
                UserModel userModel = preferences.getUserData();

                if (userModel!=null)
                {
                    userSingleTone.setUserModel(userModel);
                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                    intent.putExtra("user_type",Tags.app_user);
                    startActivity(intent);
                    finish();
                }
            }

        }

    }

    private void CreateProgress_dialog() {
        ProgressBar bar = new ProgressBar(this);
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.logging));
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminateDrawable(drawable);
    }

    private void initView() {
        user_name = findViewById(R.id.user_name);
        user_pass = findViewById(R.id.user_password);
        forget_pass = findViewById(R.id.forget_pass);
        create_account = findViewById(R.id.create_account);
        login_btn = findViewById(R.id.login_btn);
        skip = findViewById(R.id.skip);
        if (Locale.getDefault().toString().contains("ar"))
        {
            user_pass.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);


        }else
        {
            user_pass.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        }
        login_btn.setOnClickListener(view ->
                Login()
        );

        create_account.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            intent.putExtra("reg_type",Tags.reg_from_login);
            startActivity(intent);
            finish();
        });

        forget_pass.setOnClickListener((View view) ->
                {
                    Intent intent = new Intent(this,ForgetPasswordActivity.class);
                    startActivity(intent);

                }

        );
        skip.setOnClickListener(view ->
        {
            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            intent.putExtra("user_type",Tags.app_visitor);
            startActivity(intent);
        });
    }


    private void Login() {
        String userName = user_name.getText().toString();
        String userPass = user_pass.getText().toString();

        if (TextUtils.isEmpty(userName))
        {
            user_name.setError(getString(R.string.enter_un));
        }else if (TextUtils.isEmpty(userPass))
        {
            user_name.setError(null);
            user_pass.setError(getString(R.string.enter_pass));

        }else
            {

                progressDialog.show();
                user_pass.setError(null);
                user_pass.setError(null);
                Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
                Services services = retrofit.create(Services.class);
                Call<UserModel> call = services.Login(userName, userPass);
                call.enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        if (response.isSuccessful())
                        {
                            UserModel  userModel = response.body();
                            if (userModel!=null)
                            {
                                if (userModel.getSuccess()==0)
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, R.string.user_pass_error, Toast.LENGTH_LONG).show();
                                }else if (userModel.getSuccess()==1)
                                {
                                    userSingleTone.setUserModel(userModel);
                                    preferences.CreateSharedPref(userModel);
                                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                    intent.putExtra("user_type",Tags.app_user);
                                    startActivity(intent);
                                    finish();
                                    progressDialog.dismiss();



                                }
                            }else
                            {
                                Toast.makeText(LoginActivity.this, R.string.reg_error, Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        Toast.makeText(LoginActivity.this, R.string.something, Toast.LENGTH_SHORT).show();

                    }
                });


            }
    }



}
