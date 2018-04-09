package com.adamapps.muvi;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebPlayer extends AppCompatActivity {

    WebView webView;
    Element videoUrls;
    String videoQuery = "iframe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_web_player);

        webView = findViewById(R.id.webplayer);
        if(savedInstanceState!=null)
            webView.restoreState(savedInstanceState);

        new VideoTask().execute();

        enableImmersiveMode(webView);

    }

    public class VideoTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
           /* webView.setWebViewClient(new WebViewClient());
            WebViewClient webViewClient = new WebViewClient();*/

            webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //True if the host application wants to leave the current WebView and handle the url itself, otherwise return false.
                    return true;
                }
            });
            WebSettings webSetting = webView.getSettings();
            webSetting.setJavaScriptEnabled(true);
            webView.loadUrl(videoUrls.attr("src"));

            /*if (getIntent().getStringExtra("link") != null)
                Toast.makeText(WebPlayer.this, "Success", Toast.LENGTH_SHORT).show();
            Toast.makeText(WebPlayer.this, "" + videoUrls.attr("src"), Toast.LENGTH_SHORT).show();*/

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                String url = getIntent().getStringExtra("link");
                if (url != null) {
                    Document imageDoc = Jsoup.connect(url).timeout(0).get();
                    videoUrls = imageDoc.selectFirst(videoQuery);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
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
