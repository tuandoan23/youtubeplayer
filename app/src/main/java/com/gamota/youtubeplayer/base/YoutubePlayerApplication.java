package com.gamota.youtubeplayer.base;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.orhanobut.hawk.Hawk;

public class YoutubePlayerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(this).build();
        Stetho.initializeWithDefaults(this);
        Fresco.initialize(this);
    }
}

