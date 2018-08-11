package com.adamapps.muvi.TvShow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.adamapps.muvi.R;
import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;

public class VideoPlayer extends AppCompatActivity {

    EasyVideoPlayer videoPlayer;
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
    AudioManager am;
    InterstitialAd mInterstitialAd;
    final static String unitID = "ca-app-pub-5134322630248880/8959422031";
    final static String appID = "ca-app-pub-5134322630248880~5594892098";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Fabric.with(this, new Crashlytics());
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, getResources().getString(R.string.ad_app_id));

        // Use an activity context to get the rewarded video instance.

        FirebaseDatabase.getInstance().getReference().child("AdSelect").child("option")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            if (dataSnapshot.getValue(Integer.class) == 1) {
                                MobileAds.initialize(VideoPlayer.this, "ca-app-pub-5077858194293069~3201484542");

                                MobileAds.initialize(VideoPlayer.this, "ca-app-pub-5077858194293069~3201484542");

                                mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(VideoPlayer.this);

                                mRewardedVideoAd.loadAd("ca-app-pub-5077858194293069/8200910185",
                                        new AdRequest.Builder().build());
                                AdListener();
                            } else if (dataSnapshot.getValue(Integer.class) == 2) {
                                MobileAds.initialize(VideoPlayer.this, appID);

                                mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(VideoPlayer.this);

                                mRewardedVideoAd.loadAd(unitID,
                                        new AdRequest.Builder().build());
                                AdListener();
                            } else if (dataSnapshot.getValue(Integer.class) == 0) {
                                FirebaseDatabase.getInstance().getReference().child("AdSelect").child("appID")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                MobileAds.initialize(VideoPlayer.this, dataSnapshot.getValue(String.class));

                                                mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(VideoPlayer.this);

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
                                MobileAds.initialize(VideoPlayer.this, "ca-app-pub-5077858194293069~3201484542");

                                mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(VideoPlayer.this);

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

        /*if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }*/

        //MobileAds.initialize(this, getResources().getString(R.string.ad_app_id));

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.ad_interstitial_unit_id));

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

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

        android.support.v7.widget.Toolbar toolbarr = findViewById(R.id.toolbar);
        if (tit != null) {
            toolbarr.setTitle(tit);
        } else {
            toolbarr.setTitle(R.string.app_name);
        }
        toolbarr.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarr);

        videoPlayer = findViewById(R.id.player);
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
                    }
                    if (mRewardedVideoAd.isLoaded()) {
                        mRewardedVideoAd.show();
                    }

                    assert am != null;
                    int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                    final Object mFocusLock = new Object();
                    synchronized (mFocusLock) {
                        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                            player.setAutoPlay(true);
                        }
                    }
                    //Toast.makeText(VideoPlayer.this, "Prepared", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onBuffering(int percent) {

                }

                @Override
                public void onError(EasyVideoPlayer player, Exception e) {
                    Toast.makeText(VideoPlayer.this, ""+e.getCause(), Toast.LENGTH_SHORT).show();
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
        if (videoPlayer.isPlaying()) {
            int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            final Object mFocusLock = new Object();
            synchronized (mFocusLock) {
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    videoPlayer.setAutoPlay(true);
                }
            }
        }


    }

    public void AdListener(){
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                videoPlayer.setAutoPlay(true);
                videoPlayer.enableControls(true);
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

            @Override
            public void onRewardedVideoCompleted() {

            }
        });
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
                    vidUrl = vid.attr("src");
                }
            }

            Toast.makeText(VideoPlayer.this, "Vid link = "+vidUrl, Toast.LENGTH_SHORT).show();
            if (tag != null && tag.equals("toxic")&& vidUrl!=null) {
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
                    if (type != null && type.equals("recent") && time!=null) {
                        player.seekTo(Integer.parseInt(time));
                    }
                    if (mRewardedVideoAd.isLoaded()) {
                        mRewardedVideoAd.show();
                    }
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
                Document doc = Jsoup.connect(url).get();
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
        //mInterstitialAd.loadAd(new AdRequest.Builder().build());
        videoPlayer.pause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayer.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(this, ""+videoPlayer.getCurrentPosition(), Toast.LENGTH_SHORT).show();
        if (!videoPlayer.isPrepared()) {
            super.onBackPressed();
            return;
        }


        HashMap<String, Object> map = new HashMap<>();
        if (videoPlayer.getCurrentPosition() < videoPlayer.getDuration()) {
            map.put("time", (videoPlayer.getCurrentPosition() - 3000));
        } else {
            map.put("time", (Long.parseLong("1000")));
        }
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
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

        if (show != null && type != null && type.contains("recent")) {
            FirebaseDatabase.getInstance().getReference().child("Recent")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(show).updateChildren(map);
        }
        if (key != null)
            FirebaseDatabase.getInstance().getReference().child("Recent")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(key).updateChildren(map);

        super.onBackPressed();
    }


}
