package com.semicolon.tadlaly.share;

import android.app.Application;
import android.content.Context;

import com.semicolon.tadlaly.language.LocalManager;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocalManager.updateResources(base,LocalManager.getLanguage(base)));


    }


}
