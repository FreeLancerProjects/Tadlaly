package com.semicolon.tadlaly.Activities;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.semicolon.tadlaly.Fragments.AllAppAdsFragment;
import com.semicolon.tadlaly.Fragments.Home_Fragment;
import com.semicolon.tadlaly.Models.LocationModel;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.Models.ResponseModel;
import com.semicolon.tadlaly.Models.TokenModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Preferences;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.Services.UpdateLatLng;
import com.semicolon.tadlaly.SingleTone.LatLngSingleTone;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.hdodenhof.circleimageview.CircleImageView;
import me.anwarshahriar.calligrapher.Calligrapher;
import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,UserSingleTone.OnCompleteListener,View.OnClickListener ,LatLngSingleTone.onLatLngSuccess{
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private CircleImageView user_image;
    private TextView user_name;
    private ImageView share,all_ads,search;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private Preferences preferences;
    private ProgressDialog dialog;
    private Intent serviceIntent;
    private LinearLayout profileContainer;
    private String user_type="";
    private AlertDialog.Builder serviceBuilder;
    private Home_Fragment home_fragment;
    private LatLngSingleTone latLngSingleTone;
    private NotificationManager notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "OYA-Regular.ttf", true);
        EventBus.getDefault().register(this);
        getDataFromIntent();
        initView();
        StartService_UpdateLatLng();
        CreateProgress_dialog();
        CreateServiceDialog();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }

    private void getDataFromIntent()
    {
        Intent intent = getIntent();
        if (intent!=null)
        {
            if (intent.hasExtra("user_type"))
            {

                user_type = intent.getStringExtra("user_type");

                if (user_type.equals(Tags.app_user)){
                    preferences = new Preferences(this);
                }else
                    {
                        latLngSingleTone = LatLngSingleTone.getInstance();

                    }
                Log.e("user_type",user_type);
            }
        }
    }

    private void CreateServiceDialog()
    {
        serviceBuilder = new AlertDialog.Builder(this);
        serviceBuilder.setMessage(R.string.ser_not_ava);
        serviceBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
            AlertDialog alertDialog=serviceBuilder.create();
            alertDialog.dismiss();
        } );

        AlertDialog alertDialog=serviceBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(false);


    }
    private void StartService_UpdateLatLng() {
        serviceIntent = new Intent(this, UpdateLatLng.class);
        startService(serviceIntent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (user_type.equals(Tags.app_user)){
            //userSingleTone.getUser(this);
            Log.e("token",user_type);
            UpdateToken(FirebaseInstanceId.getInstance().getToken());
        }

    }
    private void UpdateUi(UserModel userModel)
    {
        try {
            Picasso.with(this).load(Uri.parse(Tags.Image_Url+userModel.getUser_photo())).into(user_image);
            user_name.setText(userModel.getUser_full_name());
            Log.e("ssss","Update Ui");

        }catch (NullPointerException e)
        {

        }catch (Exception e)
        {

        }

    }
    private void initView()
    {
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView =  findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        user_image = view.findViewById(R.id.user_image);
        user_name = view.findViewById(R.id.user_name);
        profileContainer = view.findViewById(R.id.profileContainer);
        navigationView.setNavigationItemSelectedListener(this);
        profileContainer.setOnClickListener(view1 ->
        {
            if (user_type.equals(Tags.app_user))
            {
                Intent profile = new Intent(this,ProfileActivity.class);
                startActivity(profile);
            }

        });

        if (user_type.equals(Tags.app_visitor))
            {
                profileContainer.setEnabled(false);
                user_name.setText(R.string.visitor);
                user_image.setImageResource(R.drawable.visitor);

            }else if (user_type.equals(Tags.app_user))
            {
                userSingleTone = UserSingleTone.getInstance();
                userSingleTone.getUser(this);
            }

        if (user_type.equals(Tags.app_user))
        {
            home_fragment = Home_Fragment.getInstance(Tags.app_user);
            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,home_fragment).commit();


        }else
            {
                home_fragment = Home_Fragment.getInstance(Tags.app_visitor);
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,home_fragment).commit();

            }


        share = findViewById(R.id.share);
        all_ads = findViewById(R.id.ads);
        search = findViewById(R.id.search);

        share.setOnClickListener(this);
        all_ads.setOnClickListener(this);
        search.setOnClickListener(this);

    }
    private void CreateProgress_dialog()
    {
        ProgressBar bar = new ProgressBar(this);
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.logging_out));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIndeterminateDrawable(drawable);
    }
    @Override
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().findFragmentById(R.id.home_fragments_container)instanceof Home_Fragment)
            {
                super.onBackPressed();

            }else
                {
                    if (user_type.equals(Tags.app_user))
                    {
                        home_fragment = Home_Fragment.getInstance(userModel.getUser_id());
                        getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,home_fragment).commit();


                    }else
                    {
                        home_fragment = Home_Fragment.getInstance("0");
                        getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,home_fragment).commit();

                    }
                }
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.home:
                if (user_type.equals(Tags.app_user))
                {
                    home_fragment = Home_Fragment.getInstance(userModel.getUser_id());
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,home_fragment).commit();


                }else
                {
                    home_fragment = Home_Fragment.getInstance("0");
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,home_fragment).commit();

                }
                break;
            case R.id.myAds:
                if (user_type.equals(Tags.app_user))
                {
                    Intent myAds = new Intent(this,MyAdsActivity.class);
                    startActivity(myAds);
                }else
                    {
                        serviceBuilder.show();

                    }

                break;
            case R.id.myMsg:
                if (user_type.equals(Tags.app_user))
                {
                    Intent myMsg = new Intent(this,AllMessagesActivity.class);
                    startActivity(myMsg);
                }else
                    {
                        serviceBuilder.show();
                    }

                break;
            case R.id.transMoney:
                Intent trans = new Intent(this,TransferActivity.class);
                trans.putExtra("user_type",user_type);
                startActivity(trans);
                break;
            case R.id.addAds:
                Intent addAds = new Intent(this,Add_AdsActivity.class);
                addAds.putExtra("user_type",user_type);
                startActivity(addAds);

                break;
            case R.id.contactUs:
                Intent contact = new Intent(this,ContactUsActivity.class);
                contact.putExtra("user_type",user_type);
                startActivity(contact);
                break;
            case R.id.register:
                Intent register = new Intent(HomeActivity.this,RegisterActivity.class);
                register.putExtra("reg_type",Tags.reg_from_home);
                startActivity(register);
                finish();

                break;
            case R.id.suggestion:
                /*Intent suggestion = new Intent(this,ContactUsActivity.class);
                startActivity(suggestion);*/
                break;
            case R.id.aboutApp:
                Intent aboutApp = new Intent(this,AboutAppActivity.class);
                startActivity(aboutApp);
                break;
            case R.id.rules:
                Intent rules = new Intent(this,RulesActivity.class);
                startActivity(rules);
                break;
            case R.id.logout:

                if (user_type.equals(Tags.app_user))
                {
                    Log.e("dsdsd","Logout"+user_type);
                    LogOut();

                }else if (user_type.equals(Tags.app_visitor))
                {
                    Log.e("dsdsd","finish"+user_type);

                    finish();
                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void LogOut()
    {
        dialog.show();
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<ResponseModel> call = retrofit.create(Services.class).Logout(userModel.getUser_id());
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful())
                {
                    if (response.body().getSuccess()==0)
                    {
                        dialog.dismiss();
                        Toast.makeText(HomeActivity.this, R.string.reg_error, Toast.LENGTH_LONG).show();



                    }else if (response.body().getSuccess()==1)
                    {
                        notificationManager.cancel(0);
                        ShortcutBadger.removeCount(getApplicationContext());

                        dialog.dismiss();
                        preferences.ClearSharedPref();
                        Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();


                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.e("Error",t.getMessage());
                Toast.makeText(HomeActivity.this, R.string.something, Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }
    @Override
    public void onSuccess(UserModel userModel)
    {
        this.userModel = userModel;
        UpdateUi(userModel);
    }
    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        switch (id)
        {
            case R.id.share:
                Share();
                break;
            case R.id.ads:
                if (user_type.equals(Tags.app_user))
                {
                    AllAppAdsFragment allAppAdsFragment = AllAppAdsFragment.getInstance(Tags.app_user);
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,allAppAdsFragment).commit();

                }else if (user_type.equals(Tags.app_visitor))
                {
                    AllAppAdsFragment allAppAdsFragment = AllAppAdsFragment.getInstance(Tags.app_visitor);
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,allAppAdsFragment).commit();

                }

                break;
            case R.id.search:
                Intent intent = new Intent(HomeActivity.this,GeneralSearchActivity.class);
                intent.putExtra("user_type",user_type);
                startActivity(intent);
                break;
        }
    }
    private void Share()
    {
        try {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,"تطبيق تدللي");
            startActivity(intent);

        }catch (NullPointerException e)
        {
        }catch (Exception e){}
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LocationListener(LocationModel locationModel)
    {


        if (user_type.equals(Tags.app_user))
        {
            Log.e("locationUpdate_Lat",locationModel.getLat()+"");
            Log.e("locationUpdate_Lng",locationModel.getLng()+"");
            UpdateLocation(locationModel.getLat(),locationModel.getLng());

        }else
            {
                Log.e("locationUpdate_Lat",locationModel.getLat()+"");
                Log.e("locationUpdate_Lng",locationModel.getLng()+"");
                latLngSingleTone.setLatLng(locationModel.getLat(),locationModel.getLng());
            }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TokenListener(TokenModel tokenModel)
    {
        if (user_type.equals(Tags.app_user))
        {
            Log.e("token",tokenModel.getToken_id());
            UpdateToken(tokenModel.getToken_id());
        }


    }
    private void UpdateToken(String token)
    {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<UserModel> call = retrofit.create(Services.class).UpdateToken(userModel.getUser_id(), token);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful())
                {
                    if (response.body().getSuccess()==1)
                    {
                        Log.e("token","token updated successfully");
                        preferences.UpdatePref(response.body());
                        userSingleTone.setUserModel(response.body());

                    }else if (response.body().getSuccess()==0)
                    {
                        Log.e("token","token Not updated ");

                    }
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log.e("Error",t.getMessage());
            }
        });
    }
    private void UpdateLocation(double lat, double lng)
    {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<UserModel> call = retrofit.create(Services.class).updateLocation(userModel.getUser_id(),String.valueOf(lat), String.valueOf(lng));
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful())
                {

                    if (response.body().getSuccess()==1)
                    {
                         preferences.UpdatePref(response.body());
                        userSingleTone.setUserModel(response.body());
                        Log.e("location","location updated successfully");


                    }else if (response.body().getSuccess()==0)
                    {
                        Log.e("location","location Not updated");

                    }
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log.e("Error",t.getMessage());
            }
        });
    }

    public void SetMyadsData(MyAdsModel myAdsModel)
    {
        try {
            Intent intent = new Intent(this,AdsDetailsActivity.class);
            intent.putExtra("ad_details",myAdsModel);
            intent.putExtra("whoVisit",Tags.visitor);
            startActivity(intent);
        }catch (NullPointerException e)
        {

        }catch (Exception e){}

    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        stopService(serviceIntent);
        EventBus.getDefault().unregister(this);


    }

    @Override
    public void onSuccess(double lat, double lng) {

    }
}
