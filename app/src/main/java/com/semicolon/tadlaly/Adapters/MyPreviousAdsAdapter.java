package com.semicolon.tadlaly.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.semicolon.tadlaly.Fragments.OldAds_Fragment;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyPreviousAdsAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder>{
    private final int item_row=1;
    private final int progress_row=0;
    private Context context;
    private List<MyAdsModel> myAdsModelList;
    private OldAds_Fragment oldAds_fragment;
    private int totalItemCount,lastVisibleItem;
    private int Threshold=5;
    private OnLoadListener onLoadListener;
    private boolean isLoading;

    public MyPreviousAdsAdapter(RecyclerView recyclerView,Context context, List<MyAdsModel> myAdsModelList, OldAds_Fragment oldAds_fragment) {
        this.oldAds_fragment=oldAds_fragment;
        this.context = context;
        this.myAdsModelList = myAdsModelList;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager)
        {
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy>0)
                    {
                        totalItemCount = manager.getItemCount();
                        lastVisibleItem = manager.findLastVisibleItemPosition();
                        if (!isLoading && totalItemCount<=(lastVisibleItem+Threshold))
                        {
                            if (onLoadListener!=null)
                            {
                                onLoadListener.onLoadMore();
                            }
                            isLoading=true;
                        }
                    }
                }
            });

        }



        Log.e("dfsdfs",myAdsModelList.size()+"");
    }

   /* private void setAnimation(View view)
    {
        ScaleAnimation animation = new ScaleAnimation(0f,1f,0f,1f);
        animation.setDuration(2000);
        view.startAnimation(animation);

    }*/
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==item_row)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.my_previous_ad_row,parent,false);
            return new myItemHolder(view);
        }else
            {
                View view = LayoutInflater.from(context).inflate(R.layout.load_more_item,parent,false);
                return new myProgressHolder(view);
            }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof myItemHolder)
        {
            myItemHolder itemHolder = (myItemHolder) holder;
            MyAdsModel myAdsModel = myAdsModelList.get(position);
           // setAnimation(itemHolder.itemView);

            itemHolder.BindData(myAdsModel);
            itemHolder.itemView.setOnClickListener(view -> oldAds_fragment.setPosForDetail(itemHolder.getAdapterPosition()));
        }else if (holder instanceof myProgressHolder)
        {
            myProgressHolder progressHolder = (myProgressHolder) holder;
            progressHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return myAdsModelList.size();
    }



    @Override
    public int getItemViewType(int position) {
        return myAdsModelList.get(position)==null?progress_row:item_row;
    }
    public class myProgressHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public myProgressHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progBar);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        }
    }
    public class myItemHolder extends RecyclerView.ViewHolder {
        private TextView date,state_new,state_old,state_service,name,cost,viewers;
        private RoundedImageView img;
        public myItemHolder(View itemView) {
            super(itemView);
            viewers =itemView.findViewById(R.id.viewers);
            img = itemView.findViewById(R.id.img);
            date = itemView.findViewById(R.id.date);
            state_new= itemView.findViewById(R.id.state_new);
            state_old= itemView.findViewById(R.id.state_old);
            state_service= itemView.findViewById(R.id.state_service);

            name = itemView.findViewById(R.id.name);
            cost = itemView.findViewById(R.id.cost);



        }

        public void BindData(MyAdsModel myAdsModel)
        {

           // Typeface typeface =Typeface.createFromAsset(context.getAssets(),"OYA-Regular.ttf");
            if (myAdsModel.getAdvertisement_image().size()>0)
            {
                Picasso.with(context).load(Uri.parse(Tags.Image_Url+myAdsModel.getAdvertisement_image().get(0).getPhoto_name())).into(img);
                Log.e("size1",myAdsModel.getAdvertisement_image().size()+"");
            }
            //date.setTypeface(typeface);
            date.setText("قبل "+myAdsModel.getAdvertisement_date());

            if (myAdsModel.getAdvertisement_type().equals(Tags.ad_new))
            {
                state_new.setVisibility(View.VISIBLE);
                state_old.setVisibility(View.GONE);
                state_service.setVisibility(View.GONE);

            } else if (myAdsModel.getAdvertisement_type().equals(Tags.ad_old))
            {
                state_new.setVisibility(View.GONE);
                state_old.setVisibility(View.VISIBLE);
                state_service.setVisibility(View.GONE);

            }else if (myAdsModel.getAdvertisement_type().equals(Tags.service))
            {
                state_new.setVisibility(View.GONE);
                state_old.setVisibility(View.GONE);
                state_service.setVisibility(View.VISIBLE);
            }
           // name.setTypeface(typeface);
            name.setText(myAdsModel.getAdvertisement_title());
            //cost.setTypeface(typeface);
            cost.setText(myAdsModel.getAdvertisement_price()+" ريال");
            viewers.setText(myAdsModel.getView_count());
        }
    }

    public interface OnLoadListener
    {
        void onLoadMore();
    }
    public void setLoadListener(OnLoadListener loadListener)
    {
        this.onLoadListener = loadListener;
    }

    public void setLoaded()
    {
        isLoading=false;
    }
    /*public void DeleteItems(List<MyAdsModel> myAdsModels)
    {
        myAdsModelList.removeAll(myAdsModels);
        notifyDataSetChanged();
    }*/
}
