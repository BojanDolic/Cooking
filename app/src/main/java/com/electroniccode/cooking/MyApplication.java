package com.electroniccode.cooking;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.jetbrains.annotations.NotNull;

public class MyApplication extends Application {

    private static AppOpenManager appOpenManager;

    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(@NonNull @NotNull InitializationStatus initializationStatus) {

                    }
                });

        appOpenManager = new AppOpenManager(this);

    }
}
