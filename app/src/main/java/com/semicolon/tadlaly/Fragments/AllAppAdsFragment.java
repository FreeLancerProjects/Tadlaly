package com.semicolon.tadlaly.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.tadlaly.Adapters.SubDeptAdsAdapter;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.LatLngSingleTone;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AllAppAdsFragment extends Fragment implements UserSingleTone.OnCompleteListener,LatLngSingleTone.onLatLngSuccess{
    private static final String USER_ID="user_id";
    private static final String USER_TYPE="user_type";
    private String user_id="";
    private String user_type;
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private ProgressBar progressBar;
    private RecyclerView recView;
    private SubDeptAdsAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private TextView no_ads;
    private List<MyAdsModel> myAdsModelList,myAdsModelList1,finalmyAdsModelList;
    private int page_index=2;
    private List<Double> distList;
    private Map<String,Double> map;
    private double mylat=0.0,myLng=0.0;
    private LatLngSingleTone latLngSingleTone;
    private List<String> idsList;
    //private  Call<List<MyAdsModel>> call1,call2;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_app_ads,container,false);
        initView(view);

        InitLoadMore();
        return view;
    }

    private void initView(View view) {
        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            user_id = bundle.getString(USER_ID);
            user_type = bundle.getString(USER_TYPE);
            if (user_type.equals(Tags.app_user))
            {
                userSingleTone = UserSingleTone.getInstance();
                userSingleTone.getUser(this);
            }else if (user_type.equals(Tags.app_visitor))
            {
                latLngSingleTone = LatLngSingleTone.getInstance();
                latLngSingleTone.getLatLng(this);
            }
        }
        idsList = new ArrayList<>();
        map = new HashMap<>();
        distList = new ArrayList<>();
        myAdsModelList1 = new ArrayList<>();
        finalmyAdsModelList = new ArrayList<>();
        myAdsModelList = new ArrayList<>();
        no_ads = view.findViewById(R.id.no_ads);
        progressBar = view.findViewById(R.id.progBar);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);
        adapter = new SubDeptAdsAdapter(recView,getActivity(),myAdsModelList,"3");
        recView.setAdapter(adapter);
        getAds(1,user_id);

    }

    public static AllAppAdsFragment getInstance(String user_id,String user_type)
    {
        AllAppAdsFragment allAppAdsFragment = new AllAppAdsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID,user_id);
        bundle.putString(USER_TYPE,user_type);
        allAppAdsFragment.setArguments(bundle);
        return allAppAdsFragment;
    }

    private void InitLoadMore() {
        adapter.setLoadListener(new SubDeptAdsAdapter.OnLoadListener() {
            @Override
            public void onLoadMore() {
                myAdsModelList.add(null);
                adapter.notifyItemInserted(myAdsModelList.size()-1);
                int index=page_index;
                Log.e("loadMore","loadmore");
                onLoadGetAds(index,user_id);
            }
        });

    }




    private void getAds(int page_index,String user_id) {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        if (user_type.equals(Tags.app_user))
        {
            Call<List<MyAdsModel>> call = retrofit.create(Services.class).getAllAppAds(Tags.display_nearby, user_id, page_index);

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
                            no_ads.setVisibility(View.GONE);

                        }
                    }

                }

                @Override
                public void onFailure(Call<List<MyAdsModel>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    Log.e("Error",t.getMessage());
                }
            });
        }else if (user_type.equals(Tags.app_visitor))
            {
                Log.e("latttt2",mylat+"");
                Log.e("lngggg2",myLng+"");
                Call<List<MyAdsModel>> call = retrofit.create(Services.class).visitor_getAllAppAds(Tags.display_nearby,page_index,String.valueOf(mylat),String.valueOf(myLng));

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
                                no_ads.setVisibility(View.GONE);

                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<List<MyAdsModel>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                        Log.e("Error",t.getMessage());
                    }
                });
            }






    }
    private void onLoadGetAds(int page_index,String user_id)
    {
        Log.e("innndex",page_index+"");

        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        if (user_type.equals(Tags.app_user))
        {
            Call<List<MyAdsModel>> call = retrofit.create(Services.class).getAllAppAds(Tags.display_nearby, user_id, page_index);
            call.enqueue(new Callback<List<MyAdsModel>>() {
                @Override
                public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                    if (response.isSuccessful())
                    {

                        Log.e("size4444444",response.body().size()+""+"\n"+page_index);
                        if (response.body().size()>0)
                        {
                            AllAppAdsFragment.this.page_index = AllAppAdsFragment.this.page_index+1;
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
                    Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    Log.e("Error",t.getMessage());
                }
            });
        }else if (user_type.equals(Tags.app_visitor))
        {

            Call<List<MyAdsModel>> call = retrofit.create(Services.class).visitor_getAllAppAds(Tags.display_nearby,page_index,String.valueOf(mylat),String.valueOf(myLng));
            call.enqueue(new Callback<List<MyAdsModel>>() {
                @Override
                public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                    if (response.isSuccessful())
                    {

                        Log.e("size4444444",response.body().size()+""+"\n"+page_index);
                        if (response.body().size()>0)
                        {
                            AllAppAdsFragment.this.page_index = AllAppAdsFragment.this.page_index+1;
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
                    Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    Log.e("Error",t.getMessage());
                }
            });
        }


    }


    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;
        mylat = Double.parseDouble(userModel.getUser_google_lat());
        myLng = Double.parseDouble(userModel.getUser_google_long());
    }

    @Override
    public void onSuccess(double lat, double lng) {
        mylat = lat;
        myLng = lng;
        Log.e("latttt",lat+"");
        Log.e("lngggg",lng+"");

    }
}
