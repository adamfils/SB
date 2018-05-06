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
import android.widget.TextView;

import com.adamapps.muvi.AdamClickListener;
import com.adamapps.muvi.R;
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

public class LettersActivity extends AppCompatActivity {

    //public static final String url = "https://www.toxicwap.com/";

    private Document doc;
    String url=null;
    Elements firstUl,firstLinks;
    String Query="li";
    String LinksQuery="li a";
    RecyclerView categoryList;
    ArrayList<String> titlesArray = new ArrayList<>();
    ArrayList<String> linksArray = new ArrayList<>();
    LVBlock lvBlock;
    String[] colors = {"#6258c4","#673a3f","#78d1b6","#d725de","#665fd1","#9c6d57","#fa2a55"};
    Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letters);

        random = new Random();

        Intent i = getIntent();
        url = i.getStringExtra("link");
        String tit = i.getStringExtra("word");

        android.support.v7.widget.Toolbar toolbarr = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        if(tit!=null){
            toolbarr.setTitle(tit);
        }else{
            toolbarr.setTitle(R.string.app_name);
        }
        toolbarr.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarr);

        lvBlock = (LVBlock)findViewById(R.id.block);


        categoryList = (RecyclerView)findViewById(R.id.lettersList);
        new MyTask().execute();
        //categoryList.setLayoutManager(new GridLayoutManager(this,4));
        categoryList.setLayoutManager(new LinearLayoutManager(this));
    }

    private class MyTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            lvBlock.setViewColor(Color.parseColor("#c2b709"));
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

            //Toast.makeText(LettersActivity.this, "Size = "+linksArray.get(1), Toast.LENGTH_SHORT).show();
            categoryList.setAdapter(new LettersAdapter(titlesArray,linksArray));
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
    private class LettersAdapter extends RecyclerView.Adapter<LettersActivity.LettersHolder>{

        ArrayList<String> words = new ArrayList<>();
        ArrayList<String> links = new ArrayList<>();

        public LettersAdapter(ArrayList<String> words, ArrayList<String> links) {
            this.words = words;
            this.links = links;
        }

        @Override
        public LettersActivity.LettersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(LettersActivity.this).inflate(R.layout.letters_layout,parent,false);
            return new LettersActivity.LettersHolder(v);
        }

        @Override
        public void onBindViewHolder(final LettersActivity.LettersHolder holder, int position) {
            //Toast.makeText(LettersActivity.this, "Pos1 = "+position, Toast.LENGTH_SHORT).show();
            if(titlesArray.get(position)!=null) {
                holder.text.setText(words.get(position));
                int val = random.nextInt(colors.length);
                holder.cardView.setCardBackgroundColor(Color.parseColor(colors[val]));
            }
            //holder.images.setImageResource(imageList[position]);
            holder.setAdamClickListener(new AdamClickListener() {
                @Override
                public void onClick(View v, int position, boolean isLongClick) {
                    YoYo.with(Techniques.RubberBand).duration(500).playOn(holder.mView);
                    Intent i = new Intent(LettersActivity.this,LetterDetail.class);
                    i.putExtra("link",String.valueOf(links.get(position)));
                    i.putExtra("word",String.valueOf(words.get(position)));
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return words.size();
        }
    }
    private class LettersHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        TextView text;
        View mView;
        AdamClickListener adamClickListener;
        CardView cardView;

        public LettersHolder(View itemView) {
            super(itemView);
            mView = itemView;
            text = (TextView)itemView.findViewById(R.id.letterText);
            cardView = (CardView)itemView.findViewById(R.id.card);
            text.setSelected(true);
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
