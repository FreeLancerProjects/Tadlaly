package com.semicolon.tadlaly.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.semicolon.tadlaly.Adapters.SpinnerBankAdapter;
import com.semicolon.tadlaly.Models.BankModel;
import com.semicolon.tadlaly.Models.ResponseModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TransferActivity extends AppCompatActivity implements UserSingleTone.OnCompleteListener{

    private EditText money,ads_id;
    private TextView user_name,user_phone,date;
    private Button sendBtn;
    private ProgressDialog dialog;
    private ImageView back,upload;
    private RoundedImageView img;
    private String encodedImage;
    private Bitmap bitmap;
    private final int img_req = 12221;
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private Spinner spinner_bank;
    private SpinnerBankAdapter spinnerBankAdapter;
    private List<BankModel> bankModelList;
    private String bank="";
    private String user_type="";
    private AlertDialog.Builder serviceBuilder;




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
        getBanksAccount();
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

        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<List<BankModel>> call = retrofit.create(Services.class).getBanks();
        call.enqueue(new Callback<List<BankModel>>() {
            @Override
            public void onResponse(Call<List<BankModel>> call, Response<List<BankModel>> response) {
                if (response.isSuccessful())
                {
                    if (response.body().size()>0)
                    {
                        bankModelList.addAll(response.body());
                        spinnerBankAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<BankModel>> call, Throwable t) {
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
        money = findViewById(R.id.mony);
        back = findViewById(R.id.back);
        spinner_bank = findViewById(R.id.spinner_bank);
        ads_id = findViewById(R.id.ads_id);
        upload = findViewById(R.id.upload);
        img = findViewById(R.id.img);
        sendBtn = findViewById(R.id.send_btn);
        spinnerBankAdapter = new SpinnerBankAdapter(this,R.layout.spinner_item,bankModelList);
        BankModel bankModel = new BankModel("","","",getString(R.string.bank),"");
        bankModelList.add(bankModel);
        spinner_bank.setAdapter(spinnerBankAdapter);
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

        spinner_bank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i!=0)
                {
                    bank = bankModelList.get(i).getAccount_bank_name();
                    Log.e("banks",bankModelList.get(i).getAccount_bank_name());
                    Log.e("banks",bankModelList.get(i).getAccount_id());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
        }else if (TextUtils.isEmpty(m_date))
        {
            user_name.setError(null);
            money.setError(null);
            date.setError(getString(R.string.ch_date));
        }
        else if (TextUtils.isEmpty(m_phone))
        {
            user_name.setError(null);
            money.setError(null);
            date.setError(null);
            user_phone.setError(getString(R.string.enter_phone));
        }


        else if (TextUtils.isEmpty(m_ads_id))
        {
            ads_id.setError(getString(R.string.enter_adsnum));
            user_phone.setError(null);
            user_name.setError(null);
            money.setError(null);
            date.setError(null);

        }else
            {
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
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
}
