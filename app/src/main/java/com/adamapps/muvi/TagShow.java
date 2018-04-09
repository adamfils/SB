package com.adamapps.muvi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class TagShow extends AppCompatActivity {

    DatabaseReference showRef;
    String query = "ul.ulclear >div.movies-list>li.movie-item>div.item-detail>h2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_show);

        showRef = FirebaseDatabase.getInstance().getReference().child("Media");

        showRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("tag").getValue().equals("vex")) {
                        Toast.makeText(TagShow.this, "Tag Thing = " + ds.child("tag").getValue(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(TagShow.this, "" + ds.getKey(), Toast.LENGTH_SHORT).show();
                        String key = ds.getKey();
                        /*HashMap<String, Object> map = new HashMap<>();
                        map.put("tag", "vex");*/
                        FirebaseDatabase.getInstance().getReference().child("Media")
                                .child(key).removeValue();
                    } else {
                        //Toast.makeText(TagShow.this, "Failed", Toast.LENGTH_SHORT).show();
                        Toast.makeText(TagShow.this, "Failed = " + ds.getKey(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
