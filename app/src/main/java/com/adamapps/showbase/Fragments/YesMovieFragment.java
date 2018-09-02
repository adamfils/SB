package com.adamapps.showbase.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adamapps.showbase.Movie.MovieDetail;
import com.adamapps.showbase.R;
import com.adamapps.showbase.TvShow.MuviGridLayoutManager;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.github.clans.fab.FloatingActionButton;
import com.ldoublem.loadingviewlib.view.LVFunnyBar;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
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
public class YesMovieFragment extends Fragment {


    private String url = "https://yesmovies.to/movie/filter/movie/latest/all/all/all/all/all.html/";
    private String imageTitleQuery = "div.ml-item>a>img";
    private String linkQuery = "div.ml-item>a";

    private Elements titles, searchTitles, links, searchLinks;
    private Element nextElm;
    private Element backElm;


    private ArrayList<String> titleArray = new ArrayList<>();
    private ArrayList<String> imageArray = new ArrayList<>();
    private ArrayList<String> linkArray = new ArrayList<>();

    private ArrayList<String> searchTitleArray = new ArrayList<>();
    private ArrayList<String> searchImageArray = new ArrayList<>();
    private ArrayList<String> searchLinkArray = new ArrayList<>();

    private RecyclerView yesList;
    private FloatingActionButton nextBtn, backBtn;
    private LVFunnyBar funnyBar;
    private MaterialSearchView searchView;
    private TabLayout tabLayout;
    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;



    public YesMovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_yes_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        yesList = view.findViewById(R.id.yes_list);
        //yesList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        nextBtn = view.findViewById(R.id.nextBtn);
        backBtn = view.findViewById(R.id.backBtn);
        funnyBar = view.findViewById(R.id.funnyBar);



        View search = getActivity().findViewById(R.id.searchView);
        final View tabs = getActivity().findViewById(R.id.tab_layout);
        final View pager = getActivity().findViewById(R.id.pager);

        if (isTablet(getContext())) {
            Display getOrient = getActivity().getWindowManager().getDefaultDisplay();
            int orientation = Configuration.ORIENTATION_UNDEFINED;
            if (getOrient.getWidth() == getOrient.getHeight()) {
                orientation = Configuration.ORIENTATION_SQUARE;

            } else {
                if (getOrient.getWidth() < getOrient.getHeight()) {
                    orientation = Configuration.ORIENTATION_PORTRAIT;
                    yesList.setLayoutManager(new MuviGridLayoutManager(getContext(), 3));
                } else {
                    orientation = Configuration.ORIENTATION_LANDSCAPE;
                    yesList.setLayoutManager(new MuviGridLayoutManager(getContext(), 4));
                }
            }


        } else {
            Display getOrient = getActivity().getWindowManager().getDefaultDisplay();
            int orientation = Configuration.ORIENTATION_UNDEFINED;
            if (getOrient.getWidth() == getOrient.getHeight()) {
                orientation = Configuration.ORIENTATION_SQUARE;
            } else {
                if (getOrient.getWidth() < getOrient.getHeight()) {
                    orientation = Configuration.ORIENTATION_PORTRAIT;
                    yesList.setLayoutManager(new MuviGridLayoutManager(getContext(), 2));

                } else {
                    orientation = Configuration.ORIENTATION_LANDSCAPE;
                    yesList.setLayoutManager(new MuviGridLayoutManager(getContext(), 3));

                }
            }
        }

        if (search instanceof MaterialSearchView) {
            searchView = (MaterialSearchView) search;
            searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    new SearchTask(query).execute();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

            searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
                @Override
                public void onSearchViewShown() {
                    nextBtn.setVisibility(View.GONE);
                    backBtn.setVisibility(View.GONE);

                    //viewPager.setCurrentItem(0);
                    Animation slide_up = AnimationUtils.loadAnimation(getContext(),
                            R.anim.right_to_left);
                    if (tabs instanceof TabLayout) {
                        tabLayout = (TabLayout) tabs;
                        tabLayout.startAnimation(slide_up);
                        if (tabLayout.getSelectedTabPosition() != 0) {
                            TabLayout.Tab tab = tabLayout.getTabAt(0);
                            tab.select();
                        }
                        if (pager instanceof ViewPager) {
                            ViewPager vp = (ViewPager) pager;
                            //vp.beginFakeDrag();
                        }
                    }
                    slide_up.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (tabs instanceof TabLayout) {
                                tabLayout = (TabLayout) tabs;
                                tabLayout.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });


                }

