package com.adamapps.muvi.Movie;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.adamapps.muvi.R;
import com.github.clans.fab.FloatingActionButton;
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

public class SeeHDList extends AppCompatActivity {

    Elements linkElm, imageElm, titleElm, qualityElm;
    Elements nextElement;

    Elements iframeElm, detailElm;

    ArrayList<String> titleArray = new ArrayList<>();
    ArrayList<String> qualityArray = new ArrayList<>();
    ArrayList<String> linkArray = new ArrayList<>();
    ArrayList<String> picArray = new ArrayList<>();
    ArrayList<String> nextArray = new ArrayList<>();

    ArrayList<String> iframeArray = new ArrayList<>();
    ArrayList<String> detailArrat = new ArrayList<>();
    ArrayList<String> filteredArray = new ArrayList<>();


    String movieNameQuery = "div.post_thumb";
    String movieQualityQuery = "span.catic";
    String movieLinkQuery = "div.post_thumb>a";
    String movieImageQuery = "div.post_thumb>a>img";
    String movieNextQuery = "a.next";

    String imageQuery = "img.poster";
    String describeQuery = "p.overview";
    String ratingQuery = "span.vote_average";

    ArrayList<String> imageArray = new ArrayList<>();
    ArrayList<String> wordNew = new ArrayList<>();
    ArrayList<String> newestImage = new ArrayList<>();
    ArrayList<String> describeArray = new ArrayList<>();
    ArrayList<String> ratingArray = new ArrayList<>();

    RecyclerView hdList;
    FloatingActionButton nextFAB;

    private static String url = "http://www.seehd.pl/category/movies/";
    private Element imageElement;
    private Element descElement;
    private Element ratingElement;
    private String imageEndPoint = "https://www.themoviedb.org/search/tv?query=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_hdlist);

        Intent i = getIntent();
        if (i.getStringExtra("next") != null) {
            url = i.getStringExtra("next");
        }
        hdList = findViewById(R.id.seeHdList);
        nextFAB = findViewById(R.id.nextFAB);
        hdList.setLayoutManager(new GridLayoutManager(this, 2));


        new MovieTask().execute();

    }

    public class MovieTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            if (linkElm != null) {
                for (Element link : linkElm) {
                    if (!TextUtils.isEmpty(link.text()))
                        linkArray.add(link.attr("href"));
                }
            }

            if (imageElm != null) {
                for (Element image : imageElm) {
                    picArray.add(image.attr("src"));
                    //Toast.makeText(SeeHDList.this, "Got " + image.attr("src"), Toast.LENGTH_SHORT).show();

                }

            }

            if (qualityElm != null) {
                for (Element quality : qualityElm) {
                    qualityArray.add(quality.text());
                }

            }

            if (titleElm != null) {
                for (Element title : titleElm) {
                    titleArray.add(title.text());
                }
            }

            if (nextElement != null) {
                for (Element next : nextElement) {
                    nextArray.add(next.attr("href"));
                    Toast.makeText(SeeHDList.this, "Next Link: " + next.attr("href"), Toast.LENGTH_SHORT).show();
                }
            }

            nextFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), SeeHDList.class);
                    if (nextArray.size() > 0)
                        i.putExtra("next", nextArray.get(0));
                    startActivity(i);
                }
            });


            hdList.setAdapter(new SeeHDAdapter(linkArray, titleArray));


        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                String ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";
                Document doc = Jsoup.connect(url).userAgent(ua).followRedirects(true).timeout(0).get();
                linkElm = doc.select(movieLinkQuery);
                imageElm = doc.select(movieImageQuery);
                titleElm = doc.select(movieNameQuery);
                nextElement = doc.select(movieNextQuery);
                qualityElm = doc.select(movieQualityQuery);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return linkElm;
        }
    }

    public class MovieDetail extends AsyncTask {

        String link;
        String title;

        public MovieDetail(String link, String title) {
            this.link = link;
            this.title = title;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            if (iframeElm != null) {
                for (Element iframe : iframeElm) {
                    iframeArray.add(iframe.attr("src"));

                }
                Toast.makeText(SeeHDList.this, "Iframe Size = "+iframeArray.size(), Toast.LENGTH_SHORT).show();
                for(int i=0;i<iframeArray.size();i++){

                    FirebaseDatabase.getInstance().getReference().child("Test").child(title.substring(0,title.length()-17))
                            .child("video"+i).setValue(iframeArray.get(i));
                    //Toast.makeText(SeeHDList.this, "Iframe "+iframeArray.get(i), Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(SeeHDList.this, ""+iframeArray.size(), Toast.LENGTH_SHORT).show();
            }
            if (detailElm != null) {
                for (Element detail : detailElm) {
                    detailArrat.add(detail.text());

                }
                filteredArray.add(detailArrat.get(3));
                //Toast.makeText(SeeHDList.this, "Detail = " + filteredArray.get(0), Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                Document doc = Jsoup.connect(link).get();
                iframeElm = doc.select("iframe");
                detailElm = doc.select("p>strong");


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
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
                map.put("image", imageElement.attr("data-src"));
                map.put("desc", String.valueOf(descElement.text()));
                //FirebaseDatabase.getInstance().getReference().child("Film").child(nameImage.substring(0,nameImage.length()-17)).updateChildren(map);
            }

            if (ratingElement != null) {
                ratingArray.add(String.valueOf(ratingElement.text()));
            }
            newestImage = imageArray;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                String modName = nameImage.substring(0,nameImage.length()-17).replaceAll(" ", "+");
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


    public class SeeHDAdapter extends RecyclerView.Adapter<SeeHDHolder> {

        ArrayList<String> link;
        ArrayList<String> title;

        public SeeHDAdapter(ArrayList<String> link, ArrayList<String> title) {
            this.link = link;
            this.title = title;
        }

        @NonNull
        @Override
        public SeeHDHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(SeeHDList.this).inflate(R.layout.letter_detail_layout, parent, false);
            return new SeeHDHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SeeHDHolder holder, int position) {
            holder.setImage(getApplicationContext(), picArray.get(position));

            //new ImageTask(titleArray.get(holder.getAdapterPosition())).execute();
            int pos = holder.getAdapterPosition();
            new MovieDetail(link.get(pos), title.get(pos)).execute();
            HashMap<String,Object> map = new HashMap<>();
            map.put("title",titleArray.get(pos)
                    .substring(0,titleArray.get(holder.getAdapterPosition()).length()-17));
            map.put("image",picArray.get(pos));
            map.put("link",linkArray.get(pos));
            map.put("quality",qualityArray.get(pos));
            FirebaseDatabase.getInstance().getReference().child("Film")
                    .child(titleArray.get(holder.getAdapterPosition())
                            .substring(0,titleArray.get(holder.getAdapterPosition()).length()-17))
                    .updateChildren(map);

        }

        @Override
        public int getItemCount() {
            return picArray.size();
        }


    }

    public static class SeeHDHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public SeeHDHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.movie_poster);
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
