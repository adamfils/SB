package com.adamapps.muvi.Tests;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adamapps.muvi.AdamClickListener;
import com.adamapps.muvi.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.ldoublem.loadingviewlib.view.LVBlock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String url = "https://www.toxicwap.com/";
    private Document doc;
    Elements firstUl,firstLinks;
    String Query="div [data-role=\"listview\"] li";
    String LinksQuery="div [data-role=\"listview\"] li a";
    RecyclerView categoryList;
    ArrayList<String> titlesArray = new ArrayList<>();
    ArrayList<String> linksArray = new ArrayList<>();
    LVBlock lvBlock;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");

        mAdView = findViewById(R.id.adView);
        final AdRequest adRequest = new AdRequest.Builder().build();


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mAdView.loadAd(adRequest);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        android.support.v7.widget.Toolbar toolbarr = findViewById(R.id.toolbar);
        toolbarr.setTitle(R.string.app_name);
        toolbarr.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarr);

        lvBlock = findViewById(R.id.block);

        categoryList = findViewById(R.id.itemList);
        new MyTask().execute();
        categoryList.setLayoutManager(new LinearLayoutManager(this));


    }

    private class MyTask extends AsyncTask{
        @Override
        protected void onPreExecute() {
            lvBlock.isShadow(true);
            lvBlock.setViewColor(Color.rgb(245,209,22));
            lvBlock.startAnim(5000);
            lvBlock.startAnim();
        }

        @Override
        protected void onPostExecute(Object o) {
            lvBlock.stopAnim();
            lvBlock.setVisibility(View.GONE);
            for(Element listItem : firstUl){
                titlesArray.add(String.valueOf(listItem.text()));
            }
            for (Element links : firstLinks){
                linksArray.add(String.valueOf(links.attr("href")));
            }
            //Toast.makeText(MainActivity.this, "Size = "+linksArray.get(1), Toast.LENGTH_SHORT).show();
            categoryList.setAdapter(new ListAdapter(titlesArray,linksArray));
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

   /* public io.reactivex.Observable<String> getJsoupLink(){
        return io.reactivex.Observable.fromCallable(() ->{
            try {
                doc = Jsoup.connect(url).timeout(0).get();
                firstUl = doc.select(Query);
                firstLinks = doc.select(LinksQuery);

                return firstUl;
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }*/

    private class ListAdapter extends RecyclerView.Adapter<ListHolder>{

        ArrayList<String> words = new ArrayList<>();
        ArrayList<String> links = new ArrayList<>();

        public ListAdapter(ArrayList<String> words, ArrayList<String> links) {
            this.words = words;
            this.links = links;
        }

        int[] imageList = {R.drawable.ic_music_note_black_24dp,R.drawable.ic_movie_black_24dp,
                R.drawable.ic_tv_black_24dp,R.drawable.ic_android_black_24dp,R.drawable.ic_music_video_black_24dp,
                R.drawable.ic_wallpaper_black_24dp,R.drawable.ic_book_black_24dp};



        @Override
        public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.start_page_layout,parent,false);
            return new ListHolder(v);
        }

        @Override
        public void onBindViewHolder(final ListHolder holder, int position) {
            if(titlesArray.get(position)!=null)
            holder.text.setText(words.get(position));
            holder.images.setImageResource(imageList[position]);
            if(!words.get(position).contains("Series")){
                holder.cardView.setCardBackgroundColor(Color.parseColor("#c0c0c0"));
            }
            holder.setAdamClickListener(new AdamClickListener() {
                @Override
                public void onClick(View v, int position, boolean isLongClick) {
                    if (words.get(position).contains("Series")) {
                        YoYo.with(Techniques.RubberBand).duration(500).playOn(holder.mView);
                        Intent i = new Intent(MainActivity.this, LettersActivity.class);
                        i.putExtra("link", String.valueOf(links.get(position)));
                        i.putExtra("word", String.valueOf(words.get(position)));
                        startActivity(i);
                    }else{
                        Toast.makeText(MainActivity.this, "Still In Development.\n Enjoy Tv Series For Now", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return words.size();
        }
    }
    private class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        ImageView images;
        TextView text;
        View mView;
        AdamClickListener adamClickListener;
        CardView cardView;

        public ListHolder(View itemView) {
            super(itemView);
            mView = itemView;
            text = itemView.findViewById(R.id.listText);
            images = itemView.findViewById(R.id.listLogo);
            cardView = itemView.findViewById(R.id.cardView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        public void setAdamClickListener(AdamClickListener adamClickListener){
            this.adamClickListener = adamClickListener;
        }

        @Override
        public void onClick(View view) {
            adamClickListener.onClick(view,getAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View view) {
            adamClickListener.onClick(view,getAdapterPosition(),true);
            return true;
        }
    }
}
