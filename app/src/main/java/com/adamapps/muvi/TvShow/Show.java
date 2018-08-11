package com.adamapps.muvi.TvShow;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adamapps.muvi.Movie.Animation;
import com.adamapps.muvi.Movie.HDMovie;
import com.adamapps.muvi.Movie.MovieDetail;
import com.adamapps.muvi.R;
import com.adamapps.muvi.Services.NotificationService;
import com.adamapps.muvi.StartUp.Welcome;
import com.adamapps.muvi.User.About;
import com.adamapps.muvi.User.Favorite;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Show extends AppCompatActivity {

    RecyclerView hdlist;
    com.github.clans.fab.FloatingActionButton nextBtn;
    ArrayList<String> titleArray = new ArrayList<>();
    ArrayList<String> yearArray = new ArrayList<>();
    ArrayList<String> linkArray = new ArrayList<>();
    ArrayList<String> imageArray = new ArrayList<>();
    ArrayList<String> episodeArray = new ArrayList<>();
    ArrayList<String> nextArray = new ArrayList<>();

    ArrayList<String> linkSearchArray = new ArrayList<>();
    ArrayList<String> imageSearchArray = new ArrayList<>();
    ArrayList<String> titleSearchArray = new ArrayList<>();
    ArrayList<String> episodeSearchArray = new ArrayList<>();

    Elements linkElm, imageElm, titleElm, episodeElm;
    Elements linkSearchElm, imageSearchElm, episodeSearchElm;
    Elements nextElement;
    String url = "https://hdonline.eu/tv-series/";
    String searchEndPoint = "https://hdonline.eu/search-query/";

    String titleQuery = "ul.ulclear >div.movies-list>li.movie-item>div.item-detail>h2";
    String linkQuery = "ul.ulclear >div.movies-list>li.movie-item>div.item-detail>h2>a";
    String imageQuery = "ul.ulclear >div.movies-list>li.movie-item>a>img";
    String nextLinkQuery = "ul.pagination>li>a";
    String episodeQuery = "div.gr-eps";

    com.github.clans.fab.FloatingActionButton moviesBtn, recentBtn, favoriteBtn, animationBtn;
    FloatingActionMenu floatMenu;
    MaterialSearchView searchView;
    DatabaseReference updateRef;
    SharedPreferences nextPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        //IntegrationHelper.validateIntegration(this);

        hdlist = findViewById(R.id.hdlist);
        nextBtn = findViewById(R.id.nextBtn);

        searchView = findViewById(R.id.search_view);

        Boolean isServiceRunning = ServiceTools.isServiceRunning(
                Show.this.getApplicationContext(),
                NotificationService.class);

        if (!isServiceRunning) {
            Intent in = new Intent(this, NotificationService.class);
            startService(in);
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Tv Show");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);

        updateRef = FirebaseDatabase.getInstance().getReference("lock/v1,3/close");
        updateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AlertDialog alertDialog = null;
                if (dataSnapshot.getValue(Boolean.class) != null && dataSnapshot.getValue(Boolean.class)) {
                    //finish();
                    AlertDialog.Builder builder = new AlertDialog.Builder(Show.this);
                    View v = getLayoutInflater().inflate(R.layout.update_dialog_layout, null);
                    Button update = v.findViewById(R.id.warning_update_btn);
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adamapps.muvi"));
                            FirebaseDatabase.getInstance().getReference("update/link").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataSnapshot.getValue(String.class)));
                                        startActivity(intent);
                                        YoYo.with(Techniques.RubberBand).duration(400).playOn(v);
                                    } else {
                                        Toast.makeText(Show.this, "No Update Link Found", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    });

                    builder.setView(v);
                    alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
                if (dataSnapshot.getValue(Boolean.class) != null && !dataSnapshot.getValue(Boolean.class)) {
                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        moviesBtn = findViewById(R.id.movies_btn);
        recentBtn = findViewById(R.id.recent_btn);
        favoriteBtn = findViewById(R.id.profile_btn);
        animationBtn = findViewById(R.id.animation_btn);
        floatMenu = findViewById(R.id.fb_menu);

        if (!isTablet(Show.this)) {
            favoriteBtn.setButtonSize(com.github.clans.fab.FloatingActionButton.SIZE_MINI);
            moviesBtn.setButtonSize(com.github.clans.fab.FloatingActionButton.SIZE_MINI);
            recentBtn.setButtonSize(com.github.clans.fab.FloatingActionButton.SIZE_MINI);
            animationBtn.setButtonSize(com.github.clans.fab.FloatingActionButton.SIZE_MINI);
        }

        //Favorite Button
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatMenu.close(true);
                startActivity(new Intent(Show.this, Favorite.class));
            }
        });

        animationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatMenu.close(true);
                startActivity(new Intent(Show.this, Animation.class));
            }
        });


        recentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatMenu.close(true);
                startActivity(new Intent(Show.this, Search.class));
                //Toast.makeText(Show.this, "Coming Soon...", Toast.LENGTH_SHORT).show();
            }
        });
        moviesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Show.this, HDMovie.class));
                floatMenu.close(true);
            }
        });

        Intent i = getIntent();
        if (i.getStringExtra("url") != null) {
            url = i.getStringExtra("url");
        }

        nextPref = getApplicationContext().getSharedPreferences("NextAd", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = nextPref.edit();



        if (isTablet(Show.this)) {
            Display getOrient = getWindowManager().getDefaultDisplay();
            int orientation = Configuration.ORIENTATION_UNDEFINED;
            if (getOrient.getWidth() == getOrient.getHeight()) {
                orientation = Configuration.ORIENTATION_SQUARE;

            } else {
                if (getOrient.getWidth() < getOrient.getHeight()) {
                    orientation = Configuration.ORIENTATION_PORTRAIT;
                    hdlist.setLayoutManager(new MuviGridLayoutManager(getApplicationContext(), 3));
                } else {
                    orientation = Configuration.ORIENTATION_LANDSCAPE;
                    hdlist.setLayoutManager(new MuviGridLayoutManager(getApplicationContext(), 4));
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
                    hdlist.setLayoutManager(new MuviGridLayoutManager(getApplicationContext(), 2));

                } else {
                    orientation = Configuration.ORIENTATION_LANDSCAPE;
                    hdlist.setLayoutManager(new MuviGridLayoutManager(getApplicationContext(), 3));

                }
            }
        }


        new MyTask().execute();

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                nextBtn.setVisibility(View.GONE);
                floatMenu.setVisibility(View.GONE);
            }

            @Override
            public void onSearchViewClosed() {
                imageArray.clear();
                linkArray.clear();
                titleArray.clear();
                new MyTask().execute();
                nextBtn.setVisibility(View.VISIBLE);
                floatMenu.setVisibility(View.VISIBLE);

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

    }


    public class MyTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {

            if (linkElm != null) {
                linkArray.clear();
                titleArray.clear();
                for (Element link : linkElm) {
                    linkArray.add(link.attr("href"));
                    titleArray.add(link.attr("title"));
                }
            }
            if (imageElm != null) {
                imageArray.clear();
                yearArray.clear();
                for (Element image : imageElm) {
                    imageArray.add(image.attr("src"));
                    yearArray.add("2018");
                }
            }
            if (nextElement != null) {
                nextArray.clear();
                for (Element next : nextElement) {
                    nextArray.add(next.attr("href"));
                }
            }
            if (episodeElm != null) {
                episodeArray.clear();
                for (Element eps : episodeElm) {
                    episodeArray.add(eps.text());
                }
            }
            if (nextArray.indexOf("#") < nextArray.size() - 1) {

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        YoYo.with(Techniques.RubberBand).duration(2000).playOn(view);
                        Intent i = new Intent(Show.this, Show.class);
                        i.putExtra("url", nextArray.get(nextArray.indexOf("#") + 1));
                        startActivity(i);
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
                Document doc = Jsoup.connect(url).timeout(0).get();
                linkElm = doc.select(linkQuery);
                imageElm = doc.select(imageQuery);
                titleElm = doc.select(titleQuery);
                episodeElm = doc.select(episodeQuery);
                //backdropElm = doc.selectFirst(backdropQuery);
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
            if (imageSearchElm != null) {
                imageSearchArray.clear();
                for (Element image : imageSearchElm) {
                    imageSearchArray.add(image.attr("src"));
                }
            }
            if (linkSearchElm != null) {
                linkSearchArray.clear();
                titleSearchArray.clear();
                for (Element link : linkSearchElm) {
                    linkSearchArray.add(link.attr("href"));
                    titleSearchArray.add(link.attr("title"));
                }
            }
            if (episodeSearchElm != null) {
                episodeSearchArray.clear();
                for (Element eps : episodeSearchElm) {
                    episodeSearchArray.add(eps.text());
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
                episodeSearchElm = doc.select(episodeQuery);

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
            View v = LayoutInflater.from(Show.this).inflate(R.layout.letter_detail_layout, parent, false);
            return new HDHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull HDHolder holder, final int position) {
            holder.setImage(getApplication(), imageArray.get(position));
            holder.episodeText.setText(episodeArray.get(position));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    YoYo.with(Techniques.RubberBand).duration(500).playOn(v);
                    if (position < titleArray.size()) {
                        Intent i = new Intent(Show.this, MovieDetail.class);
                        i.putExtra("title", titleArray.get(position));
                        i.putExtra("link", linkArray.get(position));
                        i.putExtra("image", imageArray.get(position));
                        floatMenu.close(true);
                        startActivity(i);
                    }
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
        TextView rate, titleView, episodeText;
        View mView;


        HDHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movie_poster);
            rate = itemView.findViewById(R.id.movie_rating);
            titleView = itemView.findViewById(R.id.letterText);
            episodeText = itemView.findViewById(R.id.episode_text);

            episodeText.setVisibility(View.VISIBLE);
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

        HDSearchAdapter(ArrayList<String> imageSearch, ArrayList<String> linkSearch, ArrayList<String> titleSearch) {
            this.imageSearch = imageSearch;
            this.linkSearch = linkSearch;
            this.titleSearch = titleSearch;
        }

        @NonNull
        @Override
        public HDHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(Show.this).inflate(R.layout.letter_detail_layout, parent, false);
            return new HDHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull HDHolder holder, final int position) {

            holder.setImage(Show.this, imageSearch.get(position));
            holder.episodeText.setVisibility(View.GONE);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.RubberBand).duration(500).playOn(v);
                    if (position < imageSearch.size()) {
                        Intent i = new Intent(Show.this, MovieDetail.class);
                        i.putExtra("title", titleSearchArray.get(position));
                        i.putExtra("link", linkSearchArray.get(position));
                        i.putExtra("image", imageSearch.get(position));
                        startActivity(i);
                    }
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
        if (item.getItemId() == R.id.action_about) {
            startActivity(new Intent(this, About.class));
        }
        return true;
    }

    long lastPress;

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPress > 5000 && getIntent().getStringExtra("url") == null) {
                Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG).show();
                lastPress = currentTime;
            } else {
                super.onBackPressed();
            }
            //super.onBackPressed();
        }


    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static class ServiceTools {
        //private String LOG_TAG = ServiceTools.class.getName();

        public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
            final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

            for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
                //Log.d(SyncStateContract.Constants.TAG, String.format("Service:%s", runningServiceInfo.service.getClassName()));
                if (runningServiceInfo.service.getClassName().equals(serviceClass.getName())) {
                    return true;
                }
            }
            return false;
        }
    }

}
