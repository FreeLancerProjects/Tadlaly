package com.semicolon.tadlaly.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
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
import com.semicolon.tadlaly.language.LanguageHelper;
import com.semicolon.tadlaly.share.Common;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TransferActivity extends AppCompatActivity implements UserSingleTone.OnCompleteListener{

    private EditText money,ads_id;
    private TextView user_name,user_phone,date,tv_bank;
    private Button sendBtn;
    private ProgressDialog dialog;
    private ImageView back,upload;
    private RoundedImageView img;
    private String encodedImage;
    private Bitmap bitmap;
    private final int img_req = 12221;
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private List<BankModel> bankModelList;
    private String bank="";
    private String user_type="";
    private AlertDialog.Builder serviceBuilder;
    private AlertDialog bank_dialog;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);

        super.attachBaseContext(LanguageHelper.onAttach(newBase, Paper.book().read("language",Locale.getDefault().getLanguage())));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        /*Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"OYA-Regular.ttf",true);
  */      initView();
        getDataFromIntent();
        if (user_type.equals(Tags.app_user))
        {
            userSingleTone = UserSingleTone.getInstance();
            userSingleTone.getUser(this);
        }
       CreateServiceDialog();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null)
        {
            if (intent.hasExtra("user_type"))
            {
                user_type = intent.getStringExtra("user_type");
            }
        }
    }

    private void getBanksAccount() {

        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.lodng_bnks));
        dialog.show();
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<List<BankModel>> call = retrofit.create(Services.class).getBanks();
        call.enqueue(new Callback<List<BankModel>>() {
            @Override
            public void onResponse(Call<List<BankModel>> call, Response<List<BankModel>> response) {
                if (response.isSuccessful())
                {
                    dialog.dismiss();

                    if (response.body().size()>0)
                    {
                        bankModelList.clear();
                        bankModelList.addAll(response.body());
                        CreateBankAlertDialog(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<BankModel>> call, Throwable t) {
                dialog.dismiss();

                Log.e("Error",t.getMessage());

            }
        });
    }
    private void CreateServiceDialog()
    {
        serviceBuilder = new AlertDialog.Builder(this);
        serviceBuilder.setMessage(R.string.ser_not_ava);
        serviceBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
            AlertDialog alertDialog=serviceBuilder.create();
            alertDialog.dismiss();
            finish();
        } );

        AlertDialog alertDialog=serviceBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(false);

    }
    private void initView() {
        bankModelList = new ArrayList<>();
        user_name = findViewById(R.id.user_name);
        user_phone= findViewById(R.id.user_phone);
        date = findViewById(R.id.date);
        tv_bank = findViewById(R.id.tv_bank);

        money = findViewById(R.id.mony);
        back = findViewById(R.id.back);
        ads_id = findViewById(R.id.ads_id);
        upload = findViewById(R.id.upload);
        img = findViewById(R.id.img);
        sendBtn = findViewById(R.id.send_btn);
        upload.setOnClickListener(view -> SelectImage());
        back.setOnClickListener(view ->
        finish()
        );


        sendBtn.setOnClickListener(view ->
                {
                    if (user_type.equals(Tags.app_user))
                    {
                        Send();

                    }else
                        {
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
                if (bankModelList.size()>0)
                {
                    CreateBankAlertDialog(bankModelList);
                }else
                    {
                        getBanksAccount();

                    }
            }
        });

        CreateProgressDialog();
    }



    private void CreateDateDialog() {
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DatePickerDialog datePickerDialog = new DatePickerDialog(TransferActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            String sDate = dateFormatter.format(newDate.getTime());

            Date dateSpecified = newDate.getTime();
            Date c = Calendar.getInstance().getTime();
            if (dateSpecified.before(c)) {

                Toast.makeText(this, R.string.ch_new_date, Toast.LENGTH_SHORT).show();
                CreateDateDialog();
                //dateall.setText("Choose date");

            } else {
                date.setText(sDate+"");
                //   dateall.setText(date);

            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    private void Send() {
        String m_name = user_name.getText().toString();
        String m_phone= user_phone.getText().toString();
        String m_date = date.getText().toString();
        String m_money = money.getText().toString();
        String m_ads_id= ads_id.getText().toString();

        if (TextUtils.isEmpty(m_name))
        {
            user_name.setError(getString(R.string.name));
        }else if (TextUtils.isEmpty(m_money))
        {
            user_name.setError(null);
            money.setError(getString(R.string.enter_money));
        }else if (TextUtils.isEmpty(bank))
        {
            tv_bank.setError(getString(R.string.choose_bank));
        }
        else if (TextUtils.isEmpty(m_date))
        {
            tv_bank.setError(null);
            user_name.setError(null);
            money.setError(null);
            date.setError(getString(R.string.ch_date));
        }
        else if (TextUtils.isEmpty(m_phone))
        {
            tv_bank.setError(null);
            user_name.setError(null);
            money.setError(null);
            date.setError(null);
            user_phone.setError(getString(R.string.enter_phone));
        }


        else if (TextUtils.isEmpty(m_ads_id))
        {
            tv_bank.setError(null);
            ads_id.setError(getString(R.string.enter_adsnum));
            user_phone.setError(null);
            user_name.setError(null);
            money.setError(null);
            date.setError(null);

        }else
            {
                tv_bank.setError(null);
                user_phone.setError(null);
                user_name.setError(null);
                money.setError(null);
                date.setError(null);
                ads_id.setError(null);
                dialog.show();

                encodedImage = EncodeImage(bitmap);
                Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
                Call<ResponseModel> call = retrofit.create(Services.class).transMoney(userModel.getUser_id(), m_name, m_money, bank, m_date, m_name, encodedImage, m_ads_id);
                call.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful())
                        {
                            if (response.body().getSuccess()==1)
                            {
                                dialog.dismiss();
                                Toast.makeText(TransferActivity.this, R.string.trans_success, Toast.LENGTH_LONG).show();
                                finish();
                            }else if (response.body().getSuccess()==0)
                            {
                                dialog.dismiss();
                                Toast.makeText(TransferActivity.this,R.string.error, Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        dialog.dismiss();
                        Log.e("Error",t.getMessage());
                        Toast.makeText(TransferActivity.this,R.string.something, Toast.LENGTH_LONG).show();
                    }
                });

            }
    }
    private void CreateProgressDialog()
    {
        ProgressBar bar = new ProgressBar(this);
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.transfering_comm));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setIndeterminateDrawable(drawable);
    }

    private void SelectImage()
    {
        Intent intent;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
        {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        }else
        {
            intent = new Intent(Intent.ACTION_GET_CONTENT);

        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent,getString(R.string.sel_image)),img_req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == img_req && resultCode == RESULT_OK && data != null)
        {
            try {
                Uri uri = data.getData();
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                img.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String EncodeImage(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,90,outputStream);
        byte [] bytes = outputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
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

    private   void CreateBankAlertDialog(List<BankModel> bankModelList)
    {
        bank_dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(this).inflate(R.layout.banks_dialog,null);

        RecyclerView recView = view.findViewById(R.id.recView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recView.setLayoutManager(manager);
        RecyclerView.Adapter adapter = new BankAdapter(this,bankModelList);
        recView.setAdapter(adapter);
        bank_dialog.getWindow().getAttributes().windowAnimations=R.style.dialog;
        bank_dialog.setCanceledOnTouchOutside(false);
        bank_dialog.setView(view);
        bank_dialog.show();
    }


    public void setBankItem(BankModel bankModel)
    {
        bank_dialog.dismiss();
        bank = bankModel.getAccount_bank_name();
        tv_bank.setText(bankModel.getAccount_bank_name());
    }

}
