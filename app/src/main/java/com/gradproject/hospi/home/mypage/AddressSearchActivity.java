package com.gradproject.hospi.home.mypage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.gradproject.hospi.R;
import com.gradproject.hospi.databinding.ActivityAddressSearchBinding;

public class AddressSearchActivity extends AppCompatActivity {
    private static final String ADDRESS = "https://hospipi.kro.kr/AddressSearch/Mobile";

    private Handler handler;

    String address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAddressSearchBinding binding = ActivityAddressSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.closeBtn.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        // WebView 초기화
        init_webView();

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void init_webView() {
        // WebView 설정
        WebView webView = findViewById(R.id.webView_address);

        // JavaScript 허용
        webView.getSettings().setJavaScriptEnabled(true);

        // JavaScript의 window.open 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);


        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        webView.addJavascriptInterface(new AndroidBridge(), "hospi");

        // web client 를 chrome 으로 설정
        webView.setWebChromeClient(new WebChromeClient());

        // webview url load. php 파일 주소
        webView.loadUrl(ADDRESS);

    }

    @SuppressWarnings("unused")
    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(() -> {
                address = String.format("(%s) %s %s", arg1, arg2, arg3);
                Intent intent = new Intent();
                intent.putExtra("address", address);
                setResult(RESULT_OK, intent);
                finish();

                // WebView를 초기화 하지않으면 재사용할 수 없음
                init_webView();
            });
        }
    }
}