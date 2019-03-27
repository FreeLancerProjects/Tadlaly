package com.semicolon.tadlaly.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewImageAdapter extends RecyclerView.Adapter<ViewImageAdapter.myHolder> {

    private List<MyAdsModel.Images> imgPath;
    private Context context;
    public ViewImageAdapter(List<MyAdsModel.Images> imgPath, Context context) {
        this.imgPath = imgPath;
        this.context = context;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_row,parent,false);
        return new myHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {
        MyAdsModel.Images images = imgPath.get(position);
        holder.BindData(images);
    }


    @Override
    public int getItemCount() {
        return imgPath.size();
    }

    public class myHolder extends RecyclerView.ViewHolder {
        private PhotoView img;
        public myHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.image);

        }

        public void BindData(MyAdsModel.Images path)
        {
            Log.e("url",path.getPhoto_name());
            Picasso.with(context).load(Uri.parse(Tags.Image_Url+path.getPhoto_name())).into(img);
        }
    }
}
