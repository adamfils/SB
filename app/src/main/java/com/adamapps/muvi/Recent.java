package com.adamapps.muvi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Recent extends AppCompatActivity {

    RecyclerView recentList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference recentRef;

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

        recentList = (RecyclerView) findViewById(R.id.recent_list);

        FirebaseRecyclerAdapter<RecentModel, Recent.RecentHolder> adapter = new FirebaseRecyclerAdapter<RecentModel, Recent.RecentHolder>
                (RecentModel.class, R.layout.letter_detail_layout, Recent.RecentHolder.class, recentRef) {

            @Override
            protected void populateViewHolder(final Recent.RecentHolder holder, final RecentModel model, final int position) {


                holder.setImage(Recent.this, model.getThumbnail());
                holder.setTitle(model.getEpisode());

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
        recentList.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        recentList.setAdapter(adapter);

    }

    public static class RecentHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView rate, titleView;
        View mView;

        public RecentHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movie_poster);
            rate = itemView.findViewById(R.id.movie_rating);
            titleView = itemView.findViewById(R.id.letterText);
            titleView.setSelected(true);
            mView = itemView;
        }

        void setImage(Context c, String image) {
            Picasso.with(c).load(image).into(imageView);
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
}
