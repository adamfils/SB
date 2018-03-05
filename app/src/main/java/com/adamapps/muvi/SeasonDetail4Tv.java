package com.adamapps.muvi;

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
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ldoublem.loadingviewlib.view.LVBlock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SeasonDetail4Tv extends AppCompatActivity {

    private Document doc;
    String url = null;
    String tit = null;
    String tag = null;
    Elements firstUl, firstLinks, nextLink;
    String LinksQuery = "div.data a";
    String NextQuery = "div.page_nav a";
    RecyclerView categoryList;
    ArrayList<String> titlesArray = new ArrayList<>();
    ArrayList<String> linksArray = new ArrayList<>();
    ArrayList<String> nextArray = new ArrayList<>();
    FloatingActionButton nextButton;
    LVBlock lvBlock;
    String[] colors = {"#6258c4", "#673a3f", "#78d1b6", "#d725de", "#665fd1", "#9c6d57", "#fa2a55"};
    Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season_detail4_tv);
        lvBlock = (LVBlock) findViewById(R.id.block);
        random = new Random();

        Intent i = getIntent();
        url = i.getStringExtra("link");
        tit = i.getStringExtra("word");
        tag = i.getStringExtra("tag");

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
            for (Element listItem : firstLinks) {
                if (!TextUtils.isEmpty(String.valueOf(listItem.text()))) {
                    titlesArray.add(String.valueOf(listItem.text()));
                }
            }
            for (Element links : firstLinks) {
                if (!TextUtils.isEmpty(String.valueOf(links.text()))) {
                    linksArray.add(String.valueOf(links.attr("href")));
                }
            }
            for (Element nextLinks : nextLink) {
                if (!TextUtils.isEmpty(String.valueOf(nextLinks.text())) && nextLinks.text().contains("Next")) {
                    nextArray.add(String.valueOf(nextLinks.attr("href")));
                }
            }
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (nextArray.size() > 0) {
                        Intent i = new Intent(SeasonDetail4Tv.this, SeasonDetail4Tv.class);
                        i.putExtra("link", nextArray.get(0));
                        i.putExtra("word", tit);
                        startActivity(i);

                    } else {
                        Toast.makeText(SeasonDetail4Tv.this, "Sorry That's All", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            categoryList.setAdapter(new SeasonDetailAdapter(titlesArray, linksArray));
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                doc = Jsoup.connect(url).timeout(0).get();
                //firstUl = doc.select(Query);
                firstLinks = doc.select(LinksQuery);
                nextLink = doc.select(NextQuery);
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
            View v = LayoutInflater.from(SeasonDetail4Tv.this).inflate(R.layout.letters_layout, parent, false);
            return new SeasonHolderHolder(v);
        }

        @Override
        public void onBindViewHolder(final SeasonHolderHolder holder, int position) {
            if (titlesArray.get(position) != null) {
                holder.text.setText(words.get(position));
                int val = random.nextInt(colors.length);
                holder.cardView.setCardBackgroundColor(Color.parseColor(colors[val]));
            }
            holder.setAdamClickListener(new AdamClickListener() {
                @Override
                public void onClick(View v, int position, boolean isLongClick) {
                    YoYo.with(Techniques.RubberBand).duration(500).playOn(holder.mView);

                    Intent i = new Intent(SeasonDetail4Tv.this, VideoPlayer.class);
                    i.putExtra("link", links.get(position));
                    i.putExtra("word", words.get(position));
                    if ((tag != null && tag.equals("tv4mobile"))) {
                        i.putExtra("tag", tag);
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

        public SeasonHolderHolder(View itemView) {
            super(itemView);
            mView = itemView;
            text = (TextView) itemView.findViewById(R.id.letterText);
            cardView = (CardView) itemView.findViewById(R.id.card);
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
