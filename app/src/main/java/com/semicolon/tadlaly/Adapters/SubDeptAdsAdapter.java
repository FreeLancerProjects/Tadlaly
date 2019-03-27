package com.semicolon.tadlaly.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.semicolon.tadlaly.Activities.GeneralSearchActivity;
import com.semicolon.tadlaly.Activities.HomeActivity;
import com.semicolon.tadlaly.Activities.SubDepartAdsActivity;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SubDeptAdsAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder>{
    private final int item_row=1;
    private final int progress_row=0;

    private Context context;
    private List<MyAdsModel> myAdsModelList;
    private SubDepartAdsActivity subDepartAdsActivity;
    private GeneralSearchActivity generalSearchActivity;
    private HomeActivity homeActivity;
    private String type;
    private int totalItemCount,lastVisibleItem;
    private int Threshold=5;
    private OnLoadListener onLoadListener;
    private boolean loading;
    private LinearLayoutManager mLinearLayoutManager;
    int pos;
    Fragment fragment;


    public SubDeptAdsAdapter(RecyclerView recView,Context context, List<MyAdsModel> myAdsModelList,String type,Fragment fragment) {
        this.context = context;
        this.myAdsModelList = myAdsModelList;
        this.type=type;
        this.fragment = fragment;
        if (type.equals("1"))
        {
            this.subDepartAdsActivity = (SubDepartAdsActivity) context;

        }else if (type.equals("2"))
        {
            this.generalSearchActivity = (GeneralSearchActivity) context;
        }else if (type.equals("3"))
        {
            this.homeActivity = (HomeActivity) context;
        }

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

                        Log.e("fc",mLinearLayoutManager.findFirstCompletelyVisibleItemPosition()+"___");
                        if (type.equals("1"))
                        {
                            subDepartAdsActivity.image_top.setVisibility(View.VISIBLE);



                        }else if (type.equals("2"))
                        {
                            generalSearchActivity.image_top.setVisibility(View.VISIBLE);


                        }
                        if (!loading&&totalItemCount<=(lastVisibleItem+Threshold))
                        {
                            if (onLoadListener !=null)
                            {
                                onLoadListener.onLoadMore();

                            }
                            loading=true;

                        }


                    }else
                        {
                            if (type.equals("1"))
                            {

                                subDepartAdsActivity.image_top.setVisibility(View.GONE);


                            }else if (type.equals("2"))
                            {
                                generalSearchActivity.image_top.setVisibility(View.GONE);


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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        pos = position;
        if (holder instanceof myItemHolder)
        {
            myItemHolder itemHolder = (myItemHolder) holder;
            MyAdsModel myAdsModel =myAdsModelList.get(position);
            itemHolder.BindData(myAdsModel);
            //setAnimation(holder.itemView);
            if (type.equals("1"))
            {
                itemHolder.itemView.setOnClickListener(view -> subDepartAdsActivity.setPos(holder.getAdapterPosition()));

            }else if (type.equals("2"))
            {
                itemHolder.itemView.setOnClickListener(view -> generalSearchActivity.setPos(holder.getAdapterPosition()));

            }else if (type.equals("3"))
            {
                itemHolder.itemView.setOnClickListener(view -> homeActivity.SetMyadsData(myAdsModel));

            }
            /*if (position>lastpos)
            {
                Animation animation = AnimationUtils.loadAnimation(context,R.anim.right_to_left);
                holder.itemView.startAnimation(animation);
            }

            lastpos = position;*/


            Animation animation = AnimationUtils.loadAnimation(context,R.anim.right_to_left);
            holder.itemView.startAnimation(animation);


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
        private TextView date,state_new,state_old,state_service,name,cost,viewers,distance,tv_upd_del;
        private RoundedImageView img;
        private LinearLayout distContainer,updContainer;

        public myItemHolder(View itemView) {
            super(itemView);
            distContainer= itemView.findViewById(R.id.distContainer);
            distance = itemView.findViewById(R.id.distance);
            viewers =itemView.findViewById(R.id.viewers);
            tv_upd_del = itemView.findViewById(R.id.tv_upd_del);
            img = itemView.findViewById(R.id.img);
            date = itemView.findViewById(R.id.date);
            state_new= itemView.findViewById(R.id.state_new);
            state_old= itemView.findViewById(R.id.state_old);
            state_service= itemView.findViewById(R.id.state_service);

            name = itemView.findViewById(R.id.name);
            cost = itemView.findViewById(R.id.cost);
            updContainer = itemView.findViewById(R.id.updContainer);
            tv_upd_del.setVisibility(View.GONE);



        }

        public void BindData(MyAdsModel myAdsModel)
        {

            updContainer.setVisibility(View.GONE);
            distContainer.setVisibility(View.VISIBLE);
            double dist = myAdsModel.getDistance();

            distance.setText(Math.round(dist)+" "+context.getString(R.string.km));
            if (myAdsModel.getAdvertisement_image().size()>0)
            {
                Picasso.with(context).load(Uri.parse(Tags.Image_Url+myAdsModel.getAdvertisement_image().get(0).getPhoto_name())).into(img);
                Log.e("size1",myAdsModel.getAdvertisement_image().size()+"");
            }
            date.setText(myAdsModel.getAdvertisement_date());

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
            //name.setTypeface(typeface);
            name.setText(myAdsModel.getAdvertisement_title());
            //cost.setTypeface(typeface);
            if (cost.equals(Tags.undefined_price))
            {
                cost.setText(myAdsModel.getAdvertisement_price());

            }else
            {
                cost.setText(myAdsModel.getAdvertisement_price()+" ريال");

            }
            viewers.setText(myAdsModel.getView_count());
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
