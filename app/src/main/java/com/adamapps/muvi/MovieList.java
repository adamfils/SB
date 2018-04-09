package com.adamapps.muvi;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.FirebaseDatabase;
import com.ldoublem.loadingviewlib.view.LVBlock;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MovieList extends AppCompatActivity {

    String url = "null";
    String Query = "span.tt";
    String LinksQuery = "div.item>a";
    String ImageQuery = "div.image>img";
    String ratingQuery = "span.imdb";
    String qualityQuery = "span.calidad2";
    String yearQuery = "span.year";
    String descQuery = "span.ttx";
    String nextQuery = "div.pag_b a";
    private Document doc;
    Elements firstUl, firstLinks, imageLinks, ratingList, qualityList, yearList, descList;
    RecyclerView categoryList;
    ArrayList<String> titlesArray = new ArrayList<>();
    ArrayList<String> linksArray = new ArrayList<>();
    ArrayList<String> nextArray = new ArrayList<>();
    ArrayList<String> imageArray = new ArrayList<>();
    ArrayList<String> qualityArray = new ArrayList<>();
    ArrayList<String> describeArray = new ArrayList<>();
    ArrayList<String> ratingArray = new ArrayList<>();
    ArrayList<String> yearArray = new ArrayList<>();
    FloatingActionButton nextButton;
    Element nextElement;

    LVBlock lvBlock;
    String tit = null;
    String[] colors = {"#6258c4", "#673a3f", "#78d1b6", "#d725de", "#665fd1", "#9c6d57", "#fa2a55"};
    Random random;
    int num = 143;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        lvBlock = (LVBlock) findViewById(R.id.block);

        random = new Random();
        Intent i = getIntent();
        String indicator = i.getStringExtra("newUrl");
        if (indicator == null) {
            url = "http://vexmovies.org/release-year/2018";
        } else {
            url = indicator;
        }

        nextButton = (FloatingActionButton) findViewById(R.id.next_btn);
        android.support.v7.widget.Toolbar toolbarr = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        if (tit != null) {
            toolbarr.setTitle(tit);
        } else {
            toolbarr.setTitle(R.string.app_name);
        }
        toolbarr.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarr);

        categoryList = (RecyclerView) findViewById(R.id.movieList);
        new MyTask().execute();
        //1859
        //series 1013


        /*FirebaseDatabase.getInstance().getReference().child("Media").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double mr = dataSnapshot.getChildrenCount();
                Toast.makeText(MovieList.this, ""+mr, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/
        categoryList.setLayoutManager(new GridLayoutManager(this, 2));

    }

    private class MyTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            lvBlock.setViewColor(Color.parseColor("#c2b709"));
            lvBlock.isShadow(true);
            lvBlock.setViewColor(Color.rgb(245, 209, 22));
            lvBlock.startAnim(5000);
            lvBlock.startAnim();
        }

        @Override
        protected void onPostExecute(Object o) {
            lvBlock.stopAnim();
            lvBlock.setVisibility(View.GONE);
            for (Element listItem : firstUl) {
                if (listItem != null)
                    titlesArray.add(listItem.text());
            }

            for (Element links : firstLinks) {
                if (links != null)
                    linksArray.add(links.attr("href"));
            }
            for (Element image : imageLinks) {
                if (image != null)
                    imageArray.add(image.attr("src"));
            }
            for (Element rate : ratingList) {
                if (rate != null)
                    ratingArray.add(rate.text());
            }
            for (Element quality : qualityList) {
                if (quality != null)
                    qualityArray.add(quality.text());
            }
            for (Element desc : descList) {
                if (desc != null)
                    describeArray.add(desc.text());
            }
            for (Element year : yearList) {
                if (!TextUtils.isEmpty(year.text()))
                    yearArray.add(year.text());
            }
            if (nextElement!=null&&nextElement.text() != null && !TextUtils.isEmpty(nextElement.text()))
                nextArray.add(nextElement.attr("href"));

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (nextArray.size() > 0) {
                        Toast.makeText(MovieList.this, "" + nextArray.get(0), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MovieList.this, MovieList.class);
                        i.putExtra("newUrl", nextArray.get(0));
                        startActivity(i);
                        finish();
                    }
                }
            });


            for (int i = 0; i < titlesArray.size(); i++) {
                HashMap<String, Object> map = new HashMap<>();
                if (i < titlesArray.size())
                    map.put("title", titlesArray.get(i));
                if (i < imageArray.size())
                    map.put("image",imageArray.get(i));
                if (i < linksArray.size())
                    map.put("link", linksArray.get(i));
                map.put("nameSearch", titlesArray.get(i).toLowerCase());
                if (i < ratingArray.size())
                    map.put("rating", ratingArray.get(i));
                if (i < qualityArray.size())
                    map.put("quality", qualityArray.get(i));
                if (i < describeArray.size())
                    map.put("desc", describeArray.get(i));
                if (i < yearArray.size())
                    map.put("year", String.valueOf(yearArray.get(i)));
                map.put("tag","vex");
                /*FirebaseDatabase.getInstance().getReference().child("Movies")
                        .child(titlesArray.get(i).replaceAll("[^a-zA-Z0-9]"," ")).updateChildren(map);*/
                FirebaseDatabase.getInstance().getReference().child("Movies")
                        .child(titlesArray.get(i).replaceAll("[\\[.#$\\]]", "")).updateChildren(map);
            }


            categoryList.setAdapter(new LettersAdapter(titlesArray, linksArray));
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                doc = Jsoup.connect(url).timeout(0).get();
                firstUl = doc.select(Query);
                firstLinks = doc.select(LinksQuery);
                imageLinks = doc.select(ImageQuery);
                ratingList = doc.select(ratingQuery);
                yearList = doc.select(yearQuery);
                descList = doc.select(descQuery);
                qualityList = doc.select(qualityQuery);
                nextElement = doc.selectFirst(nextQuery);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return firstUl;
        }

    }

    private class LettersAdapter extends RecyclerView.Adapter<DetailHolder> {

        ArrayList<String> words = new ArrayList<>();
        ArrayList<String> links = new ArrayList<>();

        public LettersAdapter(ArrayList<String> words, ArrayList<String> links) {
            this.words = words;
            this.links = links;
        }

        @Override
        public DetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(MovieList.this).inflate(R.layout.movie_layout, parent, false);
            return new DetailHolder(v);
        }

        @Override
        public void onBindViewHolder(final DetailHolder holder, final int position) {

            if (position < titlesArray.size() && titlesArray.get(position) != null)
                holder.text.setText(titlesArray.get(position));
            if (position < imageArray.size() && imageArray.get(position) != null && !TextUtils.isEmpty(imageArray.get(position)))
                Picasso.with(MovieList.this).load(imageArray.get(position)).into(holder.imageView);
            if (position < qualityArray.size() && qualityArray.get(position) != null)
                holder.quality.setText(qualityArray.get(position));
            if (position < yearArray.size() && yearArray.get(position) != null)
                holder.year.setText(yearArray.get(position));
            int val = random.nextInt(colors.length);
            holder.cardView.setCardBackgroundColor(Color.parseColor(colors[val]));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    YoYo.with(Techniques.RubberBand).duration(500).playOn(holder.mView);
                    Toast.makeText(MovieList.this, "" + linksArray.get(holder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                }
            });
        }


        @Override
        public int getItemCount() {
            return words.size();
        }

    }

    private class DetailHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView text, quality, year;
        TextView rating;
        View mView;
        ImageView imageView;
        AdamClickListener adamClickListener;
        CardView cardView;


        public DetailHolder(View itemView) {
            super(itemView);
            mView = itemView;
            text = itemView.findViewById(R.id.letterText);
            quality = itemView.findViewById(R.id.movie_quality);
            year = itemView.findViewById(R.id.movie_year);
            cardView = itemView.findViewById(R.id.card);
            imageView = itemView.findViewById(R.id.movie_poster);
            rating = itemView.findViewById(R.id.movie_rating);
            text.setSelected(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setAdamClickListener(AdamClickListener adamClickListener) {
            this.adamClickListener = adamClickListener;
        }

        @Override
        public void onClick(View view) {
            adamClickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            adamClickListener.onClick(view, getAdapterPosition(), true);
            return true;
        }
    }


}
