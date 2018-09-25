package com.adamapps.showbase.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.adamapps.showbase.Movie.MovieDetail;
import com.adamapps.showbase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationService extends Service {

    ArrayList<String> detailArray = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseDatabase.getInstance().getReference().child("Notify")
                .child("show").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnap) {
                if (dataSnap.getValue(Boolean.class) != null && dataSnap.getValue(Boolean.class)) {
                    FirebaseDatabase.getInstance().getReference().child("Notify")
                            .child("detail").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            detailArray.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                detailArray.add(ds.getValue(String.class));
                                //Toast.makeText(NotificationService.this, "Val = "+ds.getValue(), Toast.LENGTH_SHORT).show();
                            }

                            Intent intent = new Intent(NotificationService.this, MovieDetail.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("link", detailArray.get(2));
                            intent.putExtra("image", detailArray.get(1));
                            intent.putExtra("title", detailArray.get(3));
                            PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                            String CHANNEL_ID = "adam";
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(NotificationService.this, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_logo)
                                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                            R.drawable.ic_logo2))
                                    .setContentTitle(detailArray.get(3))
                                    .setContentText(detailArray.get(0))
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .bigText(detailArray.get(0)))
                                    .setContentIntent(pendingIntent)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setAutoCancel(true);

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NotificationService.this);

                            // notificationId is a unique int for each notification that you must define
                            if (dataSnap.getValue(Boolean.class) && FirebaseAuth.getInstance().getCurrentUser() != null)
                                notificationManager.notify(1, mBuilder.build());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return START_STICKY;
    }


}
