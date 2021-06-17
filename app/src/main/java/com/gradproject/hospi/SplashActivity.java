package com.gradproject.hospi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler();
        runnable = () -> {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkCheck();
    }

    private void networkCheck(){
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected){
            handler.postDelayed(runnable,700);
        }else{
            notConnectedAlert();
        }
    }

    private void notConnectedAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this)
                .setCancelable(false)
                .setMessage("데이터 설정 확인 후 다시 시도해주세요.")
                .setPositiveButton("다시 시도", (dialog, which) -> networkCheck())
                .setNegativeButton("앱 종료", (dialog, which) -> finish());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}