package com.semicolon.tadlaly.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.language.LocalManager;

import io.paperdb.Paper;

public class PromiseActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView back,img_ekhaa;
    private Button agreeBtn1,agreeBtn2,agreeBtn3,disagreeBtn1,disagreeBtn2,disagreeBtn3,send_btn;
    private boolean brand1=false;
    private boolean brand2=false;
    private boolean brand3=false;
    private TextView txt_quran;
    private LinearLayout ll_charity;
    /*private View root;
    private TextView tv_title,tv_content;
    private ProgressBar progBar;
    private Button agree_btn;
    private BottomSheetBehavior behavior;
    private boolean isRules_readed= false;*/


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);

        super.attachBaseContext(LocalManager.updateResources(newBase,LocalManager.getLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promise);
        initView();
       /* Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"JannaLT-Regular.ttf",true);
*/
    }

    private void initView() {
        back = findViewById(R.id.back);
        img_ekhaa = findViewById(R.id.img_ekhaa);
        back.setOnClickListener(view -> finish());
        txt_quran    = findViewById(R.id.txt_quran);
        txt_quran.setText("وَأَوْفُوا بِعَهْدِ اللَّهِ إِذَا عَاهَدْتُمْ وَلَا تَنْقُضُوا الْأَيْمَانَ بَعْدَ تَوْكِيدِهَا وَقَدْ جَعَلْتُمُ اللَّهَ عَلَيْكُمْ كَفِيلًا");

        send_btn     = findViewById(R.id.send_btn);
        agreeBtn1    = findViewById(R.id.agreeBtn1);
        agreeBtn2    = findViewById(R.id.agreeBtn2);
        agreeBtn3    = findViewById(R.id.agreeBtn3);
        disagreeBtn1 = findViewById(R.id.disagreeBtn1);
        disagreeBtn2 = findViewById(R.id.disagreeBtn2);
        disagreeBtn3 = findViewById(R.id.disagreeBtn3);
        ll_charity = findViewById(R.id.ll_charity);

       /* tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        progBar = findViewById(R.id.progBar);
        agree_btn = findViewById(R.id.agree_btn);
        root = findViewById(R.id.root);
        behavior = BottomSheetBehavior.from(root);*/

        agreeBtn1.setOnClickListener(this);
        agreeBtn2.setOnClickListener(this);
        agreeBtn3.setOnClickListener(this);
        disagreeBtn1.setOnClickListener(this);
        disagreeBtn2.setOnClickListener(this);
        disagreeBtn3.setOnClickListener(this);
       // agree_btn.setOnClickListener(this);
        img_ekhaa.setOnClickListener(this);
        send_btn.setOnClickListener(this);

        ll_charity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ekhaa.org.sa/?fbclid=IwAR0wxz74dgO4e8zHBwdh6pTTWkJi7kFwCK1B_QUe-i3KpYJuNnnORguWtIg"));
                startActivity(intent);
            }
        });

