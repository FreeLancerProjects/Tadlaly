package com.semicolon.tadlaly.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.semicolon.tadlaly.Activities.MyFollowActivity;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int item_row = 1;
    private final int progress_row = 0;

    private Context context;
    private List<MyAdsModel> myAdsModelList;
    private MyFollowActivity myFollowActivity;
    private int totalItemCount, lastVisibleItem;
    private int Threshold = 5;
    private OnLoadListener onLoadListener;
    private boolean loading;
    private LinearLayoutManager mLinearLayoutManager;


    public FollowAdapter(RecyclerView recView, Context context, List<MyAdsModel> myAdsModelList) {
        this.context = context;
        this.myAdsModelList = myAdsModelList;
        myFollowActivity = (MyFollowActivity) context;



        if (recView.getLayoutManager() instanceof LinearLayoutManager) {
            mLinearLayoutManager = (LinearLayoutManager) recView.getLayoutManager();
            recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) {
                        totalItemCount = mLinearLayoutManager.getItemCount();
                        lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

                        if (!loading && totalItemCount <= (lastVisibleItem + Threshold)) {
                            if (onLoadListener != null) {
                                onLoadListener.onLoadMore();

                            }
                            loading = true;

                        }


                    }


                }
            });
        }


    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == item_row) {
            View view = LayoutInflater.from(context).inflate(R.layout.follow_item_row, parent, false);
            return new myItemHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.load_more_item, parent, false);
            return new myProgressHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof myItemHolder) {
            myItemHolder itemHolder = (myItemHolder) holder;
            MyAdsModel myAdsModel = myAdsModelList.get(position);
            itemHolder.BindData(myAdsModel);

            itemHolder.itemView.setOnClickListener(view -> {
                        MyAdsModel myAdsModel2 = myAdsModelList.get(holder.getAdapterPosition());

                        myFollowActivity.setItemData(myAdsModel2,holder.getAdapterPosition());
                    }
            );


        } else if (holder instanceof myProgressHolder) {
            myProgressHolder progressHolder = (myProgressHolder) holder;
            progressHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return myAdsModelList.get(position) == null ? progress_row : item_row;
    }

    @Override
    public int getItemCount() {
        return myAdsModelList.size();
    }

    public class myItemHolder extends RecyclerView.ViewHolder {
        private TextView tv_date, tv_state_new, tv_state_old, tv_state_service, tv_name,tv_dept_name, tv_distance, tv_read_state;
        private RoundedImageView img;

        public myItemHolder(View itemView) {
            super(itemView);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            img = itemView.findViewById(R.id.img);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_state_new = itemView.findViewById(R.id.tv_state_new);
            tv_state_old = itemView.findViewById(R.id.tv_state_old);
            tv_state_service = itemView.findViewById(R.id.tv_state_service);

            tv_read_state = itemView.findViewById(R.id.tv_read_state);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_dept_name = itemView.findViewById(R.id.tv_dept_name);



        }

        public void BindData(MyAdsModel myAdsModel) {

            double dist = myAdsModel.getDistance();

            tv_distance.setText(String.format("%.2f",dist)+" "+ context.getString(R.string.km));
            if (myAdsModel.getAdvertisement_image().size() > 0) {
                Picasso.with(context).load(Uri.parse(Tags.Image_Url + myAdsModel.getAdvertisement_image().get(0).getPhoto_name())).into(img);
            }
            tv_date.setText(myAdsModel.getAdvertisement_date());

            if (myAdsModel.isRead_status()) {
                tv_read_state.setVisibility(View.GONE);

            } else {
                tv_read_state.setVisibility(View.VISIBLE);

            }

            if (myAdsModel.getAdvertisement_type().equals(Tags.ad_new)) {
                tv_state_new.setVisibility(View.VISIBLE);
                tv_state_old.setVisibility(View.GONE);
                tv_state_service.setVisibility(View.GONE);

            } else if (myAdsModel.getAdvertisement_type().equals(Tags.ad_old)) {
                tv_state_new.setVisibility(View.GONE);
                tv_state_old.setVisibility(View.VISIBLE);
                tv_state_service.setVisibility(View.GONE);

            } else if (myAdsModel.getAdvertisement_type().equals(Tags.service)) {
                tv_state_new.setVisibility(View.GONE);
                tv_state_old.setVisibility(View.GONE);
                tv_state_service.setVisibility(View.VISIBLE);
            }
            tv_name.setText(myAdsModel.getAdvertisement_title());
            tv_dept_name.setText(myAdsModel.getDepartment_name());

        }
    }

    public class myProgressHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public myProgressHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progBar);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        }
    }

    public interface OnLoadListener {
        void onLoadMore();
    }

    public void setLoadListener(OnLoadListener loadListener) {
        this.onLoadListener = loadListener;
    }

    public void setLoaded() {
        loading = false;

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }
}
