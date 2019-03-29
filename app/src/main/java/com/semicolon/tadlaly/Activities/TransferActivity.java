package com.semicolon.tadlaly.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.semicolon.tadlaly.Adapters.BankAdapter;
import com.semicolon.tadlaly.Models.BankModel;
import com.semicolon.tadlaly.Models.ResponseModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;
import com.semicolon.tadlaly.language.LocalManager;
import com.semicolon.tadlaly.share.Common;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TransferActivity extends AppCompatActivity implements UserSingleTone.OnCompleteListener, DatePickerDialog.OnDateSetListener {

    private EditText money, ads_id;
    private TextView user_name, user_phone, date, tv_bank;
    private Button sendBtn;
    private ProgressDialog dialog;
    private ImageView back, upload;
    private RoundedImageView img;
    private String encodedImage;
    private Bitmap bitmap;
    private final int img_req = 12221;
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private List<BankModel> bankModelList;
    private String bank = "";
    private String user_type = "";
    private AlertDialog.Builder serviceBuilder;
    private AlertDialog bank_dialog;
    private DatePickerDialog datePickerDialog;
    private String current_language;
    private String m_date="";
    private final String read_permission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final int read_req = 12;
    private Uri uri=null;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LocalManager.updateResources(newBase,LocalManager.getLanguage(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        current_language = Paper.book().read("language", Locale.getDefault().getLanguage());
        initView();
        getDataFromIntent();
        if (user_type.equals(Tags.app_user)) {
            userSingleTone = UserSingleTone.getInstance();
            userSingleTone.getUser(this);
        }
        CreateServiceDialog();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("user_type")) {
                user_type = intent.getStringExtra("user_type");
            }
        }
    }

    private void getBanksAccount() {

        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.lodng_bnks));
        dialog.show();
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<List<BankModel>> call = retrofit.create(Services.class).getBanks();
        call.enqueue(new Callback<List<BankModel>>() {
            @Override
            public void onResponse(Call<List<BankModel>> call, Response<List<BankModel>> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();

                    if (response.body().size() > 0) {
                        bankModelList.clear();
                        bankModelList.addAll(response.body());
                        CreateBankAlertDialog(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<BankModel>> call, Throwable t) {
                dialog.dismiss();

                Log.e("Error", t.getMessage());

            }
        });
    }

    private void CreateServiceDialog() {
        serviceBuilder = new AlertDialog.Builder(this);
        serviceBuilder.setMessage(R.string.ser_not_ava);
        serviceBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
            AlertDialog alertDialog = serviceBuilder.create();
            alertDialog.dismiss();
            finish();
        });

        AlertDialog alertDialog = serviceBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(false);

    }

    private void initView() {
        bankModelList = new ArrayList<>();
        user_name = findViewById(R.id.user_name);
        user_phone = findViewById(R.id.user_phone);
        date = findViewById(R.id.date);
        tv_bank = findViewById(R.id.tv_bank);

        money = findViewById(R.id.mony);
        back = findViewById(R.id.back);
        ads_id = findViewById(R.id.ads_id);
        upload = findViewById(R.id.upload);
        img = findViewById(R.id.img);
        sendBtn = findViewById(R.id.send_btn);
        upload.setOnClickListener(view -> checkPermission());
        back.setOnClickListener(view ->
                finish()
        );


        sendBtn.setOnClickListener(view ->
                {
                    if (user_type.equals(Tags.app_user)) {
                        CheckData();

                    } else {
                        serviceBuilder.show();
                    }

                }
        );
        date.setOnClickListener(view ->
                CreateDateDialog()
        );

        tv_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bankModelList.size() > 0) {
                    CreateBankAlertDialog(bankModelList);
                } else {
                    getBanksAccount();

                }
            }
        });

        CreateProgressDialog();
    }


    private void CreateDateDialog() {

        Calendar calendar = Calendar.getInstance(new Locale(current_language));
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setLocale(new Locale(current_language));
        datePickerDialog.setOkText(R.string.select);
        datePickerDialog.setOkColor(ContextCompat.getColor(this, R.color.colorPrimary));
        datePickerDialog.setCancelText(R.string.cancel3);
        datePickerDialog.setCancelColor(ContextCompat.getColor(this, R.color.gray6));
        datePickerDialog.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimary));
        datePickerDialog.show(getFragmentManager(), "Date");
    }

    private void CheckData() {
        String phone_regex = "^[+]?[0-9]{6,}$";

        String m_name = user_name.getText().toString().trim();
        String m_phone = user_phone.getText().toString().trim();
        String m_money = money.getText().toString().trim();
        String m_ads_id = ads_id.getText().toString().trim();


        if (!TextUtils.isEmpty(m_name)&&
                !TextUtils.isEmpty(m_phone)&&
                m_phone.matches(phone_regex)&&
                !TextUtils.isEmpty(m_money)&&
                !TextUtils.isEmpty(m_ads_id)&&
                !TextUtils.isEmpty(m_date)&&
                !TextUtils.isEmpty(bank)&&uri!=null)
        {
            tv_bank.setError(null);
            user_phone.setError(null);
            user_name.setError(null);
            money.setError(null);
            date.setError(null);
            ads_id.setError(null);

            Send(m_name,m_phone,m_money,m_ads_id,m_date,bank);
        }else
            {
                if (TextUtils.isEmpty(m_name)) {
                    user_name.setError(getString(R.string.field_req));
                }else
                    {
                        user_name.setError(null);
                    }

                if (TextUtils.isEmpty(m_money)) {
                    money.setError(getString(R.string.field_req));
                }else
                    {
                        money.setError(null);
                    }

                if (TextUtils.isEmpty(bank)) {
                    tv_bank.setError(getString(R.string.field_req));
                }else
                    {
                        tv_bank.setError(null);
                    }


                if (TextUtils.isEmpty(m_date)) {

                    date.setError(getString(R.string.field_req));
                }else
                    {
                        date.setError(null);
                    }

                if (TextUtils.isEmpty(m_phone))
                {
                    user_phone.setError(getString(R.string.field_req));
                }else if (!m_phone.matches(phone_regex))
                    {
                        user_phone.setError(getString(R.string.inv_phone));
                    }else
                        {
                            user_phone.setError(null);

                        }


                if (TextUtils.isEmpty(m_ads_id)) {
                    ads_id.setError(getString(R.string.field_req));

                } else {
                    ads_id.setError(null);

                }

                if (uri==null)
                {
                    Toast.makeText(this, R.string.ch_trans_img, Toast.LENGTH_SHORT).show();
                }
            }


    }

    private void Send(String m_name, String m_phone, String m_money, String m_ads_id, String m_date, String  bank) {
        dialog.show();
        RequestBody name_part = Common.getRequestBody(m_name);
        RequestBody phone_part = Common.getRequestBody(m_phone);
        RequestBody money_part = Common.getRequestBody(m_money);
        RequestBody ad_id_part = Common.getRequestBody(m_ads_id);
        RequestBody date_part = Common.getRequestBody(m_date);
        RequestBody bank_part = Common.getRequestBody(bank);

        MultipartBody.Part image_part = Common.getMultiPartBody(uri,this,"transform_image");

        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<ResponseModel> call = retrofit.create(Services.class).transMoney(userModel.getUser_id(),name_part,money_part,bank_part,date_part,name_part,image_part,ad_id_part);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        dialog.dismiss();
                        Toast.makeText(TransferActivity.this, R.string.trans_success, Toast.LENGTH_LONG).show();
                        finish();
                    } else if (response.body().getSuccess() == 0) {
                        dialog.dismiss();
                        Toast.makeText(TransferActivity.this, R.string.error, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                dialog.dismiss();
                Log.e("Error", t.getMessage());
                Toast.makeText(TransferActivity.this, R.string.something, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void CreateProgressDialog() {
        ProgressBar bar = new ProgressBar(this);
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.transfering_comm));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setIndeterminateDrawable(drawable);
    }


    private void checkPermission()
    {
        if (ActivityCompat.checkSelfPermission(this,read_permission)!= PackageManager.PERMISSION_GRANTED)
        {
            String [] perm = {read_permission};
            ActivityCompat.requestPermissions(this,perm,read_req);
        }else
            {
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
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent, getString(R.string.sel_image)), img_req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == img_req && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            Picasso.with(this).load(Common.getFileFromPath(Common.getImagePath(this,uri))).into(img);
        }
    }



    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;
        UpdateUi(userModel);
    }

    private void UpdateUi(UserModel userModel) {
        user_name.setText(userModel.getUser_full_name());
        user_phone.setText(userModel.getUser_phone());
    }

    private void CreateBankAlertDialog(List<BankModel> bankModelList) {
        bank_dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(this).inflate(R.layout.banks_dialog, null);

        RecyclerView recView = view.findViewById(R.id.recView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recView.setLayoutManager(manager);
        RecyclerView.Adapter adapter = new BankAdapter(this, bankModelList);
        recView.setAdapter(adapter);
        bank_dialog.getWindow().getAttributes().windowAnimations = R.style.dialog;
        bank_dialog.setCanceledOnTouchOutside(false);
        bank_dialog.setView(view);
        bank_dialog.show();
    }


    public void setBankItem(BankModel bankModel) {
        bank_dialog.dismiss();
        bank = bankModel.getAccount_bank_name();
        tv_bank.setText(bankModel.getAccount_bank_name());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        m_date = dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
        if (current_language.equals("ar"))
        {
            date.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);

        }else
            {
                date.setText(dayOfMonth+"-"+(monthOfYear+1)+"-"+year);

            }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == read_req)
        {
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                SelectImage();
            }else
                {
                    Toast.makeText(this, R.string.access_img_perm_denied, Toast.LENGTH_SHORT).show();
                }
        }
    }
}
