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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lamudi.phonefield.PhoneInputLayout;
import com.semicolon.tadlaly.Models.ContactsModel;
import com.semicolon.tadlaly.Models.ResponseModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;
import com.semicolon.tadlaly.language.LanguageHelper;
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
    private EditText subject, message;
    private TextView name, email;
    private Button send_Btn;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private ProgressDialog dialog;
    private String user_type = "";
    private PhoneInputLayout edt_check_phone;
    private LinearLayout ll_call;
    private AlertDialog.Builder serviceBuilder;
    private ContactsModel contactsModel;
    private ProgressDialog dialog_getContacts;
    private TextView tv_site,tv_email,tv_phone;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);

        super.attachBaseContext(LanguageHelper.onAttach(newBase, Paper.book().read("language",Locale.getDefault().getLanguage())));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contac_us);
       /* Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"OYA-Regular.ttf",true);*/
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
        back = findViewById(R.id.back);
        whatsApp_Btn = findViewById(R.id.whatsapp_btn);
        email_Btn = findViewById(R.id.email_btn);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        edt_check_phone = findViewById(R.id.edt_check_phone);
        edt_check_phone.setDefaultCountry("sa");
        subject = findViewById(R.id.subject);
        message = findViewById(R.id.message);
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
        send_Btn.setOnClickListener(view ->{
            if (user_type.equals(Tags.app_user))
            {
                Send(Tags.sendType_normal);

            }else
                {
                    serviceBuilder.show();
                }

        } );
        whatsApp_Btn.setOnClickListener(view ->{

            if (isWhatsApp_installed())
            {
                if (contactsModel!=null)
                {
                    String whatsphone = contactsModel.getWhatsapp();
                    if (!whatsphone.startsWith("+"))
                    {
                        whatsphone = whatsphone.replace("+","");
                    }else if (!whatsphone.startsWith("966"))
                    {
                        whatsphone = "966"+whatsphone;
                    }
                    Log.e("whats","whats"+whatsphone);

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT,"السلام عليكم");
                    sendIntent.putExtra("jid", whatsphone + "@s.whatsapp.net"); //phone number without "+" prefix
                    sendIntent.setPackage("com.whatsapp");
                    if (sendIntent.resolveActivity(getPackageManager()) == null) {
                        Toast.makeText(this, "Error\n" + "", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity(sendIntent);
                }else
                {
                    Toast.makeText(this, R.string.something, Toast.LENGTH_SHORT).show();
                }

            }else
            {
                Toast.makeText(this, R.string.wtsnotins, Toast.LENGTH_LONG).show();
            }

        } );
        email_Btn.setOnClickListener(view ->
        {

            if (user_type.equals(Tags.app_user))
            {
                if (contactsModel!=null)
                {
                    Send(Tags.sendType_email);

                }else
                    {
                        Toast.makeText(this, R.string.something, Toast.LENGTH_SHORT).show();
                    }
            }else
            {
                serviceBuilder.show();

            }
        });

    }

    private void CreateProgress_dialog() {
        ProgressBar bar = new ProgressBar(this);
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.sending_msg));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIndeterminateDrawable(drawable);
    }
    private void Send(String type) {

        String m_subject = subject.getText().toString();
        String m_message = message.getText().toString();
        String m_phone = edt_check_phone.getPhoneNumber();
        edt_check_phone.setPhoneNumber(m_phone);
        if (TextUtils.isEmpty(m_phone))
        {
            edt_check_phone.getTextInputLayout().getEditText().setError(getString(R.string.enter_phone));
        }else if (!edt_check_phone.isValid())
        {
            edt_check_phone.getTextInputLayout().getEditText().setError(getString(R.string.inv_phone));

        }
        else if (TextUtils.isEmpty(m_subject))
        {
            edt_check_phone.getTextInputLayout().getEditText().setError(null);

            subject.setError(getString(R.string.enter_sub));
        }else if (TextUtils.isEmpty(m_message))
        {
            edt_check_phone.getTextInputLayout().getEditText().setError(null);

            subject.setError(null);
            message.setError(getString(R.string.enter_msg));

        }else
            {
                edt_check_phone.getTextInputLayout().getEditText().setError(null);
                subject.setError(null);
                message.setError(null);

                SendData(contactsModel.getWhatsapp(),contactsModel.getEmail(),type,m_subject,m_message,m_phone);
                //getContacts(type,m_subject,m_message,m_phone);
            }

    }

    private void getContacts()
    {
        dialog_getContacts = Common.createProgressDialog(this,getString(R.string.wait));
        dialog_getContacts.show();
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<ContactsModel> call = retrofit.create(Services.class).getContacts();
        call.enqueue(new Callback<ContactsModel>() {
            @Override
            public void onResponse(Call<ContactsModel> call, Response<ContactsModel> response) {
                if (response.isSuccessful())
                {
                    dialog_getContacts.dismiss();
                    contactsModel = response.body();

                    String phone = contactsModel.getPhone();
                    if (phone.startsWith("966"))
                    {
                        phone = "+"+phone;
                    }else if (!phone.startsWith("+966"))
                    {
                        phone = "+966"+phone;

                    }
                    tv_site.setText(contactsModel.getWebsite());
                    tv_email.setText(contactsModel.getEmail());
                    tv_phone.setText(phone);
                   // SendData(response.body().getWhatsapp(),response.body().getEmail(),type,m_subject,m_message,m_phone);

                }
            }

            @Override
            public void onFailure(Call<ContactsModel> call, Throwable t) {
               // dialog_getContacts.dismiss();
                Log.e("Error",t.getMessage());
                Toast.makeText(ContactUsActivity.this,R.string.reg_error, Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void SendData(String whatsapp, String email, String type, String m_subject, String m_message, String m_phone)
    {
        if (type.equals(Tags.sendType_normal))
        {
            dialog.show();
            Map<String,String> map =new HashMap<>();
            map.put("name",userModel.getUser_full_name());
            map.put("email",userModel.getUser_email());
            map.put("subject",m_subject);
            map.put("message",m_message);
            map.put("phone",m_phone);
            Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
            Call<ResponseModel> call = retrofit.create(Services.class).ContactUs(map);
            call.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    if (response.isSuccessful())
                    {
                        if (response.body().getSuccess()==1)
                        {
                            dialog.dismiss();

                        }else if (response.body().getSuccess()==0)
                        {
                            Toast.makeText(ContactUsActivity.this,R.string.reg_error, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Log.e("Error",t.getMessage());
                    dialog.dismiss();
                    Toast.makeText(ContactUsActivity.this,R.string.reg_error, Toast.LENGTH_SHORT).show();

                }
            });

        }else if (type.equals(Tags.sendType_email))
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL,new String[]{email});
            intent.putExtra(Intent.EXTRA_SUBJECT,m_subject);
            intent.putExtra(Intent.EXTRA_TEXT,m_message);
            PackageManager pm =getPackageManager();
            List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
            ResolveInfo best = null;
            for(ResolveInfo info : matches)
            {
                if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                {
                    best = info;

                }
            }

            if (best != null)
            {
                intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);

            }

            startActivity(intent);

        }else if (type.equals(Tags.sendType_whats))
        {



        }

    }
    private boolean isWhatsApp_installed()
    {

        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.whatsapp",PackageManager.GET_ACTIVITIES);
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
        name.setText(userModel.getUser_full_name());
        email.setText(userModel.getUser_email());
        edt_check_phone.setPhoneNumber(userModel.getUser_phone());
    }


}
