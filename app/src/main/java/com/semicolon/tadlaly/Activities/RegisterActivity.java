package com.semicolon.tadlaly.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.tadlaly.Models.RulesModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Preferences;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;
import com.semicolon.tadlaly.language.LocalManager;
import com.semicolon.tadlaly.share.Common;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView userImage;
    private EditText user_tv_Name, edt_phone, userEmail, user_username, user_password, user_rePassword, user_city;
    private Button signUpBtn;
    private Bitmap bitmap;
    private CheckBox agree;
    private ImageView back;
    private final int IMG_REQ = 1;
    private boolean accept = false;
    private ProgressDialog dialog;
    private android.support.v7.app.AlertDialog gps_dialog;
    private final String read_perm = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final int read_req = 111;
    private final int gps_req = 125;
    private double myLat = 0.0;
    private double myLng = 0.0;
    private ProgressDialog locDialog;
    private String city = "";
    private UserSingleTone userSingleTone;
    private Preferences preferences;
    private String reg_type = "";
    private View root;
    private BottomSheetBehavior behavior;
    private Button agree_btn;
    private TextView tv_content;
    private ProgressBar progBar;
    private FrameLayout fl_add_image;
    private String current_language;

    private Uri uri = null;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);

        super.attachBaseContext(LocalManager.updateResources(newBase, LocalManager.getLanguage(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getDataFromIntent();
        initView();
        CreateAlertDialog();
        CreateProgressDialog();
        CreateProgressLocationDialog();


    }


    private void CreateAlertDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_gps_dialog, null);
        Button button = view.findViewById(R.id.openBtn);
        button.setOnClickListener(view1 -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, gps_req);
            gps_dialog.dismiss();
        });
        gps_dialog = new android.support.v7.app.AlertDialog.Builder(this)
                .setCancelable(true)
                .setView(view)
                .create();
        gps_dialog.setCanceledOnTouchOutside(false);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == read_req) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage();
                } else {
                    Toast.makeText(RegisterActivity.this, R.string.perm_read_denied, Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("reg_type")) {
                reg_type = intent.getStringExtra("reg_type");
            }
        }
    }

    private void initView() {

        current_language = Paper.book().read("language");
        back = findViewById(R.id.back);
        if (current_language.equals("ar"))
        {
         back.setRotation(180f);
        }

        fl_add_image = findViewById(R.id.fl_add_image);
        userImage = findViewById(R.id.image);
        user_tv_Name = findViewById(R.id.tv_name);
        userEmail = findViewById(R.id.email);
        edt_phone = findViewById(R.id.edt_phone);
        user_username = findViewById(R.id.username);
        user_city = findViewById(R.id.city);
        user_password = findViewById(R.id.password);
        user_rePassword = findViewById(R.id.re_password);
        agree = findViewById(R.id.agree);
        signUpBtn = findViewById(R.id.signBtn);


        root = findViewById(R.id.root);
        behavior = BottomSheetBehavior.from(root);
        tv_content = findViewById(R.id.tv_content);
        agree_btn = findViewById(R.id.agree_btn);
        progBar = findViewById(R.id.progBar);
        agree_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        fl_add_image.setOnClickListener(this);
        back.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
        agree.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fl_add_image:
                CheckReadPermission();
                break;
            case R.id.back:
                Back();
                break;
            case R.id.signBtn:
                Signup();
                break;
            case R.id.agree:
                if (agree.isChecked()) {
                    accept = true;
                    DisplayTerms();
                } else {
                    accept = false;
                }
                break;

        }

    }

    private void DisplayTerms() {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        Api.getRetrofit(Tags.Base_Url)
                .create(Services.class)
                .getRules()
                .enqueue(new Callback<RulesModel>() {
                    @Override
                    public void onResponse(Call<RulesModel> call, Response<RulesModel> response) {
                        if (response.isSuccessful()) {
                            progBar.setVisibility(View.GONE);
                            tv_content.setText(response.body().getContent());
                            agree_btn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<RulesModel> call, Throwable t) {
                        Log.e("Error", t.getMessage());
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        progBar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, R.string.something, Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private boolean isGpsOpen() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }

    private void CreateProgressDialog() {
        ProgressBar bar = new ProgressBar(this);
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.creating_account));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setIndeterminateDrawable(drawable);
    }

    private void CreateProgressLocationDialog() {
        ProgressBar bar = new ProgressBar(this);
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        locDialog = new ProgressDialog(this);
        locDialog.setMessage(getString(R.string.locating));
        locDialog.setCanceledOnTouchOutside(false);
        locDialog.setCancelable(true);
        locDialog.setIndeterminateDrawable(drawable);
    }

    private void Signup() {
        String m_name = user_tv_Name.getText().toString().trim();
        String m_username = user_username.getText().toString().trim();
        String m_phone = edt_phone.getText().toString().trim();
        String m_email = userEmail.getText().toString().trim();
        String m_pass = user_password.getText().toString().trim();
        String m_repass = user_rePassword.getText().toString().trim();
        String m_user_city = user_city.getText().toString().trim();


        if (!TextUtils.isEmpty(m_name) &&
                Patterns.PHONE.matcher(m_phone).matches()&&!TextUtils.isEmpty(m_phone) && m_phone.length() >= 6 && m_phone.length() < 13 &&
                !TextUtils.isEmpty(m_username) &&
                !TextUtils.isEmpty(m_email) &&
                Patterns.EMAIL_ADDRESS.matcher(m_email).matches() &&
                !TextUtils.isEmpty(m_pass) && !TextUtils.isEmpty(m_repass) &&
                m_pass.equals(m_repass) &&
                !TextUtils.isEmpty(m_user_city) &&
                accept
        ) {
            user_tv_Name.setError(null);
            edt_phone.setError(null);
            userEmail.setError(null);
            user_username.setError(null);
            user_password.setError(null);
            user_rePassword.setError(null);
            user_city.setError(null);
            Common.CloseKeyBoard(this, user_tv_Name);

            if (uri != null) {
                register_with_image(uri, m_name, m_email, m_phone, m_user_city, m_username, m_pass);

            } else {
                register_without_image(m_name, m_email, m_phone, m_user_city, m_username, m_pass);

            }


        } else {
            if (TextUtils.isEmpty(m_name)) {
                user_tv_Name.setError(getString(R.string.field_req));
            } else {
                user_tv_Name.setError(null);
            }

            if (TextUtils.isEmpty(m_phone)) {

                edt_phone.setError(getString(R.string.field_req));
            } else if (!Patterns.PHONE.matcher(m_phone).matches()||m_phone.length() < 6 || m_phone.length() >= 13) {
                edt_phone.setError(getString(R.string.inv_phone));
            } else {
                edt_phone.setError(null);
            }

            if (TextUtils.isEmpty(m_email)) {
                userEmail.setError(getString(R.string.field_req));

            } else if (!Patterns.EMAIL_ADDRESS.matcher(m_email).matches()) {
                userEmail.setError(getString(R.string.inv_email));

            } else {
                userEmail.setError(null);
            }

            if (TextUtils.isEmpty(m_user_city)) {
                user_city.setError(getString(R.string.field_req));
            } else {
                user_city.setError(null);
            }


            if (TextUtils.isEmpty(m_username)) {
                user_username.setError(getString(R.string.field_req));
            }else
                {
                    user_username.setError(null);
                }


            if (TextUtils.isEmpty(m_pass)&&TextUtils.isEmpty(m_repass)) {

                user_password.setError(getString(R.string.field_req));
                user_rePassword.setError(getString(R.string.field_req));

            }else if (TextUtils.isEmpty(m_pass))
            {
                user_password.setError(getString(R.string.field_req));

            }else if (TextUtils.isEmpty(m_repass))
            {
                user_rePassword.setError(getString(R.string.field_req));

            }else if (!m_pass.equals(m_repass))
            {
                user_rePassword.setError(getString(R.string.pass_notmath));
            }else
                {
                    user_password.setError(null);
                    user_rePassword.setError(null);
                }


            if (!accept) {

            }
                Toast.makeText(this, R.string.noreg_without_rules, Toast.LENGTH_LONG).show();

            }











    }

    private void register_with_image(Uri uri, String m_name, String m_email, String m_phone, String m_user_city, String m_username, String m_pass) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.creating_account));
        dialog.show();
        MultipartBody.Part image_part = Common.getMultiPartBody(uri, this,"user_photo");
        RequestBody name_part = Common.getRequestBody(m_name);
        RequestBody email_part = Common.getRequestBody(m_email);
        RequestBody phone_part = Common.getRequestBody(m_phone);
        RequestBody city_part = Common.getRequestBody(m_user_city);
        RequestBody username_part = Common.getRequestBody(m_username);
        RequestBody password_part = Common.getRequestBody(m_pass);
        RequestBody token_part = Common.getRequestBody("");
        RequestBody lat_part = Common.getRequestBody(String.valueOf(myLat));
        RequestBody lng_part = Common.getRequestBody(String.valueOf(myLng));
        Map<String, RequestBody> map = new HashMap<>();

        map.put("user_name", username_part);
        map.put("user_pass", password_part);
        map.put("user_phone", phone_part);
        map.put("user_email", email_part);
        map.put("user_token_id", token_part);
        map.put("user_full_name", name_part);
        map.put("user_city", city_part);
        map.put("user_google_lat", lat_part);
        map.put("user_google_long", lng_part);

        Api.getRetrofit(Tags.Base_Url)
                .create(Services.class)
                .Register_with_image(map, image_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        if (response.isSuccessful()) {
                            dialog.dismiss();
                            if (response.body().getSuccess() == 1) {
                                if (reg_type.equals(Tags.reg_from_login)) {
                                    userSingleTone = UserSingleTone.getInstance();
                                    preferences = new Preferences(RegisterActivity.this);
                                    preferences.CreateSharedPref(response.body());
                                    userSingleTone.setUserModel(response.body());
                                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                    intent.putExtra("user_type", Tags.app_user);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, R.string.succ_reg, Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else if (response.body().getSuccess() == 2) {
                                Toast.makeText(RegisterActivity.this, R.string.userexist, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, R.string.something, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        dialog.dismiss();
                        Log.e("Error", t.getMessage());
                        Toast.makeText(RegisterActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void register_without_image(String m_name, String m_email, String m_phone, String m_user_city, String m_username, String m_pass) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.creating_account));
        dialog.show();
        RequestBody name_part = Common.getRequestBody(m_name);
        RequestBody email_part = Common.getRequestBody(m_email);
        RequestBody phone_part = Common.getRequestBody(m_phone);
        RequestBody city_part = Common.getRequestBody(m_user_city);
        RequestBody username_part = Common.getRequestBody(m_username);
        RequestBody password_part = Common.getRequestBody(m_pass);
        RequestBody token_part = Common.getRequestBody("");
        RequestBody lat_part = Common.getRequestBody(String.valueOf(myLat));
        RequestBody lng_part = Common.getRequestBody(String.valueOf(myLng));
        Map<String, RequestBody> map = new HashMap<>();

        map.put("user_name", username_part);
        map.put("user_pass", password_part);
        map.put("user_phone", phone_part);
        map.put("user_email", email_part);
        map.put("user_token_id", token_part);
        map.put("user_full_name", name_part);
        map.put("user_city", city_part);
        map.put("user_google_lat", lat_part);
        map.put("user_google_long", lng_part);

        Api.getRetrofit(Tags.Base_Url)
                .create(Services.class)
                .Register_no_image(map)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        if (response.isSuccessful()) {
                            dialog.dismiss();
                            if (response.body().getSuccess() == 1) {
                                if (reg_type.equals(Tags.reg_from_login)) {
                                    userSingleTone = UserSingleTone.getInstance();
                                    preferences = new Preferences(RegisterActivity.this);
                                    preferences.CreateSharedPref(response.body());
                                    userSingleTone.setUserModel(response.body());
                                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                    intent.putExtra("user_type", Tags.app_user);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, R.string.succ_reg, Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else if (response.body().getSuccess() == 2) {
                                Toast.makeText(RegisterActivity.this, R.string.userexist, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, R.string.something, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        dialog.dismiss();
                        Log.e("Error", t.getMessage());
                        Toast.makeText(RegisterActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void CheckReadPermission() {
        String[] perm = {read_perm};
        if (ContextCompat.checkSelfPermission(this, read_perm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, perm, read_req);
        } else {
            SelectImage();
        }
    }

    private void SelectImage() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);

        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent, getString(R.string.sel_image)), IMG_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQ && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            bitmap = BitmapFactory.decodeFile(Common.getImagePath(this, uri));
            userImage.setImageBitmap(bitmap);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Back();
    }

    private void Back() {
        if (reg_type.equals(Tags.reg_from_login)) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            finish();
        }

    }



    public void setFullLocation(String location) {

        city = location;
        user_city.setText(city);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }



}
