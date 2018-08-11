package com.adamapps.muvi.TvShow;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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

import com.adamapps.muvi.StartUp.Welcome;
import com.adamapps.muvi.Tests.LetterDetail;
import com.adamapps.muvi.TvShowModels.ShowModel;
import com.adamapps.muvi.User.Recent;
import com.adamapps.muvi.Movie.HDMovie;
import com.adamapps.muvi.R;
import com.adamapps.muvi.User.Favorite;
import com.crashlytics.android.Crashlytics;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class Search extends AppCompatActivity {

    InterstitialAd mInterstitialAd;

    MaterialSearchView searchView;
    RecyclerView showList;
    Query query;
    DatabaseReference reference;

    ArrayList<String> smackArray = new ArrayList<>();
    ArrayList<String> wweArray = new ArrayList<>();
    ArrayList<String> results = new ArrayList<>();

    ArrayList<String> imageSearch = new ArrayList<>();
    ArrayList<String> descSearch = new ArrayList<>();
    ArrayList<String> linkSearch = new ArrayList<>();
    ArrayList<String> ratingSearch = new ArrayList<>();
    ArrayList<String> titleSearch = new ArrayList<>();
    FloatingActionButton moviesBtn, recentBtn, profileBtn;
    FloatingActionMenu floatMenu;
    private ArrayList<String> tagSearch = new ArrayList<>();
    DatabaseReference updateRef;
    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Fabric.with(this, new Crashlytics());
        Crashlytics.setUserIdentifier(FirebaseAuth.getInstance().getCurrentUser().getUid());


        updateRef = FirebaseDatabase.getInstance().getReference("lock/v1,2/close");
        updateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AlertDialog alertDialog = null;
                if (dataSnapshot.getValue(Boolean.class) != null && dataSnapshot.getValue(Boolean.class)) {
                    //finish();
                    AlertDialog.Builder builder = new AlertDialog.Builder(Search.this);
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
                                        Toast.makeText(Search.this, "No Update Link Found", Toast.LENGTH_LONG).show();
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

        FirebaseDatabase.getInstance().getReference().child("AdSelect").child("option")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            if (dataSnapshot.getValue(Integer.class) == 1) {
                                MobileAds.initialize(Search.this, "ca-app-pub-5077858194293069~3201484542");

                                mInterstitialAd = new InterstitialAd(Search.this);
                                mInterstitialAd.setAdUnitId("ca-app-pub-5077858194293069/7136860128");
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            } else if (dataSnapshot.getValue(Integer.class) == 2) {
                                MobileAds.initialize(Search.this, "ca-app-pub-5134322630248880~5594892098");

                                mInterstitialAd = new InterstitialAd(Search.this);
                                mInterstitialAd.setAdUnitId("ca-app-pub-5134322630248880/1464075399");
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            } else if (dataSnapshot.getValue(Integer.class) == 0) {
                                FirebaseDatabase.getInstance().getReference().child("AdSelect").child("appID")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                MobileAds.initialize(Search.this, dataSnapshot.getValue(String.class));

                                                mInterstitialAd = new InterstitialAd(Search.this);

                                                FirebaseDatabase.getInstance().getReference().child("AdSelect")
                                                        .child("interstitialID").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        mInterstitialAd.setAdUnitId(dataSnapshot.getValue(String.class));
                                                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
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
                                MobileAds.initialize(Search.this, "ca-app-pub-5077858194293069~3201484542");

                                mInterstitialAd = new InterstitialAd(Search.this);
                                mInterstitialAd.setAdUnitId("ca-app-pub-5077858194293069/7136860128");
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Tv Shows");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        showList = findViewById(R.id.showList);
        showList.setNestedScrollingEnabled(false);
        reference = FirebaseDatabase.getInstance().getReference().child("Media");

        moviesBtn = findViewById(R.id.movies_btn);
        recentBtn = findViewById(R.id.recent_btn);
        profileBtn = findViewById(R.id.profile_btn);
        floatMenu = findViewById(R.id.fb_menu);

        if (!isTablet(Search.this)) {
            profileBtn.setButtonSize(FloatingActionButton.SIZE_MINI);
            moviesBtn.setButtonSize(FloatingActionButton.SIZE_MINI);
            recentBtn.setButtonSize(FloatingActionButton.SIZE_MINI);
        }

        searchView = findViewById(R.id.search_view);


        queryCall(reference);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatMenu.close(true);
                startActivity(new Intent(Search.this, Favorite.class));
            }
        });


        recentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatMenu.close(true);
                startActivity(new Intent(Search.this, Recent.class));
            }
        });
        moviesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Search.this, HDMovie.class));
                floatMenu.close(true);
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                floatMenu.close(true);
                floatMenu.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSearchViewClosed() {
                floatMenu.setVisibility(View.VISIBLE);
                queryCall(reference);
                adapter.startListening();
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String queryText) {

                if (searchView.isSearchOpen()) {
                    floatMenu.close(true);
                }

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        results.clear();
                        imageSearch.clear();
                        linkSearch.clear();
                        descSearch.clear();
                        ratingSearch.clear();
                        titleSearch.clear();
                        tagSearch.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.child("nameSearch").getValue(String.class) != null && ds.child("nameSearch").getValue(String.class).contains(queryText.toLowerCase())) {
                                searchResults(ds.getKey());
                            }
                        }
                        if (isTablet(Search.this)) {
                            Display getOrient = getWindowManager().getDefaultDisplay();
                            int orientation = Configuration.ORIENTATION_UNDEFINED;
                            if (getOrient.getWidth() == getOrient.getHeight()) {
                                orientation = Configuration.ORIENTATION_SQUARE;

                            } else {
                                if (getOrient.getWidth() < getOrient.getHeight()) {
                                    orientation = Configuration.ORIENTATION_PORTRAIT;
                                    showList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                                } else {
                                    orientation = Configuration.ORIENTATION_LANDSCAPE;
                                    showList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
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
                                    showList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

                                } else {
                                    orientation = Configuration.ORIENTATION_LANDSCAPE;
                                    showList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

                                }
                            }
                        }
                        showList.setAdapter(new SearchAdapter());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String queryText) {
                //Do some magic

                if (searchView.isSearchOpen()) {
                    floatMenu.close(true);
                }
                if (queryText != null && !queryText.isEmpty()) {
                    query = reference.orderByChild("nameSearch").startAt(queryText.toLowerCase());
                    queryCall(query);
                    adapter.startListening();
                }
                return true;
            }
        });

    }

    private void searchResults(String childNode) {

        DatabaseReference newRef = reference.child(childNode);
        newRef.child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null) {
                    imageSearch.add(dataSnapshot.getValue(String.class));
                } else {
                    imageSearch.add("test.com");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        newRef.child("link").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null) {
                    linkSearch.add(dataSnapshot.getValue(String.class));
                } else {
                    linkSearch.add("0.0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        newRef.child("desc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null) {
                    descSearch.add(dataSnapshot.getValue(String.class));
                } else {
                    descSearch.add("No Description yet");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        newRef.child("rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null) {
                    ratingSearch.add(dataSnapshot.getValue(String.class));
                } else {
                    ratingSearch.add("0.0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        newRef.child("tag").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null) {
                    tagSearch.add(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        titleSearch.add(childNode);

    }

    public class SearchAdapter extends RecyclerView.Adapter<ShowHolder> {

        @Override
        public ShowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(Search.this).inflate(R.layout.letter_detail_layout, parent, false);
            return new ShowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ShowHolder holder, final int position) {
            if (position < titleSearch.size())
                if (!titleSearch.get(position).equals("Next Page") && !titleSearch.get(position).equals("Next")) {
                    holder.setImage(Search.this, imageSearch.get(position));
                    holder.setTitle(titleSearch.get(position));
                /*if (titleSearch.size() == ratingSearch.size())
                    holder.setRating(ratingSearch.get(position));*/
                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            YoYo.with(Techniques.RubberBand).duration(500).playOn(holder.mView);
                            if (tagSearch.get(position).contains("tv4mobile")) {
                                Intent i = new Intent(Search.this, SeasonActivityTv4.class);
                                i.putExtra("link", linkSearch.get(position));
                                i.putExtra("desc", descSearch.get(position));
                                i.putExtra("word", titleSearch.get(position));
                                i.putExtra("image", imageSearch.get(position));
                                i.putExtra("tag", tagSearch.get(position));
                                //Toast.makeText(Search.this, "Tag  Search = " + tagSearch.get(position), Toast.LENGTH_SHORT).show();
                                floatMenu.close(true);
                                startActivity(i);
                            } else {
                                Intent i = new Intent(Search.this, SeasonsActivity.class);
                                i.putExtra("link", linkSearch.get(position));
                                i.putExtra("desc", descSearch.get(position));
                                i.putExtra("word", titleSearch.get(position));
                                i.putExtra("image", imageSearch.get(position));
                                i.putExtra("tag", tagSearch.get(position));
                                //Toast.makeText(Search.this, "Tag  Search = " + tagSearch.get(position), Toast.LENGTH_SHORT).show();
                                floatMenu.close(true);
                                startActivity(i);
                            }
                        }
                    });

                    //reference.keepSynced(true);

                }
        }

        @Override
        public int getItemCount() {
            return imageSearch.size();
        }
    }

    private void queryCall(Query text) {

        FirebaseRecyclerOptions<ShowModel> options =
                new FirebaseRecyclerOptions.Builder<ShowModel>()
                        .setQuery(text, ShowModel.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<ShowModel, ShowHolder>(options) {
            @NonNull
            @Override
            public ShowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.letter_detail_layout, parent, false);
                return new ShowHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ShowHolder holder, final int position, @NonNull final ShowModel model) {
                // Bind the Chat object to the ChatHolder
                // ...
                if (!model.getTitle().equals("Next Page") && !model.getTitle().equals("Next")) {
                    holder.setImage(Search.this, model.getImage());
                    holder.setRating(model.getRating());
                    holder.setTitle(model.getTitle());
                    if (model.getTitle().equals("Smackdown")) {
                        smackArray.add(model.getLink());
                    }
                    if (model.getTitle().equals("WWE")) {
                        wweArray.add(model.getLink());
                    }
                }
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        YoYo.with(Techniques.RubberBand).duration(500).playOn(holder.mView);

                        if (model.getTag() != null && model.getTag().contains("tv4mobile")) {
                            Intent i = new Intent(Search.this, SeasonActivityTv4.class);
                            i.putExtra("link", model.getLink());
                            i.putExtra("word", model.getTitle());
                            i.putExtra("tag", model.getTag());
                            Toast.makeText(Search.this, "Tv4M = " + model.getTag(), Toast.LENGTH_SHORT).show();
                            if (model.getDesc() != null) {
                                i.putExtra("desc", model.getDesc());
                            }
                            if (model.getImage() != null) {
                                i.putExtra("image", model.getImage());
                            }
                            //Toast.makeText(Search.this, ""+tagSearch.get(0), Toast.LENGTH_SHORT).show();
                            floatMenu.close(true);
                            startActivity(i);
                            return;
                        }

                        if (model.getTitle().equals("Smackdown")) {
                            Intent i = new Intent(Search.this, SeasonDetail.class);
                            if (smackArray.get(position).contains("toxicunrated")) {
                                String value = "https://toxicunrated.com" + String.valueOf(smackArray.get(position));
                                i.putExtra("link", value);
                            } else {
                                String value = "https://toxicwap.com" + String.valueOf(smackArray.get(position));
                                i.putExtra("link", value);
                            }
                            floatMenu.close(true);
                            startActivity(i);
                            return;
                        }
                        if (model.getTitle().equals("WWE")) {
                            Intent i = new Intent(Search.this, LetterDetail.class);
                            i.putExtra("link", model.getLink());
                            floatMenu.close(true);
                            startActivity(i);
                            return;
                        }

                        if (model.getTag() != null && model.getTag().equals("toxic")) {
                            Intent i = new Intent(Search.this, SeasonsActivity.class);
                            i.putExtra("link", model.getLink());
                            i.putExtra("word", model.getTitle());
                            i.putExtra("tag", model.getTag());
                            //Toast.makeText(Search.this, "Tox = "+model.getTag(), Toast.LENGTH_SHORT).show();
                            if (model.getDesc() != null) {
                                i.putExtra("desc", model.getDesc());
                            }
                            if (model.getImage() != null) {
                                i.putExtra("image", model.getImage());
                            }
                            //Toast.makeText(Search.this, ""+tagSearch.get(0), Toast.LENGTH_SHORT).show();

                            floatMenu.close(true);
                            startActivity(i);
                        }
                    }
                });
            }
        };

        if (isTablet(Search.this)) {
            Display getOrient = getWindowManager().getDefaultDisplay();
            int orientation = Configuration.ORIENTATION_UNDEFINED;
            if (getOrient.getWidth() == getOrient.getHeight()) {
                orientation = Configuration.ORIENTATION_SQUARE;

            } else {
                if (getOrient.getWidth() < getOrient.getHeight()) {
                    orientation = Configuration.ORIENTATION_PORTRAIT;
                    showList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                } else {
                    orientation = Configuration.ORIENTATION_LANDSCAPE;
                    showList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
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
                    showList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

                } else {
                    orientation = Configuration.ORIENTATION_LANDSCAPE;
                    showList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

                }
            }
        }

        showList.setAdapter(adapter);
        //text.keepSynced(true);
    }


    public static class ShowHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView rate, titleView;
        View mView;

        ShowHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movie_poster);
            rate = itemView.findViewById(R.id.movie_rating);
            titleView = itemView.findViewById(R.id.letterText);
            titleView.setSelected(true);
            mView = itemView;
        }

        void setImage(final Context c, final String image) {
            Picasso.with(c).load(image).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
        }


        void setRating(String rating) {
            if (rating != null)
                rate.setText(rating);
        }

        void setTitle(String title) {
            if (title != null) {
                titleView.setText(title);
                titleView.setTextColor(Color.BLACK);
            }
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
            if (mInterstitialAd.isLoaded()&& mInterstitialAd!=null) {
                mInterstitialAd.show();
            } else {
                super.onBackPressed();
            }

        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        reference.keepSynced(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}