/*
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState==BottomSheetBehavior.STATE_DRAGGING)
                {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
*/
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id)
        {
            case R.id.agreeBtn1:
                disagreeBtn1.setBackgroundResource(R.drawable.btn_promise_disagree);
                agreeBtn1.setBackgroundResource(R.drawable.btn_promise_agree);
                agreeBtn1.setTextColor(ContextCompat.getColor(this,R.color.white));
                disagreeBtn1.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                Toast.makeText(this, "تم الموافقة على التعهد الاول", Toast.LENGTH_LONG).show();
                brand1=true;
                break;
            case R.id.agreeBtn2:
                disagreeBtn2.setBackgroundResource(R.drawable.btn_promise_disagree);
                agreeBtn2.setBackgroundResource(R.drawable.btn_promise_agree);
                agreeBtn2.setTextColor(ContextCompat.getColor(this,R.color.white));
                disagreeBtn2.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                Toast.makeText(this, "تم الموافقة على التعهد الثاني", Toast.LENGTH_LONG).show();

                brand2 = true;
                break;
            case R.id.agreeBtn3:
                disagreeBtn3.setBackgroundResource(R.drawable.btn_promise_disagree);
                agreeBtn3.setBackgroundResource(R.drawable.btn_promise_agree);
                agreeBtn3.setTextColor(ContextCompat.getColor(this,R.color.white));
                disagreeBtn3.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                Toast.makeText(this, "تم الموافقة على التعهد الثالث", Toast.LENGTH_LONG).show();

                brand3=true;
                break;
            case R.id.disagreeBtn1:
                disagreeBtn1.setBackgroundResource(R.drawable.btn_promise_agree);
                agreeBtn1.setBackgroundResource(R.drawable.btn_promise_disagree);
                agreeBtn1.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                disagreeBtn1.setTextColor(ContextCompat.getColor(this,R.color.white));

                brand1 = false;
                break;
            case R.id.disagreeBtn2:
                disagreeBtn2.setBackgroundResource(R.drawable.btn_promise_agree);
                agreeBtn2.setBackgroundResource(R.drawable.btn_promise_disagree);
                agreeBtn2.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                disagreeBtn2.setTextColor(ContextCompat.getColor(this,R.color.white));

                brand2 = false;
                break;
            case R.id.disagreeBtn3:
                disagreeBtn3.setBackgroundResource(R.drawable.btn_promise_agree);
                agreeBtn3.setBackgroundResource(R.drawable.btn_promise_disagree);
                agreeBtn3.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                disagreeBtn3.setTextColor(ContextCompat.getColor(this,R.color.white));

                brand3 = false;
                break;

            case R.id.send_btn:
                if (!brand1)
                {
                    Toast.makeText(this, "لايمكنك إضافة اعلان دون الموافقة علي التعهد الاول", Toast.LENGTH_LONG).show();
                }else if (!brand2)
                {
                    Toast.makeText(this, "لايمكنك إضافة اعلان دون الموافقة علي التعهد الثاني", Toast.LENGTH_LONG).show();

                }
                else if (!brand3)
                {
                    Toast.makeText(this, "لايمكنك إضافة اعلان دون الموافقة علي التعهد الثالث", Toast.LENGTH_LONG).show();

                }else
                    {
                        Intent intent2 = getIntent();
                        setResult(RESULT_OK,intent2);
                        finish();

                       // behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                      //  getRulesData();
                    }
                break;
            case R.id.img_ekhaa:
                Intent intent = new Intent(PromiseActivity.this,WebActivity.class);
                intent.putExtra("url","http://www.ekhaa.org.sa");
                startActivity(intent);
                break;

            /*case R.id.agree_btn:


                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                new Handler()
                        .postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent2 = getIntent();
                                setResult(RESULT_OK,intent2);
                                finish();
                            }
                        },1000);


                break;*/
        }
    }

    /*private void getRulesData() {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<RulesModel> call = retrofit.create(Services.class).getRules();
        call.enqueue(new Callback<RulesModel>() {
            @Override
            public void onResponse(Call<RulesModel> call, Response<RulesModel> response) {
                if (response.isSuccessful())
                {
                    agree_btn.setVisibility(View.VISIBLE);
                    tv_title.setText(response.body().getTitle());
                    tv_content.setText(response.body().getContent());
                    progBar.setVisibility(View.GONE);
                    isRules_readed =true;
                }
            }

            @Override
            public void onFailure(Call<RulesModel> call, Throwable t) {
                progBar.setVisibility(View.GONE);
                Toast.makeText(PromiseActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                Log.e("Error",t.getMessage());
            }
        });
    }*/

    /*@Override
    public void onBackPressed() {
        if (behavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
        {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (isRules_readed)
            {
                new Handler()
                        .postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = getIntent();
                                setResult(RESULT_OK,intent);
                                finish();
                            }
                        },1000);

            }else
                {
                    super.onBackPressed();

                }

        }else
            {
                super.onBackPressed();

            }
    }*/
}
