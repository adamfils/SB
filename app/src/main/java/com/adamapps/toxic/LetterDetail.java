package com.adamapps.toxic;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import java.util.Random;

public class LetterDetail extends AppCompatActivity {

    private Document doc;
    String url = null;
    Elements firstUl, firstLinks, imageLinks;
    String Query = "li";
    String LinksQuery = "li a";
    String imageQuery = "img.poster";
    String describeQuery = "p.overview";
    String ratingQuery = "span.vote_average";
    RecyclerView categoryList;
    ArrayList<String> titlesArray = new ArrayList<>();
    ArrayList<String> linksArray = new ArrayList<>();
    ArrayList<String> nextArray = new ArrayList<>();
    ArrayList<String> wweArray = new ArrayList<>();
    ArrayList<String> smackArray = new ArrayList<>();
    ArrayList<String> imageArray = new ArrayList<>();
    ArrayList<String> wordNew = new ArrayList<>();
    ArrayList<String> newestImage = new ArrayList<>();
    ArrayList<String> describeArray = new ArrayList<>();
    ArrayList<String> ratingArray = new ArrayList<>();
    Element imageElement, descElement, ratingElement;
    FloatingActionButton nextButton;

    LVBlock lvBlock;
    String tit = null;
    String[] colors = {"#6258c4", "#673a3f", "#78d1b6", "#d725de", "#665fd1", "#9c6d57", "#fa2a55"};
    Random random;
    String imageEndPoint = "https://www.themoviedb.org/search/tv?query=";
    int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_detail);

        lvBlock = (LVBlock) findViewById(R.id.block);

        random = new Random();

        Intent i = getIntent();
        url = i.getStringExtra("link");
        tit = i.getStringExtra("word");

        android.support.v7.widget.Toolbar toolbarr = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        if (tit != null) {
            toolbarr.setTitle(tit);
        } else {
            toolbarr.setTitle(R.string.app_name);
        }
        toolbarr.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarr);

        categoryList = (RecyclerView) findViewById(R.id.letterDetailList);


        categoryList.setLayoutManager(new GridLayoutManager(this, 2));
        nextButton = (FloatingActionButton) findViewById(R.id.next_btn);

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
                if (!TextUtils.isEmpty(String.valueOf(listItem.text()))) {
                    titlesArray.add(String.valueOf(listItem.text()));
                    if (String.valueOf(listItem.text()).contains("Next")) {
                        nextArray.add(String.valueOf(listItem.text()));
                    }
                    if (String.valueOf(listItem.text()).equals("WWE")) {
                        wweArray.add(String.valueOf(listItem.text()));
                    }
                    if (String.valueOf(listItem.text()).contains("Smackdown")) {
                        smackArray.add(String.valueOf(listItem.text()));
                    }
                }
            }

            for (Element links : firstLinks) {
                if (!TextUtils.isEmpty(String.valueOf(links.text()))) {
                    linksArray.add(String.valueOf(links.attr("href")));
                }
            }

            for (int i = 0; i < titlesArray.size(); i++) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("title", titlesArray.get(i));
                map.put("link", linksArray.get(i));
                map.put("nameSearch", titlesArray.get(i).toLowerCase());
                /*if (imageElement.attr("data-src") != null)
                    map.put("image", imageElement.attr("data-src"));*/
                //num = num + 1;
                FirebaseDatabase.getInstance().getReference().child("Media").child(titlesArray.get(i)).updateChildren(map);
            }


            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (nextArray.size() > 0) {
                        int pos = titlesArray.indexOf(nextArray.get(0));
                        url = linksArray.get(pos);
                        Intent i = new Intent(LetterDetail.this, LetterDetail.class);
                        i.putExtra("link", linksArray.get(pos));
                        i.putExtra("word", tit);
                        startActivity(i);
                    } else {
                        Toast.makeText(LetterDetail.this, "Sorry That's All", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            wordNew = titlesArray;
            categoryList.setAdapter(new LettersAdapter(titlesArray, linksArray));
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                doc = Jsoup.connect(url).timeout(0).get();
                firstUl = doc.select(Query);
                firstLinks = doc.select(LinksQuery);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return firstUl;
        }

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

            if (imageElement != null && descElement != null) {
                imageArray.add(imageElement.attr("data-src"));
                describeArray.add(String.valueOf(descElement.text()));

                HashMap<String, Object> map = new HashMap<>();
                map.put("image",imageElement.attr("data-src"));
                map.put("desc", String.valueOf(descElement.text()));
                FirebaseDatabase.getInstance().getReference().child("Media").child(nameImage).updateChildren(map);
            }

            if (ratingElement != null) {
                ratingArray.add(String.valueOf(ratingElement.text()));
            }
            newestImage = imageArray;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                String modName = nameImage.replaceAll(" ", "+");
                String url2 = imageEndPoint + modName;
                Document imageDoc = Jsoup.connect(url2).timeout(0).get();
                imageElement = imageDoc.selectFirst(imageQuery);
                descElement = imageDoc.selectFirst(describeQuery);
                ratingElement = imageDoc.selectFirst(ratingQuery);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return imageElement;
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
            View v = LayoutInflater.from(LetterDetail.this).inflate(R.layout.letter_detail_layout, parent, false);
            return new DetailHolder(v);
        }

        @Override
        public void onBindViewHolder(final DetailHolder holder, final int position) {
            holder.setIsRecyclable(true);
            //Toast.makeText(LetterDetail.this, "Pos1 = "+position, Toast.LENGTH_SHORT).show();

            if (titlesArray.get(position) != null) {
                holder.text.setText(words.get(position));
                int val = random.nextInt(colors.length);
                holder.cardView.setCardBackgroundColor(Color.parseColor(colors[val]));
                if (ratingArray.size() > 0 && position < ratingArray.size())
                    holder.rating.setText(ratingArray.get(position));
                new ImageTask(titlesArray.get(position)).execute();
                if (newestImage.size() > 0 && position < newestImage.size() && words.get(position).equals(titlesArray.get(position))) {
                    Picasso.with(LetterDetail.this).load(newestImage.get(position)).placeholder(R.drawable.videoplaceholder).into(holder.imageView);

                }

            }
            holder.setAdamClickListener(new AdamClickListener() {
                @Override
                public void onClick(View v, int position, boolean isLongClick) {
                    YoYo.with(Techniques.RubberBand).duration(500).playOn(holder.mView);
                    if (position == words.size() - 1 && words.get(words.size() - 1).contains("Next")) {
                        return;
                    }

                    if (smackArray.size() > 0) {
                        Intent i = new Intent(LetterDetail.this, SeasonDetail.class);
                        if (url.contains("toxicunrated")) {
                            String value = "https://toxicunrated.com" + String.valueOf(links.get(position));
                            i.putExtra("link", value);
                        } else {
                            String value = "https://toxicwap.com" + String.valueOf(links.get(position));
                            i.putExtra("link", value);
                        }
                        startActivity(i);
                        return;
                    }
                    if (wweArray.size() > 0) {
                        int pos = titlesArray.indexOf("WWE");
                        //String link = linksArray.get(pos);
                        Intent i = new Intent(LetterDetail.this, LetterDetail.class);
                        i.putExtra("link", String.valueOf(links.get(pos)));
                        startActivity(i);
                        return;
                    }


                    Intent i = new Intent(LetterDetail.this, SeasonsActivity.class);
                    i.putExtra("link", String.valueOf(links.get(position)));
                    i.putExtra("word", String.valueOf(words.get(position)));
                    if (describeArray.size() > 0 && position < describeArray.size() - 1) {
                        i.putExtra("desc", describeArray.get(position));
                    }
                    if (newestImage.size() > 0 && newestImage.get(position) != null) {
                        i.putExtra("image", newestImage.get(position));
                    }

                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return words.size();
        }
    }

    private class DetailHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView text;
        TextView rating;
        View mView;
        ImageView imageView;
        AdamClickListener adamClickListener;
        CardView cardView;


        public DetailHolder(View itemView) {
            super(itemView);
            mView = itemView;
            text = (TextView) itemView.findViewById(R.id.letterText);
            cardView = (CardView) itemView.findViewById(R.id.card);
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

    @Override
    protected void onStart() {
        super.onStart();
        new MyTask().execute();
    }
}
