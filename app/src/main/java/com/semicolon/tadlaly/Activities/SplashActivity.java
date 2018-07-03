package com.semicolon.tadlaly.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.semicolon.tadlaly.R;

import me.anwarshahriar.calligrapher.Calligrapher;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"Amble-Regular.ttf",true);
    }
}
