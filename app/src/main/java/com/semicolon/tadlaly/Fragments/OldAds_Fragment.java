package com.semicolon.tadlaly.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.tadlaly.Activities.AdsDetailsActivity;
import com.semicolon.tadlaly.Activities.MyAdsActivity;
import com.semicolon.tadlaly.Adapters.MyPreviousAdsAdapter;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OldAds_Fragment extends Fragment implements UserSingleTone.OnCompleteListener{
    private RecyclerView recView;
    private MyPreviousAdsAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private List<MyAdsModel> myAdsModelList;
    private List<MyAdsModel> removed;
    private ProgressBar progBar;
    public boolean inNormalMode=true;
    private TextView item_count;
    public int Counter=0;
    private MyAdsActivity adsActivity;
    private AlertDialog alertDialog;
    private ProgressDialog dialog;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private TextView no_ads;
    private int page_index =1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_old_ads,container,false);
        userSingleTone = UserSingleTone.getInstance();
        userSingleTone.getUser(this);
        initView(view);
        CreateProgressDialog();
        return view;
    }

    private void initView(View view) {
        removed = new ArrayList<>();
        myAdsModelList = new ArrayList<>();
        no_ads = view.findViewById(R.id.no_ads);
        item_count = view.findViewById(R.id.item_count);
        recView = view.findViewById(R.id.recView);
        progBar = view.findViewById(R.id.progBar);
        manager = new LinearLayoutManager(getActivity());
        recView.setHasFixedSize(true);
        recView.setLayoutManager(manager);
        adapter = new MyPreviousAdsAdapter(recView,getActivity(), myAdsModelList,this);
        recView.setAdapter(adapter);
        adsActivity = (MyAdsActivity) getActivity();
        getData(1);
        initOnLoadMore();

    }
    private void initOnLoadMore() {
        adapter.setLoadListener(new MyPreviousAdsAdapter.OnLoadListener() {
            @Override
            public void onLoadMore() {
                myAdsModelList.add(null);
                adapter.notifyItemInserted(myAdsModelList.size()-1);
                int index=page_index;

                onLoadgetData(index);
            }
        });
    }


    private void CreateProgressDialog()
    {
        ProgressBar bar = new ProgressBar(getActivity());
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(getActivity(),R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.deleting));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setIndeterminateDrawable(drawable);
    }
    /*private void CreateAlertDialog(int counter)
    {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_delete_alert,null);
        TextView title = view.findViewById(R.id.title);
        //TextView done = view.findViewById(R.id.done);
        TextView cancel = view.findViewById(R.id.cancel);
        *//*done.setOnClickListener(view1 -> {
            Delete();
            alertDialog.dismiss();
        });*//*
        cancel.setOnClickListener(view12 -> alertDialog.dismiss());

        title.setText(getString(R.string.wdel)+"("+counter+") "+getString(R.string.item));
        alertDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setView(view)
                .create();
    }*/
    public void getData(int page_index) {
        //myAdsModelList.clear();


        try {
            Log.e("id",userModel.getUser_id());
            Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
            Call<List<MyAdsModel>> call = retrofit.create(Services.class).getPreviousAds(userModel.getUser_id(),page_index);
            call.enqueue(new Callback<List<MyAdsModel>>() {
                @Override
                public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                    if (response.isSuccessful())
                    {
                        progBar.setVisibility(View.GONE);

                        myAdsModelList.clear();

                        if (response.body().size()>0)
                        {
                            no_ads.setVisibility(View.GONE);
                            myAdsModelList.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        }else
                        {
                            no_ads.setVisibility(View.VISIBLE);

                        }

                    }
                }

                @Override
                public void onFailure(Call<List<MyAdsModel>> call, Throwable t) {
                    try {
                        progBar.setVisibility(View.GONE);
                        Log.e("error",t.getMessage());
                        Toast.makeText(adsActivity,getString(R.string.something), Toast.LENGTH_LONG).show();

                    }catch (NullPointerException e)
                    {
                        progBar.setVisibility(View.GONE);
                        Log.e("error",t.getMessage());
                        Toast.makeText(adsActivity,getString(R.string.something), Toast.LENGTH_LONG).show();

                    }
                    catch (Exception e)
                    {
                        progBar.setVisibility(View.GONE);
                        Log.e("error",t.getMessage());
                        Toast.makeText(adsActivity,getString(R.string.something), Toast.LENGTH_LONG).show();

                    }

                }
            });

        }catch (NullPointerException e)
        {
            progBar.setVisibility(View.GONE);
            Toast.makeText(adsActivity,getString(R.string.something), Toast.LENGTH_LONG).show();

        }
        catch (Exception e)
        {
            progBar.setVisibility(View.GONE);
            Toast.makeText(adsActivity,getString(R.string.something), Toast.LENGTH_LONG).show();

        }


    }
    private void onLoadgetData(int page_index) {
        //myAdsModelList.clear();


        try {
            Log.e("id",userModel.getUser_id());
            Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
            Call<List<MyAdsModel>> call = retrofit.create(Services.class).getPreviousAds(userModel.getUser_id(),page_index);
            call.enqueue(new Callback<List<MyAdsModel>>() {
                @Override
                public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                    if (response.isSuccessful())
                    {
                        if (response.body().size()>0)
                        {
                            OldAds_Fragment.this.page_index = OldAds_Fragment.this.page_index+1;
                            int lastpos = myAdsModelList.size()-1;
                            myAdsModelList.remove(myAdsModelList.size()-1);
                            adapter.notifyItemRemoved(myAdsModelList.size());
                            adapter.setLoaded();
                            myAdsModelList.addAll(response.body());
                            adapter.notifyItemRangeChanged(lastpos,myAdsModelList.size()-1);
                        }else
                        {
                            myAdsModelList.remove(myAdsModelList.size()-1);
                            adapter.notifyItemRemoved(myAdsModelList.size());
                            adapter.setLoaded();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<MyAdsModel>> call, Throwable t) {
                    try {
                        progBar.setVisibility(View.GONE);
                        Log.e("error",t.getMessage());
                        Toast.makeText(adsActivity,getString(R.string.something), Toast.LENGTH_LONG).show();

                    }catch (NullPointerException e)
                    {
                        progBar.setVisibility(View.GONE);
                        Log.e("error",t.getMessage());
                        Toast.makeText(adsActivity,getString(R.string.something), Toast.LENGTH_LONG).show();

                    }
                    catch (Exception e)
                    {
                        progBar.setVisibility(View.GONE);
                        Log.e("error",t.getMessage());
                        Toast.makeText(adsActivity,getString(R.string.something), Toast.LENGTH_LONG).show();

                    }

                }
            });

        }catch (NullPointerException e)
        {
            progBar.setVisibility(View.GONE);
            Toast.makeText(adsActivity,getString(R.string.something), Toast.LENGTH_LONG).show();

        }
        catch (Exception e)
        {
            progBar.setVisibility(View.GONE);
            Toast.makeText(adsActivity,getString(R.string.something), Toast.LENGTH_LONG).show();

        }


    }

   /* @Override
    public boolean onLongClick(View view) {
        inNormalMode = false;
        MyCurrentAdsAdapter myCurrentAdsAdapter = (MyCurrentAdsAdapter) recView.getAdapter();
        myCurrentAdsAdapter.notifyDataSetChanged();
        item_count.setVisibility(View.VISIBLE);
        item_count.setText(getString(R.string.numitem_delete) +"("+0+")");


        return false;
    }*/

    /*@SuppressLint("SetTextI18n")
    private void UpdateUi(int count) {
        if (count==0)
        {
            item_count.setText(getString(R.string.numitem_delete) +"("+0+")");
            adsActivity.setVisibility(0);
        }else if (count>0)
        {
            item_count.setText(getString(R.string.numitem_delete) +"("+count+")");
            adsActivity.setVisibility(1);

        }
        else if (count < 0)
        {
            adsActivity.setVisibility(0);

            Counter=0;
            count = Counter;
            item_count.setText(getString(R.string.numitem_delete) +"("+count+")");

        }
    }*/

   /* public void setPos(View view,int pos,String type)
    {
        MyAdsModel myAdsModel = myAdsModelList.get(pos);
        if (myPreviousAdsAdapter ==null)
        {
            myPreviousAdsAdapter = (MyPreviousAdsAdapter) recView.getAdapter();

        }

       *//* if (type.equals(Tags.del))
        {
            ImageView icon = (ImageView) view;

            if (removed.contains(myAdsModel))
            {
                icon.setImageResource(R.drawable.delete_icon);
                removed.remove(myAdsModel);
                Counter--;
                UpdateUi(Counter);

            }else
            {
                removed.add(myAdsModel);
                Counter++;
                UpdateUi(Counter);

                icon.setImageResource(R.drawable.clear_icon);

            }
            //myCurrentAdsAdapter.DeleteItems(removed);

        }else if (type.equals(Tags.upd))
        {
            Toast.makeText(getActivity(), R.string.update, Toast.LENGTH_SHORT).show();
        }*//*



    }*/
    public void setPosForDetail(int pos)
    {
        MyAdsModel myAdsModel = myAdsModelList.get(pos);
        Intent intent = new Intent(getActivity(), AdsDetailsActivity.class);
        intent.putExtra("ad_details",myAdsModel);
        intent.putExtra("whoVisit", Tags.me_visit);
        getActivity().startActivity(intent);
    }
   /* public void setPos(View view,int pos,String type)
    {
        MyAdsModel myAdsModel = myAdsModelList.get(pos);
        if (myPreviousAdsAdapter ==null)
        {
            myPreviousAdsAdapter = (MyPreviousAdsAdapter) recView.getAdapter();

        }

       *//* if (type.equals(Tags.del))
        {
            ImageView icon = (ImageView) view;

            if (removed.contains(myAdsModel))
            {
                icon.setImageResource(R.drawable.delete_icon);
                removed.remove(myAdsModel);
                Counter--;
                UpdateUi(Counter);

            }else
            {
                removed.add(myAdsModel);
                Counter++;
                UpdateUi(Counter);

                icon.setImageResource(R.drawable.clear_icon);

            }
            //myCurrentAdsAdapter.DeleteItems(removed);

        }else if (type.equals(Tags.upd))
        {
            Toast.makeText(getActivity(), R.string.update, Toast.LENGTH_SHORT).show();
        }*//*



    }*/
    /*public void setPosFavourit(int pos,boolean isFav)
    {
        MyAdsModel myAdsModel = myAdsModelList.get(pos);
        if (isFav)
        {
            Toast.makeText(adsActivity, "fav"+pos, Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(adsActivity, "not fav"+pos, Toast.LENGTH_SHORT).show();

        }
    }*/
   /* private void ClearUi()
    {
        item_count.setVisibility(View.GONE);
        removed.clear();
        Counter=0;
        adsActivity.setVisibility(0);

    }*/

    /*public void onBack()
    {
        if (!inNormalMode)
        {
            inNormalMode = true;
            if (myCurrentAdsAdapter ==null)
            {
                myCurrentAdsAdapter = (MyCurrentAdsAdapter) recView.getAdapter();

            }
            ClearUi();
            myCurrentAdsAdapter.notifyDataSetChanged();
        }
    }

    private void Delete()
    {
        myCurrentAdsAdapter.DeleteItems(removed);
        ClearUi();
        inNormalMode=true;
        myCurrentAdsAdapter.notifyDataSetChanged();

    }
    public void showDeleteDialog()
    {
        CreateAlertDialog(Counter);

        alertDialog.show();
    }*/

    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel= userModel;
    }
}
