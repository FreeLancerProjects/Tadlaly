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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.tadlaly.Activities.AdsDetailsActivity;
import com.semicolon.tadlaly.Activities.MyAdsActivity;
import com.semicolon.tadlaly.Adapters.MyCurrentAdsAdapter;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.Models.ResponseModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;
import com.semicolon.tadlaly.share.Common;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CurrentAds_Fragment extends Fragment implements View.OnLongClickListener,UserSingleTone.OnCompleteListener{
    private RecyclerView recView;
    private MyCurrentAdsAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private List<MyAdsModel> myAdsModelList;
    private ProgressBar progBar;
    public boolean inNormalMode=true;
    private TextView item_count;
    private MyAdsActivity adsActivity;
    private AlertDialog alertDialog;
    private ProgressDialog dialog;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private TextView no_ads;
    private List<String> ads_ids;
    private int page_index=1;
    private int sell_type = -1;
    private MyAdsActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_ads, container, false);
        userSingleTone = UserSingleTone.getInstance();
        userSingleTone.getUser(this);
        initView(view);
        CreateProgressDialog();
        return view;
    }



    private void initView(View view) {
        activity = (MyAdsActivity) getActivity();
        ads_ids = new ArrayList<>();
        myAdsModelList = new ArrayList<>();
        no_ads = view.findViewById(R.id.no_ads);
        item_count = view.findViewById(R.id.item_count);
        recView = view.findViewById(R.id.recView);
        progBar = view.findViewById(R.id.progBar);
        manager = new LinearLayoutManager(getActivity());
        recView.setHasFixedSize(true);
        recView.setLayoutManager(manager);
        adapter = new MyCurrentAdsAdapter(recView,getActivity(), myAdsModelList,this);
        recView.setAdapter(adapter);
        adsActivity = (MyAdsActivity) getActivity();
        getData(1);

        initOnLoadMore();
    }

    private void initOnLoadMore() {
        adapter.setLoadListener(new MyCurrentAdsAdapter.onLoadMoreListener() {
            @Override
            public void onLoadMore() {
                myAdsModelList.add(null);
                adapter.notifyItemInserted(myAdsModelList.size()-1);
                int index=page_index;
                Log.e("loadMore","loadmore");
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
    private void CreateAlertDialog(int pos)
    {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_delete_alert,null);
        TextView title = view.findViewById(R.id.title);
        TextView done = view.findViewById(R.id.done);
        TextView cancel = view.findViewById(R.id.cancel);
        RadioButton sellBtn = view.findViewById(R.id.sellBtn);
        RadioButton undoBtn =view.findViewById(R.id.undoBtn);
        sellBtn.setOnClickListener(view13 ->
                {
                    sellBtn.setChecked(true);
                    sell_type = Tags.isSell;
                }
                );
        undoBtn.setOnClickListener(view14 ->
        {
            sell_type = Tags.isdeleted;
            undoBtn.setChecked(true);
        } );
        done.setOnClickListener(view1 -> {

            if (sell_type != -1) {

                alertDialog.dismiss();
                Delete(sell_type,pos);
            }else
                {
                    Toast.makeText(adsActivity, R.string.sel_reason, Toast.LENGTH_SHORT).show();

                }
        });
        cancel.setOnClickListener(view12 -> alertDialog.dismiss());

        //title.setText(getString(R.string.wdel)+"("+counter+") "+getString(R.string.item));
        title.setText(getString(R.string.delete));
        alertDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setView(view)
                .create();
        alertDialog.show();
    }
    public void getData(int page_index) {

        Log.e("1255555","11111");

        try {
            Log.e("id",userModel.getUser_id());
            Log.e("page_indexx",page_index+"");

            Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
            Call<List<MyAdsModel>> call = retrofit.create(Services.class).getCurrentAds(userModel.getUser_id(),page_index);
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

                    }catch (Exception e)
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
    private void onLoadgetData(int page_index)
    {
        try {
            Log.e("id",userModel.getUser_id());
            Log.e("page_indexx",page_index+"");

            Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
            Call<List<MyAdsModel>> call = retrofit.create(Services.class).getCurrentAds(userModel.getUser_id(),page_index);
            call.enqueue(new Callback<List<MyAdsModel>>() {
                @Override
                public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                    if (response.isSuccessful())
                    {
                        if (response.body().size()>0)
                        {
                            CurrentAds_Fragment.this.page_index = CurrentAds_Fragment.this.page_index+1;
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

                    }catch (Exception e)
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
    @Override
    public boolean onLongClick(View view) {

        //item_count.setText("");



        return false;
    }

    public void DisPlayUdate_Delete()
    {
        inNormalMode = false;
        MyCurrentAdsAdapter myCurrentAdsAdapter = (MyCurrentAdsAdapter) recView.getAdapter();
        myCurrentAdsAdapter.notifyDataSetChanged();
        item_count.setVisibility(View.VISIBLE);
    }


    public void setPosForDetails(MyAdsModel myAdsModel)
    {
        try {
            Intent intent = new Intent(getActivity(), AdsDetailsActivity.class);
            intent.putExtra("ad_details",myAdsModel);
            intent.putExtra("whoVisit",Tags.me_visit);
            intent.putExtra("user_id",userModel.getUser_id());
            getActivity().startActivity(intent);

        }catch (NullPointerException e)
        {

        } catch (Exception e){}


    }

    public void setPosFavourit(int pos,boolean isFav)
    {
        MyAdsModel myAdsModel = myAdsModelList.get(pos);
        if (isFav)
        {
            Toast.makeText(adsActivity, "fav"+pos, Toast.LENGTH_SHORT).show();
        }else
            {
                Toast.makeText(adsActivity, "not fav"+pos, Toast.LENGTH_SHORT).show();

            }
    }
    private void ClearUi()
    {
        inNormalMode=true;

        adapter.notifyDataSetChanged();
        item_count.setVisibility(View.GONE);
        ads_ids.clear();



    }

    public void onBack()
    {
        if (!inNormalMode)
        {
            inNormalMode = true;

            ClearUi();
            adapter.notifyDataSetChanged();
        }
    }

    public void Delete(int sell_type,int pos)
    {
        ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.show();
        ads_ids.add(myAdsModelList.get(pos).getId_advertisement());

        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<ResponseModel> call = retrofit.create(Services.class).deleteAds(sell_type, ads_ids);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful())
                {
                    dialog.dismiss();
                    if (response.body().getSuccess()==1)
                    {
                        myAdsModelList.remove(pos);
                        if (myAdsModelList.size()==0)
                        {
                            no_ads.setVisibility(View.VISIBLE);
                        }
                        ClearUi();
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }else
                        {
                            dialog.dismiss();

                        }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(getActivity(),R.string.error, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                Log.e("Error",t.getMessage());
            }
        });



    }




    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel= userModel;
    }

    public void setItemData(MyAdsModel myAdsModel, int pos, String action_type) {

        if (action_type.equals(Tags.del))
        {
            CreateAlertDialog(pos);
        }else if (action_type.equals(Tags.upd))
        {
            adapter.notifyDataSetChanged();
            ClearUi();
            activity.UpdateAds(myAdsModel);
            /*Intent intent = new Intent(getActivity(), UpdateAdsActivity.class);
            intent.putExtra("ad_details",myAdsModel);
            getActivity().startActivity(intent);*/

        }
    }
}
