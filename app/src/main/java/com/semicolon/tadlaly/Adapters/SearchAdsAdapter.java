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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.semicolon.tadlaly.Activities.SearchActivity;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchAdsAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder>{
    private final int item_row=1;
    private final int progress_row=0;

    private Context context;
    private List<MyAdsModel> myAdsModelList;
    private SearchActivity searchActivity;
    private int totalItemCount,lastVisibleItem;
    private int Threshold=5;
    private OnLoadListener onLoadListener;
    private boolean loading;
    private LinearLayoutManager mLinearLayoutManager;


    public SearchAdsAdapter(RecyclerView recView, Context context, List<MyAdsModel> myAdsModelList) {
        this.context = context;
        this.myAdsModelList = myAdsModelList;

            this.searchActivity = (SearchActivity) context;


        if (recView.getLayoutManager() instanceof LinearLayoutManager)
        {
            mLinearLayoutManager = (LinearLayoutManager) recView.getLayoutManager();
            recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy>0)
                    {
                        totalItemCount = mLinearLayoutManager.getItemCount();
                        lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                        if (!loading&&totalItemCount<=(lastVisibleItem+Threshold))
                        {
                            if (onLoadListener !=null)
                            {
                                onLoadListener.onLoadMore();

                            }
                            loading=true;

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
            View view = LayoutInflater.from(context).inflate(R.layout.subdept_visitor_row,parent,false);
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
            MyAdsModel myAdsModel =myAdsModelList.get(position);
            itemHolder.BindData(myAdsModel);
            //setAnimation(holder.itemView);

                itemHolder.itemView.setOnClickListener(view -> searchActivity.SetMyadsData(myAdsModel));
            Animation animation = AnimationUtils.loadAnimation(context,R.anim.right_to_left);
            itemHolder.itemView.startAnimation(animation);


        }else if (holder instanceof myProgressHolder)
        {
            myProgressHolder progressHolder = (myProgressHolder) holder;
            progressHolder.progressBar.setIndeterminate(true);
        }


    }

    /*private void setAnimation(View view)
    {
        ScaleAnimation animation = new ScaleAnimation(0f,1f,0f,1f);
        animation.setDuration(2000);
        view.startAnimation(animation);

    }*/
    @Override
    public int getItemViewType(int position) {
        return myAdsModelList.get(position)==null?progress_row:item_row;
    }

    @Override
    public int getItemCount() {
        return myAdsModelList.size();
    }

    public class myItemHolder extends RecyclerView.ViewHolder {
        private TextView date,state_new,state_old,name,cost,city,distance;
        private RoundedImageView img;

        public myItemHolder(View itemView) {
            super(itemView);
            distance = itemView.findViewById(R.id.distance);
            city =itemView.findViewById(R.id.city);
            img = itemView.findViewById(R.id.img);
            date = itemView.findViewById(R.id.date);
            state_new= itemView.findViewById(R.id.state_new);
            state_old= itemView.findViewById(R.id.state_old);
            name = itemView.findViewById(R.id.name);
            cost = itemView.findViewById(R.id.cost);



        }

        public void BindData(MyAdsModel myAdsModel)
        {

            double dist = myAdsModel.getDistance();
            distance.setText(Math.round(dist)+context.getString(R.string.km));
            //Typeface typeface =Typeface.createFromAsset(context.getAssets(),"OYA-Regular.ttf");
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
            } else
                {
                    state_new.setVisibility(View.GONE);
                    state_old.setVisibility(View.VISIBLE);
                }
           // name.setTypeface(typeface);
            name.setText(myAdsModel.getAdvertisement_title());
            //cost.setTypeface(typeface);
            if (cost.equals(Tags.undefined_price))
            {
                cost.setText(myAdsModel.getAdvertisement_price());

            }else
                {
                    cost.setText(myAdsModel.getAdvertisement_price()+" ريال");

                }
            city.setText(myAdsModel.getCity());
        }
    }

    public class myProgressHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public myProgressHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progBar);
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
        loading=false;
    }
    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }
}
