package com.gradproject.hospi.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gradproject.hospi.BuildConfig;
import com.gradproject.hospi.databinding.ActivityPrescriptionInfoPopUpBinding;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class PrescriptionInfoPopUpActivity extends AppCompatActivity {
    private static final String TAG = "PrescriptionInfoPopUpActivity";
    private ActivityPrescriptionInfoPopUpBinding binding;

    Prescription prescription;
    ArrayList<String> seqList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrescriptionInfoPopUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.closeBtn.setOnClickListener(v -> onBackPressed());

        try{
            prescription = (Prescription) getIntent().getSerializableExtra("prescription");
            if(prescription.getMedicine() != null){
                seqList = (ArrayList<String>) prescription.getMedicine();
                binding.loadingLayout.setVisibility(View.VISIBLE);
                binding.nothingInfoView.setVisibility(View.GONE);
            }else{
                throw new NullPointerException();
            }
            requestMedicineData();
        }catch(Exception e){
            e.printStackTrace();
            binding.loadingLayout.setVisibility(View.GONE);
            binding.infoLayout.setVisibility(View.GONE);
            binding.nothingInfoView.setVisibility(View.VISIBLE);
        }
    }

    private void requestMedicineData(){
        new Thread(() -> {
            try{
                for(int i=0; i<seqList.size(); i++){
                    Medicine medicine = getMedicineInfo(seqList.get(i));
                    Bitmap bitmap = getMedicineImage(medicine.getImageUrl());

                    int finalI = i;
                    runOnUiThread(() -> {
                        TextView nameTxt = new TextView(getApplicationContext());
                        TextView chartTxt = new TextView(getApplicationContext());
                        TextView classTxt = new TextView(getApplicationContext());
                        nameTxt.setText(medicine.getItemName());
                        nameTxt.setTextColor(Color.BLACK);
                        nameTxt.setTextSize(20);
                        chartTxt.setText(medicine.getChart());
                        classTxt.setText(medicine.getClassName());

                        ImageView imageView = new ImageView(getApplicationContext());
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        if(finalI != seqList.size()-1){
                            layoutParams.bottomMargin = 100;
                        }else{
                            layoutParams.bottomMargin = 50;
                        }
                        imageView.setLayoutParams(layoutParams);
                        imageView.setImageBitmap(bitmap);
                        imageView.invalidate();

                        binding.infoLayout.addView(nameTxt);
                        binding.infoLayout.addView(chartTxt);
                        binding.infoLayout.addView(classTxt);
                        binding.infoLayout.addView(imageView);
                    });
                }

                runOnUiThread(() -> {
                    binding.infoLayout.setVisibility(View.VISIBLE);
                    binding.loadingLayout.setVisibility(View.GONE);
                });
            }catch (Exception e){
                e.printStackTrace();
                runOnUiThread(() -> {
                    binding.loadingLayout.setVisibility(View.GONE);
                    binding.infoLayout.setVisibility(View.GONE);
                    binding.nothingInfoView.setVisibility(View.VISIBLE);
                });
            }
        }).start();
    }

    private Bitmap getMedicineImage(String urlStr) throws IOException {
        HashMap<String, Bitmap> bitmapHash = new HashMap<>();
        Bitmap bitmap;
        URL url = new URL(urlStr);
        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

        bitmapHash.put(urlStr,bitmap);

        return bitmap;
    }

    private Medicine getMedicineInfo(String itemSeq) throws IOException, XmlPullParserException {
        final String apiUrl = "http://apis.data.go.kr/1470000/MdcinGrnIdntfcInfoService/getMdcinGrnIdntfcInfoList?"
                + "serviceKey=" + BuildConfig.MEDICINE_API_KEY
                + "&item_seq=" + itemSeq
                + "&pageNo=1&numOfRows=1";

        Medicine medicine = new Medicine();
        URL url= new URL(apiUrl);//문자열로 된 요청 url을 URL 객체로 생성.
        InputStream is= url.openStream(); //url위치로 입력스트림 연결

        XmlPullParserFactory factory= XmlPullParserFactory.newInstance();//xml파싱을 위한
        XmlPullParser xpp= factory.newPullParser();
        xpp.setInput( new InputStreamReader(is, StandardCharsets.UTF_8) ); //inputstream 으로부터 xml 입력받기

        String tag;

        xpp.next();
        int eventType= xpp.getEventType();
        while( eventType != XmlPullParser.END_DOCUMENT ){
            switch( eventType ){
                case XmlPullParser.START_DOCUMENT:
                    Log.d(TAG, "파싱 시작");
                    break;

                case XmlPullParser.START_TAG:
                    tag= xpp.getName();//테그 이름 얻어오기

                    switch (tag) {
                        case "ITEM_NAME":
                            xpp.next();
                            medicine.setItemName(xpp.getText());
                            break;
                        case "CHART":
                            xpp.next();
                            medicine.setChart(xpp.getText());
                            break;
                        case "ITEM_IMAGE":
                            xpp.next();
                            medicine.setImageUrl(xpp.getText());
                            break;
                        case "CLASS_NAME":
                            xpp.next();
                            medicine.setClassName(xpp.getText());
                            break;
                    }
                    break;

                case XmlPullParser.TEXT:
                case XmlPullParser.END_TAG:
                    break;
            }

            eventType= xpp.next();
        }

        Log.d(TAG, "파싱 끝\n");
        return medicine;
    }
}