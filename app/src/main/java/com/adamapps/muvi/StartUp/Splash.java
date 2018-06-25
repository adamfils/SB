package com.adamapps.muvi.StartUp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.adamapps.muvi.R;
import com.adamapps.muvi.TvShow.SeasonDetail;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Splash extends AppCompatActivity {

    InterstitialAd mInterstitialAd;
    Boolean showAd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseDatabase.getInstance().getReference().child("AdSelect").child("option")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            if (dataSnapshot.getValue(Integer.class) == 1) {
                                MobileAds.initialize(Splash.this, "ca-app-pub-5077858194293069~3201484542");

                                mInterstitialAd = new InterstitialAd(Splash.this);
                                mInterstitialAd.setAdUnitId("ca-app-pub-5077858194293069/7136860128");
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                if (showAd) {
                                    AdListener();
                                }
                            } else if (dataSnapshot.getValue(Integer.class) == 2) {
                                MobileAds.initialize(Splash.this, "ca-app-pub-5134322630248880~5594892098");

                                mInterstitialAd = new InterstitialAd(Splash.this);
                                mInterstitialAd.setAdUnitId("ca-app-pub-5134322630248880/1464075399");
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                if (showAd) {
                                    AdListener();
                                }
                            } else if (dataSnapshot.getValue(Integer.class) == 0) {
                                FirebaseDatabase.getInstance().getReference().child("AdSelect").child("appID")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                MobileAds.initialize(Splash.this, dataSnapshot.getValue(String.class));

                                                mInterstitialAd = new InterstitialAd(Splash.this);

                                                FirebaseDatabase.getInstance().getReference().child("AdSelect")
                                                        .child("interstitialID").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        mInterstitialAd.setAdUnitId(dataSnapshot.getValue(String.class));
                                                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                                        if (showAd) {
                                                            AdListener();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            } else {
                                MobileAds.initialize(Splash.this, "ca-app-pub-5077858194293069~3201484542");

                                mInterstitialAd = new InterstitialAd(Splash.this);
                                mInterstitialAd.setAdUnitId("ca-app-pub-5077858194293069/7136860128");
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                if (showAd) {
                                    AdListener();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        //mInterstitialAd.loadAd(new AdRequest.Builder().build());
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            showAd = true;
        }
        if (!showAd) {
            //showAd = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), Welcome.class));
                    finish();
                }
            }, 2000);
        }

    }

    public void AdListener(){
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                startActivity(new Intent(getApplicationContext(), Welcome.class));
                finish();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                startActivity(new Intent(getApplicationContext(), Welcome.class));
                finish();
            }
        });
    }
}
