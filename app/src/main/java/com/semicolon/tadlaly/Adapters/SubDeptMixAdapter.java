package com.semicolon.tadlaly.Adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.semicolon.tadlaly.Models.DepartmentsModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;

import java.util.ArrayList;
import java.util.List;

public class SubDeptMixAdapter extends FragmentStatePagerAdapter implements UserSingleTone.OnCompleteListener{
    List<Fragment>fragmentList;
    List<DepartmentsModel.SubdepartObject> subdepartObjectList;
    Context context;

    public SubDeptMixAdapter(FragmentManager fm,Context context,List<DepartmentsModel.SubdepartObject> subdepartObjectList) {
        super(fm);
        this.context=context;
        this.subdepartObjectList = subdepartObjectList;
        fragmentList = new ArrayList<>();
    }

    @Override
    public void onSuccess(UserModel userModel) {
        /*this.userModel = userModel;
        mylat = Double.parseDouble(userModel.getUser_google_lat());
        myLng = Double.parseDouble(userModel.getUser_google_long());*/

    }


    public void AddFragment(List<Fragment> fragmentList)
    {
        this.fragmentList.clear();
        this.fragmentList.addAll(fragmentList);
    }
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return subdepartObjectList.get(position).getSub_department_name();
    }
}
