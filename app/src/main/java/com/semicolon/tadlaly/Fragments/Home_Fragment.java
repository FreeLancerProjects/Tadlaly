package com.semicolon.tadlaly.Fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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

import com.semicolon.tadlaly.Activities.HomeActivity;
import com.semicolon.tadlaly.Activities.SubDepartAdsActivity;
import com.semicolon.tadlaly.Adapters.BranchAdapter;
import com.semicolon.tadlaly.Adapters.Department_Adapter;
import com.semicolon.tadlaly.Adapters.SlideShowPagerAdapter;
import com.semicolon.tadlaly.Models.DepartmentsModel;
import com.semicolon.tadlaly.Models.SlideShowModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.DepartmentSingletone;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Home_Fragment extends Fragment {
    public static final String USER_ID="user_id";
    public static final String USER_TYPE="user_type";

    private ViewPager pager;
    private SlideShowPagerAdapter adapter;
    private TabLayout tabLayout;
    private List<SlideShowModel> slideShowList;
    private DiscreteScrollView departmentSlider;
    private Department_Adapter department_adapter;
    private List<DepartmentsModel> departmentsModelList;
    private Timer timer;
    private TextView no_dept,no_branch;
    private List<DepartmentsModel.SubdepartObject> subdepartObjectList;
    private BranchAdapter branchAdapter;
    private RecyclerView recView;
    private RecyclerView.LayoutManager manager;
    private ProgressBar progBar;
    private DepartmentSingletone departmentSingletone;
    private String user_id="";
    private String user_type="";
    private ImageView imgPrev,imgNext;
    private HomeActivity homeActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      /*  Calligrapher calligrapher=new Calligrapher(getActivity());
        calligrapher.setFont(getActivity(),"JannaLT-Regular.ttf",true);
    */    View view = inflater.inflate(R.layout.fragment_home,container,false);
        initView(view);
        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            user_id = bundle.getString(USER_ID);
            user_type = bundle.getString(USER_TYPE);
        }
        departmentSingletone = DepartmentSingletone.getInstansce();
        getDepartment();
        return view;
    }

    public static Home_Fragment getInstance(String user_id,String user_type)
    {
        Home_Fragment home_fragment = new Home_Fragment();
        Bundle bundle = new Bundle();
        bundle.putString(USER_TYPE,user_type);
        bundle.putString(USER_ID,user_id);
        home_fragment.setArguments(bundle);
        return home_fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        getSlideShowData();
    }
    private void getDepartment() {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<List<DepartmentsModel>> call = retrofit.create(Services.class).getDepartmentData();
        call.enqueue(new Callback<List<DepartmentsModel>>() {
            @Override
            public void onResponse(Call<List<DepartmentsModel>> call, Response<List<DepartmentsModel>> response) {
                if (response.isSuccessful())
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            departmentsModelList.clear();
                            departmentsModelList.addAll(response.body());
                            departmentSingletone.setDpartmentData(response.body());

                            if (departmentsModelList.size()==0)
                            {
                                progBar.setVisibility(View.GONE);
                                no_dept.setVisibility(View.VISIBLE);
                                imgNext.setVisibility(View.INVISIBLE);
                                imgPrev.setVisibility(View.INVISIBLE);

                            }else if (departmentsModelList.size()>0)
                            {

                                if (departmentsModelList.size()>=2)

                                {
                                departmentSlider.scrollToPosition(1);
                                }
                                department_adapter.notifyDataSetChanged();
                                no_dept.setVisibility(View.GONE);
                                progBar.setVisibility(View.GONE);


                            }else if (departmentsModelList.size()==1)
                            {
                                imgNext.setVisibility(View.INVISIBLE);
                                imgPrev.setVisibility(View.INVISIBLE);
                            }
                        }
                    },500);

                }
            }

            @Override
            public void onFailure(Call<List<DepartmentsModel>> call, Throwable t) {
                try {
                    progBar.setVisibility(View.GONE);
                    Log.e("Error",t.getMessage());
                    Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                }catch (NullPointerException e)
                {
                    progBar.setVisibility(View.GONE);
                    Log.e("Error",t.getMessage());
                    Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                }catch (Exception e)
                {
                    progBar.setVisibility(View.GONE);
                    Log.e("Error",t.getMessage());
                    Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    private void getSlideShowData() {

        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<List<SlideShowModel>> call = retrofit.create(Services.class).getSlideShowData();
        call.enqueue(new Callback<List<SlideShowModel>>() {
            @Override
            public void onResponse(Call<List<SlideShowModel>> call, Response<List<SlideShowModel>> response) {
                if (response.isSuccessful())
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            slideShowList.clear();
                            slideShowList.addAll(response.body());
                            adapter.notifyDataSetChanged();
                            timer = new Timer();
                            timer.scheduleAtFixedRate(new Timer_Trans(),4000,7000);
                        }
                    },500);

                }
            }

            @Override
            public void onFailure(Call<List<SlideShowModel>> call, Throwable t) {
                Log.e("Error",t.getMessage());
                Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void initView(View view) {
        homeActivity = (HomeActivity) getActivity();
        no_dept = view.findViewById(R.id.no_dept);
        no_branch = view.findViewById(R.id.no_branch);
        subdepartObjectList = new ArrayList<>();
        slideShowList = new ArrayList<>();
        departmentsModelList = new ArrayList<>();
        pager = view.findViewById(R.id.pager);
        tabLayout = view.findViewById(R.id.tab);
        departmentSlider = view.findViewById(R.id.slider);
        imgPrev = view.findViewById(R.id.imgPrev);
        imgNext = view.findViewById(R.id.imgNext);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        adapter = new SlideShowPagerAdapter(slideShowList,getActivity());
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(),R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        ////////////////////////////////////////////////////////////////////
        branchAdapter = new BranchAdapter(getActivity(),this,subdepartObjectList);
        recView.setLayoutManager(manager);
        recView.setAdapter(branchAdapter);


        ////////////////////////////////////////////////////////////////////



        department_adapter = new Department_Adapter(departmentsModelList,getActivity(),this);
        departmentSlider.setAdapter(department_adapter);
        departmentSlider.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.05f)
                .setMinScale(0.85f)
                .setPivotX(Pivot.X.CENTER)
                .setPivotY(Pivot.Y.BOTTOM)
                .build());


        departmentSlider.addOnItemChangedListener((viewHolder, adapterPosition) -> {

            subdepartObjectList.clear();
             DepartmentsModel departmentsModel = departmentsModelList.get(adapterPosition);
            if (departmentsModel.getSubdepartObjectList().size()>0)
            {
                subdepartObjectList.addAll(departmentsModel.getSubdepartObjectList());
                branchAdapter.notifyDataSetChanged();
                no_branch.setVisibility(View.GONE);

            }else
            {
                no_branch.setVisibility(View.VISIBLE);

            }
            Log.e("AdapterPos",adapterPosition+"");
            if (adapterPosition==0){
                imgPrev.setVisibility(View.INVISIBLE);
                imgNext.setVisibility(View.VISIBLE);
            }else if (adapterPosition==departmentsModelList.size()-1)
            {
                imgNext.setVisibility(View.INVISIBLE);
                imgPrev.setVisibility(View.VISIBLE);
            }else
                {
                    imgPrev.setVisibility(View.VISIBLE);
                    imgNext.setVisibility(View.VISIBLE);

                }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                departmentSlider.smoothScrollToPosition(departmentSlider.getCurrentItem()+1);

            }
        });

        imgPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                departmentSlider.smoothScrollToPosition(departmentSlider.getCurrentItem() - 1);
            }
        });
    }

    private class Timer_Trans extends TimerTask{
        @Override
        public void run() {
            try {
                getActivity().runOnUiThread(() -> {
                    if (pager.getCurrentItem()<slideShowList.size()-1)
                    {
                        pager.setCurrentItem(pager.getCurrentItem()+1);
                    }else
                    {
                        try {
                            timer.cancel();
                            pager.setAdapter(adapter);
                            pager.setCurrentItem(0);
                            timer.scheduleAtFixedRate(new Timer_Trans(),4000,7000);

                        }catch (NullPointerException e)
                        {

                        }catch (Exception e)
                        { }

                    }
                });
            }catch (NullPointerException e)
            {

            }catch (Exception e)
            {}

        }
    }

    public void setPos(int pos)
    {
        subdepartObjectList.clear();
        DepartmentsModel departmentsModel = departmentsModelList.get(pos);
        homeActivity.setTitle(departmentsModel.getMain_department_name());

        if (departmentsModel.getSubdepartObjectList().size()>0)
        {
           SubDeptDataFragment subDeptDataFragment = new SubDeptDataFragment();
           Bundle bundle = new Bundle();
           bundle.putString("user_id",user_id);
           bundle.putSerializable("subDept_data", (Serializable) departmentsModel.getSubdepartObjectList());
           subDeptDataFragment.setArguments(bundle);
           getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_fragments_container,subDeptDataFragment).commit();

        }else
            {
                no_branch.setVisibility(View.VISIBLE);

            }


    }
    public void setSubDeptPos(DepartmentsModel.SubdepartObject subdepartObject)
    {
        Intent intent = new Intent(getActivity(), SubDepartAdsActivity.class);
        intent.putExtra("user_id",user_id);
        intent.putExtra("user_type",user_type);
        intent.putExtra("subDeptData",subdepartObject);
        startActivity(intent);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer!=null)
        {
            timer.cancel();

        }

    }
}
