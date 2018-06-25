package com.adamapps.muvi.Movie;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.adamapps.muvi.R;
import com.adamapps.muvi.TvShow.SeasonDetail;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ldoublem.loadingviewlib.view.LVBlazeWood;

public class MovieWebView extends AppCompatActivity {

    private RewardedVideoAd mRewardedVideoAd;
    WebView mWebView;
    LVBlazeWood lvWood;
    final static String unitID = "ca-app-pub-5134322630248880/8959422031";
    final static String appID = "ca-app-pub-5134322630248880~5594892098";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_web_view);

        mWebView = findViewById(R.id.movie_web_view);
        lvWood = findViewById(R.id.lvWood);
        lvWood.startAnim(5000);
        lvWood.startAnim();


        FirebaseDatabase.getInstance().getReference().child("AdSelect").child("option")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            if (dataSnapshot.getValue(Integer.class) == 1) {
                                MobileAds.initialize(MovieWebView.this, "ca-app-pub-5077858194293069~3201484542");

                                MobileAds.initialize(MovieWebView.this, "ca-app-pub-5077858194293069~3201484542");

                                mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(MovieWebView.this);

                                mRewardedVideoAd.loadAd("ca-app-pub-5077858194293069/8200910185",
                                        new AdRequest.Builder().build());
                                AdListener();
                            } else if (dataSnapshot.getValue(Integer.class) == 2) {
                                MobileAds.initialize(MovieWebView.this, appID);

                                mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(MovieWebView.this);

                                mRewardedVideoAd.loadAd(unitID,
                                        new AdRequest.Builder().build());
                                AdListener();
                            } else if (dataSnapshot.getValue(Integer.class) == 0) {
                                FirebaseDatabase.getInstance().getReference().child("AdSelect").child("appID")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                MobileAds.initialize(MovieWebView.this, dataSnapshot.getValue(String.class));

                                                mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(MovieWebView.this);

                                                FirebaseDatabase.getInstance().getReference().child("AdSelect")
                                                        .child("videoID").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        mRewardedVideoAd.loadAd(dataSnapshot.getValue(String.class),
                                                                new AdRequest.Builder().build());
                                                        AdListener();
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
                                MobileAds.initialize(MovieWebView.this, "ca-app-pub-5077858194293069~3201484542");

                                mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(MovieWebView.this);

                                mRewardedVideoAd.loadAd("ca-app-pub-5077858194293069/8200910185",
                                        new AdRequest.Builder().build());
                                AdListener();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



        WebSettings mSettings = mWebView.getSettings();
        mSettings.setSupportMultipleWindows(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mSettings.setSafeBrowsingEnabled(true);
        }
        //setDesktopMode(mWebView,true);
        mWebView.loadUrl(getIntent().getStringExtra("link"));
        //mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //True if the host application wants to leave the current WebView and handle the url itself, otherwise return false.
                return true;
            }
        });
        mSettings.setJavaScriptEnabled(true);
        //mSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mSettings.setDomStorageEnabled(true);
        mSettings.setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);

        enableImmersiveMode(mWebView);


    }

    private void AdListener() {
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                mRewardedVideoAd.show();
                lvWood.setVisibility(View.GONE);
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                lvWood.setVisibility(View.GONE);
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                mWebView.loadUrl(getIntent().getStringExtra("link"));
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                mWebView.loadUrl(getIntent().getStringExtra("link"));
                lvWood.setVisibility(View.GONE);
            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });
    }

    public static void enableImmersiveMode(final View decorView) {
        decorView.setSystemUiVisibility(setSystemUiVisibility());
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(setSystemUiVisibility());
                }
            }
        });
    }

    public static int setSystemUiVisibility() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    }


}
