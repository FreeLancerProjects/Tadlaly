package com.semicolon.tadlaly.Activities;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private  List<MyAdsModel> myAdsModelList1,finalmyAdsModelList;
    private int page_index=0;
    private String user_id="";
    private LatLngSingleTone latLngSingleTone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_depart_ads);

        initView();
        getDataFromIntent();


    }
    private void initView()
    {
        myAdsModelList1= new ArrayList<>();
        finalmyAdsModelList= new ArrayList<>();

        map = new HashMap<>();
        distList = new ArrayList<>();
        idsList = new ArrayList<>();
        myAdsModelList = new ArrayList<>();
        back = findViewById(R.id.back);
        subDept_name = findViewById(R.id.subDept_name);
        no_ads = findViewById(R.id.no_ads);
        progressBar = findViewById(R.id.progBar);
        recView = findViewById(R.id.recView);
        manager = new LinearLayoutManager(this);
        recView.setLayoutManager(manager);
        adapter = new SubDeptAdsAdapter(recView,this,myAdsModelList,"1");
        recView.setAdapter(adapter);
        back.setOnClickListener(view -> finish());
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        adapter.setLoadListener(new SubDeptAdsAdapter.OnLoadListener() {
            @Override
            public void onLoadMore() {
                myAdsModelList.add(null);
                adapter.notifyItemInserted(myAdsModelList.size()-1);
                page_index++;
                getData(supDept_id,page_index);
            }
        });

    }
    private void getDataFromIntent()
    {
        Intent intent = getIntent();
        if (intent!=null)
        {
            user_id = intent.getStringExtra("user_id");
            subdepartObject = (DepartmentsModel.SubdepartObject) intent.getSerializableExtra("subDeptData");
            supDept_id = subdepartObject.getSub_department_id();

            if (user_id.equals(Tags.app_visitor))
            {
                latLngSingleTone = LatLngSingleTone.getInstance();
                latLngSingleTone.getLatLng(this);
            }else if (user_id.equals(Tags.app_user))
            {
                userSingleTone =UserSingleTone.getInstance();
                userSingleTone.getUser(this);
            }
            subDept_name.setText(subdepartObject.getSub_department_name());
            page_index=0;
            page_index++;
            Log.e("p1",page_index+"");
            myAdsModelList.clear();
            getData(supDept_id,page_index);
        }
    }

    private void getData(String supDept_id, int page_index)
    {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        if (user_id.equals(Tags.app_user))
        {
            Call<List<MyAdsModel>> call = retrofit.create(Services.class).getSubDept_Ads(Tags.display_nearby, user_id, supDept_id, page_index);
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
                                SubDepartAdsActivity.this.page_index=0;
                                progressBar.setVisibility(View.GONE);
                                no_ads.setVisibility(View.VISIBLE);
                                adapter.setLoaded();

                            }
                        }
                            /*if (page_index==1&&myAdsModelList.size()==0)
                            {
                                progressBar.setVisibility(View.GONE);
                                no_ads.setVisibility(View.VISIBLE);
                            }else
                            {
                                no_ads.setVisibility(View.GONE);

                                if (myAdsModelList.size()>0)
                                {

                                    myAdsModelList.remove(myAdsModelList.size()-1);
                                    adapter.notifyItemRemoved(myAdsModelList.size());
                                    adapter.setLoaded();
                                    adapter.notifyDataSetChanged();
                                    myAdsModelList.addAll(response.body());
                                    adapter.notifyDataSetChanged();
                                }else
                                {
                                    myAdsModelList.remove(myAdsModelList.size()-1);
                                    adapter.notifyItemRemoved(myAdsModelList.size());
                                    adapter.setLoaded();
                                    adapter.notifyDataSetChanged();
                                }

                            }*/
                    }
                }

                @Override
                public void onFailure(Call<List<MyAdsModel>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SubDepartAdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                    Log.e("Error",t.getMessage());
                }
            });
        }else if (user_id.equals(Tags.app_visitor))
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
                            /*if (page_index==1&&myAdsModelList.size()==0)
                            {
                                progressBar.setVisibility(View.GONE);
                                no_ads.setVisibility(View.VISIBLE);
                            }else
                            {
                                no_ads.setVisibility(View.GONE);

                                if (myAdsModelList.size()>0)
                                {

                                    myAdsModelList.remove(myAdsModelList.size()-1);
                                    adapter.notifyItemRemoved(myAdsModelList.size());
                                    adapter.setLoaded();
                                    adapter.notifyDataSetChanged();
                                    myAdsModelList.addAll(response.body());
                                    adapter.notifyDataSetChanged();
                                }else
                                {
                                    myAdsModelList.remove(myAdsModelList.size()-1);
                                    adapter.notifyItemRemoved(myAdsModelList.size());
                                    adapter.setLoaded();
                                    adapter.notifyDataSetChanged();
                                }

                            }*/
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



     /*   myAdsModelList1.clear();
        Retrofit retrofit = Api.getRetrofit2(Tags.Base_Url);
        Observable<List<MyAdsModel>> observable = retrofit.create(Services.class).getSubDept_Ads(Tags.display_nearby,user_id, supDept_id, page_index);
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
                        e.printStackTrace();
                        Log.e("error",e.getLocalizedMessage());

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

        /*Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<List<MyAdsModel>> call = retrofit.create(Services.class).getSubDept_Ads(userModel.getUser_id(),supDept_id,page_index);
        call.enqueue(new Callback<List<MyAdsModel>>() {
            @Override
            public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                if (response.isSuccessful())
                {
                    SortData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<MyAdsModel>> call, Throwable t) {

            }
        });*/
