package com.semicolon.tadlaly.Activities;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.tadlaly.Models.RulesModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.language.LocalManager;

import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RulesActivity extends AppCompatActivity {
    private ImageView back;
    private TextView title,content;
    private ProgressBar progressBar;
    private String current_language;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);

        super.attachBaseContext(LocalManager.updateResources(newBase,LocalManager.getLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        initView();
        getRules();
    }

    private void getRules() {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<RulesModel> call = retrofit.create(Services.class).getRules();
        call.enqueue(new Callback<RulesModel>() {
            @Override
            public void onResponse(Call<RulesModel> call, Response<RulesModel> response) {
                if (response.isSuccessful())
                {
                    title.setText(response.body().getTitle());
                    content.setText(response.body().getContent());
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<RulesModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RulesActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                Log.e("Error",t.getMessage());
            }
        });
    }

    private void initView() {
        current_language = Paper.book().read("language", Locale.getDefault().getLanguage());
        back = findViewById(R.id.back);
        if (current_language.equals("ar"))
        {
            back.setRotation(180f);
        }
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        progressBar = findViewById(R.id.progBar);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        back.setOnClickListener(view -> finish());
    }
}
