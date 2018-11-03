package com.semicolon.tadlaly.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Preferences;
import com.semicolon.tadlaly.language.LanguageHelper;

import io.paperdb.Paper;

public class SelectLanguageActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private Preferences preferences;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.onAttach(newBase,Paper.book().read("language")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        initView();
    }

    private void initView() {

        preferences = new Preferences(this);
        if (preferences.isLanguageSelected())
        {
            NavToGps();
        }else
            {
                createAlertDialog();

            }


    }

    private void createAlertDialog() {
        alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .create();
        View view = LayoutInflater.from(this).inflate(R.layout.custom_lang_dialog,null);
        Button btn_ar = view.findViewById(R.id.btn_ar);
        Button btn_en = view.findViewById(R.id.btn_en);

        btn_ar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                preferences.setIsSelectedLang(true);
                Paper.book().write("language","ar");
                refreshLayout();

            }
        });
        btn_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                preferences.setIsSelectedLang(true);
                Paper.book().write("language","en");
                refreshLayout();


            }
        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.dialog;
        alertDialog.setView(view);
        alertDialog.show();
    }

    private void refreshLayout() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void NavToGps()
    {
        Intent intent = new Intent(this,GpsActivity.class);
        startActivity(intent);
        finish();
    }
}
