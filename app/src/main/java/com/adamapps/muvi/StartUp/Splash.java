package com.adamapps.muvi.StartUp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.adamapps.muvi.R;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {

    InterstitialAd mInterstitialAd;
    Boolean showAd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //mInterstitialAd.loadAd(new AdRequest.Builder().build());
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            showAd = true;
        }

        //showAd = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), Welcome.class));
                finish();
            }
        }, 1500);


    }




}
