package com.adamapps.showbase.User;

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

import com.adamapps.showbase.Movie.MovieDetail;
import com.adamapps.showbase.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Favorite extends AppCompatActivity {

    RecyclerView favoriteList;
    String favLink = null;

    ArrayList<String> firstTitle = new ArrayList<>();

    ArrayList<String> titleArray = new ArrayList<>();
    ArrayList<String> imageArray = new ArrayList<>();
    ArrayList<String> tagArray = new ArrayList<>();
    ArrayList<String> descArray = new ArrayList<>();
    ArrayList<String> linkArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Favorite");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);

        favoriteList = findViewById(R.id.favorite_list);

        if (isTablet(Favorite.this)) {
            Display getOrient = getWindowManager().getDefaultDisplay();
            int orientation = Configuration.ORIENTATION_UNDEFINED;
            if (getOrient.getWidth() == getOrient.getHeight()) {
                orientation = Configuration.ORIENTATION_SQUARE;

            } else {
                if (getOrient.getWidth() < getOrient.getHeight()) {
                    orientation = Configuration.ORIENTATION_PORTRAIT;
                    favoriteList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                } else {
                    orientation = Configuration.ORIENTATION_LANDSCAPE;
                    favoriteList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
                }
            }


        } else {
            Display getOrient = getWindowManager().getDefaultDisplay();
            int orientation = Configuration.ORIENTATION_UNDEFINED;
            if (getOrient.getWidth() == getOrient.getHeight()) {
                //Orientation Square
                orientation = Configuration.ORIENTATION_SQUARE;
            } else {
                if (getOrient.getWidth() < getOrient.getHeight()) {
                    //Orientation Portrait
                    orientation = Configuration.ORIENTATION_PORTRAIT;
                    favoriteList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

                } else {
                    //Orientation Landscape
                    orientation = Configuration.ORIENTATION_LANDSCAPE;
                    favoriteList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

                }
            }
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            FirebaseDatabase.getInstance().getReference().child("ShowBaseFavorite").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            firstTitle.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                firstTitle.add(ds.getKey());
                            }
                            for (int i = 0; i < firstTitle.size(); i++) {
                                imageArray.clear();
                                linkArray.clear();
                                descArray.clear();
                                tagArray.clear();
                                DatabaseReference fav = FirebaseDatabase.getInstance().getReference().child("ShowBaseFavorite").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(firstTitle.get(i));
                                fav.child("image").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        imageArray.add(dataSnapshot.getValue(String.class));
                                        favoriteList.setAdapter(new FavouriteAdapter());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                fav.child("link").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        linkArray.add(dataSnapshot.getValue(String.class));
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
    }

    public class FavouriteAdapter extends RecyclerView.Adapter<FavoriteHolder> {

        @NonNull
        @Override
        public FavoriteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.letter_detail_layout, parent, false);
            return new FavoriteHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final FavoriteHolder holder, final int position) {

            holder.setImage(Favorite.this, imageArray.get(position));
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.RubberBand).duration(500).playOn(holder.imageView);

                    if (position < linkArray.size()) {

                        Intent i = new Intent(Favorite.this, MovieDetail.class);
                        i.putExtra("link", linkArray.get(position));
                        i.putExtra("title", firstTitle.get(position));
                        i.putExtra("image", imageArray.get(position));
                        startActivity(i);
                    }

                }
            });


        }

        @Override
        public int getItemCount() {
            return linkArray.size();
        }
    }

    public class FavoriteHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView rate, titleView, episodeText;
        View mView;

        public FavoriteHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.movie_poster);
            rate = itemView.findViewById(R.id.movie_rating);
            titleView = itemView.findViewById(R.id.letterText);
            episodeText = itemView.findViewById(R.id.episode_text);

            episodeText.setVisibility(View.INVISIBLE);
            titleView.setSelected(true);
            mView = itemView;
        }

        void setImage(Context c, String image) {
            if(!image.isEmpty()) {
                Picasso.with(c).load(image).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
            }else{
                Picasso.with(c).load("http://www.neotechnocraft.com/images/NoImageFound.jpg").memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);

            }
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
