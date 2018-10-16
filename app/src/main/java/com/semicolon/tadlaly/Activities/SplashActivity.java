package com.semicolon.tadlaly.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.semicolon.tadlaly.R;

public class SplashActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        /*Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"Amble-Regular.ttf",true);*/

        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
    }

    @Override
    protected void onStart() {
        super.onStart();
        surfaceHolder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        new AsynkTask().execute(holder);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    private class AsynkTask extends AsyncTask<SurfaceHolder,Void,Void>{


        @Override
        protected Void doInBackground(SurfaceHolder... voids) {
            String path = "android.resource://"+getPackageName()+"/"+R.raw.splash;
            mp = MediaPlayer.create(SplashActivity.this, Uri.parse(path));
            mp.setDisplay(voids[0]);
            mp.setLooping(false);
            mp.setVolume(0f,0f);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.release();
                    Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
        mp=null;
    }
}
