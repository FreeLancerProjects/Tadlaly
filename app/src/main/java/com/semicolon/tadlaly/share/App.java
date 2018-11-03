package com.semicolon.tadlaly.share;

import android.app.Application;
import android.content.Context;

import com.semicolon.tadlaly.language.LanguageHelper;

import java.util.Locale;

import io.paperdb.Paper;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base,"ar"));


    }

    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);

        String lang = Paper.book().read("language");
        if (lang==null)
        {
            Paper.book().write("language", Locale.getDefault().getLanguage());

        }else
        {
            Paper.book().write("language",lang);

        }
    }
}
