package com.semicolon.tadlaly.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.semicolon.tadlaly.Adapters.MyAdsPagerAdapter;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.LatLngSingleTone;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;

public class AllAppAdsFragment extends Fragment implements UserSingleTone.OnCompleteListener,LatLngSingleTone.onLatLngSuccess{
    private static final String USER_ID="user_id";
    private static final String USER_TYPE="user_type";
    private String user_id="";
    private String user_type;
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private int page_index=1;
    private double mylat=0.0,myLng=0.0;
    private LatLngSingleTone latLngSingleTone;
    private ViewPager pager;
    private TabLayout tab;
    private MyAdsPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_app_ads,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        pager = view.findViewById(R.id.pager);
        tab = view.findViewById(R.id.tab);
        tab.setupWithViewPager(pager);

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            user_id = bundle.getString(USER_ID);
            user_type = bundle.getString(USER_TYPE);
            UpdateUI(user_type);
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



    }

    private void UpdateUI(String user_type) {

        adapter = new MyAdsPagerAdapter(getChildFragmentManager());
        adapter.AddFragments(Fragment_AllAds_Recent.getInstance(user_type));
        adapter.AddFragments(Fragment_AllAds_Nearby.getInstance(user_type));

        adapter.AddTitle(getString(R.string.newest));
        adapter.AddTitle(getString(R.string.nearme));

        pager.setAdapter(adapter);

        for (int i = 0 ; i<tab.getTabCount()-1 ; i++)
        {
            View view = ((ViewGroup)tab.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.setMargins(8,0,8,0);
            tab.requestLayout();
        }

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

    }
}
