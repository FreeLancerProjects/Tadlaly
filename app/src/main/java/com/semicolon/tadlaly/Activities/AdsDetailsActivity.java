package com.semicolon.tadlaly.Activities;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.tadlaly.Adapters.AdsDetailsPagerAdapter;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.Models.ResponseModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;
import com.semicolon.tadlaly.language.LocalManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AdsDetailsActivity extends AppCompatActivity implements UserSingleTone.OnCompleteListener{
    private ViewPager pager;
    private TabLayout tab;
    private TextView ad_number, ad_cost, ad_date, ad_viewers, ad_shares, ad_distance, ad_title, ad_details, ad_state_new, ad_state_old, city, no_ads, ad_name,city2;
    private AdsDetailsPagerAdapter adapter;
    private List<MyAdsModel.Images> images;
    private LinearLayout distContainer;
    private MyAdsModel myAdsModel;
    private String whoVisit = "";
    private Timer timer;
    private ImageView back, shareBtn, viewerBtn;
    private LinearLayout contactContainer;
    private FrameLayout call_btn, email_btn, whats_btn;
    private final int share_req=968;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private String user_id;
    private AlertDialog alertDialog;
    private String current_language;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LocalManager.updateResources(newBase,LocalManager.getLanguage(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_details);
        timer = new Timer();
        initView();
        getDataFromIntent();
        CreateAlertDialog();
    }



    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            myAdsModel = (MyAdsModel) intent.getSerializableExtra("ad_details");
            whoVisit = intent.getStringExtra("whoVisit");
            user_id = intent.getStringExtra("user_id");
            Log.e("USERType_ADS_Det_Ac",user_id);
            UpdateUi(myAdsModel, whoVisit);
            if (whoVisit.equals(Tags.visitor))
            {
                UpdateViewers(myAdsModel.getId_advertisement());

            }


        }
    }
    private void UpdateViewers(String id_advertisement) {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<MyAdsModel> call = retrofit.create(Services.class).IncreaseShare_Viewers(id_advertisement, Tags.view);
        call.enqueue(new Callback<MyAdsModel>() {
            @Override
            public void onResponse(Call<MyAdsModel> call, Response<MyAdsModel> response) {
                if (response.isSuccessful())
                {
                    if (response.body().getSuccess()==1)
                    {
                        ad_viewers.setText(response.body().getView_count());
                    }else if (response.body().getSuccess()==0)
                    {
                        Log.e("Error","Error");
                    }
                }
            }

            @Override
            public void onFailure(Call<MyAdsModel> call, Throwable t) {
                Log.e("Error",t.getMessage());

            }
        });
    }
    private void initView() {

        current_language = Paper.book().read("language", Locale.getDefault().getLanguage());

        back             = findViewById(R.id.back);

        if (current_language.equals("ar"))
        {
            back.setRotation(180f);
        }

        images           = new ArrayList<>();
        pager            = findViewById(R.id.pager);
        tab              = findViewById(R.id.tab);
        shareBtn         = findViewById(R.id.shareBtn);
        viewerBtn        = findViewById(R.id.viewerBtn);
        no_ads           = findViewById(R.id.no_ads);
        distContainer    = findViewById(R.id.distContainer);
        ad_name          = findViewById(R.id.ad_name);
        ad_number        = findViewById(R.id.ad_number);
        ad_cost          = findViewById(R.id.ad_cost);
        ad_date          = findViewById(R.id.ad_date);
        ad_viewers       = findViewById(R.id.viewers);
        ad_shares        = findViewById(R.id.shares);
        ad_distance      = findViewById(R.id.distance);
        ad_title         = findViewById(R.id.ad_title);
        ad_details       = findViewById(R.id.ad_details);
        ad_state_new     = findViewById(R.id.state_new);
        ad_state_old     = findViewById(R.id.state_old);
        contactContainer = findViewById(R.id.contactContainer);
        call_btn         = findViewById(R.id.call_btn);
        email_btn        = findViewById(R.id.email_btn);
        whats_btn        = findViewById(R.id.whatsapp_btn);
        city             = findViewById(R.id.city);
        city2            = findViewById(R.id.city2);

        tab.setupWithViewPager(pager);
        adapter = new AdsDetailsPagerAdapter(images, this);
        pager.setAdapter(adapter);
        back.setOnClickListener(view -> finish());



        call_btn.setOnClickListener(view -> {
            Log.e("phone",myAdsModel.getPhone());
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + myAdsModel.getPhone()));
            startActivity(intent);
        });
        email_btn.setOnClickListener(view -> {

            if (user_id.equals("0"))
            {
                alertDialog.show();
            }else if (!user_id.equals("0"))
                {
                    Intent intent = new Intent(this,SendMsgActivity.class);
                    intent.putExtra("chat_user_id",myAdsModel.getAdvertisement_user());
                    intent.putExtra("chat_user_name",myAdsModel.getAdvertisement_owner());
                    intent.putExtra("chat_user_image",myAdsModel.getAdvertisement_owner_image());
                    intent.putExtra("chat_user_phone",myAdsModel.getPhone());
                    intent.putExtra("curr_user_name",userModel.getUser_full_name());
                    intent.putExtra("curr_user_image",userModel.getUser_photo());
                    startActivity(intent);
                }

        });

        whats_btn.setOnClickListener(view -> {
            String phone_number = myAdsModel.getPhone();
            String text = "السلام عليكم بخصوص اعلانك في تطبيق تدللي "+myAdsModel.getAdvertisement_title()+"\n"+"الكود"+myAdsModel.getAdvertisement_code();
            String uri = "whatsapp://send?phone="+phone_number+"&text="+text;
            Intent sendIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(uri));
            if (sendIntent.resolveActivity(getPackageManager()) == null) {
                Toast.makeText(this, "Error/n" + "", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(sendIntent);

        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Share();
            }
        });

        ad_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setText(ad_title.getText().toString());
                Toast.makeText(AdsDetailsActivity.this, R.string.copied, Toast.LENGTH_SHORT).show();

            }
        });

        ad_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setText(ad_title.getText().toString());
                Toast.makeText(AdsDetailsActivity.this, R.string.copied, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CreateAlertDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog_msging,null);
        TextView done = view.findViewById(R.id.done);

        alertDialog =  new AlertDialog.Builder(this)
                .setCancelable(false)
                .create();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(view);
        alertDialog.setCanceledOnTouchOutside(false);

    }
    private void Share() {

        String text = "Code #"+myAdsModel.getAdvertisement_code()+"\n"+"Ads link \n"+myAdsModel.getShare_link();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,text);
        startActivityForResult(intent,share_req);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==share_req && resultCode==RESULT_OK)
        {
            if (whoVisit.equals(Tags.visitor))
            {
                UpdateShare();

            }
        }
    }

    private void UpdateShare() {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<MyAdsModel> call = retrofit.create(Services.class).IncreaseShare_Viewers(myAdsModel.getId_advertisement(), Tags.share);
        call.enqueue(new Callback<MyAdsModel>() {
            @Override
            public void onResponse(Call<MyAdsModel> call, Response<MyAdsModel> response) {
                if (response.isSuccessful())
                {
                    if (response.body().getSuccess()==1)
                    {
                        ad_shares.setText(response.body().getShare_count());
                    }else if (response.body().getSuccess()==0)
                    {
                        Log.e("Error","Error");
                    }
                }
            }

            @Override
            public void onFailure(Call<MyAdsModel> call, Throwable t) {
                Log.e("Error",t.getMessage());

            }
        });
    }

    private boolean checkWhatsAppFounded()
    {
        PackageManager packageManager = getPackageManager();
        try {
            packageManager.getPackageInfo("com.whatsapp",PackageManager.GET_ACTIVITIES);
            return true;
        }catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    private void  UpdateUi(MyAdsModel myAdsModel ,String whoVisit)
    {


        if (whoVisit.equals(Tags.me_visit))
        {
            distContainer.setVisibility(View.GONE);
            city.setVisibility(View.VISIBLE);
            city.setText(myAdsModel.getCity());
            shareBtn.setEnabled(false);
            viewerBtn.setEnabled(false);
            contactContainer.setVisibility(View.GONE);
            city2.setVisibility(View.INVISIBLE);

        }else
            {
                if (!user_id.equals("0"))
                {
                    userSingleTone = UserSingleTone.getInstance();
                    userSingleTone.getUser(this);
                    contactContainer.setVisibility(View.VISIBLE);

                    if (!myAdsModel.isReaded())
                    {
                        ReadAds();
                    }



                }else if (user_id.equals("0"))
                    {

                        contactContainer.setVisibility(View.GONE);

                    }
                shareBtn.setEnabled(true);
                viewerBtn.setEnabled(true);
                distContainer.setVisibility(View.VISIBLE);
                city.setVisibility(View.GONE);
                city2.setVisibility(View.VISIBLE);
                city2.setText(myAdsModel.getCity());
                ad_distance.setText(myAdsModel.getDistance()+" "+getString(R.string.km));
                city.setVisibility(View.VISIBLE);



            }
            if (myAdsModel.getAdvertisement_image().size()>0)
            {
                no_ads.setVisibility(View.GONE);
            }else
                {
                    no_ads.setVisibility(View.VISIBLE);

                }

        images.addAll(myAdsModel.getAdvertisement_image());
        adapter.notifyDataSetChanged();
        ad_name.setText(myAdsModel.getAdvertisement_title());
        timer.scheduleAtFixedRate(new TimerClass(),4000,5000);
        ad_number.setText("#"+myAdsModel.getAdvertisement_code());
        ad_cost.setText(myAdsModel.getAdvertisement_price()+" "+getString(R.string.sar));
        ad_date.setText(myAdsModel.getAdvertisement_date());
        ad_viewers.setText(myAdsModel.getView_count());
        ad_shares.setText(myAdsModel.getShare_count());
        ad_title.setText(myAdsModel.getAdvertisement_title());
        ad_details.setText(myAdsModel.getAdvertisement_content());
        if (myAdsModel.getAdvertisement_type().equals(Tags.ad_new))
        {
            ad_state_new.setVisibility(View.VISIBLE);
            ad_state_old.setVisibility(View.GONE);
        }else
            {
                ad_state_new.setVisibility(View.GONE);
                ad_state_old.setVisibility(View.VISIBLE);
            }

    }

    private void ReadAds() {

        Api.getRetrofit(Tags.Base_Url)
                .create(Services.class)
                .readAds(myAdsModel.getId_advertisement(),userModel.getUser_id(),"read")
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        Log.e("eeeeeeee",response.body().getSuccess_doread()+"_");
                        if (response.isSuccessful()&&response.body().getSuccess_doread() ==1)
                        {
                            Log.e("success","readed");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        try
                        {
                            Log.e("ErrorRead",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }

    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;
    }

    public class TimerClass extends TimerTask{
        @Override
        public void run() {
            try {
                runOnUiThread(() -> {
                    if (pager.getCurrentItem()<images.size()-1)
                    {
                        pager.setCurrentItem(pager.getCurrentItem()+1);
                    }else
                    {
                        try {
                            pager.setAdapter(adapter);
                            pager.setCurrentItem(0);

                        }catch (NullPointerException e)
                        {

                        }catch (Exception e)
                        { }
                    }
                });
            }catch (NullPointerException e){}
            catch (Exception e){}

        }
    }

    public void setPagerItemClick()
    {
        Intent intent = new Intent(AdsDetailsActivity.this,ViewImagesActivity.class);
        intent.putExtra("images", (Serializable) images);
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        if (timer!=null)
        {
            timer.cancel();
        }
        super.onDestroy();

    }
}
