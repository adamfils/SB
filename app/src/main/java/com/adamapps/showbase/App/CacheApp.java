package com.adamapps.showbase.App;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;

/**
 * Created by chris on 2/26/2018.
 */

public class CacheApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        //setupLeakCanary();


        Fabric.with(this, new Crashlytics());

        if(FirebaseAuth.getInstance()!=null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }


    }

    protected void setupLeakCanary() {
        //enabledStrictMode();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    private static void enabledStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //
                .detectAll() //
                .penaltyLog() //
                .penaltyDeath() //
                .build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}