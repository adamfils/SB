package com.adamapps.muvi;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class VideoPlayer extends AppCompatActivity implements RewardedVideoAdListener {

    EasyVideoPlayer videoPlayer;
    private Document doc;
    String url = null;
    Elements firstLinks;
    Element first4Tv;
    String Query = "source";
    String vidUrl = null, show = null;
    String tit = null, duration;
    String tag = null, type = null, time = null;
    String key = null, season = null, thumb = null;
    ArrayList<String> linksArray = new ArrayList<>();
    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());

        /*if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }*/

        Intent i = getIntent();
        url = i.getStringExtra("link");
        tit = i.getStringExtra("word");
        tag = i.getStringExtra("tag");
        key = i.getStringExtra("key");
        type = i.getStringExtra("type");
        season = i.getStringExtra("season");
        thumb = i.getStringExtra("thumb");
        time = i.getStringExtra("time");
        show = i.getStringExtra("show");
        duration = i.getStringExtra("duration");

        android.support.v7.widget.Toolbar toolbarr = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        if (tit != null) {
            toolbarr.setTitle(tit);
        } else {
            toolbarr.setTitle(R.string.app_name);
        }
        toolbarr.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarr);

        videoPlayer = (EasyVideoPlayer) findViewById(R.id.player);
        videoPlayer.setAutoFullscreen(true);
        // videoPlayer.disableControls();
        videoPlayer.setBottomLabelText(tit);

        if (type != null && type.contains("recent")) {
            videoPlayer.setSource(Uri.parse(url));
            videoPlayer.setCallback(new EasyVideoCallback() {
                @Override
                public void onStarted(EasyVideoPlayer player) {

                }

                @Override
                public void onPaused(EasyVideoPlayer player) {
                    //Toast.makeText(VideoPlayer.this, ""+videoPlayer.getCurrentPosition()/1000, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPreparing(EasyVideoPlayer player) {

                }

                @Override
                public void onPrepared(EasyVideoPlayer player) {
                    if (type != null && type.equals("recent")) {
                        player.seekTo(Integer.parseInt(time));
                    } else {
                        Toast.makeText(VideoPlayer.this, "Nothing To Show", Toast.LENGTH_SHORT).show();
                    }
                    /*if (mRewardedVideoAd.isLoaded()) {
                        mRewardedVideoAd.show();
                    }*/
                    //Toast.makeText(VideoPlayer.this, "Prepared", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onBuffering(int percent) {

                }

                @Override
                public void onError(EasyVideoPlayer player, Exception e) {

                }

                @Override
                public void onCompletion(EasyVideoPlayer player) {

                }

                @Override
                public void onRetry(EasyVideoPlayer player, Uri source) {

                }

                @Override
                public void onSubmit(EasyVideoPlayer player, Uri source) {

                }
            });
        } else {
            new MyTask().execute();
        }
        videoPlayer.setAutoPlay(true);
        videoPlayer.enableControls(true);


    }

    private void loadRewardedVideoAd() {
        if (!mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                    new AdRequest.Builder().build());
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        //mRewardedVideoAd.show();
        //Toast.makeText(this, "Rewarded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        //Toast.makeText(this, "You'll Watch Without Video Controls", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        videoPlayer.setAutoPlay(true);
        videoPlayer.enableControls(true);
    }


    private class MyTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            if (firstLinks != null) {
                for (Element vid : firstLinks) {
                    vidUrl = String.valueOf(vid.attr("src"));
                }
            }

            if (tag != null && tag.equals("toxic")) {
                videoPlayer.setSource(Uri.parse(vidUrl));
            }

            if (tag != null && tag.equals("tv4mobile") && first4Tv != null) {
                videoPlayer.setSource(Uri.parse(first4Tv.attr("href")));
            }

            videoPlayer.setCallback(new EasyVideoCallback() {
                @Override
                public void onStarted(EasyVideoPlayer player) {

                }

                @Override
                public void onPaused(EasyVideoPlayer player) {
                    //Toast.makeText(VideoPlayer.this, ""+videoPlayer.getCurrentPosition()/1000, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPreparing(EasyVideoPlayer player) {

                }

                @Override
                public void onPrepared(EasyVideoPlayer player) {
                    if (type != null && type.equals("recent")) {
                        player.seekTo(Integer.parseInt(time));
                    } else {
                        Toast.makeText(VideoPlayer.this, "Nothing To Show", Toast.LENGTH_SHORT).show();
                    }
                    /*if (mRewardedVideoAd.isLoaded()) {
                        mRewardedVideoAd.show();
                    }*/
                    //Toast.makeText(VideoPlayer.this, "Prepared", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onBuffering(int percent) {

                }

                @Override
                public void onError(EasyVideoPlayer player, Exception e) {

                }

                @Override
                public void onCompletion(EasyVideoPlayer player) {

                }

                @Override
                public void onRetry(EasyVideoPlayer player, Uri source) {

                }

                @Override
                public void onSubmit(EasyVideoPlayer player, Uri source) {

                }
            });
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                doc = Jsoup.connect(url).get();
                if (tag != null && tag.contains("toxic")) {
                    firstLinks = doc.select(Query);
                }
                if (tag != null && tag.equals("tv4mobile")) {
                    first4Tv = doc.selectFirst("div.data a");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return firstLinks;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        /*if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            // only for gingerbread and newer versions
            videoPlayer.pause();
        }
        videoPlayer.setSaveEnabled(true);*/
        videoPlayer.pause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayer.release();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Toast.makeText(this, ""+videoPlayer.getCurrentPosition(), Toast.LENGTH_SHORT).show();
        HashMap<String, Object> map = new HashMap<>();
        if (videoPlayer.getCurrentPosition() < videoPlayer.getDuration()) {
            map.put("time", (videoPlayer.getCurrentPosition()));
        } else {
            map.put("time", (Long.parseLong("1000")));
        }
        if (key != null)
            map.put("show", key);
        if (season != null)
            map.put("season", season);
        if (tit != null)
            map.put("episode", tit);
        if (thumb != null)
            map.put("thumbnail", thumb);
        if (vidUrl != null)
            map.put("link", vidUrl);
        if (tag != null)
            map.put("tag", tag);
        if (duration != null)
            map.put("duration", videoPlayer.getDuration());
        
        if (type != null && type.contains("recent")) {
            FirebaseDatabase.getInstance().getReference().child("Recent")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(show).updateChildren(map);
        } else {
            FirebaseDatabase.getInstance().getReference().child("Recent")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(key).updateChildren(map);
        }
    }

}