                @Override
                public void onSearchViewClosed() {
                    new MyTask(url).execute();
                    nextBtn.setVisibility(View.VISIBLE);
                    backBtn.setVisibility(View.VISIBLE);
                    Animation slide_down = AnimationUtils.loadAnimation(getContext(),
                            R.anim.left_to_right);
                    if (tabs instanceof TabLayout) {
                        tabLayout = (TabLayout) tabs;
                        tabLayout.startAnimation(slide_down);
                    }
                    if (pager instanceof ViewPager) {
                        ViewPager vp = (ViewPager) pager;
                        //vp.endFakeDrag();
                    }
                    slide_down.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (tabs instanceof TabLayout) {
                                tabLayout = (TabLayout) tabs;
                                tabLayout.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
            });

        }


        new MyTask(url).execute();

        mWaveSwipeRefreshLayout = view.findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setWaveColor(Color.argb(200,213, 23, 78));
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new MyTask(url).execute();
            }
        });
    }

    public class MyTask extends AsyncTask {

        String url;

        public MyTask(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            funnyBar.setVisibility(View.VISIBLE);
            funnyBar.startAnim(500);
        }

        @Override
        protected void onPostExecute(Object o) {
            if (mWaveSwipeRefreshLayout.isRefreshing())
                mWaveSwipeRefreshLayout.setRefreshing(false);

            funnyBar.stopAnim();
            funnyBar.setVisibility(View.GONE);
            if (titles != null) {
                titleArray.clear();
                imageArray.clear();
                for (Element title : titles) {
                    titleArray.add(title.attr("title"));
                    imageArray.add(title.attr("data-original"));
                }

                yesList.setAdapter(new YesAdapter(titleArray, imageArray));
            }

            if (nextElm != null) {
                nextBtn.setEnabled(true);
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new MyTask(nextElm.attr("href")).execute();
                    }
                });
            }
            if (nextElm == null) {
                nextBtn.setEnabled(false);
            }
            if (backElm != null) {
                backBtn.setEnabled(true);
                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new MyTask(backElm.attr("href")).execute();
                    }
                });
            }
            if (backElm == null) {
                backBtn.setEnabled(false);
            }

            if (links != null) {
                linkArray.clear();
                for (Element link : links) {
                    linkArray.add(link.attr("href"));
                }
            }

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Document doc = Jsoup.connect(url).timeout(0).get();

                titles = doc.select(imageTitleQuery);
                String nextQuery = "ul.pagination>li.next>a";
                nextElm = doc.selectFirst(nextQuery);
                String backQuery = "ul.pagination>li.prev>a";
                backElm = doc.selectFirst(backQuery);
                links = doc.select(linkQuery);


            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public class SearchTask extends AsyncTask {

        String query;

        public SearchTask(String query) {
            this.query = query;
        }

        @Override
        protected void onPreExecute() {
            funnyBar.setVisibility(View.VISIBLE);
            funnyBar.startAnim(500);
        }

        @Override
        protected void onPostExecute(Object o) {
            funnyBar.stopAnim();
            funnyBar.setVisibility(View.GONE);

            if (searchTitles != null) {
                searchImageArray.clear();
                searchTitleArray.clear();
                for (Element tit : searchTitles) {
                    searchTitleArray.add(tit.attr("title"));
                    searchImageArray.add(tit.attr("data-original"));
                }

                yesList.setAdapter(new YesSearchAdapter(searchTitleArray, searchImageArray));
            }

            if (searchLinks != null) {
                searchLinkArray.clear();
                for (Element link : searchLinks) {
                    searchLinkArray.add(link.attr("href"));
                }
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String searchUrl = "https://yesmovies.to/search/";
            String finalQuery = searchUrl + query.replaceAll(" ", "+") + ".html";
            try {
                Document doc = Jsoup.connect(finalQuery).timeout(0).get();
                searchTitles = doc.select(imageTitleQuery);
                searchLinks = doc.select(linkQuery);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public class YesAdapter extends RecyclerView.Adapter<YesHolder> {

        ArrayList<String> title;
        ArrayList<String> image;

        public YesAdapter(ArrayList<String> title, ArrayList<String> image) {
            this.title = title;
            this.image = image;
        }

        @NonNull
        @Override
        public YesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.letter_detail_layout, parent, false);
            return new YesHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull YesHolder holder, final int position) {
            holder.setImage(getContext(), image.get(position));

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
            return image.size();
        }
    }

    public class YesHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView rate, titleView;
        View mView;

        public YesHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movie_poster);
            rate = itemView.findViewById(R.id.movie_rating);
            titleView = itemView.findViewById(R.id.letterText);
            titleView.setSelected(true);
            mView = itemView;
        }

        void setImage(final Context c, final String image) {
            if (!image.isEmpty())
                Picasso.with(c).load(image).error(R.drawable.noimage).placeholder(R.drawable.noimage).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
        }
    }

    public class YesSearchAdapter extends RecyclerView.Adapter<YesHolder> {

        ArrayList<String> title;
        ArrayList<String> image;

        public YesSearchAdapter(ArrayList<String> title, ArrayList<String> image) {
            this.title = title;
            this.image = image;
        }

        @NonNull
        @Override
        public YesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.letter_detail_layout, parent, false);
            return new YesHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull YesHolder holder, final int position) {
            holder.setImage(getContext(), image.get(position));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    YoYo.with(Techniques.RubberBand).duration(500).playOn(v);
                    if (position < searchTitleArray.size()) {
                        Intent i = new Intent(getContext(), MovieDetail.class);
                        i.putExtra("title", searchTitleArray.get(position));
                        i.putExtra("link", searchLinkArray.get(position));
                        i.putExtra("image", searchImageArray.get(position));
                        //floatMenu.close(true);
                        startActivity(i);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return searchTitleArray.size();
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}
