package com.semicolon.tadlaly.Fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.tadlaly.Adapters.FragmentAllAds_NearbyAdapter;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.LatLngSingleTone;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_AllAds_Nearby extends Fragment implements UserSingleTone.OnCompleteListener,LatLngSingleTone.onLatLngSuccess{
    private static final String TAG="user_type";
    private RecyclerView recView;
    private FragmentAllAds_NearbyAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private ProgressBar progBar;
    private TextView tv_no_ad;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private List<MyAdsModel> myAdsModelList;
    public ImageView image_top;
    private String user_type;
    private int page_index=1;
    private LatLngSingleTone latLngSingleTone;
    private double myLat=0.0,myLng=0.0;
    private String user_id;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allads_nearby,container,false);
        initView(view);
        return view;
    }

    public static Fragment_AllAds_Nearby getInstance(String user_type)
    {
        Bundle bundle = new Bundle();
        bundle.putString(TAG,user_type);
        Fragment_AllAds_Nearby fragment_allAds_nearby = new Fragment_AllAds_Nearby();
        fragment_allAds_nearby.setArguments(bundle);
        return fragment_allAds_nearby;
    }
    private void initView(View view) {
        myAdsModelList = new ArrayList<>();
        image_top = view.findViewById(R.id.image_top);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);
        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(),R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        tv_no_ad = view.findViewById(R.id.tv_no_ads);
        adapter = new FragmentAllAds_NearbyAdapter(recView,getActivity(),myAdsModelList,this);
        recView.setAdapter(adapter);

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            user_type = bundle.getString(TAG);
            if (user_type.equals(Tags.app_user))
            {
                userSingleTone = UserSingleTone.getInstance();
                userSingleTone.getUser(this);
                this.myLat = Double.parseDouble(userModel.getUser_google_lat());
                this.myLng = Double.parseDouble(userModel.getUser_google_long());
                user_id = userModel.getUser_id();
                UpdateUI(user_id);

            }else if (user_type.equals(Tags.app_visitor))
            {
                latLngSingleTone = LatLngSingleTone.getInstance();
                latLngSingleTone.getLatLng(this);
                user_id = "all";
                UpdateUI(user_id);

            }


        }

        image_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_top.setVisibility(View.GONE);
                recView.smoothScrollToPosition(0);
            }
        });


    }

    private void UpdateUI(String user_id)
    {

        Api.getRetrofit(Tags.Base_Url)
                .create(Services.class)
                .getAllAdsBasedOn_New_Nearby(user_id,Tags.display_nearby,page_index,String.valueOf(myLat),String.valueOf(myLng))
                .enqueue(new Callback<List<MyAdsModel>>() {
                    @Override
                    public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                        if (response.isSuccessful())
                        {
                            progBar.setVisibility(View.GONE);

                            myAdsModelList.clear();

                            if (response.body().size()>0)
                            {
                                tv_no_ad.setVisibility(View.GONE);
                                myAdsModelList.addAll(response.body());
                                adapter.notifyDataSetChanged();
                            }else
                            {
                                tv_no_ad.setVisibility(View.VISIBLE);

                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<List<MyAdsModel>> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                        progBar.setVisibility(View.GONE);

                    }
                });

        initLoadMore();

    }

    private void initLoadMore() {

        adapter.setLoadListener(new FragmentAllAds_NearbyAdapter.OnLoadListener() {
            @Override
            public void onLoadMore() {
                myAdsModelList.add(null);
                adapter.notifyItemInserted(myAdsModelList.size()-1);
                int index = page_index;
                onLoadGetData(user_id,index);
            }
        });

    }

    private void onLoadGetData(String user_id,int page_index)
    {
        Api.getRetrofit(Tags.Base_Url)
                .create(Services.class)
                .getAllAdsBasedOn_New_Nearby(user_id,Tags.display_nearby,page_index,String.valueOf(myLat),String.valueOf(myLng))
                .enqueue(new Callback<List<MyAdsModel>>() {
                    @Override
                    public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                        if (response.isSuccessful())
                        {
                            if (response.body().size()>0)
                            {
                                Fragment_AllAds_Nearby.this.page_index = Fragment_AllAds_Nearby.this.page_index+1;
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
                        Log.e("Error",t.getMessage());
                        progBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;
    }

    @Override
    public void onSuccess(double lat, double lng) {
        this.myLat = lat;
        this.myLng = lng;
    }
}
