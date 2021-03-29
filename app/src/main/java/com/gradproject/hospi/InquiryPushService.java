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

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.gradproject.hospi.home.HomeActivity;

public class InquiryPushService extends Service {
    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "channel1";

    ServiceThread thread;
    NotificationCompat.Builder builder;
    NotificationManager manager;
    Notification noti;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //서비스가 종료될 때 할 작업
    @Override
    public void onDestroy() {
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread( handler );
        thread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.start();

        if(intent == null){
            return Service.START_STICKY;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    class myServiceHandler extends Handler {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(android.os.Message msg) {
            push();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void push() {
        Intent intent = new Intent(InquiryPushService.this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(InquiryPushService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        manager.createNotificationChannel(new NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
        ));

        builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);

        builder.setContentTitle("문의 답변 등록 알림");
        builder.setContentText("문의글에 답변이 등록되었습니다.");
        builder.setSmallIcon(R.drawable.icon);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        noti = builder.build();
        manager.notify(1, noti);

        startForeground(1, noti);

        // TODO: 알림 지워지지 않는 문제 수정 필요
    }
}