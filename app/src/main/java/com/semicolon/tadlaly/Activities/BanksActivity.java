package com.semicolon.tadlaly.Activities;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.semicolon.tadlaly.Adapters.BankAccountAdapter;
import com.semicolon.tadlaly.Models.BankModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.language.LocalManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BanksActivity extends AppCompatActivity {
    private ImageView back;
    private ProgressBar progBar;
    private RecyclerView recView;
    private RecyclerView.LayoutManager manager;
    private List<BankModel> bankModelList;
    private BankAccountAdapter adapter;
    private String current_language;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);

        super.attachBaseContext(LocalManager.updateResources(newBase,LocalManager.getLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banks);
        initView();

    }

    private void initView() {
        bankModelList = new ArrayList<>();
        current_language = Paper.book().read("language", Locale.getDefault().getLanguage());
        back = findViewById(R.id.back);

        if (current_language.equals("ar"))
        {
            back.setRotation(180f);
        }
        back.setOnClickListener(v -> finish());
        progBar = findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        recView = findViewById(R.id.recView);
        manager = new LinearLayoutManager(this);
        recView.setLayoutManager(manager);
        adapter = new BankAccountAdapter(this,bankModelList);
        recView.setAdapter(adapter);
        getBanksAccount();
    }



    private void getBanksAccount() {


        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<List<BankModel>> call = retrofit.create(Services.class).getBanks();
        call.enqueue(new Callback<List<BankModel>>() {
            @Override
            public void onResponse(Call<List<BankModel>> call, Response<List<BankModel>> response) {
                if (response.isSuccessful()) {
                    progBar.setVisibility(View.GONE);
                    if (response.body().size() > 0) {
                        bankModelList.clear();
                        bankModelList.addAll(response.body());
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<BankModel>> call, Throwable t) {
                progBar.setVisibility(View.GONE);


                Log.e("Error", t.getMessage());

            }
        });
    }

}
