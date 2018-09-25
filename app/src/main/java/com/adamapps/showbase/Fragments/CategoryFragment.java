package com.adamapps.showbase.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adamapps.showbase.Movie.MovieDetail;
import com.adamapps.showbase.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.clans.fab.FloatingActionButton;
import com.ldoublem.loadingviewlib.view.LVBlazeWood;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    private String genreQuery = "ul.sub-menu>li>a";

    String titleQuery = "ul.ulclear >div.movies-list>li.movie-item>div.item-detail>h2";
    String linkQuery = "div.ml-item>a";
    String imageQuery = "ul.ulclear >div.movies-list>li.movie-item>a>img";
    String nextLinkQuery = "ul.pagination>li>a";

    Elements genreElms;
    ArrayList<String> genreTitle = new ArrayList<>();
    ArrayList<String> genreLink = new ArrayList<>();
    RecyclerView genreList;
    LVBlazeWood wood;
    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    private Elements linkElm, imageElm, titleElm;
    private Element nextElement;
    private ArrayList<String> linkArray = new ArrayList<>();
    private ArrayList<String> titleArray = new ArrayList<>();
    private ArrayList<String> nextArray = new ArrayList<>();
    private ArrayList<String> imageArray = new ArrayList<>();
    String url = "https://yesmovies.to/movie/filter/movies.html/";

    FloatingActionButton backBtn, nextBtn;
    private Element prevElement;


    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        wood = view.findViewById(R.id.wood);
        nextBtn = view.findViewById(R.id.nextBtn);
        backBtn = view.findViewById(R.id.backBtn);
        mWaveSwipeRefreshLayout = view.findViewById(R.id.swip);

        mWaveSwipeRefreshLayout.setWaveColor(Color.argb(200, 230, 25, 85));
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new MyTask(url).execute();
            }
        });
        genreList = view.findViewById(R.id.genreList);


        new MyTask(url).execute();
    }

    public class MyTask extends AsyncTask {

        String url;

        public MyTask(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            nextBtn.setVisibility(View.GONE);
            backBtn.setVisibility(View.GONE);
            wood.setVisibility(View.VISIBLE);
            wood.startAnim(1000);
        }

        @Override
        protected void onPostExecute(Object o) {
            if (mWaveSwipeRefreshLayout.isRefreshing())
                mWaveSwipeRefreshLayout.setRefreshing(false);

            wood.stopAnim();
            wood.setVisibility(View.GONE);
            if (genreElms != null) {
                genreTitle.clear();
                genreLink.clear();
                for (Element genre : genreElms) {
                    genreTitle.add(genre.text());
                    genreLink.add(genre.attr("href"));
                }
                genreList.setLayoutManager(new LinearLayoutManager(getContext()));
                genreList.setAdapter(new GenreAdapter());
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Document doc = Jsoup.connect(url).timeout(0).get();
                genreElms = doc.select(genreQuery);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public class GenreTask extends AsyncTask {

        String url;

        public GenreTask(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            wood.setVisibility(View.VISIBLE);
            wood.startAnim(500);
        }

        @Override
        protected void onPostExecute(Object o) {

            wood.stopAnim();
            if (wood.getVisibility() == View.VISIBLE)
                wood.setVisibility(View.GONE);

            Toast.makeText(getActivity(), "Swipe Down To Refresh To Genres", Toast.LENGTH_SHORT).show();
            if (linkElm != null) {
                linkArray.clear();
                titleArray.clear();
                for (Element link : linkElm) {
                    linkArray.add(link.attr("href"));
                    titleArray.add(link.attr("title"));
                }
            }
            if (titleElm != null) {
                imageArray.clear();
                for (Element image : titleElm) {
                    imageArray.add(image.attr("data-original"));
                }

            }

            if (nextElement != null) {
                nextBtn.setEnabled(true);
                nextArray.clear();
                nextArray.add(nextElement.attr("href"));
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getActivity(), ""+nextElement.attr("href"), Toast.LENGTH_SHORT).show();

                        new GenreTask(nextElement.attr("href")).execute();
                    }
                });


            }


            if (nextElement == null) {
                nextBtn.setEnabled(false);
                //backBtn.setEnabled(false);
                nextBtn.setVisibility(View.VISIBLE);
                //backBtn.setVisibility(View.VISIBLE);
            } else {
                nextBtn.setEnabled(true);
                nextBtn.setVisibility(View.VISIBLE);
                //backBtn.setVisibility(View.VISIBLE);
            }

            if(prevElement==null){
                backBtn.setEnabled(false);
                backBtn.setVisibility(View.VISIBLE);
            }else{
                backBtn.setEnabled(true);
                backBtn.setVisibility(View.VISIBLE);
                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getActivity(), ""+prevElement.attr("href"), Toast.LENGTH_SHORT).show();
                        new GenreTask(prevElement.attr("href")).execute();
                    }
                });
            }

            genreList.setLayoutManager(new GridLayoutManager(getContext(), 2));
            genreList.setAdapter(new SelectedAdapter());
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Document doc = Jsoup.connect(url).timeout(0).get();

                titleElm = doc.select("div.ml-item>a>img");
                String nextQuery = "ul.pagination>li.next>a";
                String prevQuery = "ul.pagination>li.prev>a";
                prevElement = doc.selectFirst(prevQuery);
                nextElement = doc.selectFirst(nextQuery);

                linkElm = doc.select(linkQuery);


            } catch (IOException e) {
                e.printStackTrace();
            }
            return linkElm;
        }
    }

    public class GenreAdapter extends RecyclerView.Adapter<GenreHolder> {

        @NonNull
        @Override
        public GenreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.genre_layout, parent, false);
            return new GenreHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull GenreHolder holder, final int position) {
            holder.textView.setText(genreTitle.get(position));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.RubberBand).duration(500).playOn(v);
                    Toast.makeText(getActivity(), "" + linkArray.size(), Toast.LENGTH_SHORT).show();
                    new GenreTask(genreLink.get(position)).execute();
                    wood.setVisibility(View.VISIBLE);
                    wood.startAnim(1000);
                }
            });
        }

        @Override
        public int getItemCount() {
            return genreTitle.size();
        }
    }

    public class GenreHolder extends RecyclerView.ViewHolder {

        TextView textView;
        View mView;

        public GenreHolder(View itemView) {
            super(itemView);

            mView = itemView;
            textView = itemView.findViewById(R.id.genreTitle);
        }
    }

    public class SelectedAdapter extends RecyclerView.Adapter<SelectHolder> {

        @NonNull
        @Override
        public SelectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.letter_detail_layout, parent, false);
            return new SelectHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SelectHolder holder, final int position) {
            if (!imageArray.get(position).isEmpty())
                holder.setImage(getContext(), imageArray.get(position));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.RubberBand).duration(500).playOn(v);


                    if (position < titleArray.size()) {
                        Intent i = new Intent(getContext(), MovieDetail.class);
                        i.putExtra("title", titleArray.get(position));
                        i.putExtra("link", linkArray.get(position));
                        i.putExtra("image", imageArray.get(position));
                        //floatMenu.close(true);
                        startActivity(i);
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            return titleArray.size();
        }
    }

    public class SelectHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView rate, titleView;
        View mView;

        public SelectHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.movie_poster);
            rate = itemView.findViewById(R.id.movie_rating);
            titleView = itemView.findViewById(R.id.letterText);
            titleView.setSelected(true);
            mView = itemView;

        }

        void setImage(final Context c, final String image) {
            Picasso.with(c).load(image).error(R.drawable.noimage).placeholder(R.drawable.noimage).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
        }

    }

}
