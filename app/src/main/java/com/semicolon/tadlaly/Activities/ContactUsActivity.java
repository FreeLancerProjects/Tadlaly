package com.semicolon.tadlaly.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.tadlaly.Models.ContactsModel;
import com.semicolon.tadlaly.Models.ResponseModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;
import com.semicolon.tadlaly.language.LocalManager;
import com.semicolon.tadlaly.share.Common;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ContactUsActivity extends AppCompatActivity implements UserSingleTone.OnCompleteListener {
    private ImageView back, whatsApp_Btn, email_Btn;
    private EditText edt_subject, edt_message, edt_name, edt_email;
    private EditText edt_phone;
    private Button send_Btn;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private ProgressDialog dialog;
    private String user_type = "";
    private LinearLayout ll_call;
    private AlertDialog.Builder serviceBuilder;
    private ContactsModel contactsModel;
    private ProgressDialog dialog_getContacts;
    private TextView tv_site, tv_email, tv_phone;
    private String current_language;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);

        super.attachBaseContext(LocalManager.updateResources(newBase, LocalManager.getLanguage(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contac_us);
        getDataFromIntent();
        initView();
        CreateProgress_dialog();
        if (user_type.equals(Tags.app_user)) {
            userSingleTone = UserSingleTone.getInstance();
            userSingleTone.getUser(this);
        }
        CreateServiceDialog();
        getContacts();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("user_type")) {
                user_type = intent.getStringExtra("user_type");
            }
        }
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
        current_language = Paper.book().read("language", Locale.getDefault().getLanguage());
        back = findViewById(R.id.back);

        if (current_language.equals("ar"))
        {
            back.setRotation(180f);
        }
        whatsApp_Btn = findViewById(R.id.whatsapp_btn);
        email_Btn = findViewById(R.id.email_btn);
        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        edt_phone = findViewById(R.id.edt_phone);
        edt_subject = findViewById(R.id.edt_subject);
        edt_message = findViewById(R.id.edt_message);
        tv_site = findViewById(R.id.tv_site);
        tv_email = findViewById(R.id.tv_email);
        tv_phone = findViewById(R.id.tv_phone);

        send_Btn = findViewById(R.id.send_btn);
        ll_call = findViewById(R.id.ll_call);
        back.setOnClickListener(view -> finish());
        ll_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = "+9660550411663";
                Uri uri = Uri.parse("tel:" + phone);

                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });
        send_Btn.setOnClickListener(view -> {
            if (user_type.equals(Tags.app_user)) {
                CheckData(Tags.sendType_normal);

            } else {
                serviceBuilder.show();
            }

        });
        whatsApp_Btn.setOnClickListener(view -> {

                    Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("whatsapp://send?phone=9660550411663&text=السلام عليكم ورحمة الله وبركاتة"));

                    if (sendIntent.resolveActivity(getPackageManager()) == null) {
                        Toast.makeText(this, "Error\n" + "", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity(sendIntent);
                }

        );
        email_Btn.setOnClickListener(view ->
        {

            if (user_type.equals(Tags.app_user)) {
                if (contactsModel != null) {
                    CheckData(Tags.sendType_email);

                } else {
                    Toast.makeText(this, R.string.something, Toast.LENGTH_SHORT).show();
                }
            } else {
                serviceBuilder.show();

            }
        });

    }

    private void CreateProgress_dialog() {
        ProgressBar bar = new ProgressBar(this);
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.sending_msg));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIndeterminateDrawable(drawable);
    }

    private void CheckData(String type) {

        String phone_regex = "^[+]?[0-9]{6,}$";
        String m_name = edt_name.getText().toString().trim();
        String m_email = edt_email.getText().toString().trim();
        String m_subject = edt_subject.getText().toString().trim();
        String m_message = edt_message.getText().toString().trim();
        String m_phone = edt_phone.getText().toString().trim();

        if (!TextUtils.isEmpty(m_name) &&
                !TextUtils.isEmpty(m_email)&&
                Patterns.EMAIL_ADDRESS.matcher(m_email).matches()&&
                !TextUtils.isEmpty(m_phone)&&
                m_phone.matches(phone_regex)&&
                !TextUtils.isEmpty(m_subject)&&
                !TextUtils.isEmpty(m_message)

        )
        {
            edt_name.setError(null);
            edt_email.setError(null);
            edt_phone.setError(null);
            edt_subject.setError(null);
            edt_message.setError(null);

            SendData(contactsModel.getWhatsapp(), contactsModel.getEmail(), type, m_subject, m_message, m_phone);

        }else
            {
                if (TextUtils.isEmpty(m_name)) {

                    edt_name.setError(getString(R.string.field_req));
                }else
                    {
                        edt_name.setError(null);
                    }

                if (TextUtils.isEmpty(m_email)) {

                    edt_email.setError(getString(R.string.field_req));
                }else if (!Patterns.EMAIL_ADDRESS.matcher(m_email).matches())
                {
                    edt_email.setError(getString(R.string.inv_email));
                }else
                    {
                        edt_email.setError(null);
                    }

                if (TextUtils.isEmpty(m_phone)) {

                    edt_phone.setError(getString(R.string.field_req));
                }else if (!m_phone.matches(phone_regex))
                {
                    edt_phone.setError(getString(R.string.inv_phone));
                }else
                {
                    edt_phone.setError(null);
                }


                if (TextUtils.isEmpty(m_subject)) {

                    edt_subject.setError(getString(R.string.field_req));
                }else
                {
                    edt_subject.setError(null);
                }

                if (TextUtils.isEmpty(m_message)) {

                    edt_message.setError(getString(R.string.field_req));
                }else
                {
                    edt_message.setError(null);
                }

            }




    }

    private void getContacts() {
        dialog_getContacts = Common.createProgressDialog(this, getString(R.string.wait));
        dialog_getContacts.show();
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<ContactsModel> call = retrofit.create(Services.class).getContacts();
        call.enqueue(new Callback<ContactsModel>() {
            @Override
            public void onResponse(Call<ContactsModel> call, Response<ContactsModel> response) {
                if (response.isSuccessful()) {
                    dialog_getContacts.dismiss();
                    contactsModel = response.body();

                    String phone = contactsModel.getPhone();
                    tv_site.setText(contactsModel.getWebsite());
                    tv_email.setText(contactsModel.getEmail());
                    tv_phone.setText(phone);
                    // SendData(response.body().getWhatsapp(),response.body().getEmail(),type,m_subject,m_message,m_phone);

                }
            }

            @Override
            public void onFailure(Call<ContactsModel> call, Throwable t) {
                // dialog_getContacts.dismiss();
                Log.e("Error", t.getMessage());
                Toast.makeText(ContactUsActivity.this, R.string.reg_error, Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void SendData(String whatsapp, String email, String type, String m_subject, String m_message, String m_phone) {
        if (type.equals(Tags.sendType_normal)) {
            dialog.show();
            Map<String, String> map = new HashMap<>();
            map.put("name", userModel.getUser_full_name());
            map.put("email", email);
            map.put("subject", m_subject);
            map.put("message", m_message);
            map.put("phone", m_phone);
            Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
            Call<ResponseModel> call = retrofit.create(Services.class).ContactUs(map);
            call.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess() == 1) {
                            dialog.dismiss();

                        } else if (response.body().getSuccess() == 0) {
                            Toast.makeText(ContactUsActivity.this, R.string.reg_error, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Log.e("Error", t.getMessage());
                    dialog.dismiss();
                    Toast.makeText(ContactUsActivity.this, R.string.reg_error, Toast.LENGTH_SHORT).show();

                }
            });

        } else if (type.equals(Tags.sendType_email)) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            intent.putExtra(Intent.EXTRA_SUBJECT, m_subject);
            intent.putExtra(Intent.EXTRA_TEXT, m_message);
            PackageManager pm = getPackageManager();
            List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
            ResolveInfo best = null;
            for (ResolveInfo info : matches) {
                if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail")) {
                    best = info;

                }
            }

            if (best != null) {
                intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);

            }

            startActivity(intent);

        } else if (type.equals(Tags.sendType_whats)) {


        }

    }

    private boolean isWhatsApp_installed() {

        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;
        updateUi(userModel);
    }

    private void updateUi(UserModel userModel) {

        if (userModel!=null)
        {
            edt_name.setText(userModel.getUser_full_name());
            edt_email.setText(userModel.getUser_email());
            edt_phone.setText(userModel.getUser_phone());
        }


    }


}
