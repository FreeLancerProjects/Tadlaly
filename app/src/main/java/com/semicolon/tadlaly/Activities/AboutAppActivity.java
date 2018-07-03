package com.semicolon.tadlaly.Activities;

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

import com.semicolon.tadlaly.Models.AboutAppModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;

import me.anwarshahriar.calligrapher.Calligrapher;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AboutAppActivity extends AppCompatActivity {
    private TextView txt_aboutApp;
    private ProgressBar progBar;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"OYA-Regular.ttf",true);
        initView();
        getData();

    }

    private void getData() {
        try {
            Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
            Call<AboutAppModel> call = retrofit.create(Services.class).AboutApp();
            call.enqueue(new Callback<AboutAppModel>() {
                @Override
                public void onResponse(Call<AboutAppModel> call, Response<AboutAppModel> response) {
                    if (response.isSuccessful())
                    {
                        txt_aboutApp.setText(response.body().getAbout_app());
                        progBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<AboutAppModel> call, Throwable t) {
                    progBar.setVisibility(View.GONE);
                    Toast.makeText(AboutAppActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                    Log.e("Error",t.getMessage());
                }
            });

        }catch (NullPointerException e){}
        catch (Exception e){}

    }

    private void initView() {
        back = findViewById(R.id.back);
        back.setOnClickListener(view -> finish());
        txt_aboutApp = findViewById(R.id.txt_aboutApp);
        progBar = findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
    }


}
