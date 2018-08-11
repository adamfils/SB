package com.adamapps.muvi.Movie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adamapps.muvi.R;
import com.adamapps.muvi.WebPlayer.MovieWebviewNew;
import com.crashlytics.android.Crashlytics;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MovieDetail extends AppCompatActivity {

    String descQuery = "div.dci-desc";
    String durationYearQuery = "div.dci-spe-right>div";
    String genreQuery = "div.dci-spe-left>div";
    String backdropQuery = "div.grayscale";

    TextView titleText, qualityText, ratingText, timeText, descText, yearText, imdbText;
    KenBurnsView burnsView;
    String title;
    String link;
    TextView[] leftTextArray = null;
    TextView[] rightTextArray = null;
    KenBurnsView backBlur;
    LikeButton favoriteButton;

    Elements durationElm, descElm, genreElm;
    Element backdropElm;

    ArrayList<String> genreList = new ArrayList<>();
    ArrayList<String> actorList = new ArrayList<>();
    ArrayList<String> directorList = new ArrayList<>();
    ArrayList<String> countryList = new ArrayList<>();


    ArrayList<String> durationGeneralList = new ArrayList<>();
    ArrayList<String> genreGeneralList = new ArrayList<>();
    InterstitialAd mInterstitialAd;
    String image;
    FirebaseAnalytics firebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        titleText = findViewById(R.id.title_movie);
        qualityText = findViewById(R.id.movie_quality);
        ratingText = findViewById(R.id.movie_rating);
        timeText = findViewById(R.id.movie_time);
        yearText = findViewById(R.id.movie_year);
        descText = findViewById(R.id.movie_desc);
        burnsView = findViewById(R.id.backdrop_movie);
        imdbText = findViewById(R.id.movie_imdb);
        backBlur = findViewById(R.id.back_blur);
        favoriteButton = findViewById(R.id.favourite);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);


        rightTextArray = new TextView[]{timeText, qualityText, yearText, imdbText};
        leftTextArray = new TextView[]{};

        MobileAds.initialize(this, "ca-app-pub-5077858194293069~3201484542");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5077858194293069/7136860128");

        //mInterstitialAd.loadAd(new AdRequest.Builder().build());

        YoYo.with(Techniques.Shake).duration(2000).playOn(burnsView);
        Intent i = getIntent();
        image = i.getStringExtra("image");
        title = i.getStringExtra("title");
        link = i.getStringExtra("link");


        FirebaseDatabase.getInstance().getReference().child("Favorite")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (title != null && dataSnapshot!=null) {
                    if (dataSnapshot.hasChild(title.replaceAll("[\\[.#$\\]]", ""))) {
                        favoriteButton.setLiked(true);
                    } else {
                        favoriteButton.setLiked(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Crashlytics.setString("title", title);
        Crashlytics.setString("link", link);

        burnsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), MovieWebviewNew.class);
                Bundle params = new Bundle();
                if(image!=null&&title!=null&&link!=null) {
                    params.putString("movie_image", image);
                    params.putString("movie_title", title);
                    params.putString("movie_link", link);
                    params.putString("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    params.putString("email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    firebaseAnalytics.logEvent("viewed_movies", params);
                }
                if (link != null)
                    i.putExtra("link", link);
                startActivity(i);
            }
        });
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MovieDAd", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();


        if (pref.getString("openTime", null) == null) {
            editor.putString("openTime", "1");
            editor.apply();
        }
        if (pref.getString("openTime", null) != null && Integer.parseInt(pref.getString("openTime", null)) >= 3) {
            editor.clear();
            editor.apply();
        }
        if (pref.getString("openTime", null) != null && Integer.parseInt(pref.getString("openTime", null)) <= 3) {
            int added = Integer.parseInt(pref.getString("openTime", null)) + 1;
            editor.putString("openTime", String.valueOf(added));
            editor.apply();
        }



        if (title != null)
            titleText.setText(title);
        if (image != null) {
            Picasso.with(getApplicationContext()).load(image).error(R.drawable.noimage).into(backBlur);
        }
        //Toast.makeText(this, "Title = "+link, Toast.LENGTH_SHORT).show();
        new DetailTask().execute();
        favoriteButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                //String img = backdropElm.attr("style").substring(23, backdropElm.attr("style").length() - 3);

                HashMap<String, Object> map = new HashMap<>();
                if (link != null)
                    map.put("link", link);
                if (image != null)
                    map.put("image", image);
                if (title != null ) {
                    FirebaseDatabase.getInstance().getReference().child("Favorite")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(title.replaceAll("[\\[.#$\\]]", ""))
                            .updateChildren(map);
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if (title != null) {
                    FirebaseDatabase.getInstance().getReference().child("Favorite")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(title.replaceAll("[\\[.#$\\]]", ""))
                            .removeValue();
                }
            }
        });

    }

    public void CardClick(View view) {
        YoYo.with(Techniques.RubberBand).duration(500).playOn(view);
    }

    public class DetailTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            if (backdropElm != null) {
                final String imageUrl = backdropElm.attr("style").substring(23, backdropElm.attr("style").length() - 3);
                Picasso.with(getApplicationContext()).load(imageUrl).error(R.drawable.noimage).into(burnsView);
            }

            if (durationElm != null) {
                for (Element dura : durationElm) {
                    durationGeneralList.add(dura.text());
                }
            }
            if (genreElm != null) {
                for (Element gene : genreElm) {
                    genreGeneralList.add(gene.text());
                }
            }

            for (int i = 0; i < durationGeneralList.size(); i++) {
                rightTextArray[i].setText(durationGeneralList.get(i));
                if (i % 2 == 0) {
                    rightTextArray[i].setTextColor(Color.parseColor("#c0c0c0"));
                }
            }

            if (descElm != null) {
                for (Element des : descElm) {
                    descText.setText(des.text());
                }
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                if (title != null && !title.isEmpty()) {
                    Document doc = Jsoup.connect(link).timeout(0).get();
                    durationElm = doc.select(durationYearQuery);
                    genreElm = doc.select(genreQuery);
                    descElm = doc.select(descQuery);
                    backdropElm = doc.selectFirst(backdropQuery);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

}
