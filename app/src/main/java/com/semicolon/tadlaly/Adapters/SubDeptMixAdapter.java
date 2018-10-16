package com.semicolon.tadlaly.Adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.semicolon.tadlaly.Models.DepartmentsModel;

import java.util.ArrayList;
import java.util.List;

public class SubDeptMixAdapter extends FragmentStatePagerAdapter {
    List<Fragment>fragmentList;
    List<DepartmentsModel.SubdepartObject> subdepartObjectList;
    Context context;

    public SubDeptMixAdapter(FragmentManager fm,Context context,List<DepartmentsModel.SubdepartObject> subdepartObjectList) {
        super(fm);
        this.context=context;
        this.subdepartObjectList = subdepartObjectList;
        fragmentList = new ArrayList<>();
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
        String t = subdepartObjectList.get(position).getSub_department_name().trim().replaceAll("\n","");
        t = t.replaceAll("\t","");
        return t;
    }
}
