package com.adamapps.muvi.TvShow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adamapps.muvi.AdamClickListener;
import com.adamapps.muvi.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ldoublem.loadingviewlib.view.LVBlock;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SeasonDetail extends AppCompatActivity {

    String url = null;
    String tit = null;
    String key = null, image = null;
    String tag = null;
    Elements firstUl, firstLinks, nextLink, thumbLink;
    String Query = "li";
    String LinksQuery = "li a";
    String NextQuery = "a.default_page";
    String ThumbnailQuery = "li>a>img";
    RecyclerView categoryList;
    ArrayList<String> titlesArray = new ArrayList<>();
    ArrayList<String> linksArray = new ArrayList<>();
    ArrayList<String> nextArray = new ArrayList<>();
    ArrayList<String> thumbArray = new ArrayList<>();
    FloatingActionButton nextButton;
    LVBlock lvBlock;
    //String[] colors = {"#D5D5D5", "#A8A5A3", "#E8E2DB"};
    Random random;
    InterstitialAd mInterstitialAd;
    String pin;
    SharedPreferences nextPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season_detail);
        lvBlock = findViewById(R.id.block);

        random = new Random();

        Intent i = getIntent();
        url = i.getStringExtra("link");
        tit = i.getStringExtra("word");
        key = i.getStringExtra("key");
        image = i.getStringExtra("images");
        tag = i.getStringExtra("tag");
        pin = i.getStringExtra("pin");


        nextPref = getApplicationContext().getSharedPreferences("SDetail", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = nextPref.edit();

        FirebaseDatabase.getInstance().getReference().child("AdSelect").child("option")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            if (dataSnapshot.getValue(Integer.class) == 1) {
                                MobileAds.initialize(SeasonDetail.this, "ca-app-pub-5077858194293069~3201484542");

                                mInterstitialAd = new InterstitialAd(SeasonDetail.this);
                                mInterstitialAd.setAdUnitId("ca-app-pub-5077858194293069/7136860128");
                                //mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                AdListener();
                            } else if (dataSnapshot.getValue(Integer.class) == 2) {
                                MobileAds.initialize(SeasonDetail.this, "ca-app-pub-5134322630248880~5594892098");

                                mInterstitialAd = new InterstitialAd(SeasonDetail.this);
                                mInterstitialAd.setAdUnitId("ca-app-pub-5134322630248880/1464075399");
                                //mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                AdListener();
                            } else if (dataSnapshot.getValue(Integer.class) == 0) {
                                FirebaseDatabase.getInstance().getReference().child("AdSelect").child("appID")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                MobileAds.initialize(SeasonDetail.this, dataSnapshot.getValue(String.class));

                                                mInterstitialAd = new InterstitialAd(SeasonDetail.this);

                                                FirebaseDatabase.getInstance().getReference().child("AdSelect")
                                                        .child("interstitialID").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        mInterstitialAd.setAdUnitId(dataSnapshot.getValue(String.class));
                                                        //mInterstitialAd.loadAd(new AdRequest.Builder().build());
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
                                MobileAds.initialize(SeasonDetail.this, "ca-app-pub-5077858194293069~3201484542");

                                mInterstitialAd = new InterstitialAd(SeasonDetail.this);
                                mInterstitialAd.setAdUnitId("ca-app-pub-5077858194293069/7136860128");
                                //mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                AdListener();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        if (nextPref.getString("openTime", null) == null) {
            editor2.putString("openTime", "1");
            editor2.apply();
        }
        if (nextPref.getString("openTime", null) != null && Integer.parseInt(nextPref.getString("openTime", null)) >= 4) {
            editor2.clear();
            editor2.apply();
        }
        if (nextPref.getString("openTime", null) != null && Integer.parseInt(nextPref.getString("openTime", null)) <= 4) {
            int added = Integer.parseInt(nextPref.getString("openTime", null)) + 1;
            editor2.putString("openTime", String.valueOf(added));
            editor2.apply();
        }

        android.support.v7.widget.Toolbar toolbarr = findViewById(R.id.toolbar);
        if (tit != null) {
            toolbarr.setTitle(tit);
        } else {
            toolbarr.setTitle(R.string.app_name);
        }
        toolbarr.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarr);


        categoryList = findViewById(R.id.seasonDetail);
        new MyTask().execute();
        //categoryList.setLayoutManager(new GridLayoutManager(this,4));
        nextButton = findViewById(R.id.next_btn);
        categoryList.setLayoutManager(new GridLayoutManager(this, 2));
    }

    public void AdListener() {

        if (nextPref.getString("openTime", null) == null) {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                if (nextPref.getString("openTime", null) == null)
                    mInterstitialAd.show();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });
    }

    private class MyTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            lvBlock.isShadow(true);
            lvBlock.setViewColor(Color.rgb(245, 209, 22));
            lvBlock.startAnim(5000);
            lvBlock.startAnim();
        }

        @Override
        protected void onPostExecute(Object o) {
            lvBlock.stopAnim();
            lvBlock.setVisibility(View.GONE);
            if (firstUl != null)
                for (Element listItem : firstUl) {
                    if (!TextUtils.isEmpty(String.valueOf(listItem.text()))) {
                        titlesArray.add(String.valueOf(listItem.text()));
                    }
                }
            if (firstLinks != null)
                for (Element links : firstLinks) {
                    if (!TextUtils.isEmpty(String.valueOf(links.text()))) {
                        linksArray.add(String.valueOf(links.attr("href")));
                    }
                }
            if (nextLink != null)
                for (Element nextLinks : nextLink) {
                    if (!TextUtils.isEmpty(String.valueOf(nextLinks.text())) && nextLinks.text().contains("Next")) {
                        nextArray.add(String.valueOf(nextLinks.attr("href")));
                    }
                }
            if (thumbLink != null)
                for (Element thumb : thumbLink) {
                    if (!TextUtils.isEmpty(String.valueOf(thumb.attr("src")))) {
                        if (url.contains("toxicunrated")) {
                            thumbArray.add("http://www.toxicunrated.com" + thumb.attr("src"));
                        } else {
                            thumbArray.add("http://www.toxicwap.com" + thumb.attr("src"));
                        }
                    }
                }
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (nextArray.size() > 0) {
                        Intent i = new Intent(SeasonDetail.this, SeasonDetail.class);
                        if (url.contains("toxicunrated")) {
                            String value = "https://toxicunrated.com" + String.valueOf(nextArray.get(0));
                            i.putExtra("link", value);
                            i.putExtra("word", tit);
                            i.putExtra("tag", tag);
                            i.putExtra("pin", "adam");
                        } else {
                            String value = "https://toxicwap.com" + String.valueOf(nextArray.get(0));
                            i.putExtra("link", value);
                            i.putExtra("word", tit);
                            i.putExtra("tag", tag);
                            i.putExtra("pin", "adam");
                        }
                        startActivity(i);

                    } else {
                        Toast.makeText(SeasonDetail.this, "Sorry That's All", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            categoryList.setAdapter(new SeasonDetailAdapter(titlesArray, linksArray));
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Document doc = Jsoup.connect(url).timeout(0).get();
                firstUl = doc.select(Query);
                firstLinks = doc.select(LinksQuery);
                nextLink = doc.select(NextQuery);
                thumbLink = doc.select(ThumbnailQuery);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return firstUl;
        }

    }

    private class SeasonDetailAdapter extends RecyclerView.Adapter<SeasonHolderHolder> {

        ArrayList<String> words;
        ArrayList<String> links;

        SeasonDetailAdapter(ArrayList<String> words, ArrayList<String> links) {
            this.words = words;
            this.links = links;
        }

        @NonNull
        @Override
        public SeasonHolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(SeasonDetail.this).inflate(R.layout.test_episode_layout, parent, false);
            return new SeasonHolderHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final SeasonHolderHolder holder, int position) {
            if (titlesArray.get(position) != null && position < words.size()) {
                if (words.size() > 13) {
                    holder.showName.setText(words.get(position).substring(0, String.valueOf(words.get(position)).length() - 13));
                }else{
                    holder.showName.setText(words.get(position));
                }
                Picasso.with(getApplicationContext()).load(thumbArray.get(position)).into(holder.testImage);
                //int val = random.nextInt(colors.length);
                holder.testText.setText(String.valueOf(position + 1));
            }
            holder.setAdamClickListener(new AdamClickListener() {
                @Override
                public void onClick(View v, int position, boolean isLongClick) {
                    YoYo.with(Techniques.RubberBand).duration(500).playOn(holder.mView);
                    Intent i = new Intent(SeasonDetail.this, VideoPlayer.class);
                    if (url.contains("toxicunrated")) {
                        String value = "https://toxicunrated.com" + String.valueOf(links.get(position));
                        i.putExtra("link", value);
                        i.putExtra("word", words.get(position));
                        i.putExtra("key", key);
                        i.putExtra("tag", tag);
                        i.putExtra("season", tit);
                        i.putExtra("thumb", image);

                    } else {
                        String value = "https://toxicwap.com" + String.valueOf(links.get(position));
                        i.putExtra("link", value);
                        i.putExtra("word", words.get(position));
                        i.putExtra("key", key);
                        i.putExtra("season", tit);
                        i.putExtra("tag", tag);
                        i.putExtra("thumb", image);
                    }
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return links.size();
        }
    }

    private class SeasonHolderHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView text, testText, showName;
        View mView;
        AdamClickListener adamClickListener;
        CardView cardView;
        ImageView thumbnailView, testImage;

        private SeasonHolderHolder(View itemView) {
            super(itemView);
            mView = itemView;
            text = itemView.findViewById(R.id.letterText);
            cardView = itemView.findViewById(R.id.card);
            thumbnailView = itemView.findViewById(R.id.thumbnail_video);
            testImage = itemView.findViewById(R.id.test_image);
            testText = itemView.findViewById(R.id.test_text);
            showName = itemView.findViewById(R.id.show_name);
            showName.setSelected(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setAdamClickListener(AdamClickListener adamClickListener) {
            this.adamClickListener = adamClickListener;
        }

        @Override
        public void onClick(View view) {
            adamClickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            adamClickListener.onClick(view, getAdapterPosition(), true);
            return true;
        }
    }

}
