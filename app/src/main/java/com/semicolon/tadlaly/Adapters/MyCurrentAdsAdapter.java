package com.semicolon.tadlaly.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.semicolon.tadlaly.Fragments.CurrentAds_Fragment;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyCurrentAdsAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder>{
    private final int item_row=1;
    private final int progress_row=0;
    private Context context;
    private List<MyAdsModel> myAdsModelList;
    private CurrentAds_Fragment ads_fragment;
    private int totalItem_count,lastVisibileItem_count,threshold=5;
    private boolean isLoding;
    private onLoadMoreListener onLoadMoreListener;
    public MyCurrentAdsAdapter(RecyclerView recyclerView,Context context, List<MyAdsModel> myAdsModelList, CurrentAds_Fragment ads_fragment) {
        this.ads_fragment=ads_fragment;
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
                        totalItem_count = manager.getItemCount();
                        lastVisibileItem_count = manager.findLastVisibleItemPosition();
                        if (!isLoding && totalItem_count<=(lastVisibileItem_count+threshold))
                        {
                            if (onLoadMoreListener!=null)
                            {
                                onLoadMoreListener.onLoadMore();
                            }
                            isLoding=true;

                        }
                    }
                }
            });

        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==item_row)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.ads_item_row,parent,false);
            return new myItemHolder(view);
        }else
            {
                View view = LayoutInflater.from(context).inflate(R.layout.load_more_item,parent,false);
                return new myProgressHolder(view);
            }

    }
    /*private void setAnimation(View view)
    {
        ScaleAnimation animation = new ScaleAnimation(0f,1f,0f,1f);
        animation.setDuration(2000);
        view.startAnimation(animation);

    }*/
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof myItemHolder)
        {
            myItemHolder itemHolder = (myItemHolder) holder;
            //setAnimation(itemHolder.itemView);
            if (ads_fragment.inNormalMode)
            {
                itemHolder.updContainer.setVisibility(View.GONE);
                itemHolder.delete.setImageResource(R.drawable.delete_icon);
                itemHolder.itemView.setEnabled(true);
            }else
            {
                itemHolder.updContainer.setVisibility(View.VISIBLE);
                itemHolder.itemView.setEnabled(false);

            }
            itemHolder.itemView.setOnClickListener(view -> ads_fragment.setPosForDetails(itemHolder.getAdapterPosition()));
            itemHolder.delete.setOnClickListener(view -> ads_fragment.setPos(itemHolder.delete,itemHolder.getAdapterPosition(), Tags.del));
            itemHolder.update.setOnClickListener(view -> ads_fragment.setPos(itemHolder.update,itemHolder.getAdapterPosition(), Tags.upd));
            MyAdsModel myAdsModel = myAdsModelList.get(position);
            itemHolder.BindData(myAdsModel);
            itemHolder.tv_upd_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ads_fragment.DisPlayUdate_Delete();
                }
            });
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

    public class myProgressHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public myProgressHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progBar);
        }
    }

    public class myItemHolder extends RecyclerView.ViewHolder {
        private LinearLayout updContainer;
        private TextView date,state_new,state_old,name,cost,viewers,update,distance,tv_upd_del;
        private ImageView delete;
        private RoundedImageView img;
        private LinearLayout distContainer;

        public myItemHolder(View itemView) {
            super(itemView);
            distContainer= itemView.findViewById(R.id.distContainer);
            updContainer = itemView.findViewById(R.id.updContainer);
            distance = itemView.findViewById(R.id.distance);
            tv_upd_del = itemView.findViewById(R.id.tv_upd_del);
            update = itemView.findViewById(R.id.update);
            delete = itemView.findViewById(R.id.delete);
            viewers =itemView.findViewById(R.id.viewers);
            img = itemView.findViewById(R.id.img);
            date = itemView.findViewById(R.id.date);
            state_new= itemView.findViewById(R.id.state_new);
            state_old= itemView.findViewById(R.id.state_old);
            name = itemView.findViewById(R.id.name);
            cost = itemView.findViewById(R.id.cost);



        }

        public void BindData(MyAdsModel myAdsModel)
        {

            distContainer.setVisibility(View.GONE);

            if (myAdsModel.getAdvertisement_image().size()>0)
            {
                Picasso.with(context).load(Uri.parse(Tags.Image_Url+myAdsModel.getAdvertisement_image().get(0).getPhoto_name())).into(img);
                Log.e("size1",myAdsModel.getAdvertisement_image().size()+"");
            }
            date.setText("منذ "+myAdsModel.getAdvertisement_date());

            if (myAdsModel.getAdvertisement_type().equals(Tags.ad_new))
            {
                state_new.setVisibility(View.VISIBLE);
                state_old.setVisibility(View.GONE);
            } else
                {
                    state_new.setVisibility(View.GONE);
                    state_old.setVisibility(View.VISIBLE);
                }
            //name.setTypeface(typeface);
            name.setText(myAdsModel.getAdvertisement_title());
            //cost.setTypeface(typeface);
            //cost.setText(myAdsModel.getAdvertisement_price()+" ريال");
            viewers.setText(myAdsModel.getView_count());
        }
    }



    @Override
    public int getItemViewType(int position) {
        return myAdsModelList.get(position)==null?progress_row:item_row;
    }

    public void DeleteItems(List<MyAdsModel> myAdsModels)
    {
        myAdsModelList.removeAll(myAdsModels);
        notifyDataSetChanged();
    }

    public interface onLoadMoreListener
    {
        void onLoadMore();
    }
    public void setLoadListener(onLoadMoreListener loadMoreListener)
    {
        this.onLoadMoreListener =loadMoreListener;
    }
    public void setLoaded()
    {
        isLoding = false;
    }
}
