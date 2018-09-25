package com.adamapps.showbase.WebPlayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.adamapps.showbase.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.ldoublem.loadingviewlib.view.LVBlazeWood;


public class MovieWebviewNew extends AppCompatActivity {
    RewardedVideoAd mRewardedVideoAd;
    /*final static String unitID = "ca-app-pub-5134322630248880/8959422031";
    final static String appID = "ca-app-pub-5134322630248880~5594892098";*/
    private VideoEnabledWebView webView;
    private VideoEnabledWebChromeClient webChromeClient;
    LVBlazeWood lvWood;
    InterstitialAd interstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_web_view);

        lvWood = findViewById(R.id.lvWood);
        lvWood.startAnim(5000);
        lvWood.startAnim();

        interstitialAd = new InterstitialAd(this,"252221818766145_252222278766099");
        AdSettings.addTestDevice("0165ff96-12fd-4485-9221-0f61e306b990");
        interstitialAd.loadAd();

        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });


        for (int i = 0; i < 2; i++)
            Toast.makeText(this, "Double Tap Videplayer to go fullscreen", Toast.LENGTH_LONG).show();


        // Save the web view
        webView = findViewById(R.id.movie_web_view);

        // Initialize the VideoEnabledWebChromeClient and set event handlers
        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = findViewById(R.id.videoLayout); // Your own view, read class comments
        //noinspection all
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
        {
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int progress) {
                // Your code...
            }



        };
        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
            @Override
            public void toggledFullscreen(boolean fullscreen) {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen) {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    //noinspection all
                    getWindow().getDecorView().setSystemUiVisibility(setSystemUiVisibility());
                } else {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    //noinspection all
                    getWindow().getDecorView().setSystemUiVisibility(setSystemUiVisibility());
                }

            }
        });
        webView.setWebChromeClient(webChromeClient);
        // Call private class InsideWebViewClient
        webView.setWebViewClient(new InsideWebViewClient());
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webView.getSettings().setDomStorageEnabled(true);

        // Navigate anywhere you want, but consider that this classes have only been tested on YouTube's mobile site

        webView.loadUrl(getIntent().getStringExtra("link"));

        enableImmersiveMode(webView);


    }

    private class InsideWebViewClient extends WebViewClient {
        @Override
        // Force links to be opened inside WebView and not in Default Browser
        // Thanks http://stackoverflow.com/a/33681975/1815624
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //True if the host application wants to leave the current WebView and handle the url itself, otherwise return false.
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            lvWood.stopAnim();
            lvWood.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it

        if (webChromeClient.isVideoFullscreen()) {
            if (webView.canGoBack()) {
                webView.goBack();
            }

        } else {
            super.onBackPressed();
        }

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
