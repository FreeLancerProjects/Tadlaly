package com.semicolon.tadlaly.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.semicolon.tadlaly.Adapters.MyAdsPagerAdapter;
import com.semicolon.tadlaly.Fragments.CurrentAds_Fragment;
import com.semicolon.tadlaly.Fragments.OldAds_Fragment;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.language.LocalManager;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class MyAdsActivity extends AppCompatActivity {
    private TabLayout tab;
    private ViewPager pager;
    private MyAdsPagerAdapter adapter;
    private CurrentAds_Fragment ads_fragment;
    private ImageView back;
    private String current_language;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);

        super.attachBaseContext(LocalManager.updateResources(newBase,LocalManager.getLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);
        initView();
    }

    private void initView() {
        current_language = Paper.book().read("language", Locale.getDefault().getLanguage());
        back = findViewById(R.id.back);

        if (current_language.equals("ar"))
        {
            back.setRotation(180f);
        }
        tab = findViewById(R.id.tab);
        pager = findViewById(R.id.pager);
        tab.setupWithViewPager(pager);
        adapter = new MyAdsPagerAdapter(getSupportFragmentManager());
        AddFragments();
        pager.setAdapter(adapter);
        back.setOnClickListener(view -> {
            if (pager.getCurrentItem()==0)
            {
                   if (ads_fragment.inNormalMode)
                   {
                       finish();
                   }else
                   {
                       ads_fragment.onBack();

                   }
               }else
               {

                   pager.setCurrentItem(0);
               }

           });


        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private void AddFragments() {
        ads_fragment = new CurrentAds_Fragment();
        adapter.AddFragments(ads_fragment);
        adapter.AddFragments(new OldAds_Fragment());
        adapter.AddTitle(getString(R.string.curr));
        adapter.AddTitle(getString(R.string.prev));

    }


    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem()==0)
        {

            if (ads_fragment.inNormalMode)
            {
                super.onBackPressed();

            }else
                {
                    ads_fragment.onBack();

                }
        }else
            {
                pager.setCurrentItem(0);

            }
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    public void UpdateAds(MyAdsModel myAdsModel)
    {
        Intent intent = new Intent(this, UpdateAdsActivity.class);
        intent.putExtra("ad_details",myAdsModel);
        startActivityForResult(intent,78);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList)
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode==78)
        {

            if (resultCode==RESULT_OK)
            {
                adapter = (MyAdsPagerAdapter) pager.getAdapter();
                CurrentAds_Fragment currentAds_fragment = (CurrentAds_Fragment) adapter.getItem(0);
                OldAds_Fragment oldAds_fragment = (OldAds_Fragment) adapter.getItem(1);

                currentAds_fragment.getData(1);
                oldAds_fragment.getData(1);
            }
        }
    }
}
