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
    private String user_id="";
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private ProgressBar progressBar;
    private RecyclerView recView;
    private SubDeptAdsAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private TextView no_ads;
    private List<MyAdsModel> myAdsModelList,myAdsModelList1,finalmyAdsModelList;
    private int page_index=0;
    private List<Double> distList;
    private Map<String,Double> map;
    private double mylat=0.0,myLng=0.0;
    private LatLngSingleTone latLngSingleTone;
    private List<String> idsList;
    private  Call<List<MyAdsModel>> call;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_app_ads,container,false);
        initView(view);
        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            user_id = bundle.getString(USER_ID);
            if (!user_id.equals("0"))
            {
                userSingleTone = UserSingleTone.getInstance();
                userSingleTone.getUser(this);
            }else
            {
                latLngSingleTone = LatLngSingleTone.getInstance();
                latLngSingleTone.getLatLng(this);
            }
        }


        InitLoadMore();
        return view;
    }

    private void InitLoadMore() {
        adapter.setLoadListener(new SubDeptAdsAdapter.OnLoadListener() {
            @Override
            public void onLoadMore() {
                myAdsModelList.add(null);
                adapter.notifyItemInserted(myAdsModelList.size()-1);
                page_index++;
                getAds(page_index);
            }
        });

    }

    private void initView(View view) {
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
    }

    public static AllAppAdsFragment getInstance(String user_id)
    {
        AllAppAdsFragment allAppAdsFragment = new AllAppAdsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID,user_id);
        allAppAdsFragment.setArguments(bundle);
        return allAppAdsFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        distList.clear();
        myAdsModelList1.clear();
        finalmyAdsModelList.clear();
        myAdsModelList.clear();
        page_index=0;
        getAds(page_index++);

    }

    private void getAds(int page_index) {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        if (user_id.equals(Tags.app_user))
        {
            call = retrofit.create(Services.class).getAllAppAds(Tags.display_nearby, user_id, page_index);

        }else if (user_id.equals(Tags.app_visitor))
            {
                Log.e("latttt2",mylat+"");
                Log.e("lngggg2",myLng+"");
                call = retrofit.create(Services.class).visitor_getAllAppAds(Tags.display_nearby,page_index,String.valueOf(mylat),String.valueOf(myLng));

            }

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
                                adapter.setLoaded();


                            }else
                            {
                                if (response.body().size()>0)
                                {
                                    progressBar.setVisibility(View.GONE);
                                    no_ads.setVisibility(View.GONE);
                                    myAdsModelList.addAll(response.body());
                                    adapter.notifyDataSetChanged();
                                    adapter.setLoaded();

                                }else
                                {
                                    progressBar.setVisibility(View.GONE);
                                    no_ads.setVisibility(View.VISIBLE);
                                    adapter.setLoaded();

                                }
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


       /* Observable<List<MyAdsModel>> observable = retrofit.create(Services.class).getAllAppAds(Tags.display_nearby,user_id,page_index);
        observable.flatMap(ads->Observable.fromIterable(ads))
                .flatMap(dis -> retrofit.create(Services.class).getDistance("https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + mylat + "," + myLng + "&destinations=" + dis.getGoogle_lat() + "," + dis.getGoogle_long() + "&key=" + getString(R.string.Api_key)), new BiFunction<MyAdsModel, PlacesDistanceModel, MyAdsModel>() {
                    @Override
                    public MyAdsModel apply(MyAdsModel myAdsModel, PlacesDistanceModel placesDistanceModel) throws Exception {
                        myAdsModel.setM_distance(String.valueOf(placesDistanceModel.getRows().get(0).getElements().get(0).getDistanceObject().getValue()/1000));
                        Log.e("distanccccccccc",myAdsModel.getM_distance());
                        return myAdsModel;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<MyAdsModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MyAdsModel myAdsModel) {
                        myAdsModelList1.add(myAdsModel);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("error",e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        if (page_index==1&&myAdsModelList1.size()==0)
                        {
                            progressBar.setVisibility(View.GONE);
                            no_ads.setVisibility(View.VISIBLE);
                        }else
                        {
                            no_ads.setVisibility(View.GONE);

                            if (myAdsModelList1.size()>0)
                            {
                                SortData(myAdsModelList1);

                            }else
                            {
                                myAdsModelList.remove(myAdsModelList.size()-1);
                                adapter.notifyItemRemoved(myAdsModelList.size());
                            }

                        }
                    }
                });*/

    }

    /*private void SortData(List<MyAdsModel> myAdsModelList1) {
        finalmyAdsModelList.clear();
        distList.clear();
        for (MyAdsModel myAdsModel:myAdsModelList1)
        {
            map.put(myAdsModel.getId_advertisement(),Double.parseDouble(myAdsModel.getM_distance()));
            distList.add(Double.parseDouble(myAdsModel.getM_distance()));
        }

        if (map.size()>0)
        {
            Collections.sort(distList);

            for (Double d:distList)
            {
                Log.e("distance",d+"_");
                for (String key:map.keySet())
                {
                    Log.e("key",key);
                    Log.e("dis_key",d+"_"+map.get(key));
                    if (d.equals(map.get(key)))
                    {

                        if (!idsList.contains(key))
                        {
                            idsList.add(key);
                            Log.e("sizzzz",idsList.size()+"");
                        }


                    }
                }
            }



            for (String key:idsList)
            {
                for (MyAdsModel myAdsModel:myAdsModelList1)
                {
                    if (myAdsModel.getId_advertisement().equals(key))
                    {
                        myAdsModel.setM_distance(String.valueOf(map.get(key)));
                        finalmyAdsModelList.add(myAdsModel);
                    }
                }
            }
            if (myAdsModelList.size()>0)
            {
                myAdsModelList.remove(myAdsModelList.size()-1);
                adapter.notifyItemRemoved(myAdsModelList.size());
                adapter.setLoaded();
                adapter.notifyDataSetChanged();
            }

            myAdsModelList.addAll(finalmyAdsModelList);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);

            Log.e("ss2233",idsList.size()+"");
        }


    }*/

    /*private void Distance(List<MyAdsModel> myAdsModelList1)
    {
        this.myAdsModelList1 = myAdsModelList1;
        size=myAdsModelList1.size();
        if (size>0)
        {
            Dist(mylat,myLng,Double.parseDouble(myAdsModelList1.get(count).getGoogle_lat()), Double.parseDouble(myAdsModelList1.get(count).getGoogle_long()));
        }
    }*/
   /* private void Dist(double mylat, double myLng, double adLat, double adLng)
    {
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+mylat+","+myLng+"&destinations="+adLat+","+adLng+"&key="+getString(R.string.Api_key);
        Retrofit retrofit = Api.getRetrofit(Tags.GeoPlaceUrl);
        Call<PlacesDistanceModel> call = retrofit.create(Services.class).getDistance(url);
        call.enqueue(new Callback<PlacesDistanceModel>() {
            @Override
            public void onResponse(Call<PlacesDistanceModel> call, Response<PlacesDistanceModel> response) {
                if (response.isSuccessful())
                {
                    try {
                        int dist = response.body().getRows().get(0).getElements().get(0).getDistanceObject().getValue();
                        int dis =dist/1000;

                        distList.add((double)dis);

                        count++;
                        if (count<size)
                        {
                            Dist(mylat,myLng,Double.parseDouble(myAdsModelList1.get(count).getGoogle_lat()), Double.parseDouble(myAdsModelList1.get(count).getGoogle_long()));
                        }else
                        {

                            for (int i=0;i<myAdsModelList1.size();i++)
                            {
                                MyAdsModel myAdsModel = myAdsModelList1.get(i);
                                myAdsModel.setM_distance(String.valueOf(distList.get(i)));
                                finalmyAdsModelList.add(myAdsModel);
                                Log.e("id",myAdsModelList1.get(i).getId_advertisement());
                            }

                           *//* myAdsModelList.remove(myAdsModelList.size());
                            adapter.notifyItemRemoved(myAdsModelList.size());
                            adapter.setLoaded();
                            adapter.notifyDataSetChanged();*//*
                            myAdsModelList.addAll(finalmyAdsModelList);
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);

                            Log.e("ss22",myAdsModelList.size()+"");

                        }
                    }catch (NullPointerException e)
                    {
                        progressBar.setVisibility(View.GONE);

                    }catch (NumberFormatException e){
                        progressBar.setVisibility(View.GONE);
                    }
                    catch (Exception e){
                        progressBar.setVisibility(View.GONE);


                    }



                }
            }

            @Override
            public void onFailure(Call<PlacesDistanceModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);

                Log.e("Error",t.getMessage());
                Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
            }
        });
    }*/
    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;
        mylat = Double.parseDouble(userModel.getUser_google_lat());
        myLng = Double.parseDouble(userModel.getUser_google_long());
    }

    @Override
    public void onSuccess(double lat, double lng) {
        mylat = lat;
        mylat = lng;
        Log.e("latttt",lat+"");
        Log.e("lngggg",lng+"");

    }
}
