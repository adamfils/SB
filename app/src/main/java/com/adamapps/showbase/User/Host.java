package com.adamapps.showbase.User;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.adamapps.showbase.Fragments.CategoryFragment;
import com.adamapps.showbase.Adapter.TabPagerAdapter;
import com.adamapps.showbase.Fragments.YesMovieFragment;
import com.adamapps.showbase.Fragments.YesShowFragment;
import com.adamapps.showbase.R;
import com.adamapps.showbase.Services.NotificationService;
import com.adamapps.showbase.StartUp.Welcome;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

public class Host extends AppCompatActivity {

    MaterialSearchView searchView;
    //FloatingActionMenu floatMenu;
    FirebaseAuth auth;
    FirebaseUser user;
    ViewPager viewPager;
    DatabaseReference updateRef;
    InterstitialAd interstitialAd;
    AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);



        Boolean isServiceRunning = ServiceTools.isServiceRunning(
                this.getApplicationContext(),
                NotificationService.class);

        if (!isServiceRunning) {
            Intent in = new Intent(this, NotificationService.class);
            startService(in);
        }

        adView = new AdView(this, "252221818766145_253372971984363", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();

        interstitialAd = new InterstitialAd(this,"252221818766145_252221892099471");
        //AdSettings.addTestDevice("0165ff96-12fd-4485-9221-0f61e306b990");
        interstitialAd.loadAd();

        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

        searchView = findViewById(R.id.searchView);


        updateRef = FirebaseDatabase.getInstance().getReference("ShowbaseLock/v1,1/lock");
        updateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AlertDialog alertDialog = null;
                if (dataSnapshot.getValue(Boolean.class)!=null&&dataSnapshot.getValue(Boolean.class)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Host.this);
                    View v = getLayoutInflater().inflate(R.layout.update_dialog_layout, null);
                    Button update = v.findViewById(R.id.warning_update_btn);
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adamapps.muvi"));
                            FirebaseDatabase.getInstance().getReference("update/link2").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataSnapshot.getValue(String.class)));
                                        startActivity(intent);
                                        YoYo.with(Techniques.RubberBand).duration(400).playOn(v);
                                    } else {
                                        Toast.makeText(Host.this, "No Update Link Found", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    });

                    builder.setView(v);
                    alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();





        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);

        configureTabLayout();

    }

    public void configureTabLayout() {
        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Genre"));
        tabLayout.addTab(tabLayout.newTab().setText("Movie"));
        tabLayout.addTab(tabLayout.newTab().setText("Tv Show"));

        viewPager = findViewById(R.id.pager);



        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager());


        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() != 0) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shrink);
                    //floatMenu.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //floatMenu.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
                /*if (tab.getPosition() == 0) {
                    return;
                    //floatMenu.setVisibility(View.VISIBLE);
                }*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }

    private void setupViewPager(ViewPager viewPager) {
        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CategoryFragment(), "GENRE");
        adapter.addFragment(new YesMovieFragment(), "Movie");
        adapter.addFragment(new YesShowFragment(), "Show");
        viewPager.setAdapter(adapter);
    }
    public void setViewPager(int fragmentNumber){
        viewPager.setCurrentItem(fragmentNumber);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, Welcome.class));
        }
        if (item.getItemId() == R.id.action_about) {
            startActivity(new Intent(this, About.class));
        }
        if (item.getItemId() == R.id.action_favorite) {
            startActivity(new Intent(this, Favorite.class));
        }
        return true;
    }

    long lastPress;

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPress > 5000 && getIntent().getStringExtra("url") == null) {
                Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG).show();
                lastPress = currentTime;
            } else {
                super.onBackPressed();
            }
            //super.onBackPressed();
        }


    }



    public static class ServiceTools {
        //private String LOG_TAG = ServiceTools.class.getName();

        public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
            final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

            for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
                //Log.d(SyncStateContract.Constants.TAG, String.format("Service:%s", runningServiceInfo.service.getClassName()));
                if (runningServiceInfo.service.getClassName().equals(serviceClass.getName())) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.loadAd();
    }
}
