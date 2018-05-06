package com.adamapps.muvi.UserModels;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamapps.muvi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;

public class FeaturedMovies extends AppCompatActivity {

    FeatureCoverFlow coverFlow;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference recentRef;
    Query linkQuery;
    ArrayList<String> recentLinkArray = new ArrayList<>();
    ArrayList<String> recentImageArray = new ArrayList<>();
    ArrayList<String> recentTitleArray = new ArrayList<>();
    ArrayList<Long> recentTimeArray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_movies);

        coverFlow = (FeatureCoverFlow) findViewById(R.id.coverflow);

        firebaseDatabase = FirebaseDatabase.getInstance();

        recentRef = firebaseDatabase.getReference().child("Recent")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        recentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("link").getValue(String.class) != null)
                        recentLinkArray.add(ds.child("link").getValue(String.class));
                    if (ds.child("show").getValue(String.class) != null)
                        recentTitleArray.add(ds.child("show").getValue(String.class));
                    if (ds.child("thumbnail").getValue(String.class) != null)
                        recentImageArray.add(ds.child("thumbnail").getValue(String.class));
                    if (ds.child("time").getValue(Long.class) != null) {
                        recentTimeArray.add(ds.child("time").getValue(Long.class));
                    } else {
                        recentTimeArray.add(Long.parseLong("0000"));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        coverFlow.setAdapter(new ShowAdapter());
    }

    public class ShowAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return recentTitleArray.size();
        }

        @Override
        public Object getItem(int i) {
            return recentTitleArray.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if(rowView!=null){
                rowView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.cover_item,null);
                TextView textView = rowView.findViewById(R.id.cover_text);
                ImageView imageView = rowView.findViewById(R.id.cover_image);
                Picasso.with(getApplicationContext()).load(recentImageArray.get(i)).into(imageView);
                textView.setText(recentTitleArray.get(i));
            }
            return rowView;
        }
    }


}