/*
        call.enqueue(new Callback<List<MyAdsModel>>() {
            @Override
            public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                if (response.isSuccessful())
                {
                    //progressBar.setVisibility(View.GONE);

                    if (page_index==1)
                    {
                        Log.e("index1",page_index+"");
                        if (response.body().size()>0)
                        {
                            Log.e("index1>0",page_index+"");

                            no_ads.setVisibility(View.GONE);


                            SortData(response.body());


                        }else
                        {
                            Log.e("index<0",page_index+"");

                            progressBar.setVisibility(View.GONE);
                            no_ads.setVisibility(View.VISIBLE);

                        }
                    }else
                        {
                            Log.e("index2++",page_index+"");

                            if (response.body().size()==0)
                            {
                                Log.e("nodata",page_index+"");

                                if (myAdsModelList.size()>0)
                                {
                                    myAdsModelList.remove(myAdsModelList.size()-1);
                                    adapter.notifyItemRemoved(myAdsModelList.size());
                                    adapter.notifyDataSetChanged();
                                }


                            }else if (response.body().size()>0)
                            {
                                Log.e("data",page_index+"");

                                no_ads.setVisibility(View.GONE);
                                SortData(response.body());

//                                myAdsModelList.addAll(response.body());
//                                adapter.notifyDataSetChanged();
//                                progressBar.setVisibility(View.GONE);

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
*/

    }

    /*private void SortData(List<MyAdsModel> myAdsModelList1) {
        finalmyAdsModelList.clear();
        distList.clear();
        map.clear();
        idsList.clear();
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

    /* private void SortData(List<MyAdsModel> myAdsModelList1) {
        *//*this.myAdsModelList1 = myAdsModelList1;
        size=myAdsModelList1.size();
        if (size>0) {
            getDistance(mylat, myLng, Double.parseDouble(myAdsModelList1.get(cont).getGoogle_lat()), Double.parseDouble(myAdsModelList1.get(cont).getGoogle_long()), myAdsModelList1.get(cont).getId_advertisement());
        }*//*
        for (int i=0;i<myAdsModelList1.size();i++)
        {
            MyAdsModel myAdsModel = myAdsModelList1.get(i);
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+mylat+","+myLng+"&destinations="+myAdsModel.getGoogle_lat()+","+myAdsModel.getGoogle_long()+"&key="+getString(R.string.Api_key);
            Retrofit retrofit = Api.getRetrofit(Tags.GeoPlaceUrl);
            Call<PlacesDistanceModel> call = retrofit.create(Services.class).getDistance(url);
            call.enqueue(new Callback<PlacesDistanceModel>() {
                @Override
                public void onResponse(Call<PlacesDistanceModel> call, Response<PlacesDistanceModel> response) {
                    if (response.isSuccessful())
                    {
                        *//*try {*//*
                        int dist = response.body().getRows().get(0).getElements().get(0).getDistanceObject().getValue();
                        int dis =dist/1000;
                        map.put(myAdsModel.getId_advertisement(),(double)dis);
                        distList.add((double)dis);
                        //mapList.add(map);

                        // cont++;
                       *//* if (cont<size)
                        {
                           // getDistance(mylat,myLng,Double.parseDouble(myAdsModelList1.get(cont).getGoogle_lat()), Double.parseDouble(myAdsModelList1.get(cont).getGoogle_long()), myAdsModelList1.get(cont).getId_advertisement());
                        }*//*
                        cont--;
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
                            if (myAdsModelList.size()>0)
                            {
                                myAdsModelList.remove(myAdsModelList.size()-1);
                                adapter.notifyItemRemoved(myAdsModelList.size());
                            }
                            myAdsModelList.addAll(finalmyAdsModelList);
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);

                            Log.e("ss2233",idsList.size()+"");
                        }



                    }
                    *//*}catch (NullPointerException e)
                    {

                    }catch (NumberFormatException e){}
                    catch (Exception e){}*//*



                    }


                @Override
                public void onFailure(Call<PlacesDistanceModel> call, Throwable t) {
                    Log.e("Error",t.getMessage());
                    Toast.makeText(SubDepartAdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void getDistance(double mylat, double myLng, double adLat, double adLng, String id_advertisement,int index,int Size) {
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+mylat+","+myLng+"&destinations="+adLat+","+adLng+"&key="+getString(R.string.Api_key);
        Retrofit retrofit = Api.getRetrofit(Tags.GeoPlaceUrl);
        Call<PlacesDistanceModel> call = retrofit.create(Services.class).getDistance(url);
        call.enqueue(new Callback<PlacesDistanceModel>() {
            @Override
            public void onResponse(Call<PlacesDistanceModel> call, Response<PlacesDistanceModel> response) {
                if (response.isSuccessful())
                {
                    *//*try {*//*
                        int dist = response.body().getRows().get(0).getElements().get(0).getDistanceObject().getValue();
                        int dis =dist/1000;
                        map.put(id_advertisement,(double)dis);
                        distList.add((double)dis);
                        //mapList.add(map);

                       // cont++;
                       *//* if (cont<size)
                        {
                           // getDistance(mylat,myLng,Double.parseDouble(myAdsModelList1.get(cont).getGoogle_lat()), Double.parseDouble(myAdsModelList1.get(cont).getGoogle_long()), myAdsModelList1.get(cont).getId_advertisement());
                        }*//*if (index<size)
                        {
                            cont--;
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
                                if (myAdsModelList.size()>0)
                                {
                                    myAdsModelList.remove(myAdsModelList.size()-1);
                                    adapter.notifyItemRemoved(myAdsModelList.size());
                                }
                                myAdsModelList.addAll(finalmyAdsModelList);
                                adapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);

                                Log.e("ss2233",idsList.size()+"");
                            }



                        }
                    *//*}catch (NullPointerException e)
                    {

                    }catch (NumberFormatException e){}
                    catch (Exception e){}*//*



                }
            }

            @Override
            public void onFailure(Call<PlacesDistanceModel> call, Throwable t) {
                Log.e("Error",t.getMessage());
                Toast.makeText(SubDepartAdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
            }
        });


    }*/

    public void setPos(int pos)
    {
        Log.e("size",myAdsModelList.size()+"");
        Log.e("pos",pos+"");

        if (pos<0)
        {
            MyAdsModel myAdsModel = myAdsModelList.get(0);
            Intent intent = new Intent(this,AdsDetailsActivity.class);
            intent.putExtra("ad_details",myAdsModel);
            intent.putExtra("whoVisit",Tags.visitor);
            startActivity(intent);
        }else
            {
                MyAdsModel myAdsModel = myAdsModelList.get(pos);
                Intent intent = new Intent(this,AdsDetailsActivity.class);
                intent.putExtra("ad_details",myAdsModel);
                intent.putExtra("whoVisit",Tags.visitor);
                startActivity(intent);
            }

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
