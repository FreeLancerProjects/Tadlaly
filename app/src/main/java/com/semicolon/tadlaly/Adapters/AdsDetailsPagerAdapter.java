package com.semicolon.tadlaly.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.semicolon.tadlaly.Activities.AdsDetailsActivity;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdsDetailsPagerAdapter extends android.support.v4.view.PagerAdapter {
    private List<MyAdsModel.Images> imgs;
    private Context context;
    private AdsDetailsActivity adsDetailsActivity;

    public AdsDetailsPagerAdapter(List<MyAdsModel.Images> imgs, Context context) {
        this.imgs = imgs;
        this.context = context;
        adsDetailsActivity = (AdsDetailsActivity) context;
    }

    @Override
    public int getCount() {
        return imgs.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.ads_details_pager_item,container,false);
        String img = imgs.get(position).getPhoto_name();
        RoundedImageView ads_img = view.findViewById(R.id.ads_img);
        Picasso.with(context).load(Uri.parse(Tags.Image_Url+img)).into(ads_img);
        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view,0);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adsDetailsActivity.setPagerItemClick();
            }
        });


        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        viewPager.removeView((View) object);

    }
}
