package com.semicolon.tadlaly.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.semicolon.tadlaly.Fragments.AllAppAdsFragment;
import com.semicolon.tadlaly.Fragments.Home_Fragment;
import com.semicolon.tadlaly.Models.Cong_Model;
import com.semicolon.tadlaly.Models.LocationModel;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.Models.ResponseModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Preferences;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.LatLngSingleTone;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;
import com.semicolon.tadlaly.language.LocalManager;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static org.greenrobot.eventbus.EventBus.TAG;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,UserSingleTone.OnCompleteListener,View.OnClickListener ,LatLngSingleTone.onLatLngSuccess,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private CircleImageView user_image;
    private TextView user_name;
    private LinearLayout share,all_ads,search;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private Preferences preferences;
    private ProgressDialog dialog;
    private LinearLayout profileContainer;
    private String user_type="";
    private AlertDialog.Builder serviceBuilder;
    private Home_Fragment home_fragment;
    private LatLngSingleTone latLngSingleTone;
    private NotificationManager notificationManager;
    private ImageView img_back;
    private TextView tv_title;
    private AllAppAdsFragment allAppAdsFragment;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private final String fineLoc = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int loc_req = 1;
    private TextView tv_follow_counter;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);

        super.attachBaseContext(LocalManager.updateResources(newBase,LocalManager.getLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getDataFromIntent();
        initView();
        CreateProgress_dialog();
        CreateServiceDialog();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        CheckPermission();
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
        View actionView =  navigationView.getMenu().findItem(R.id.follow).getActionView();
        tv_follow_counter = actionView.findViewById(R.id.tv_follow_counter);
        tv_follow_counter.setVisibility(View.GONE);
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
            profileContainer.setEnabled(true);
            userSingleTone = UserSingleTone.getInstance();
            userSingleTone.getUser(this);
        }

        if (user_type.equals(Tags.app_user))
        {
            //home_fragment = Home_Fragment.getInstance(Tags.app_user);

            home_fragment = Home_Fragment.getInstance(userModel.getUser_id(),Tags.app_user);

            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,home_fragment).commit();


        }else if (user_type.equals(Tags.app_visitor))
        {

            //home_fragment = Home_Fragment.getInstance(Tags.app_visitor);
            home_fragment = Home_Fragment.getInstance("0",Tags.app_visitor);

            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,home_fragment).commit();

        }


        share = findViewById(R.id.share);
        all_ads = findViewById(R.id.ads);
        search = findViewById(R.id.search);
        img_back = findViewById(R.id.img_back);
        tv_title = findViewById(R.id.tv_title);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_title.setText(getString(R.string.home));
                img_back.setVisibility(View.GONE);
                if (user_type.equals(Tags.app_user))
                {
                    home_fragment = Home_Fragment.getInstance(userModel.getUser_id(),Tags.app_user);
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,home_fragment).commit();


                }else
                {
                    home_fragment = Home_Fragment.getInstance("0",Tags.app_visitor);
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,home_fragment).commit();

                }
            }
        });
        share.setOnClickListener(this);
        all_ads.setOnClickListener(this);
        search.setOnClickListener(this);

        if (Locale.getDefault().getLanguage().equals("ar"))
        {
            img_back.setImageResource(R.drawable.white_back_arrow);

        }else
        {
            img_back.setImageResource(R.drawable.right_arrow);


        }

        preferences = new Preferences(this);
        String date = preferences.getLastDate();
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);
        String now = dateFormat.format(new Date(time));

        if (!date.equals(now))
        {
            increaseVisit(now);
        }


    }

    private void increaseVisit(String now) {
        Api.getRetrofit(Tags.Base_Url)
                .create(Services.class)
                .increaseVisit(now)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful()&&response.body().getSuccess_visit() ==1)
                        {
                            preferences.saveLastDate(now);

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        try
                        {
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
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
                    if (!EventBus.getDefault().isRegistered(this))
                    {
                        EventBus.getDefault().register(this);
                    }
                }else
                {
                    latLngSingleTone = LatLngSingleTone.getInstance();

                }
            }
        }
    }

    private void CheckPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, fineLoc) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{fineLoc}, loc_req);
        } else {

           BuildGoogleApiClient();
        }
    }

    private boolean isGpsOpen()
    {
        boolean isOpened = false;
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager != null) {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)||manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                isOpened = true;
            }
        }

        return isOpened;
    }




    @Override
    protected void onStart()
    {
        super.onStart();
        if (user_type.equals(Tags.app_user)){
            //userSingleTone.getUser(this);
            Log.e("token",user_type);
            UpdateToken();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (requestCode == loc_req && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            BuildGoogleApiClient();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onActivityResult(requestCode, resultCode, data);
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

    private void UpdateUi(UserModel userModel)
    {
        try {
            Picasso.with(this).load(Uri.parse(Tags.Image_Url+userModel.getUser_photo())).into(user_image);
            user_name.setText(userModel.getUser_full_name());

        }catch (NullPointerException e)
        {

        }catch (Exception e)
        {

        }

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
                    tv_title.setText(getString(R.string.home));
                    img_back.setVisibility(View.GONE);
                    if (user_type.equals(Tags.app_user))
                    {
                        home_fragment = Home_Fragment.getInstance(userModel.getUser_id(),user_type);
                        getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,home_fragment).commit();


                    }else
                    {
                        home_fragment = Home_Fragment.getInstance("0",user_type);
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
            case R.id.change_language:
                createLanguageDialog();
                break;

            case R.id.home:
                tv_title.setText(getString(R.string.home));
                img_back.setVisibility(View.GONE);
                if (user_type.equals(Tags.app_user))
                {
                    home_fragment = Home_Fragment.getInstance(userModel.getUser_id(),Tags.app_user);
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,home_fragment).commit();


                }else
                {
                    home_fragment = Home_Fragment.getInstance("0",Tags.app_visitor);
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
                Log.e("user_type000",user_type);

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

            case R.id.aboutApp:
                Intent aboutApp = new Intent(this,AboutAppActivity.class);
                startActivity(aboutApp);
                break;
            case R.id.rules:
                Intent rules = new Intent(this,RulesActivity.class);
                startActivity(rules);
                break;
            case R.id.bank_account:
                Intent bank = new Intent(this,BanksActivity.class);
                startActivity(bank);
                break;

            case R.id.logout:

                if (user_type.equals(Tags.app_user))
                {
                    LogOut();

                }else if (user_type.equals(Tags.app_visitor))
                {

                    finish();
                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createLanguageDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .create();
        View view = LayoutInflater.from(this).inflate(R.layout.custom_lang_dialog,null);
        Button btn_ar = view.findViewById(R.id.btn_ar);
        Button btn_en = view.findViewById(R.id.btn_en);

        btn_ar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Paper.book().read("language",Locale.getDefault().getLanguage()).equals("ar"))
                {
                    Paper.book().write("language","ar");
                    LocalManager.setNewLocale(HomeActivity.this,"ar");
                    dialog.dismiss();
                    refreshLayout();
                }


            }
        });
        btn_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Paper.book().read("language",Locale.getDefault().getLanguage()).equals("en"))
                {
                    Paper.book().write("language","en");
                    LocalManager.setNewLocale(HomeActivity.this,"en");
                    dialog.dismiss();
                    refreshLayout();
                }


            }
        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.dialog;
        alertDialog.setView(view);
        alertDialog.show();
    }

    private void refreshLayout() {
        Intent intent =getIntent();
        finish();
        startActivity(intent);
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
                tv_title.setText(R.string.all_ads);
                img_back.setVisibility(View.VISIBLE);

                if (user_type.equals(Tags.app_user))
                {

                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.home_fragments_container);
                    if (fragment instanceof AllAppAdsFragment)
                    {
                        tv_title.setText(getString(R.string.home));
                        img_back.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,home_fragment).commit();
                        return;
                    }

                    allAppAdsFragment = AllAppAdsFragment.getInstance(userModel.getUser_id(),Tags.app_user);
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,allAppAdsFragment).commit();

                }else if (user_type.equals(Tags.app_visitor))
                {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.home_fragments_container);

                    if (fragment instanceof AllAppAdsFragment)
                    {
                        tv_title.setText(getString(R.string.home));
                        img_back.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,home_fragment).commit();
                        return;
                    }
                    allAppAdsFragment = AllAppAdsFragment.getInstance("0",Tags.app_visitor);
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,allAppAdsFragment).commit();

                }

                break;
            case R.id.search:

                Intent intent = new Intent(HomeActivity.this,SearchActivity.class);
                intent.putExtra("user_type",user_type);
                startActivity(intent);
                break;
        }
    }
    private void Share()
    {
        try {

            //Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), BitmapFactory.decodeResource(getResources(),R.drawable.share_image),null,null));
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (user_type.equals(Tags.app_user))
            {
                String user_id = userModel.getUser_id();
                intent.putExtra(Intent.EXTRA_TEXT, "\nتطبيق تدللي"+"\n"+"https://goo.gl/TTNBA7"+"\n"+"http://tdlly.com/Api/AppShare/"+user_id+"/android");

            }else if (user_type.equals(Tags.app_visitor))
            {
                intent.putExtra(Intent.EXTRA_TEXT,"تطبيق تدللي\n"+"\n"+"https://goo.gl/TTNBA7"+"\n"+"Android URL : https://play.google.com/store/apps/details?id=com.semicolon.tadlaly"+"IOS URL:   https://itunes.apple.com/us/app/tadlly-%D8%AA%D8%AF%D9%84%D9%84%D9%8A/id1422871307?ls=1&mt=8"+"\n WEB URL : http://tdlly.com/");

            }
            startActivity(intent);


        }catch (NullPointerException e)
        {
        }catch (Exception e){}
    }


    private void UpdateToken()
    {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful())
                        {
                            String token = task.getResult().getToken();
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
            if (user_type.equals(Tags.app_user))
            {
                Intent intent = new Intent(this,AdsDetailsActivity.class);
                intent.putExtra("ad_details",myAdsModel);
                intent.putExtra("whoVisit",Tags.visitor);
                intent.putExtra("user_id",userModel.getUser_id());
                startActivity(intent);
            }else if (user_type.equals(Tags.app_visitor))
            {
                Intent intent = new Intent(this,AdsDetailsActivity.class);
                intent.putExtra("ad_details",myAdsModel);
                intent.putExtra("whoVisit",Tags.visitor);
                intent.putExtra("user_id","0");

                startActivity(intent);
            }

        }catch (NullPointerException e)
        {

        }catch (Exception e){}

    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().unregister(this);
        }


    }

    @Override
    public void onSuccess(double lat, double lng) {

    }

    public void setTitle(String title)
    {
        tv_title.setText(title);
        img_back.setVisibility(View.VISIBLE);
    }


    protected synchronized void BuildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        StartLocationUpdate();
    }

    private void StartLocationUpdate() {

        initLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        builder.setNeedBle(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient,builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        }
                        LocationServices.getFusedLocationProviderClient(HomeActivity.this).requestLocationUpdates(locationRequest,new LocationCallback()
                        {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                onLocationChanged(locationResult.getLastLocation());
                            }
                        }, Looper.myLooper());
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    HomeActivity.this,
                                    8);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        break;

                }
            }
        });

    }

    private void initLocationRequest()
    {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000*60*5);
        locationRequest.setFastestInterval(1000*60*5);
    }
    @Override
    public void onConnectionSuspended(int i)
    {
        if (googleApiClient!=null)
        {
            googleApiClient.connect();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        LocationModel locationModel = new LocationModel(location.getLatitude(),location.getLongitude());
        LocationListener(locationModel);
    }








    private void createCongDialog() {
          AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();
        View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog_cong,null);
        Button donBtn = view.findViewById(R.id.doneBtn);
        donBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setView(view);
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.dialog;
        alertDialog.show();


    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateCongratulation(Cong_Model cong_model)
    {
        createCongDialog();
    }

    public void LocationListener( LocationModel locationModel)
    {
        if (user_type.equals(Tags.app_user))
        {
            Log.e("locationUpdate_Lat",locationModel.getLat()+"");
            Log.e("locationUpdate_Lng",locationModel.getLng()+"");
            UpdateLocation(locationModel.getLat(),locationModel.getLng());

        }else
        {
            Log.e("locationUpdate_Lat2",locationModel.getLat()+"");
            Log.e("locationUpdate_Lng2",locationModel.getLng()+"");
            latLngSingleTone.setLatLng(locationModel.getLat(),locationModel.getLng());
        }

    }
}
