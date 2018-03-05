package com.adamapps.toxic;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
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
import com.ldoublem.loadingviewlib.view.LVBlock;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SeasonDetail extends AppCompatActivity {

    private Document doc;
    String url = null;
    String tit = null;
    String key = null,image = null;
    Elements firstUl, firstLinks, nextLink, thumbLink;
    String Query = "li";
    String LinksQuery = "li a";
    String NextQuery = "a.default_page";
    String ThumbnailQuery = "li>a>img";
    RecyclerView categoryList;
    ArrayList<String> titlesArray = new ArrayList<>();
    ArrayList<String> linksArray = new ArrayList<>();
    ArrayList<String> nextArray = new ArrayList<>();
    ArrayList<String> thumbArray = new ArrayList<>();
    FloatingActionButton nextButton;
    LVBlock lvBlock;
    //String[] colors = {"#6258c4", "#673a3f", "#78d1b6", "#d725de", "#665fd1", "#9c6d57", "#fa2a55"};
    String[] colors = {"#D5D5D5", "#A8A5A3", "#E8E2DB"};
    Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season_detail);
        lvBlock = (LVBlock) findViewById(R.id.block);

        random = new Random();

        Intent i = getIntent();
        url = i.getStringExtra("link");
        tit = i.getStringExtra("word");
        key = i.getStringExtra("key");
        image = i.getStringExtra("images");

        android.support.v7.widget.Toolbar toolbarr = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        if (tit != null) {
            toolbarr.setTitle(tit);
        } else {
            toolbarr.setTitle(R.string.app_name);
        }
        toolbarr.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarr);


        categoryList = (RecyclerView) findViewById(R.id.seasonDetail);
        new MyTask().execute();
        //categoryList.setLayoutManager(new GridLayoutManager(this,4));
        nextButton = (FloatingActionButton) findViewById(R.id.next_btn);
        categoryList.setLayoutManager(new LinearLayoutManager(this));
    }

    private class MyTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            lvBlock.isShadow(true);
            lvBlock.setViewColor(Color.rgb(245, 209, 22));
            lvBlock.startAnim(5000);
            lvBlock.startAnim();
        }

        @Override
        protected void onPostExecute(Object o) {
            lvBlock.stopAnim();
            lvBlock.setVisibility(View.GONE);
            if (firstUl != null)
                for (Element listItem : firstUl) {
                    if (!TextUtils.isEmpty(String.valueOf(listItem.text()))) {
                        titlesArray.add(String.valueOf(listItem.text()));
                    }
                }
            if (firstLinks != null)
                for (Element links : firstLinks) {
                    if (!TextUtils.isEmpty(String.valueOf(links.text()))) {
                        linksArray.add(String.valueOf(links.attr("href")));
                    }
                }
            if (nextLink != null)
                for (Element nextLinks : nextLink) {
                    if (!TextUtils.isEmpty(String.valueOf(nextLinks.text())) && nextLinks.text().contains("Next")) {
                        nextArray.add(String.valueOf(nextLinks.attr("href")));
                    }
                }
            if (thumbLink != null)
                for (Element thumb : thumbLink) {
                    if (!TextUtils.isEmpty(String.valueOf(thumb.attr("src")))) {
                        if (url.contains("toxicunrated")) {
                            thumbArray.add("http://www.toxicunrated.com" + thumb.attr("src"));
                        } else {
                            thumbArray.add("http://www.toxicwap.com" + thumb.attr("src"));
                        }
                    }
                }
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (nextArray.size() > 0) {
                        Intent i = new Intent(SeasonDetail.this, SeasonDetail.class);
                        if (url.contains("toxicunrated")) {
                            String value = "https://toxicunrated.com" + String.valueOf(nextArray.get(0));
                            i.putExtra("link", value);
                            i.putExtra("word", tit);
                        } else {
                            String value = "https://toxicwap.com" + String.valueOf(nextArray.get(0));
                            i.putExtra("link", value);
                            i.putExtra("word", tit);
                        }
                        startActivity(i);

                    } else {
                        Toast.makeText(SeasonDetail.this, "Sorry That's All", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //Toast.makeText(LetterDetailthis, "Size = "+linksArray.get(1), Toast.LENGTH_SHORT).show();
            categoryList.setAdapter(new SeasonDetailAdapter(titlesArray, linksArray));
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                doc = Jsoup.connect(url).timeout(0).get();
                firstUl = doc.select(Query);
                firstLinks = doc.select(LinksQuery);
                nextLink = doc.select(NextQuery);
                thumbLink = doc.select(ThumbnailQuery);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return firstUl;
        }

    }

    private class SeasonDetailAdapter extends RecyclerView.Adapter<SeasonHolderHolder> {

        ArrayList<String> words = new ArrayList<>();
        ArrayList<String> links = new ArrayList<>();

        public SeasonDetailAdapter(ArrayList<String> words, ArrayList<String> links) {
            this.words = words;
            this.links = links;
        }

        @Override
        public SeasonHolderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(SeasonDetail.this).inflate(R.layout.single_season_detail, parent, false);
            return new SeasonHolderHolder(v);
        }

        @Override
        public void onBindViewHolder(final SeasonHolderHolder holder, int position) {
            if (titlesArray.get(position) != null) {
                holder.text.setText(words.get(position));
                Picasso.with(getApplicationContext()).load(thumbArray.get(position)).into(holder.thumbnailView);
                int val = random.nextInt(colors.length);
                holder.cardView.setCardBackgroundColor(Color.parseColor(colors[val]));
            }
            holder.setAdamClickListener(new AdamClickListener() {
                @Override
                public void onClick(View v, int position, boolean isLongClick) {
                    YoYo.with(Techniques.RubberBand).duration(500).playOn(holder.mView);
                    Intent i = new Intent(SeasonDetail.this, VideoPlayer.class);
                    if (url.contains("toxicunrated")) {
                        String value = "https://toxicunrated.com" + String.valueOf(links.get(position));
                        i.putExtra("link", value);
                        i.putExtra("word", words.get(position));
                        i.putExtra("key", key);
                        i.putExtra("season", tit);
                        i.putExtra("thumb", image);
                    } else {
                        String value = "https://toxicwap.com" + String.valueOf(links.get(position));
                        i.putExtra("link", value);
                        i.putExtra("word", words.get(position));
                        i.putExtra("key", key);
                        i.putExtra("season", tit);
                        i.putExtra("thumb", image);
                    }
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return links.size();
        }
    }

    private class SeasonHolderHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView text;
        View mView;
        AdamClickListener adamClickListener;
        CardView cardView;
        ImageView thumbnailView;

        public SeasonHolderHolder(View itemView) {
            super(itemView);
            mView = itemView;
            text = (TextView) itemView.findViewById(R.id.letterText);
            cardView = (CardView) itemView.findViewById(R.id.card);
            thumbnailView = itemView.findViewById(R.id.thumbnail_video);
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
