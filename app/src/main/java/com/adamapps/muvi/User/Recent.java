package com.adamapps.muvi.User;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamapps.muvi.Movie.MovieDetail;
import com.adamapps.muvi.Movie.SearchMovie;
import com.adamapps.muvi.MovieModels.MovieModel;
import com.adamapps.muvi.R;
import com.adamapps.muvi.TvShow.Search;
import com.adamapps.muvi.UserModels.RecentModel;
import com.adamapps.muvi.TvShow.VideoPlayer;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class Recent extends AppCompatActivity {

    RecyclerView recentList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference recentRef;
    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Recent");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        recentRef = firebaseDatabase.getReference().child("Recent").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        recentList = findViewById(R.id.recent_list);

        FirebaseRecyclerOptions<RecentModel> options =
                new FirebaseRecyclerOptions.Builder<RecentModel>()
                        .setQuery(recentRef, RecentModel.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<RecentModel, RecentHolder>(options) {
            @NonNull
            @Override
            public RecentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_episode_layout, parent, false);
                return new RecentHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final RecentHolder holder, final int position, @NonNull final RecentModel model) {
                // Bind the Chat object to the ChatHolder
                // ...

                holder.setImage(Recent.this, model.getThumbnail());

                holder.setTitle(model.getEpisode());
                holder.titleView.setTextColor(Color.WHITE);
                holder.number.setVisibility(View.GONE);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        YoYo.with(Techniques.RubberBand).duration(500).playOn(holder.mView);

                        Intent i = new Intent(Recent.this, VideoPlayer.class);
                        i.putExtra("link", model.getLink());
                        i.putExtra("word", model.getEpisode());
                        i.putExtra("show",model.getShow());
                        //i.putExtra("tag", model.getTag());
                        i.putExtra("type","recent");
                        if (model.getTime() != 0) {
                            i.putExtra("time", String.valueOf(model.getTime()));
                        }

                        startActivity(i);


                    }
                });
            }
        };

        if (isTablet(Recent.this)) {
            Display getOrient = getWindowManager().getDefaultDisplay();
            int orientation = Configuration.ORIENTATION_UNDEFINED;
            if (getOrient.getWidth() == getOrient.getHeight()) {
                orientation = Configuration.ORIENTATION_SQUARE;

            } else {
                if (getOrient.getWidth() < getOrient.getHeight()) {
                    orientation = Configuration.ORIENTATION_PORTRAIT;
                    recentList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                } else {
                    orientation = Configuration.ORIENTATION_LANDSCAPE;
                    recentList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
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
                    recentList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

                } else {
                    orientation = Configuration.ORIENTATION_LANDSCAPE;
                    recentList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

                }
            }
        }
        
        recentList.setAdapter(adapter);

    }

    public static class RecentHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView titleView,number;
        View mView;

        public RecentHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.test_image);
            titleView = itemView.findViewById(R.id.show_name);
            number = itemView.findViewById(R.id.test_text);
            titleView.setSelected(true);
            mView = itemView;
        }

        void setImage(Context c, String image) {
            Picasso.with(c).load(image).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
        }

        void setLink(String link) {
            //links.add(link);
        }

        void setTitle(String title) {
            if (title != null) {
                titleView.setText(title);
                titleView.setTextColor(Color.BLACK);
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

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    
}
