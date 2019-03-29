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

import com.semicolon.tadlaly.Adapters.SubDeptAdsAdapter;
import com.semicolon.tadlaly.Models.DepartmentsModel;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.LatLngSingleTone;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;
import com.semicolon.tadlaly.language.LocalManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SubDepartAdsActivity extends AppCompatActivity implements UserSingleTone.OnCompleteListener,LatLngSingleTone.onLatLngSuccess{
    private ImageView back;
    private TextView subDept_name,no_ads;
    private ProgressBar progressBar;
    private RecyclerView recView;
    private RecyclerView.LayoutManager manager;
    private SubDeptAdsAdapter adapter;
    private List<MyAdsModel> myAdsModelList;
    private String supDept_id;
    private DepartmentsModel.SubdepartObject subdepartObject;
    private List<Double> distList;
    private Map<String,Double> map;
    private List<String> idsList;
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private double mylat=0.0,myLng=0.0;
    private int cont=0;
    private int size=0;
    private int page_index=2;
    private String user_id="";
    private String user_type="";
    private LatLngSingleTone latLngSingleTone;
    public ImageView image_top;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);

        super.attachBaseContext(LocalManager.updateResources(newBase,LocalManager.getLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_depart_ads);

        initView();
        getDataFromIntent();


    }
    private void initView()
    {


        map = new HashMap<>();
        distList = new ArrayList<>();
        idsList = new ArrayList<>();
        myAdsModelList = new ArrayList<>();
        back = findViewById(R.id.back);
        image_top = findViewById(R.id.image_top);
        subDept_name = findViewById(R.id.subDept_name);
        no_ads = findViewById(R.id.no_ads);
        progressBar = findViewById(R.id.progBar);
        recView = findViewById(R.id.recView);
        manager = new LinearLayoutManager(this);
        recView.setLayoutManager(manager);
        back.setOnClickListener(view -> finish());
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        image_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recView.smoothScrollToPosition(0);
                image_top.setVisibility(View.GONE);
            }
        });

    }
    private void getDataFromIntent()
    {
        Intent intent = getIntent();
        if (intent!=null)
        {
            user_id = intent.getStringExtra("user_id");
            user_type =intent.getStringExtra("user_type");
            subdepartObject = (DepartmentsModel.SubdepartObject) intent.getSerializableExtra("subDeptData");
            supDept_id = subdepartObject.getSub_department_id();

            if (user_id.equals("0"))
            {
                latLngSingleTone = LatLngSingleTone.getInstance();
                latLngSingleTone.getLatLng(this);
                adapter = new SubDeptAdsAdapter(recView,this,myAdsModelList,"1",false);
                recView.setAdapter(adapter);

            }else if (!user_id.equals("0"))
            {
                userSingleTone =UserSingleTone.getInstance();
                userSingleTone.getUser(this);
                adapter = new SubDeptAdsAdapter(recView,this,myAdsModelList,"1",true);
                recView.setAdapter(adapter);
            }
            String sd=subdepartObject.getSub_department_name().replaceAll("\n","").replaceAll("\t","");
            subDept_name.setText(sd);
            myAdsModelList.clear();
            getData(supDept_id,user_id,1);
        }

        adapter.setLoadListener(new SubDeptAdsAdapter.OnLoadListener() {
            @Override
            public void onLoadMore() {
                myAdsModelList.add(null);
                adapter.notifyItemInserted(myAdsModelList.size()-1);
                int index = page_index;
                onLoadgetData(supDept_id,user_id,index);
            }
        });

    }

    private void getData(String supDept_id,String user_id, int page_index)
    {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        if (user_type.equals(Tags.app_user))
        {
            Call<List<MyAdsModel>> call = retrofit.create(Services.class).getSubDept_Ads(Tags.display_nearby, user_id, supDept_id, page_index);
            call.enqueue(new Callback<List<MyAdsModel>>() {
                @Override
                public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                    if (response.isSuccessful())
                    {
                        progressBar.setVisibility(View.GONE);
                        myAdsModelList.clear();
                        if (response.body().size()>0)
                        {
                            no_ads.setVisibility(View.GONE);
                            myAdsModelList.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        }else
                        {
                            no_ads.setVisibility(View.VISIBLE);

                        }
                    }
                }

                @Override
                public void onFailure(Call<List<MyAdsModel>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SubDepartAdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                    Log.e("Error",t.getMessage());
                }
            });
        }else if (user_type.equals(Tags.app_visitor))
        {
            Log.e("lat",mylat+"");
            Log.e("lng",myLng+"");

            Call<List<MyAdsModel>> call = retrofit.create(Services.class).visitor_getSubDeptAds(Tags.display_nearby,supDept_id,page_index,String.valueOf(mylat),String.valueOf(myLng));
            call.enqueue(new Callback<List<MyAdsModel>>() {
                @Override
                public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                    if (response.isSuccessful())
                    {
                        if (myAdsModelList.size()>0)
                        {
                            myAdsModelList.remove(myAdsModelList.size()-1);
                            adapter.notifyItemRemoved(myAdsModelList.size());
                            adapter.setLoaded();
                            myAdsModelList.addAll(response.body());
                            adapter.notifyDataSetChanged();

                        }else
                        {
                            if (response.body().size()>0)
                            {
                                progressBar.setVisibility(View.GONE);
                                no_ads.setVisibility(View.GONE);
                                myAdsModelList.addAll(response.body());
                                adapter.notifyDataSetChanged();
                            }else
                            {
                                SubDepartAdsActivity.this.page_index=0;
                                progressBar.setVisibility(View.GONE);
                                no_ads.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                }

                @Override
                public void onFailure(Call<List<MyAdsModel>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SubDepartAdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                    Log.e("Error",t.getMessage());
                }
            });
        }


    }
    private void onLoadgetData(String supDept_id,String user_id, int page_index)
    {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        if (user_type.equals(Tags.app_user))
        {
            Call<List<MyAdsModel>> call = retrofit.create(Services.class).getSubDept_Ads(Tags.display_nearby, user_id, supDept_id, page_index);
            call.enqueue(new Callback<List<MyAdsModel>>() {
                @Override
                public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                    if (response.isSuccessful())
                    {

                        Log.e("size4444444",response.body().size()+""+"\n"+page_index);
                        if (response.body().size()>0)
                        {
                            SubDepartAdsActivity.this.page_index = SubDepartAdsActivity.this.page_index+1;
                            int lastpos = myAdsModelList.size()-1;
                            myAdsModelList.remove(myAdsModelList.size()-1);
                            adapter.notifyItemRemoved(myAdsModelList.size());
                            adapter.setLoaded();
                            myAdsModelList.addAll(response.body());
                            adapter.notifyItemRangeChanged(lastpos,myAdsModelList.size()-1);
                        }else
                        {
                            myAdsModelList.remove(myAdsModelList.size()-1);
                            adapter.notifyItemRemoved(myAdsModelList.size());
                            adapter.setLoaded();
                        }

                    }
                }

                @Override
                public void onFailure(Call<List<MyAdsModel>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SubDepartAdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                    Log.e("Error",t.getMessage());
                }
            });
        }else if (user_type.equals(Tags.app_visitor))
        {
            Log.e("lat",mylat+"");
            Log.e("lng",myLng+"");

            Call<List<MyAdsModel>> call = retrofit.create(Services.class).visitor_getSubDeptAds(Tags.display_nearby,supDept_id,page_index,String.valueOf(mylat),String.valueOf(myLng));
            call.enqueue(new Callback<List<MyAdsModel>>() {
                @Override
                public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                    if (response.isSuccessful())
                    {
                        if (myAdsModelList.size()>0)
                        {
                            myAdsModelList.remove(myAdsModelList.size()-1);
                            adapter.notifyItemRemoved(myAdsModelList.size());
                            adapter.setLoaded();
                            myAdsModelList.addAll(response.body());
                            adapter.notifyDataSetChanged();

                        }else
                        {
                            if (response.body().size()>0)
                            {
                                progressBar.setVisibility(View.GONE);
                                no_ads.setVisibility(View.GONE);
                                myAdsModelList.addAll(response.body());
                                adapter.notifyDataSetChanged();
                            }else
                            {
                                SubDepartAdsActivity.this.page_index=0;
                                progressBar.setVisibility(View.GONE);
                                no_ads.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                }

                @Override
                public void onFailure(Call<List<MyAdsModel>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SubDepartAdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                    Log.e("Error",t.getMessage());
                }
            });
        }

    }



    public void setItemData(MyAdsModel myAdsModel, int pos)
    {
        if (!myAdsModel.isRead_status()) {

            myAdsModel.setRead_status(true);
            myAdsModel.setReaded(false);
            myAdsModelList.set(pos, myAdsModel);
            adapter.notifyItemChanged(pos);
        }
        Intent intent = new Intent(this,AdsDetailsActivity.class);
        intent.putExtra("ad_details",myAdsModel);
        intent.putExtra("whoVisit",Tags.visitor);
        intent.putExtra("user_id",user_id);
        startActivity(intent);

    }


    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel =userModel;
        mylat = Double.parseDouble(userModel.getUser_google_lat());
        myLng = Double.parseDouble(userModel.getUser_google_long());
    }

    @Override
    public void onSuccess(double lat, double lng) {
        mylat = lat;
        myLng = lng;
    }
}
