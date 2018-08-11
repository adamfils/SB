package com.adamapps.muvi.Movie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamapps.muvi.R;
import com.adamapps.muvi.StartUp.Welcome;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class HDMovie extends AppCompatActivity {

    RecyclerView hdlist;
    FloatingActionButton nextBtn;
    ArrayList<String> titleArray = new ArrayList<>();
    ArrayList<String> yearArray = new ArrayList<>();
    ArrayList<String> linkArray = new ArrayList<>();
    ArrayList<String> imageArray = new ArrayList<>();
    ArrayList<String> nextArray = new ArrayList<>();

    ArrayList<String> linkSearchArray = new ArrayList<>();
    ArrayList<String> imageSearchArray = new ArrayList<>();
    ArrayList<String> titleSearchArray = new ArrayList<>();

    private Document doc;
    Elements linkElm, imageElm, titleElm;
    Elements linkSearchElm, imageSearchElm, titleSearchElm;
    Elements nextElement;
    String url = "https://hdonline.eu/movies/";
    String searchEndPoint = "https://hdonline.eu/search-query/";

    String titleQuery = "ul.ulclear >div.movies-list>li.movie-item>div.item-detail>h2";
    String linkQuery = "ul.ulclear >div.movies-list>li.movie-item>div.item-detail>h2>a";
    String imageQuery = "ul.ulclear >div.movies-list>li.movie-item>a>img";
    String nextLinkQuery = "ul.pagination>li>a";
    MaterialSearchView searchView;
    SharedPreferences nextPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hdmovie);

        hdlist = findViewById(R.id.hdlist);
        nextBtn = findViewById(R.id.nextBtn);

        searchView = findViewById(R.id.search_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Movies");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        if (i.getStringExtra("url") != null) {
            url = i.getStringExtra("url");
        }

        nextPref = getApplicationContext().getSharedPreferences("NextAd", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = nextPref.edit();

        /*FirebaseDatabase.getInstance().getReference().child("AdSelect").child("option")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            if (dataSnapshot.getValue(Integer.class) == 1) {
                                MobileAds.initialize(HDMovie.this, "ca-app-pub-5077858194293069~3201484542");

                                mInterstitialAd = new InterstitialAd(HDMovie.this);
                                mInterstitialAd.setAdUnitId("ca-app-pub-5077858194293069/7136860128");
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                AdListener();
                            } else if (dataSnapshot.getValue(Integer.class) == 2) {
                                MobileAds.initialize(HDMovie.this, "ca-app-pub-5134322630248880~5594892098");

                                mInterstitialAd = new InterstitialAd(HDMovie.this);
                                mInterstitialAd.setAdUnitId("ca-app-pub-5134322630248880/1464075399");
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                AdListener();
                            } else if (dataSnapshot.getValue(Integer.class) == 0) {
                                FirebaseDatabase.getInstance().getReference().child("AdSelect").child("appID")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                MobileAds.initialize(HDMovie.this, dataSnapshot.getValue(String.class));

                                                mInterstitialAd = new InterstitialAd(HDMovie.this);

                                                FirebaseDatabase.getInstance().getReference().child("AdSelect")
                                                        .child("interstitialID").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        mInterstitialAd.setAdUnitId(dataSnapshot.getValue(String.class));
                                                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
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
                                MobileAds.initialize(HDMovie.this, "ca-app-pub-5077858194293069~3201484542");

                                mInterstitialAd = new InterstitialAd(HDMovie.this);
                                mInterstitialAd.setAdUnitId("ca-app-pub-5077858194293069/7136860128");
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                AdListener();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/


        if (isTablet(HDMovie.this)) {
            Display getOrient = getWindowManager().getDefaultDisplay();
            int orientation = Configuration.ORIENTATION_UNDEFINED;
            if (getOrient.getWidth() == getOrient.getHeight()) {
                orientation = Configuration.ORIENTATION_SQUARE;

            } else {
                if (getOrient.getWidth() < getOrient.getHeight()) {
                    orientation = Configuration.ORIENTATION_PORTRAIT;
                    hdlist.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                } else {
                    orientation = Configuration.ORIENTATION_LANDSCAPE;
                    hdlist.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
                }
            }


        } else {
            Display getOrient = getWindowManager().getDefaultDisplay();
            int orientation = Configuration.ORIENTATION_UNDEFINED;
            if (getOrient.getWidth() == getOrient.getHeight()) {
                orientation = Configuration.ORIENTATION_SQUARE;
            } else {
                if (getOrient.getWidth() < getOrient.getHeight()) {
                    orientation = Configuration.ORIENTATION_PORTRAIT;
                    hdlist.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

                } else {
                    orientation = Configuration.ORIENTATION_LANDSCAPE;
                    hdlist.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

                }
            }
        }


        new MyTask().execute();

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                nextBtn.setVisibility(View.GONE);
            }

            @Override
            public void onSearchViewClosed() {
                imageArray.clear();
                linkArray.clear();
                titleArray.clear();
                new MyTask().execute();
                nextBtn.setVisibility(View.VISIBLE);
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                imageSearchArray.clear();
                titleSearchArray.clear();
                linkSearchArray.clear();
                new SearchTask(query).execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        if (nextPref.getString("openTime", null) == null) {
            editor2.putString("openTime", "1");
            editor2.apply();
        }
        if (nextPref.getString("openTime", null) != null && Integer.parseInt(nextPref.getString("openTime", null)) >= 5) {
            editor2.clear();
            editor2.apply();
        }
        if (nextPref.getString("openTime", null) != null && Integer.parseInt(nextPref.getString("openTime", null)) <= 5) {
            int added = Integer.parseInt(nextPref.getString("openTime", null)) + 1;
            editor2.putString("openTime", String.valueOf(added));
            editor2.apply();
        }



    }



    public class MyTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {

            if (linkElm != null) {
                for (Element link : linkElm) {
                    linkArray.add(link.attr("href"));
                    titleArray.add(link.attr("title"));
                }
            }
            if (imageElm != null) {
                for (Element image : imageElm) {
                    imageArray.add(image.attr("src"));
                    yearArray.add("2018");
                }
            }
            if (nextElement != null) {
                for (Element next : nextElement) {
                    nextArray.add(next.attr("href"));
                }
            }
            if (nextArray.indexOf("#") < nextArray.size() - 1) {

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        YoYo.with(Techniques.RubberBand).duration(2000).playOn(view);
                        Intent i = new Intent(getApplicationContext(), HDMovie.class);
                        i.putExtra("url", nextArray.get(nextArray.indexOf("#") + 1));
                        startActivity(i);

                        //Toast.makeText(HDMovie.this, ""
                        //       + nextArray.get(nextArray.indexOf("#") + 1), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                YoYo.with(Techniques.Shake).duration(3000).playOn(nextBtn);
            }
            //Toast.makeText(HDMovie.this, "" + titleArray.size(), Toast.LENGTH_SHORT).show();
            hdlist.setAdapter(new HDAdapter());
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                doc = Jsoup.connect(url).timeout(0).get();
                linkElm = doc.select(linkQuery);
                imageElm = doc.select(imageQuery);
                titleElm = doc.select(titleQuery);
                nextElement = doc.select(nextLinkQuery);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return linkElm;
        }
    }

    public class SearchTask extends AsyncTask {
        String query;

        public SearchTask(String query) {
            this.query = query;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            if (searchView.hasFocus()) {
                searchView.clearFocus();
            }
            if (linkSearchElm != null) {
                for (Element image : imageSearchElm) {
                    imageSearchArray.add(image.attr("src"));
                }
            }
            if (linkSearchElm != null) {
                for (Element link : linkSearchElm) {
                    linkSearchArray.add(link.attr("href"));
                    titleSearchArray.add(link.attr("title"));
                }
            }

            hdlist.setAdapter(new HDSearchAdapter(imageSearchArray, linkSearchArray, titleSearchArray));
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String modText = query.replaceAll(" ", "+");
            try {
                Document doc = Jsoup.connect(searchEndPoint + modText + "/").timeout(0).get();
                linkSearchElm = doc.select(linkQuery);
                imageSearchElm = doc.select(imageQuery);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public class HDAdapter extends RecyclerView.Adapter<HDHolder> {
        @NonNull
        @Override
        public HDHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.letter_detail_layout, parent, false);
            return new HDHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull HDHolder holder, final int position) {
            //if (imageArray != null && position < imageArray.size())
            holder.setImage(getApplication(), imageArray.get(position));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.RubberBand).duration(500).playOn(v);
                    Intent i = new Intent(HDMovie.this, MovieDetail.class);
                    i.putExtra("title", titleArray.get(position));
                    i.putExtra("link", linkArray.get(position));
                    i.putExtra("image", imageArray.get(position));
                    startActivity(i);
                }
            });

        }

        @Override
        public int getItemCount() {
            return titleArray.size();
        }
    }

    public class HDHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView rate, titleView;
        View mView;

        public HDHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movie_poster);
            rate = itemView.findViewById(R.id.movie_rating);
            titleView = itemView.findViewById(R.id.letterText);
            titleView.setSelected(true);
            mView = itemView;
        }

        void setImage(final Context c, final String image) {
            Picasso.with(c).load(image).error(R.drawable.noimage).placeholder(R.drawable.noimage).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
        }
    }

    public class HDSearchAdapter extends RecyclerView.Adapter<HDHolder> {

        ArrayList<String> imageSearch;
        ArrayList<String> linkSearch;
        ArrayList<String> titleSearch;

        public HDSearchAdapter(ArrayList<String> imageSearch, ArrayList<String> linkSearch, ArrayList<String> titleSearch) {
            this.imageSearch = imageSearch;
            this.linkSearch = linkSearch;
            this.titleSearch = titleSearch;
        }

        @NonNull
        @Override
        public HDHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(HDMovie.this).inflate(R.layout.letter_detail_layout, parent, false);
            return new HDHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull HDHolder holder, final int position) {

            holder.setImage(HDMovie.this, imageSearch.get(position));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.RubberBand).duration(500).playOn(v);
                    Intent i = new Intent(HDMovie.this, MovieDetail.class);
                    if (titleSearchArray.size() < position)
                        i.putExtra("title", titleSearchArray.get(position));
                    if (linkSearchArray.size() < position)
                        i.putExtra("link", linkSearchArray.get(position));
                    if (imageSearch.size() < position)
                        i.putExtra("image", imageSearch.get(position));
                    startActivity(i);
                }
            });

        }

        @Override
        public int getItemCount() {
            return imageSearch.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, Welcome.class));
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}
