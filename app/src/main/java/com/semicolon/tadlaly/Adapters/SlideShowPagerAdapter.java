package com.semicolon.tadlaly.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.semicolon.tadlaly.Models.SlideShowModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SlideShowPagerAdapter extends android.support.v4.view.PagerAdapter {
    private List<SlideShowModel> slideShow_modelList;
    private Context context;

    public SlideShowPagerAdapter(List<SlideShowModel> slideShow_modelList, Context context) {
        this.slideShow_modelList = slideShow_modelList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return slideShow_modelList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.pager_item,container,false);
        SlideShowModel model = slideShow_modelList.get(position);
        RoundedImageView ads_img = view.findViewById(R.id.ads_img);
       // Typeface typeface = Typeface.createFromAsset(context.getAssets(),"OYA-Regular.ttf");
        TextView title = view.findViewById(R.id.ads_title);
        TextView content = view.findViewById(R.id.ads_content);
        //title.setTypeface(typeface);
        //content.setTypeface(typeface);

        Picasso.with(context).load(Uri.parse(Tags.Image_Url+model.getImage())).into(ads_img);

        title.setText(model.getImage_title());
        content.setText(model.getImage_content());
        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view,0);


        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        viewPager.removeView((View) object);

    }
}
