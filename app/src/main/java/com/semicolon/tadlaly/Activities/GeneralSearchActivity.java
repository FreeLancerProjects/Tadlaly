package com.semicolon.tadlaly.Activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.semicolon.tadlaly.Adapters.SpinnerBranchAdapter;
import com.semicolon.tadlaly.Adapters.SpinnerDeptAdapter;
import com.semicolon.tadlaly.Adapters.SubDeptAdsAdapter;
import com.semicolon.tadlaly.Models.DepartmentsModel;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.Models.Spinner_DeptModel;
import com.semicolon.tadlaly.Models.Spinner_branchModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.DepartmentSingletone;
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

public class GeneralSearchActivity extends AppCompatActivity implements DepartmentSingletone.onCompleteListener,UserSingleTone.OnCompleteListener,LatLngSingleTone.onLatLngSuccess{
    private ImageView back;
    private ProgressBar progressBar;
    private Spinner spinner_dept,spinner_branch;
    private Button search_btn;
    private RecyclerView recView;
    private SubDeptAdsAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private SpinnerDeptAdapter spinnerDeptAdapter;
    private SpinnerBranchAdapter spinnerBranchAdapter;
    private List<Spinner_DeptModel> spinner_deptModelList;
    private List<Spinner_branchModel> spinner_branchModelList;
    private List<DepartmentsModel> departmentsModelList;
    private String m_dept_id="",m_branch_id="";
    private List<MyAdsModel> myAdsModelList,finalmyAdsModelList,myAdsModelList1;
    private LinearLayout no_result_container;
    private DepartmentSingletone departmentSingletone;
    private List<Double> distList;
    private Map<String,Double> map;
    private List<String> idsList;
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private double mylat=0.0,myLng=0.0;
    private static int cont1=0;
    private int size=0;
    private int page_index=0;
    private String user_type="";
    private Call<List<MyAdsModel>> call;
    private LatLngSingleTone latLngSingleTone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_search);
        getDataFromIntent();
        initView();
        departmentSingletone = DepartmentSingletone.getInstansce();
        departmentSingletone.getDepartmentData(this);
        if (user_type.equals(Tags.app_user))
        {
            userSingleTone = UserSingleTone.getInstance();
            userSingleTone.getUser(this);
        }else if (user_type.equals(Tags.app_visitor))
            {
                latLngSingleTone = LatLngSingleTone.getInstance();
                latLngSingleTone.getLatLng(this);
            }

        initLoadMore();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null)
        {
            if (intent.hasExtra("user_type"))
            {
                user_type = intent.getStringExtra("user_type");
            }
        }
    }

    private void initLoadMore() {
       adapter.setLoadListener(new SubDeptAdsAdapter.OnLoadListener() {
           @Override
           public void onLoadMore() {
               myAdsModelList.add(null);
               adapter.notifyItemInserted(myAdsModelList.size()-1);
               page_index++;
               Log.e("page_index",page_index+"");
               Search(page_index);

           }
       });
    }

    private void initView() {
        distList = new ArrayList<>();
        map = new HashMap<>();
        idsList = new ArrayList<>();
        myAdsModelList = new ArrayList<>();
        spinner_deptModelList = new ArrayList<>();
        spinner_branchModelList = new ArrayList<>();
        finalmyAdsModelList = new ArrayList<>();
        myAdsModelList1 = new ArrayList<>();
        back = findViewById(R.id.back);
        spinner_dept = findViewById(R.id.spinner_dept);
        spinner_branch = findViewById(R.id.spinner_branch);
        search_btn = findViewById(R.id.search_btn);
        progressBar = findViewById(R.id.progBar);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        no_result_container = findViewById(R.id.no_result_container);
        recView = findViewById(R.id.recView);
        manager = new LinearLayoutManager(this);
        recView.setLayoutManager(manager);
        adapter = new SubDeptAdsAdapter(recView,this,myAdsModelList,"2");
        recView.setAdapter(adapter);
        back.setOnClickListener(view -> finish());
        spinnerDeptAdapter = new SpinnerDeptAdapter(this,R.layout.spinner_item,spinner_deptModelList);
        spinnerBranchAdapter = new SpinnerBranchAdapter(this,R.layout.spinner_item,spinner_branchModelList);
        spinner_dept.setAdapter(spinnerDeptAdapter);
        spinner_branch.setAdapter(spinnerBranchAdapter);
        spinner_dept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                m_dept_id = spinner_deptModelList.get(i).getId();

                if (i==0||spinner_dept.getSelectedItem().toString().equals(getString(R.string.dept))|| TextUtils.isEmpty(m_dept_id))
                {
                    spinner_branchModelList.clear();
                    m_dept_id="";
                    Spinner_branchModel spinner_branchModel = new Spinner_branchModel("",getString(R.string.branch));
                    spinner_branchModelList.add(spinner_branchModel);
                    spinner_branch.setSelection(0);
                    spinnerBranchAdapter.notifyDataSetChanged();
                }else
                {
                    String id = m_dept_id;
                    UpdateBranchAdapter(id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String id = spinner_branchModelList.get(i).getId();
                Log.e("branchid",id);
                if (i==0&&id.equals(getString(R.string.branch)))
                {
                    m_branch_id = "";
                }
                else if (id.equals(getString(R.string.no_branch)))
                {
                    m_branch_id="0";

                }else
                {
                    m_branch_id=id;
                }
                Log.e("branch id",m_branch_id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        search_btn.setOnClickListener(view -> Search(page_index++));

    }

    private void Search(int page_index)
    {
        if (TextUtils.isEmpty(m_dept_id))
        {
            Toast.makeText(this,R.string.ch_dept, Toast.LENGTH_LONG).show();
        }else if (TextUtils.isEmpty(m_branch_id))
        {
            Toast.makeText(this,R.string.ch_branch, Toast.LENGTH_LONG).show();

        }else if (m_branch_id.equals("0"))
        {
            Toast.makeText(this,R.string.nobranch_to_dept, Toast.LENGTH_LONG).show();

        }
        else
            {
                try {
                    Log.e("dept_id",m_dept_id);
                    Log.e("brnch_id",m_branch_id);
                    Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
                    if (user_type.equals(Tags.app_user))
                    {
                        call = retrofit.create(Services.class).Search(Tags.display_nearby,userModel.getUser_id(),page_index,m_dept_id, m_branch_id);

                    }else if (user_type.equals(Tags.app_visitor))
                        {
                            call = retrofit.create(Services.class).visitor_Search(Tags.display_nearby,page_index,m_dept_id,m_branch_id,String.valueOf(mylat),String.valueOf(myLng));

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
                                        myAdsModelList.addAll(response.body());
                                        adapter.notifyDataSetChanged();
                                        adapter.setLoaded();


                                    }else
                                    {
                                        if (response.body().size()>0)
                                        {

                                            progressBar.setVisibility(View.GONE);
                                            no_result_container.setVisibility(View.GONE);
                                            myAdsModelList.addAll(response.body());
                                            adapter.notifyDataSetChanged();
                                            adapter.setLoaded();

                                        }else
                                        {
                                            progressBar.setVisibility(View.GONE);
                                            no_result_container.setVisibility(View.VISIBLE);
                                            adapter.setLoaded();

                                        }
                                    }                                }
                            }

                            @Override
                            public void onFailure(Call<List<MyAdsModel>> call, Throwable t) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(GeneralSearchActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                Log.e("error",t.getMessage());
                            }
                        });
                   /* call.flatMap(ads->Observable.fromIterable(ads))
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
                                        no_result_container.setVisibility(View.VISIBLE);
                                    }else
                                    {
                                        no_result_container.setVisibility(View.GONE);

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

                   /* call.enqueue(new Callback<List<MyAdsModel>>() {
                        @Override
                        public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                            if (response.isSuccessful())
                            {
                                if (page_index==1)
                                {
                                    if (response.body().size()>0)
                                    {

                                        no_result_container.setVisibility(View.GONE);
                                        SortData(response.body());


                                    }else
                                    {
                                        myAdsModelList.clear();
                                        adapter.notifyDataSetChanged();
                                        no_result_container.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);

                                    }
                                }else
                                {
                                    if (response.body().size()==0)
                                    {
                                        *//*myAdsModelList.remove(myAdsModelList.size());
                                        adapter.notifyItemRemoved(myAdsModelList.size());
                                        adapter.setLoaded();
                                        adapter.notifyDataSetChanged();*//*

                                    }else if (response.body().size()>0)
                                    {
                                        no_result_container.setVisibility(View.GONE);

                                        myAdsModelList.addAll(response.body());
                                        adapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<List<MyAdsModel>> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(GeneralSearchActivity.this,R.string.something, Toast.LENGTH_LONG).show();
                            Log.e("Error",t.getMessage());
                        }
                    });*/

                }catch (NullPointerException e){}
                catch (Exception e){}


            }
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
                        map.put(id_advertisement,(double)dis);
                        distList.add((double)dis);
                        //mapList.add(map);

                        cont1++;
                        if (cont1<size)
                        {
                            getDistance(mylat,myLng,Double.parseDouble(myAdsModelList1.get(cont1).getGoogle_lat()), Double.parseDouble(myAdsModelList1.get(cont1).getGoogle_long()), myAdsModelList1.get(cont1).getId_advertisement());
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



                                for (MyAdsModel myAdsModel:myAdsModelList1)
                                {
                                    for (String key :idsList)
                                    {
                                        if (myAdsModel.getId_advertisement().equals(key))
                                        {
                                            myAdsModel.setM_distance(String.valueOf(map.get(key)));
                                            finalmyAdsModelList.add(myAdsModel);
                                        }
                                    }
                                }

                                */
/*myAdsModelList.remove(myAdsModelList.size());
                                adapter.notifyItemRemoved(myAdsModelList.size());
                                adapter.setLoaded();
                                adapter.notifyDataSetChanged();*//*

                                myAdsModelList.clear();
                                myAdsModelList.addAll(finalmyAdsModelList);
                                adapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);

                                //Log.e("ss22",idsList.size()+"");
                            }



                        }
                    }catch (NullPointerException e)
                    {

                    }catch (NumberFormatException e){}
                    catch (Exception e){}


                }
            }

            @Override
            public void onFailure(Call<PlacesDistanceModel> call, Throwable t) {
                Log.e("Error",t.getMessage());
                Toast.makeText(GeneralSearchActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
            }
        });


    }
*/

    public void setPos(int pos)
    {
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
    private void UpdateBranchAdapter(String id)
    {
        spinner_branchModelList.clear();
        Spinner_branchModel spinner_branchModel2 = new Spinner_branchModel("",getString(R.string.branch));
        spinner_branchModelList.add(spinner_branchModel2);
        for (DepartmentsModel departmentsModel :departmentsModelList)
        {
            if (departmentsModel.getMain_department_id().equals(id))
            {
                List<DepartmentsModel.SubdepartObject> subdepartObjectList = departmentsModel.getSubdepartObjectList();
                if (subdepartObjectList.size()>0)
                {
                    for (DepartmentsModel.SubdepartObject object:subdepartObjectList)
                    {
                        Spinner_branchModel spinner_branchModel = new Spinner_branchModel(object.getSub_department_id(),object.getSub_department_name());
                        spinner_branchModelList.add(spinner_branchModel);
                    }
                }else
                {
                    Spinner_branchModel spinner_branchModel3 = new Spinner_branchModel(getString(R.string.no_branch),getString(R.string.no_branch));

                    spinner_branchModelList.add(spinner_branchModel3);

                }

                spinnerBranchAdapter.notifyDataSetChanged();
                spinner_branch.setSelection(0);

            }
        }
        spinnerBranchAdapter.notifyDataSetChanged();
        spinner_branch.setSelection(0);



    }
    @Override
    public void onSuccess(List<DepartmentsModel> departmentsModelList)
    {
        try {
            this.departmentsModelList = departmentsModelList;
            spinner_deptModelList.clear();
            if (departmentsModelList.size()>0)
            {  Spinner_DeptModel spinner_deptModel = new Spinner_DeptModel("",getString(R.string.dept));
                spinner_deptModelList.add(spinner_deptModel);
                for (int i=0;i<departmentsModelList.size();i++)
                {
                    Spinner_DeptModel spinner_deptModel2 = new Spinner_DeptModel(departmentsModelList.get(i).getMain_department_id(),departmentsModelList.get(i).getMain_department_name());
                    spinner_deptModelList.add(spinner_deptModel2);
                }
                spinnerDeptAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e)
        {
            getDepartment();
        }

    }
    private void getDepartment()
    {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<List<DepartmentsModel>> call = retrofit.create(Services.class).getDepartmentData();
        call.enqueue(new Callback<List<DepartmentsModel>>() {
            @Override
            public void onResponse(Call<List<DepartmentsModel>> call, Response<List<DepartmentsModel>> response) {
                if (response.isSuccessful())
                {
                    departmentsModelList = new ArrayList<>();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            departmentSingletone.setDpartmentData(response.body());

                            if (departmentsModelList.size()==0)
                            {
                                Toast.makeText(GeneralSearchActivity.this,R.string.no_dept, Toast.LENGTH_LONG).show();

                            }else if (departmentsModelList.size()>0)
                            {

                                departmentSingletone.getDepartmentData(GeneralSearchActivity.this);

                            }
                        }
                    },500);

                }
            }

            @Override
            public void onFailure(Call<List<DepartmentsModel>> call, Throwable t) {
                try {
                    Log.e("Error",t.getMessage());
                    Toast.makeText(GeneralSearchActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                }catch (NullPointerException e)
                {
                    Log.e("Error",t.getMessage());
                    Toast.makeText(GeneralSearchActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                }catch (Exception e)
                {
                    Log.e("Error",t.getMessage());
                    Toast.makeText(GeneralSearchActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;
        mylat = Double.parseDouble(userModel.getUser_google_lat());
        myLng = Double.parseDouble(userModel.getUser_google_long());
    }

    @Override
    public void onSuccess(double lat, double lng) {
        mylat =lat;
        myLng =lng;
    }
}
