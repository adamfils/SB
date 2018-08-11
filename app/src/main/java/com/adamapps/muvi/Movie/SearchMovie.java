package com.adamapps.muvi.Movie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamapps.muvi.MovieModels.MovieModel;
import com.adamapps.muvi.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchMovie extends AppCompatActivity {

    RecyclerView movieList;
    MaterialSearchView searchView;
    DatabaseReference movieRef;
    private ArrayList<String> imageArray = new ArrayList<>();
    Element imageElement;
    String imageQuery = "img.poster";
    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_movie);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);

        searchView = findViewById(R.id.search_view);

        movieRef = FirebaseDatabase.getInstance().getReference().child("Movies");

        movieList = findViewById(R.id.movieList);

        movieRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    // String name = "https://image.tmdb.org/t/p/w185_and_h278_bestv2/uqXFqZT1cT3Q6fHYilPSnBUJAp5.jpg";
                    /*if (TextUtils.isEmpty(String.valueOf(ds.child("image").getValue()))) {*/
                    /*if (ds.child("image").getValue().equals("https://image.tmdb.org/t/p/w185_and_h278_bestv2/5DIF0yzFKAJaWws5wWDVzwntjM6.jpg")) {
                        Toast.makeText(SearchMovie.this, "Failed = " + ds.getKey(), Toast.LENGTH_SHORT).show();
                        new ImageTask(ds.getKey()).execute();
                    }*/
                    //movieRef.keepSynced(true);
                    //movieRef.child(ds.getKey()).child("title").setValue(ds.getKey().toLowerCase());
                    //Toast.makeText(SearchMovie.this, ""+ds.child("title").getValue(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions<MovieModel> options =
                new FirebaseRecyclerOptions.Builder<MovieModel>()
                        .setQuery(movieRef, MovieModel.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<MovieModel, MovieHolder>(options) {
            @NonNull
            @Override
            public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.letter_detail_layout, parent, false);
                return new MovieHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final MovieHolder holder, final int position, @NonNull final MovieModel model) {
                // Bind the Chat object to the ChatHolder
                // ...

                movieRef.keepSynced(true);
                //if (model.getYear().equals("2018")) {
                holder.setImage(getApplicationContext(), model.getImage());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        YoYo.with(Techniques.RubberBand).duration(500).playOn(view);
                        //Toast.makeText(SearchMovie.this, "" + model.getTitle(), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(SearchMovie.this, MovieDetail.class);
                        i.putExtra("link", model.getLink());
                        i.putExtra("word", model.getTitle());
                        i.putExtra("tag", model.getTag());
                        i.putExtra("year", model.getYear());
                        i.putExtra("quality", model.getQuality());
                        if (model.getDesc() != null) {
                            i.putExtra("desc", model.getDesc());
                        }
                        if (model.getImage() != null) {
                            i.putExtra("image", model.getImage());
                        }
                        startActivity(i);


                    }
                });

            }
        };
        /*FirebaseRecyclerAdapter<MovieModel, MovieHolder> adapter =
                new FirebaseRecyclerAdapter<MovieModel, MovieHolder>(MovieModel.class, R.layout.letter_detail_layout,
                        MovieHolder.class, movieRef) {
                    @Override
                    protected void populateViewHolder(final MovieHolder viewHolder, final MovieModel model, int position) {

                    }
                    //}
                };*/
        movieList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        movieList.setAdapter(adapter);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query textQ = movieRef.orderByChild("nameSearch").startAt(newText.toLowerCase());
                queryCall(textQ);
                return true;
            }
        });
    }

    private void queryCall(Query text) {

        FirebaseRecyclerOptions<MovieModel> options =
                new FirebaseRecyclerOptions.Builder<MovieModel>()
                        .setQuery(text, MovieModel.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<MovieModel, MovieHolder>(options) {
            @NonNull
            @Override
            public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.letter_detail_layout, parent, false);
                return new MovieHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final MovieHolder holder, final int position, @NonNull final MovieModel model) {
                // Bind the Chat object to the ChatHolder
                // ...

                holder.setImage(SearchMovie.this, model.getImage());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        YoYo.with(Techniques.RubberBand).duration(500).playOn(holder.mView);

                        Intent i = new Intent(SearchMovie.this, MovieDetail.class);
                        i.putExtra("link", model.getLink());
                        i.putExtra("word", model.getTitle());
                        i.putExtra("tag", model.getTag());
                        i.putExtra("year", model.getYear());
                        i.putExtra("quality", model.getQuality());
                        if (model.getDesc() != null) {
                            i.putExtra("desc", model.getDesc());
                        }
                        if (model.getImage() != null) {
                            i.putExtra("image", model.getImage());
                        }
                        startActivity(i);

                    }
                });

            }
        };

        /*FirebaseRecyclerAdapter<MovieModel, SearchMovie.MovieHolder> adapter = new FirebaseRecyclerAdapter<MovieModel, MovieHolder>
                (MovieModel.class, R.layout.letter_detail_layout, SearchMovie.MovieHolder.class, text) {

            @Override
            protected void populateViewHolder(final SearchMovie.MovieHolder holder, final MovieModel model, final int position) {

            }
        };*/
        movieList.setLayoutManager(new GridLayoutManager(this, 2));
        movieList.setAdapter(adapter);
        text.keepSynced(true);
    }

    private class ImageTask extends AsyncTask {

        String nameImage;

        public ImageTask(String nameImage) {
            this.nameImage = nameImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {

            if (imageElement != null) {
                imageArray.add(imageElement.attr("data-src"));

                HashMap<String, Object> map = new HashMap<>();
                map.put("image", imageElement.attr("data-src"));
                //Toast.makeText(SearchMovie.this, ""+imageElement.attr("data-src"), Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference().child("Movies").child(nameImage).updateChildren(map);
            }

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                String modName = nameImage.replaceAll(" ", "+");
                String imageEndPoint = "https://www.themoviedb.org/search/movie?query=";
                String url2 = imageEndPoint + modName;
                Document imageDoc = Jsoup.connect(url2).timeout(0).get();
                imageElement = imageDoc.selectFirst(imageQuery);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return imageElement;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    public static class MovieHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView rate, titleView;
        View mView;

        public MovieHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movie_poster);
            rate = itemView.findViewById(R.id.movie_rating);
            titleView = itemView.findViewById(R.id.letterText);
            titleView.setSelected(true);
            mView = itemView;
        }

        void setImage(final Context c, final String image) {
            if (image != null && !TextUtils.isEmpty(image)) {
                Picasso.with(c).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(c).load(image).into(imageView);
                    }
                });
            } else {
                Picasso.with(c).load("google.com").into(imageView);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
