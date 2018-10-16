package com.semicolon.tadlaly.Activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
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
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.lamudi.phonefield.PhoneInputLayout;
import com.semicolon.tadlaly.Models.LocationModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.GetLocationDetails;
import com.semicolon.tadlaly.Services.Preferences;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.Services.UpdateLatLng;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private CircleImageView userImage;
    private EditText user_firstName, user_lastName, userEmail, user_username, user_password, user_rePassword,user_city;
    private TextView user_location;
    private PhoneInputLayout userPhone;
    private Button signUpBtn;
    private Bitmap bitmap;
    private String encodedImage;
    private CheckBox agree;
    private ImageView back;
    private final int IMG_REQ = 1;
    private boolean accept = false;
    private ProgressDialog dialog;
    private android.support.v7.app.AlertDialog gps_dialog;

    private LocationManager manager;
    private String FineLoc = Manifest.permission.ACCESS_FINE_LOCATION;
    private String CoarseLoc = Manifest.permission.ACCESS_COARSE_LOCATION;

    private final int permission_Req = 1245;
    private final int gps_req = 125;
    private double myLat = 0.0;
    private double myLng = 0.0;
    private ProgressDialog locDialog;
    private String city = "";
    private UserSingleTone userSingleTone;
    private Preferences preferences;
    private AlertDialog alertDialog;
    private GetLocationDetails getLocationDetails;
    private String reg_type="";
    private Intent intentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getDataFromIntent();
        if (reg_type.equals(Tags.reg_from_login))
        {
            userSingleTone = UserSingleTone.getInstance();
            preferences = new Preferences(this);
        }

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        initView();
        CreateAlertDialog();
        CreateProgressDialog();
        CreateProgressLocationDialog();
        CheckPermission();




    }

    private void CheckPermission()
    {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), FineLoc) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), CoarseLoc) != PackageManager.PERMISSION_GRANTED) {
            String perm [] ={FineLoc,CoarseLoc};
            ActivityCompat.requestPermissions(this, perm, permission_Req);
        } else {

            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                getDeviceLocation();
            }else
            {
                gps_dialog.show();
            }
        }
    }

    private void CreateAlertDialog()
    {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_gps_dialog,null);
        Button button = view.findViewById(R.id.openBtn);
        button.setOnClickListener(view1 -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent,gps_req);
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
        if (requestCode==permission_Req)
        {
            if (grantResults.length>0)
            {
                for (int i =0;i<grantResults.length;i++)
                {
                    if (grantResults[i]!=PackageManager.PERMISSION_GRANTED)
                    {
                        return;
                    }
                }


                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    getDeviceLocation();
                }else
                {
                    gps_dialog.show();
                }
            }
        }
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null)
        {
            if (intent.hasExtra("reg_type"))
            {
                reg_type = intent.getStringExtra("reg_type");
            }
        }
    }

    private void initView() {
        userImage = findViewById(R.id.image);
        user_firstName = findViewById(R.id.first_name);
        user_lastName = findViewById(R.id.last_name);
        userEmail = findViewById(R.id.email);
        userPhone = findViewById(R.id.phone);
        user_username = findViewById(R.id.username);
        user_city = findViewById(R.id.city);
        user_password = findViewById(R.id.password);
        user_rePassword = findViewById(R.id.re_password);
        user_location = findViewById(R.id.location);
        agree = findViewById(R.id.agree);
        signUpBtn = findViewById(R.id.signBtn);
        back = findViewById(R.id.back);
        userPhone.setDefaultCountry("sa");
        userPhone.getTextInputLayout().getEditText().setTextColor(ContextCompat.getColor(this, R.color.white));
        userPhone.getTextInputLayout().getEditText().setHint(R.string.phone);
        userPhone.getTextInputLayout().getEditText().setHintTextColor(ContextCompat.getColor(this, R.color.white));
        userPhone.getTextInputLayout().getEditText().setTextSize(14f);

        if (Locale.getDefault().toString().contains("ar")) {
            userPhone.getTextInputLayout().getEditText().setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            user_password.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            user_rePassword.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);


        } else {
            userPhone.getTextInputLayout().getEditText().setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            user_password.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            user_rePassword.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        }


        userImage.setOnClickListener(this);
        back.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
        agree.setOnClickListener(this);
        user_location.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.image:
                SelectImage();
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
                } else {
                    accept = false;
                }
                break;
            case R.id.location:
                getDeviceLocation();
                break;
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenToLocationUpdate(LocationModel locationModel)
    {
        myLat = locationModel.getLat();
        myLng = locationModel.getLng();

        Log.e("Lat",myLat+"_");
        Log.e("Lng",myLng+"_");

        if (myLat!=0.0&& myLng!=0.0)
        {
            user_location.setText(R.string.loc_deter);
            getAddress(myLat,myLng);

            locDialog.dismiss();
        }else
        {
            user_location.setError(getString(R.string.cnt_find_loc));
        }

    }
    private void getDeviceLocation() {

        if (EventBus.getDefault().isRegistered(this))
        {
            locDialog.show();

            StartLocationUpdate();
        }else
            {

                EventBus.getDefault().register(this);
                locDialog.show();

                StartLocationUpdate();

            }

    }

    private void StartLocationUpdate()
    {

        if (intentService==null)
        {
            intentService = new Intent(this, UpdateLatLng.class);

        }

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo serviceInfo :activityManager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceInfo.service.getClassName().equals(UpdateLatLng.class.getName()))
            {
                StopLocationUpdate();
            }
        }


        startService(intentService);
        Log.e("ff","ffffffff");
    }
    private void StopLocationUpdate()
    {
        if (intentService!=null)
        {
            stopService(intentService);
        }
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
        String m_fname = user_firstName.getText().toString();
        String m_lname = user_lastName.getText().toString();
        String m_username = user_username.getText().toString();
        String m_phone = userPhone.getPhoneNumber();
        String m_email = userEmail.getText().toString();
        String m_pass = user_password.getText().toString();
        String m_repass = user_rePassword.getText().toString();
        String m_user_city = user_city.getText().toString();
        String m_loc = user_location.getText().toString();

        if (TextUtils.isEmpty(m_fname)) {
            user_firstName.setError(getString(R.string.enter_fn));
        } else if (TextUtils.isEmpty(m_lname)) {
            user_firstName.setError(null);
            user_lastName.setError(getString(R.string.enter_ln));

        } else if (TextUtils.isEmpty(m_phone)) {
            user_firstName.setError(null);
            user_lastName.setError(null);
            userPhone.getTextInputLayout().getEditText().setError(getString(R.string.enter_phone));

        } else if (!userPhone.isValid()) {
            user_firstName.setError(null);
            user_lastName.setError(null);
            userPhone.getTextInputLayout().getEditText().setError(getString(R.string.inv_phone));

        } else if (TextUtils.isEmpty(m_email)) {
            user_firstName.setError(null);
            user_lastName.setError(null);
            userPhone.getTextInputLayout().getEditText().setError(null);
            userEmail.setError(getString(R.string.enter_email));

        } else if (!Patterns.EMAIL_ADDRESS.matcher(m_email).matches()) {
            user_firstName.setError(null);
            user_lastName.setError(null);
            userPhone.getTextInputLayout().getEditText().setError(null);
            userEmail.setError(getString(R.string.inv_email));

        }else if (TextUtils.isEmpty(m_user_city))
        {
            user_firstName.setError(null);
            user_lastName.setError(null);
            userPhone.getTextInputLayout().getEditText().setError(null);
            userEmail.setError(null);
            user_city.setError(getString(R.string.enter_city));


        }
        else if (TextUtils.isEmpty(m_username)) {
            user_firstName.setError(null);
            user_lastName.setError(null);
            userPhone.getTextInputLayout().getEditText().setError(null);
            userEmail.setError(null);
            user_city.setError(null);

            user_username.setError(getString(R.string.enter_un));
        } else if (TextUtils.isEmpty(m_pass)) {
            user_firstName.setError(null);
            user_lastName.setError(null);
            userPhone.getTextInputLayout().getEditText().setError(null);
            userEmail.setError(null);
            user_username.setError(null);
            user_city.setError(null);

            user_password.setError(getString(R.string.enter_pass));

        } else if (TextUtils.isEmpty(m_repass)) {
            user_firstName.setError(null);
            user_lastName.setError(null);
            userPhone.getTextInputLayout().getEditText().setError(null);
            userEmail.setError(null);
            user_username.setError(null);
            user_password.setError(null);
            user_city.setError(null);

            user_rePassword.setError(getString(R.string.enter_re_pass));

        } else if (!m_pass.equals(m_repass)) {
            user_firstName.setError(null);
            user_lastName.setError(null);
            userPhone.getTextInputLayout().getEditText().setError(null);
            userEmail.setError(null);
            user_username.setError(null);
            user_password.setError(null);
            user_city.setError(null);

            user_rePassword.setError(getString(R.string.pass_notmath));

        } else if (myLat==0.0&&myLng==0.0) {
            user_firstName.setError(null);
            user_lastName.setError(null);
            userPhone.getTextInputLayout().getEditText().setError(null);
            userEmail.setError(null);
            user_username.setError(null);
            user_password.setError(null);
            user_rePassword.setError(null);
            user_city.setError(null);

            user_location.setError(getString(R.string.enter_loc));

        } else if (bitmap == null) {
            user_firstName.setError(null);
            user_lastName.setError(null);
            userPhone.getTextInputLayout().getEditText().setError(null);
            userEmail.setError(null);
            user_username.setError(null);
            user_password.setError(null);
            user_rePassword.setError(null);
            user_location.setError(null);
            Toast.makeText(this, R.string.ch_per_img, Toast.LENGTH_LONG).show();
        } else if (!accept) {
            user_firstName.setError(null);
            user_lastName.setError(null);
            userPhone.getTextInputLayout().getEditText().setError(null);
            userEmail.setError(null);
            user_username.setError(null);
            user_password.setError(null);
            user_rePassword.setError(null);
            user_location.setError(null);

            Toast.makeText(this, R.string.noreg_without_rules, Toast.LENGTH_LONG).show();
        } else {
            dialog.show();

            encodedImage = EncodeImage(bitmap);
            Map<String, String> map = new HashMap<>();
            map.put("user_name", m_username);
            map.put("user_pass", m_pass);
            map.put("user_phone", m_phone);
            map.put("user_email", m_email);
            map.put("user_token_id", FirebaseInstanceId.getInstance().getToken());
            map.put("user_photo", encodedImage);
            map.put("user_full_name", m_fname + " " + m_lname);
            map.put("user_google_lat", String.valueOf(myLat));
            map.put("user_google_long", String.valueOf(myLng));
            map.put("user_city", city);
            Log.e("sss","ssss0");

            Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
            Services services = retrofit.create(Services.class);
            Call<UserModel> call = services.Register(map);
            call.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                    if (response.isSuccessful()) {
                        Log.e("sss","ssss");
                        UserModel userModel = response.body();
                        if (userModel != null) {
                            if (userModel.getSuccess() == 0) {
                                dialog.dismiss();
                                Log.e("sss","ssss2");

                                Toast.makeText(RegisterActivity.this, R.string.reg_error, Toast.LENGTH_LONG).show();

                            } else if (userModel.getSuccess() == 1) {
                                Log.e("sss","ssss3");

                                View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.custom_dialog_cong, null);
                                TextView doneBtn = view.findViewById(R.id.done);
                                doneBtn.setOnClickListener(view1 ->
                                {
                                    if (reg_type.equals(Tags.reg_from_login))
                                    {
                                        userSingleTone.setUserModel(userModel);
                                        preferences.CreateSharedPref(userModel);

                                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                        intent.putExtra("user_type",Tags.app_user);
                                        startActivity(intent);
                                        finish();
                                        dialog.dismiss();
                                        alertDialog.dismiss();
                                    }else
                                        {
                                            Toast.makeText(RegisterActivity.this, R.string.succ_reg, Toast.LENGTH_SHORT).show();
                                            finish();
                                            dialog.dismiss();
                                            alertDialog.dismiss();
                                        }

                                });
                                alertDialog = new AlertDialog.Builder(RegisterActivity.this)
                                        .setView(view)
                                        .setCancelable(false)
                                        .create();
                                alertDialog.setCanceledOnTouchOutside(false);
                                alertDialog.show();

                            } else if (userModel.getSuccess() == 2)

                            {
                                dialog.dismiss();

                                Toast.makeText(RegisterActivity.this, R.string.userexist, Toast.LENGTH_LONG).show();

                            }
                        } else {
                            dialog.dismiss();
                            Toast.makeText(RegisterActivity.this, R.string.reg_error, Toast.LENGTH_LONG).show();

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

    }


    private void SelectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent, getString(R.string.sel_image)), IMG_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQ && resultCode == RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                userImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode==gps_req)
        {
            if (resultCode==RESULT_CANCELED)
            {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    getDeviceLocation();
                }else
                {
                    gps_dialog.show();
                }
            }
        }
    }


    private String EncodeImage(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
        byte[] bytes = outputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Back();
    }

    private void Back() {
        if (reg_type.equals(Tags.reg_from_login))
        {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else
            {
                finish();
            }

    }





    private void getAddress(double myLat, double myLng) {
      if (getLocationDetails==null)
      {
          getLocationDetails = new GetLocationDetails();
      }

      getLocationDetails.getLocation(this,"1",myLat,myLng);
    }

    public void setFullLocation(String location)
    {

        city = location;
        user_city.setText(city);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (intentService!=null)
        {
            StopLocationUpdate();
        }
        if (EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().unregister(this);
        }
    }
}
