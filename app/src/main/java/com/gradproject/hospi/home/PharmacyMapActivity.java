package com.gradproject.hospi.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gradproject.hospi.BuildConfig;
import com.gradproject.hospi.R;
import com.gradproject.hospi.rest.category.Document;
import com.gradproject.hospi.rest.category.CategoryService;
import com.gradproject.hospi.rest.category.ResultCategorySearch;
import com.gradproject.hospi.databinding.ActivityPharmacyMapBinding;
import com.gradproject.hospi.utils.GpsTracker;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PharmacyMapActivity extends AppCompatActivity{
    private static final String TAG = "PharmacyMapActivity";
    private static final String URL = "https://dapi.kakao.com/";

    GpsTracker gpsTracker;
    MapView mapView;
    MapPoint mapPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPharmacyMapBinding binding = ActivityPharmacyMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gpsTracker = new GpsTracker(getApplicationContext());

        // 기본 위치값
        mapPoint = MapPoint.mapPointWithGeoCoord(37.5579452, 126.9941904);

        binding.closeBtn.setOnClickListener(v -> onBackPressed());

        mapView = new MapView(PharmacyMapActivity.this);
        mapView.setCalloutBalloonAdapter(new CustomBalloonAdapter(getLayoutInflater()));
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        binding.mapView.addView(mapView);
        mapView.setZoomLevel(3, true); // 맵 줌레벨 설정
        mapView.setMapCenterPoint(mapView.getMapCenterPoint(), true);

        binding.gpsRefreshBtn.setOnClickListener(v -> {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            mapView.setZoomLevel(3, true);
            mapPoint = MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            mapView.setMapCenterPoint(mapPoint, true);
            mapView.removeAllPOIItems();
            getPharmacyData(gpsTracker.getLongitude(), gpsTracker.getLatitude());
            endTrackingModeThread();
        });

        getPharmacyData(gpsTracker.getLongitude(), gpsTracker.getLatitude());
        endTrackingModeThread();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void endTrackingModeThread(){
        new Thread(() -> {
            try{
                Thread.sleep(5000);
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            }catch(Exception ignored){}
        }).start();
    }

    private void setMarker(Double longitude, Double latitude, Document doc){
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(doc.getPlaceName());
        marker.setUserObject(doc);
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        // 기본으로 제공하는 BluePin 마커 모양.
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(marker);
    }

    private void getPharmacyData(Double longitude, Double latitude){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CategoryService as = retrofit.create(CategoryService.class);

        for(int i=1; i<=20; i++){
            Call<ResultCategorySearch> call = as.getSearchCategoryPoint(
                    BuildConfig.KAKAO_REST_API_KEY, "PM9", String.valueOf(longitude), String.valueOf(latitude),
                    "20000", String.valueOf(i), "15");

            call.enqueue(new Callback<ResultCategorySearch>() {
                @Override
                public void onResponse(@NonNull Call<ResultCategorySearch> call, @NonNull Response<ResultCategorySearch> response) {
                    if(response.isSuccessful()){
                        Log.d(TAG, "Raw: " + response.raw());
                        Log.d(TAG, "Body: " + response.body());
                        ResultCategorySearch result = response.body();
                        ArrayList<Document> pharmacyInfo = null;
                        if (result != null) {
                            pharmacyInfo = (ArrayList<Document>) result.getDocuments();
                        }

                        if (pharmacyInfo != null) {
                            for(Document doc : pharmacyInfo){
                                setMarker(Double.parseDouble(doc.getX()), Double.parseDouble(doc.getY()), doc);
                            }
                        }

                    }else{
                        Log.d(TAG, "Error");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResultCategorySearch> call, @NonNull Throwable t) {
                    Log.d(TAG, "통신 실패: " + t.getMessage());
                }
            });
        }
    }

    static class CustomBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        @SuppressLint("InflateParams")
        public CustomBalloonAdapter(LayoutInflater inflater) {
            mCalloutBalloon = inflater.inflate(R.layout.balloon_layout3, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            Document doc = (Document) poiItem.getUserObject();
            ((TextView)mCalloutBalloon.findViewById(R.id.pharmacyNameTxt)).setText(doc.getPlaceName());
            ((TextView)mCalloutBalloon.findViewById(R.id.pharmacyTelTxt)).setText(doc.getPhone());
            ((TextView)mCalloutBalloon.findViewById(R.id.pharmacyAddressTxt)).setText(doc.getRoadAddressName());
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return getCalloutBalloon(poiItem);
        }
    }
}