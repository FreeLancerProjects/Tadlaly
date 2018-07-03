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
import android.widget.Button;
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

public class SubDataFragment extends Fragment implements UserSingleTone.OnCompleteListener,LatLngSingleTone.onLatLngSuccess{
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private SubDeptAdsAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private TextView no_ads;
    private Button order_onNewBtn,order_onNearbyBtn;
    private List<MyAdsModel> myAdsModelList,myAdsModelList1,finalmyAdsModelList;
    private String depId;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private static final String dept_id_="dept_id";
    private static final String USER_ID="user_id";
    private double mylat=0.0,myLng=0.0;
    private List<Double> distList;
    private Map<String,Double> map;
    private List<String> idsList;
    private int page_index=0;
    private String button_type="nearby";
    private String user_id="";
    private Call<List<MyAdsModel>> call;
    private LatLngSingleTone latLngSingleTone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sub_dept_row,container,false);
        initView(view);
        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            depId = bundle.getString(dept_id_);
            user_id = bundle.getString("user_id");
            if (!user_id.equals("0"))
            {
                userSingleTone = UserSingleTone.getInstance();
                userSingleTone.getUser(this);
            }else if (user_id.equals(Tags.app_visitor))
            {
                latLngSingleTone = LatLngSingleTone.getInstance();
                latLngSingleTone.getLatLng(this);
            }
        }

        initLoadMore();
        return view;
    }

    private void initLoadMore() {
        adapter.setLoadListener(new SubDeptAdsAdapter.OnLoadListener() {
            @Override
            public void onLoadMore() {
                if (button_type.equals("nearby"))
                {
                    myAdsModelList.add(null);
                    adapter.notifyItemInserted(myAdsModelList.size()-1);
                    page_index++;
                    getData(depId,page_index);
                }else
                {
                    myAdsModelList.add(null);
                    adapter.notifyItemInserted(myAdsModelList.size()-1);
                    page_index++;
                    getOrderedData(depId,page_index);
                }
            }
        });



    }



    @Override
    public void onStart() {
        super.onStart();
        myAdsModelList1.clear();
        myAdsModelList.clear();
        finalmyAdsModelList.clear();
        idsList.clear();
        distList.clear();
        map.clear();
        page_index=0;
        getData(depId, page_index++);

    }

    private void initView(View view) {
        myAdsModelList = new ArrayList<>();
        finalmyAdsModelList = new ArrayList<>();
        myAdsModelList1 = new ArrayList<>();
        distList = new ArrayList<>();
        order_onNewBtn = view.findViewById(R.id.order_onNewBtn);
        order_onNearbyBtn = view.findViewById(R.id.order_onNearbyBtn);
        map = new HashMap<>();
        idsList = new ArrayList<>();
        no_ads = view.findViewById(R.id.no_ads);
        progressBar =view.findViewById(R.id.progBar);
        recyclerView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        adapter = new SubDeptAdsAdapter(recyclerView,getActivity(),myAdsModelList,"3");
        recyclerView.setAdapter(adapter);

        order_onNewBtn.setOnClickListener(view1 -> {
            button_type="ordered";
            page_index=0;
            myAdsModelList1.clear();
            myAdsModelList.clear();
            finalmyAdsModelList.clear();
            idsList.clear();
            distList.clear();
            map.clear();
            order_onNewBtn.setBackgroundResource(R.drawable.btn_selected);
            order_onNearbyBtn.setBackgroundResource(R.drawable.login_btn);
            page_index++;
            getOrderedData(depId,page_index);
        });

        order_onNearbyBtn.setOnClickListener(view1 -> {
            button_type="nearby";
            page_index=0;
            myAdsModelList1.clear();
            myAdsModelList.clear();
            finalmyAdsModelList.clear();
            idsList.clear();
            distList.clear();
            map.clear();
            order_onNewBtn.setBackgroundResource(R.drawable.login_btn);
            order_onNearbyBtn.setBackgroundResource(R.drawable.btn_selected);
            page_index++;

            getData(depId,page_index);
            Log.e("id",depId);
        });

    }

    public static SubDataFragment newInstance(String dept_id,String user_id)
    {
        SubDataFragment dataFragment = new SubDataFragment();
        Bundle bundle = new Bundle();
        bundle.putString(dept_id_,dept_id);
        bundle.putString(USER_ID,user_id);
        dataFragment.setArguments(bundle);
        return dataFragment;
    }

    private void getData(String supDept_id, int page_index)
    {

        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        if (user_id.equals(Tags.app_visitor))
        {
            call = retrofit.create(Services.class).visitor_getSubDeptAds(Tags.display_nearby, supDept_id,page_index,String.valueOf(mylat),String.valueOf(myLng));

        }else
            {
                call = retrofit.create(Services.class).getSubDept_Ads(Tags.display_nearby, user_id, supDept_id, page_index);

            }
                call.enqueue(new Callback<List<MyAdsModel>>() {
                    @Override
                    public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                        if (response.isSuccessful())
                        {

                            if (myAdsModelList.size()>0)
                            {
                                no_ads.setVisibility(View.GONE);

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
        /*myAdsModelList1.clear();
        Retrofit retrofit = Api.getRetrofit2(Tags.Base_Url);
        Observable<List<MyAdsModel>> observable = retrofit.create(Services.class).getSubDept_Ads(Tags.display_nearby,user_id, supDept_id, page_index);
        observable.flatMap(ads->Observable.fromIterable(ads))
                .flatMap(dis -> retrofit.create(Services.class).getDistance("https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + mylat + "," + myLng + "&destinations=" + dis.getGoogle_lat() + "," + dis.getGoogle_long() + "&key=" + getString(R.string.Api_key)), (myAdsModel, placesDistanceModel) -> {
                    myAdsModel.setM_distance(String.valueOf(placesDistanceModel.getRows().get(0).getElements().get(0).getDistanceObject().getValue()/1000));
                    Log.e("distanccccccccc",myAdsModel.getM_distance());
                    return myAdsModel;
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
                                if (myAdsModelList.size()>0)
                                {
                                    myAdsModelList.remove(myAdsModelList.size()-1);
                                    adapter.notifyItemRemoved(myAdsModelList.size());
                                }

                            }

                        }
                    }
                });*/


    }

    private void getOrderedData(String depId, int page_index)
    {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        if (user_id.equals(Tags.app_visitor))
        {
            call = retrofit.create(Services.class).visitor_getSubDeptAds(Tags.display_new,depId,page_index,String.valueOf(myLng),String.valueOf(myLng));

        }else
            {
                call = retrofit.create(Services.class).getSubDept_Ads(Tags.display_new, user_id, depId, page_index);

            }
                call.enqueue(new Callback<List<MyAdsModel>>() {
                    @Override
                    public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                        if (response.isSuccessful())
                        {
                            if (myAdsModelList.size()>0)
                            {
                                no_ads.setVisibility(View.GONE);

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
        /*Retrofit retrofit = Api.getRetrofit2(Tags.Base_Url);
        Observable<List<MyAdsModel>> observable = retrofit.create(Services.class).getSubDept_Ads(Tags.display_new,user_id, depId, page_index);
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
                                myAdsModelList.remove(myAdsModelList.size()-1);
                                adapter.notifyItemRemoved(myAdsModelList.size());
                                myAdsModelList.addAll(myAdsModelList1);
                                adapter.notifyDataSetChanged();
                            }else
                            {
                                myAdsModelList.remove(myAdsModelList.size()-1);
                                adapter.notifyItemRemoved(myAdsModelList.size());
                            }

                        }
                    }
                });
*/

    }

/*
    private void getDistance(double mylat, double myLng, double adLat, double adLng, String id_advertisement) {
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

                        map.put(myAdsModelList1.get(cont).getId_advertisement(),(double)dis);
                        distList.add((double)dis);

                        cont++;
                        if (cont<size)
                        {
                            getDistance(mylat,myLng,Double.parseDouble(myAdsModelList1.get(cont).getGoogle_lat()), Double.parseDouble(myAdsModelList1.get(cont).getGoogle_long()), myAdsModelList1.get(cont).getId_advertisement());
                        }else
                        {



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
                                            Log.e("ff",key+"_"+myAdsModel.getId_advertisement());
                                            myAdsModel.setM_distance(String.valueOf(map.get(key)));
                                            finalmyAdsModelList.add(myAdsModel);
                                        }
                                    }
                                }
                               */
/* myAdsModelList.remove(myAdsModelList.size());
                                adapter.notifyItemRemoved(myAdsModelList.size());
                                adapter.setLoaded();*//*

                                myAdsModelList.addAll(finalmyAdsModelList);
                                adapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);

                                Log.e("ss22",myAdsModelList.size()+"");
                            }



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


    }
*/

   /* private void getOrderedData(String supDept_id, int page_index)
    {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<List<MyAdsModel>> call = retrofit.create(Services.class).getSubDept_Ads(userModel.getUser_id(),supDept_id,page_index);
        call.enqueue(new Callback<List<MyAdsModel>>() {
            @Override
            public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                if (response.isSuccessful())
                {

                    if (page_index==1)
                    {
                        if (response.body().size()>0)
                        {
                            no_ads.setVisibility(View.GONE);
                            Distance(response.body());


                        }else if (response.body().size()==0)
                        {
                            *//*myAdsModelList.remove(myAdsModelList.size());
                            adapter.notifyItemRemoved(myAdsModelList.size());
                            adapter.setLoaded();
                            myAdsModelList.addAll(finalmyAdsModelList);
                            adapter.notifyDataSetChanged();*//*
                            progressBar.setVisibility(View.GONE);
                            no_ads.setVisibility(View.VISIBLE);

                        }
                    }else
                    {
                        if (response.body().size()==0)
                        {
                           *//* myAdsModelList.remove(myAdsModelList.size());
                            adapter.notifyItemRemoved(myAdsModelList.size());
                            adapter.setLoaded();
                            adapter.notifyDataSetChanged();*//*

                        }else if (response.body().size()>0)
                        {
                            no_ads.setVisibility(View.GONE);
                            Distance(response.body());


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
    }*/
    /*private void Distance(List<MyAdsModel> myAdsModelList1)
    {
        this.myAdsModelList1 = myAdsModelList1;
        size=myAdsModelList1.size();
        if (size>0)
        {
            Dist(mylat,myLng,Double.parseDouble(myAdsModelList1.get(cont).getGoogle_lat()), Double.parseDouble(myAdsModelList1.get(cont).getGoogle_long()));
        }
    }*/
    /*private void Dist(double mylat, double myLng, double adLat, double adLng)
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

                        cont++;
                        if (cont<size)
                        {
                            Dist(mylat,myLng,Double.parseDouble(myAdsModelList1.get(cont).getGoogle_lat()), Double.parseDouble(myAdsModelList1.get(cont).getGoogle_long()));
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
                            adapter.setLoaded();*//*
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
