package com.semicolon.tadlaly.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.semicolon.tadlaly.Adapters.SubDeptMixAdapter;
import com.semicolon.tadlaly.Models.DepartmentsModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;

import java.util.ArrayList;
import java.util.List;

public class SubDeptDataFragment extends Fragment implements UserSingleTone.OnCompleteListener{
    private ViewPager pager;
    private TabLayout tab;
    private SubDeptMixAdapter subDeptMixAdapter;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private String deptId;
    private List<Fragment> fragmentList;
    private List<DepartmentsModel.SubdepartObject> subdepartObjectList2;
    private String user_id="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subdept_data,container,false);
        getDataFromBundle();

        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        subDeptMixAdapter.notifyDataSetChanged();
    }

    private void getDataFromBundle() {
        fragmentList = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            subdepartObjectList2 = (List<DepartmentsModel.SubdepartObject>) bundle.getSerializable("subDept_data");
            user_id = bundle.getString("user_id");
            deptId = bundle.getString("dept_id");
            Log.e("SDDF_id",user_id);
            for (DepartmentsModel.SubdepartObject object:subdepartObjectList2)
            {
                fragmentList.add(SubDataFragment.newInstance(deptId,object.getSub_department_id(),user_id));

            }



        }


    }

    private void initView(View view) {

        if (!user_id.equals("0"))
        {
            userSingleTone = UserSingleTone.getInstance();
            userSingleTone.getUser(this);
        }
        pager = view.findViewById(R.id.pager);
        tab = view.findViewById(R.id.tab);

        subDeptMixAdapter = new SubDeptMixAdapter(getActivity().getSupportFragmentManager(),getActivity(),subdepartObjectList2);
        subDeptMixAdapter.AddFragment(fragmentList);
        pager.setAdapter(subDeptMixAdapter);
        tab.setupWithViewPager(pager);

        for (int i=0;i<tab.getTabCount()-1;i++)
        {
            View viewTab = ((ViewGroup)tab.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewTab.getLayoutParams();
            params.setMargins(8,0,8,0);
            tab.requestLayout();
        }




    }

       @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;

    }


}
