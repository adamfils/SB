package com.adamapps.muvi.TvShow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamapps.muvi.Tests.LetterDetail;
import com.adamapps.muvi.R;
import com.adamapps.muvi.User.Recent;
import com.adamapps.muvi.Movie.SearchMovie;
import com.adamapps.muvi.TvShowModels.ShowModel;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

    MaterialSearchView searchView;
    RecyclerView showList;
    Query query;
    DatabaseReference reference;
    ArrayList<String> linksArray = new ArrayList<>();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        updateRef = FirebaseDatabase.getInstance().getReference("lock/beta/close");
        updateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Boolean.class)){

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
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

        searchView = findViewById(R.id.search_view);

        queryCall(reference);

        recentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatMenu.close(true);
                startActivity(new Intent(getApplicationContext(), Recent.class));
            }
        });
        moviesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SearchMovie.class));
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
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String queryText) {
                //Do some magic
                //query = reference.orderByChild("nameSearch").startAt(queryText.toLowerCase());
                //queryCall(query);
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
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.child("nameSearch").getValue(String.class).contains(queryText.toLowerCase())) {
                                searchResults(ds.getKey());
                            }
                            reference.keepSynced(true);
                        }
                        showList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                        showList.setAdapter(new SearchAdapter());
                        reference.keepSynced(true);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String queryText) {
                //Do some magic
                if (searchView.isSearchOpen()) {
                    floatMenu.close(true);
                }

                query = reference.orderByChild("nameSearch").startAt(queryText.toLowerCase());
                queryCall(query);
                return true;
            }
        });
        reference.keepSynced(true);

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

        @NonNull
        @Override
        public ShowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.letter_detail_layout, parent, false);
            return new ShowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ShowHolder holder, final int position) {
            if (!titleSearch.get(position).equals("Next Page") && !titleSearch.get(position).equals("Next")) {
                holder.setImage(Search.this, imageSearch.get(position));
                holder.setTitle(titleSearch.get(position));
                if (titleSearch.size() == ratingSearch.size())
                    holder.setRating(ratingSearch.get(position));
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        YoYo.with(Techniques.RubberBand).duration(500).playOn(holder.mView);
                        Intent i = new Intent(getApplicationContext(), SeasonsActivity.class);
                        i.putExtra("link", linkSearch.get(position));
                        i.putExtra("desc", descSearch.get(position));
                        i.putExtra("word", titleSearch.get(position));
                        i.putExtra("image", imageSearch.get(position));
                        i.putExtra("tag", tagSearch.get(position));
                        floatMenu.close(true);
                        startActivity(i);
                    }
                });

                reference.keepSynced(true);

            }
        }

        @Override
        public int getItemCount() {
            return imageSearch.size();
        }
    }

    private void queryCall(Query text) {

        FirebaseRecyclerAdapter<ShowModel, ShowHolder> adapter = new FirebaseRecyclerAdapter<ShowModel, ShowHolder>
                (ShowModel.class, R.layout.letter_detail_layout, ShowHolder.class, text) {

            @Override
            protected void populateViewHolder(final ShowHolder holder, final ShowModel model, final int position) {

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

                        if (model.getTag() != null && model.getTag().equals("tv4mobile")) {
                            Intent i = new Intent(Search.this, SeasonActivityTv4.class);
                            i.putExtra("link", model.getLink());
                            i.putExtra("word", model.getTitle());
                            i.putExtra("tag", model.getTag());
                            if (model.getDesc() != null) {
                                i.putExtra("desc", model.getDesc());
                            }
                            if (model.getImage() != null) {
                                i.putExtra("image", model.getImage());
                            }
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
                            //int pos = titlesArray.indexOf("WWE");
                            //String link = linksArray.get(pos);
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
                            if (model.getDesc() != null) {
                                i.putExtra("desc", model.getDesc());
                            }
                            if (model.getImage() != null) {
                                i.putExtra("image", model.getImage());
                            }
                            floatMenu.close(true);
                            startActivity(i);
                        }
                        /*Intent i = new Intent(Search.this, SeasonsActivity.class);
                        i.putExtra("link", model.getLink());
                        i.putExtra("word", model.getTitle());
                        if (model.getDesc() != null) {
                            i.putExtra("desc", model.getDesc());
                        }
                        if (model.getImage() != null) {
                            i.putExtra("image", model.getImage());
                        }
                        floatMenu.close(true);
                        startActivity(i);*/

                    }
                });
            }
        };
        showList.setLayoutManager(new GridLayoutManager(this, 2));
        showList.setAdapter(adapter);
        text.keepSynced(true);
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
            Picasso.with(c).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(c).load(image).into(imageView);
                }
            });
        }

        void setLink(String link) {
            //links.add(link);
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
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
}
