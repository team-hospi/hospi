package com.gradproject.hospi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.gradproject.hospi.home.HomeActivity;

public class InquiryPushService extends Service {
    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "channel1";

    boolean isRun = true;
    NotificationCompat.Builder builder;
    NotificationManager manager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(manager.getNotificationChannel(CHANNEL_ID) == null){
                manager.createNotificationChannel(new NotificationChannel(
                        CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                ));

                builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            }
        }else{
            builder = new NotificationCompat.Builder(getApplicationContext());
        }
        if(intent == null){
            return Service.START_STICKY;
        }else{
            processCommand(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void processCommand(Intent intent){

            try{
                db.collection(Inquiry.DB_NAME)
                        .whereEqualTo("id", user.getEmail())
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                for (DocumentChange dc : value.getDocumentChanges()) {
                                    switch (dc.getType()) {
                                        case ADDED:
                                        case REMOVED:
                                            break;
                                        case MODIFIED:
                                            Log.d("pushthread", "Modified city: " + dc.getDocument().getData());
                                            push();
                                            break;
                                    }
                                }
                            }
                        });
                Thread.sleep(10000);
            }catch (Exception e){

            }

    }

    public void push() {
        Intent intent = new Intent(InquiryPushService.this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(InquiryPushService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle("문의 답변 등록 알림");
        builder.setContentText("문의글에 답변이 등록되었습니다.");
        builder.setSmallIcon(R.drawable.icon);
        builder.setContentIntent(pendingIntent);
        Notification noti = builder.build();

        manager.notify(1, noti);
    }
}