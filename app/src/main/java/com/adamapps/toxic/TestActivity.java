package com.adamapps.toxic;

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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ldoublem.loadingviewlib.view.LVBlock;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class TestActivity extends AppCompatActivity {

    ListView categoryList;

    private Document doc;
    String url = null;
    Elements firstUl, firstLinks, imageLinks;
    String Query = "li";
    String LinksQuery = "li a";
    String imageQuery = "img.poster";
    String describeQuery = "p.overview";
    String ratingQuery = "span.vote_average";

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
    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

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

        categoryList = (ListView) findViewById(R.id.letterDetailList);

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
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (nextArray.size() > 0) {
                        int pos = titlesArray.indexOf(nextArray.get(0));
                        url = linksArray.get(pos);
                        Intent i = new Intent(TestActivity.this, LetterDetail.class);
                        i.putExtra("link", linksArray.get(pos));
                        i.putExtra("word", tit);
                        startActivity(i);
                    } else {
                        Toast.makeText(TestActivity.this, "Sorry That's All", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            wordNew = titlesArray;
            categoryList.setAdapter(new ListAdapter(titlesArray,linksArray));
            //**categoryList.setAdapter(new LetterDetail.LettersAdapter(titlesArray, linksArray));
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
            }
            //Toast.makeText(TestActivity.this, "Total Images: "+imageArray.size(), Toast.LENGTH_SHORT).show();

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

    public class ListAdapter extends BaseAdapter{
        TextView text;
        TextView rating;
        ImageView imageView;
        CardView cardView;

        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> links = new ArrayList<>();

        public ListAdapter(ArrayList<String> titles, ArrayList<String> links) {
            this.titles = titles;
            this.links = links;
        }

        @Override
        public int getCount() {
            return titles.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(TestActivity.this).inflate(R.layout.letter_detail_layout,viewGroup,false);

            text = view.findViewById(R.id.letterText);
            cardView = view.findViewById(R.id.card);
            imageView = view.findViewById(R.id.movie_poster);
            rating = view.findViewById(R.id.movie_rating);
            text.setText(titles.get(position));
            int val = random.nextInt(colors.length);
            cardView.setBackgroundColor(Color.parseColor(colors[val]));
            text.setSelected(true);

            Toast.makeText(TestActivity.this, "List = "+position, Toast.LENGTH_SHORT).show();
            if (newestImage.size() > 0 && position < newestImage.size()) {
                Picasso.with(TestActivity.this).load(newestImage.get(position)).placeholder(R.drawable.videoplaceholder).into(imageView);
            }
            return view;
        }

        @Override
        public int getViewTypeCount() {
            return getCount();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        new MyTask().execute();
    }


}
