package com.adamapps.muvi.Movie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.adamapps.muvi.R;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {

    private String descQuery = "div#cap1";
    private String yearQuery = "div.data>p.meta>span";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    TextView titleText, qualityText, ratingText, timeText, descText, yearText;
    KenBurnsView burnsView;

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


        Intent i = getIntent();
        String image = i.getStringExtra("image");
        String title = i.getStringExtra("word");
        String quality = i.getStringExtra("quality");
        String year = i.getStringExtra("year");
        String rating = i.getStringExtra("rating");
        String desc = i.getStringExtra("desc");
        final String link = i.getStringExtra("link");

        burnsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), WebPlayer.class);
                if (link != null)
                    i.putExtra("link", link);
                startActivity(i);
            }
        });

        if (title != null)
            titleText.setText(title);
        if (image != null)
            Picasso.with(getApplicationContext()).load(image).into(burnsView);
        if (quality != null)
            qualityText.setText(quality);
        if (year != null)
            yearText.setText(year);
        if (rating != null)
            ratingText.setText(rating);
        if (desc != null)
            descText.setText(desc);


        firebaseDatabase = FirebaseDatabase.getInstance();
        if (title != null) {
            reference = firebaseDatabase.getReference().child("Movie").child(title.replaceAll("[\\[.#$\\]]",""));
            /*reference.child("desc").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    descText.setText(String.valueOf(dataSnapshot.getValue()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            reference.child("").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    descText.setText(String.valueOf(dataSnapshot.getValue()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            reference.child("desc").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    descText.setText(String.valueOf(dataSnapshot.getValue()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            reference.child("desc").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    descText.setText(String.valueOf(dataSnapshot.getValue()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            reference.child("desc").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    descText.setText(String.valueOf(dataSnapshot.getValue()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/
        }

        /*FirebaseRecyclerAdapter<MovieModel,MovieDetailHolder> adapter =
                new FirebaseRecyclerAdapter<MovieModel, MovieDetailHolder>(MovieModel.class,R.layout.letter_detail_layout) {
            @Override
            protected void populateViewHolder(MovieDetailHolder viewHolder, MovieModel model, int position) {

            }
        }*/
    }

    public static class MovieDetailHolder extends RecyclerView.ViewHolder {

        public MovieDetailHolder(View itemView) {
            super(itemView);
        }
    }


}
