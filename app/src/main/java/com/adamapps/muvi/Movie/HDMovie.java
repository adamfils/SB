package com.adamapps.muvi.Movie;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adamapps.muvi.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class HDMovie extends AppCompatActivity {

    RecyclerView hdlist;
    FloatingActionButton nextBtn;
    ArrayList<String> titleArray = new ArrayList<>();
    ArrayList<String> yearArray = new ArrayList<>();
    ArrayList<String> linkArray = new ArrayList<>();
    ArrayList<String> imageArray = new ArrayList<>();
    ArrayList<String> nextArray = new ArrayList<>();

    private Document doc;
    Elements linkElm, imageElm, titleElm;
    Elements nextElement;
    String url = "https://hdonline.eu/movie/filter/movies/lastest/all/all/9228/all/page/13/";

    String titleQuery = "ul.ulclear >div.movies-list>li.movie-item>div.item-detail>h2";
    String linkQuery = "ul.ulclear >div.movies-list>li.movie-item>div.item-detail>h2>a";
    String imageQuery = "ul.ulclear >div.movies-list>li.movie-item>a>img";
    String nextLinkQuery = "ul.pagination>li>a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hdmovie);

        hdlist = findViewById(R.id.hdlist);
        nextBtn = findViewById(R.id.nextBtn);

        Intent i = getIntent();
        if (i.getStringExtra("url") != null) {
            url = i.getStringExtra("url");
        }

        hdlist.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        new MyTask().execute();

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

                        Toast.makeText(HDMovie.this, ""
                                + nextArray.get(nextArray.indexOf("#") + 1), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                YoYo.with(Techniques.Shake).duration(3000).playOn(nextBtn);
            }
            Toast.makeText(HDMovie.this, "" + titleArray.size(), Toast.LENGTH_SHORT).show();
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

    public class HDAdapter extends RecyclerView.Adapter<HDHolder> {
        @Override
        public HDHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.letter_detail_layout, parent, false);
            return new HDHolder(v);
        }

        @Override
        public void onBindViewHolder(HDHolder holder, int position) {
            //if (imageArray != null && position < imageArray.size())
            holder.setImage(getApplication(), imageArray.get(position));
            HashMap<String, Object> map = new HashMap<>();
            map.put("tag", "hdonline");
            map.put("year", "2017");
            map.put("image", imageArray.get(position));
            map.put("title", titleArray.get(position));
            map.put("link", linkArray.get(position));
            map.put("nameSearch", titleArray.get(position).toLowerCase());
            FirebaseDatabase.getInstance().getReference().child("Movie")
                    .child(titleArray.get(position).replaceAll("[\\[/.#$\\]]", "")).updateChildren(map);
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
    }
}
