package com.gradproject.hospi.home.hospital;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.BuildConfig;
import com.gradproject.hospi.R;
import com.gradproject.hospi.rest.address.Document;
import com.gradproject.hospi.rest.address.AddressService;
import com.gradproject.hospi.rest.address.ResultSearchAddressPoint;
import com.gradproject.hospi.rest.address.RoadAddress;
import com.gradproject.hospi.databinding.ActivityHospitalMapBinding;
import com.gradproject.hospi.home.search.Hospital;
import com.gradproject.hospi.utils.GpsTracker;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HospitalMapActivity extends AppCompatActivity implements MapView.POIItemEventListener {
    private static final String TAG = "HospitalMapActivity";
    private static final String URL = "https://dapi.kakao.com/";

    GpsTracker gpsTracker;
    MapView mapView;
    MapPoint mapPoint;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHospitalMapBinding binding = ActivityHospitalMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gpsTracker = new GpsTracker(getApplicationContext());
        db = FirebaseFirestore.getInstance();

        // 기본 위치값
        mapPoint = MapPoint.mapPointWithGeoCoord(37.5579452, 126.9941904);

        binding.closeBtn.setOnClickListener(v -> onBackPressed());

        mapView = new MapView(HospitalMapActivity.this);
        mapView.setCalloutBalloonAdapter(new CustomBalloonAdapter(getLayoutInflater()));
        mapView.setPOIItemEventListener(this);
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
            endTrackingModeThread();
            addHospitalPOIItems();
        });

        endTrackingModeThread();
        addHospitalPOIItems();
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

    private void addHospitalPOIItems(){
        db.collection(Hospital.DB_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Hospital hospital = document.toObject(Hospital.class);
                            getAddressPointSearchResult(hospital);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void setMarker(Double longitude, Double latitude, Hospital hospital){
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(hospital.getName());
        marker.setUserObject(hospital);
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        // 기본으로 제공하는 BluePin 마커 모양.
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(marker);
    }

    private void getAddressPointSearchResult(Hospital hospital){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AddressService as = retrofit.create(AddressService.class);

        Call<ResultSearchAddressPoint> call = as.getSearchAddressPoint(
                BuildConfig.KAKAO_REST_API_KEY, "exact", hospital.getAddress());

        call.enqueue(new Callback<ResultSearchAddressPoint>() {
            @Override
            public void onResponse(@NonNull Call<ResultSearchAddressPoint> call,
                                   @NonNull Response<ResultSearchAddressPoint> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "Raw: " + response.raw());
                    Log.d(TAG, "Body: " + response.body());
                    ResultSearchAddressPoint result = response.body();

                    ArrayList<Document> hospitalInfo = null;
                    if (result != null) {
                        hospitalInfo = (ArrayList<Document>) result.getDocuments();
                    }

                    RoadAddress roadAddress = null;
                    if (hospitalInfo != null && !(hospitalInfo.isEmpty())) {
                        roadAddress = hospitalInfo.get(0).getRoadAddress();
                    }

                    if(roadAddress != null){
                        Double longitude = Double.parseDouble(roadAddress.getX());
                        Double latitude = Double.parseDouble(roadAddress.getY());
                        setMarker(longitude, latitude, hospital);
                    }

                }else{
                    Log.d(TAG, "Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultSearchAddressPoint> call, @NonNull Throwable t) {
                Log.d(TAG, "통신 실패: " + t.getMessage());
            }
        });
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        Log.d(TAG, mapPOIItem.getItemName());
        Intent intent = new Intent(getApplicationContext(), HospitalInfoPopUp.class);
        Hospital hospital = (Hospital) mapPOIItem.getUserObject();
        intent.putExtra("hospital", hospital);
        startActivity(intent);
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem,
                                                 MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    static class CustomBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        @SuppressLint("InflateParams")
        public CustomBalloonAdapter(LayoutInflater inflater) {
            mCalloutBalloon = inflater.inflate(R.layout.balloon_layout, null);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            Hospital hospital = (Hospital) poiItem.getUserObject();
            ((TextView)mCalloutBalloon.findViewById(R.id.hospitalNameTxt)).setText(hospital.getName());
            if(hospital.getKind().equals("의원")){
                ((TextView)mCalloutBalloon.findViewById(R.id.hospitalKindTxt)).setText(hospital.getDepartment().get(0));
            }else{
                ((TextView)mCalloutBalloon.findViewById(R.id.hospitalKindTxt)).setText(hospital.getKind() + "병원");
            }
            ((TextView)mCalloutBalloon.findViewById(R.id.hospitalAddressTxt)).setText(hospital.getAddress());
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return getCalloutBalloon(poiItem);
        }
    }
}