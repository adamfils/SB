package com.adamapps.toxic.App;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by chris on 2/26/2018.
 */

public class CacheApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if(FirebaseAuth.getInstance()!=null)
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}