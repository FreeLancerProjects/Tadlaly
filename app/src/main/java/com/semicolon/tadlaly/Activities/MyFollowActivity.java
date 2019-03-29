package com.semicolon.tadlaly.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.tadlaly.Adapters.FollowAdapter;
import com.semicolon.tadlaly.Models.FollowDataModel;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;
import com.semicolon.tadlaly.language.LocalManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFollowActivity extends AppCompatActivity implements UserSingleTone.OnCompleteListener{
    private ImageView back;
    private RecyclerView recView;
    private RecyclerView.LayoutManager manager;
    private ProgressBar progBar;
    private TextView tv_no_ads;
    private FollowAdapter adapter;
    private List<MyAdsModel> myAdsModelList;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private boolean doneRead = false;
    private int current_page=1;
    private String current_language;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);

        super.attachBaseContext(LocalManager.updateResources(newBase,LocalManager.getLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_follow);
        initView();
    }

    private void initView() {
        current_language = Paper.book().read("language", Locale.getDefault().getLanguage());
        back = findViewById(R.id.back);
        if (current_language.equals("ar"))
        {
            back.setRotation(180f);
        }


        userSingleTone = UserSingleTone.getInstance();
        userSingleTone.getUser(this);
        myAdsModelList = new ArrayList<>();
        progBar = findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        tv_no_ads = findViewById(R.id.tv_no_ads);
        recView = findViewById(R.id.recView);
        manager = new LinearLayoutManager(this);
        recView.setLayoutManager(manager);
        adapter = new FollowAdapter(recView,this,myAdsModelList);
        recView.setAdapter(adapter);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Back();
            }
        });
        adapter.setLoadListener(new FollowAdapter.OnLoadListener() {
            @Override
            public void onLoadMore() {
                myAdsModelList.add(null);
                adapter.notifyItemInserted(myAdsModelList.size()-1);
                int next_page = current_page+1;
                LoadMore(next_page);
            }
        });


        getFollowAds();
    }

    private void LoadMore(int next_page) {

        Api.getRetrofit(Tags.Base_Url)
                .create(Services.class)
                .getMyFollowAds(userModel.getUser_id(),next_page,userModel.getUser_google_lat(),userModel.getUser_google_long())
                .enqueue(new Callback<FollowDataModel>() {
                    @Override
                    public void onResponse(Call<FollowDataModel> call, Response<FollowDataModel> response) {

                        if (response.isSuccessful()&&response.body()!=null&&response.body().getData()!=null)
                        {
                            myAdsModelList.remove(myAdsModelList.size()-1);

                            if (response.body().getData().size()>0)
                            {

                                current_page = response.body().getMeta().getCurrent_page();
                                myAdsModelList.addAll(response.body().getData());
                                adapter.setLoaded();
                            }

                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<FollowDataModel> call, Throwable t) {
                        progBar.setVisibility(View.GONE);
                        try {
                            Toast.makeText(MyFollowActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });

    }

    private void getFollowAds()
    {
        Api.getRetrofit(Tags.Base_Url)
                .create(Services.class)
                .getMyFollowAds(userModel.getUser_id(),1,userModel.getUser_google_lat(),userModel.getUser_google_long())
                .enqueue(new Callback<FollowDataModel>() {
                    @Override
                    public void onResponse(Call<FollowDataModel> call, Response<FollowDataModel> response) {
                        progBar.setVisibility(View.GONE);

                        if (response.isSuccessful()&&response.body()!=null&&response.body().getData()!=null)
                        {
                            if (response.body().getData().size()>0)
                            {
                                myAdsModelList.clear();
                                myAdsModelList.addAll(response.body().getData());
                                adapter.notifyDataSetChanged();
                                tv_no_ads.setVisibility(View.GONE);
                            }else
                                {
                                    tv_no_ads.setVisibility(View.VISIBLE);

                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<FollowDataModel> call, Throwable t) {
                        progBar.setVisibility(View.GONE);
                        try {
                            Toast.makeText(MyFollowActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }

    private void Back() {

        Intent intent = getIntent();
        if (doneRead)
        {
            setResult(RESULT_OK,intent);
        }else
            {
                setResult(RESULT_CANCELED,intent);

            }

            finish();
    }


    @Override
    public void onBackPressed() {
        Back();
    }

    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;
    }

    public void setItemData(MyAdsModel myAdsModel, int pos) {

        if (!myAdsModel.isRead_status()) {
            doneRead = true;
            myAdsModel.setRead_status(true);
            myAdsModel.setReaded(false);
            myAdsModelList.set(pos, myAdsModel);
            adapter.notifyItemChanged(pos);
        }
        Intent intent = new Intent(this,AdsDetailsActivity.class);
        intent.putExtra("ad_details",myAdsModel);
        intent.putExtra("whoVisit", Tags.visitor);
        intent.putExtra("user_id",userModel.getUser_id());
        startActivity(intent);
    }
}
