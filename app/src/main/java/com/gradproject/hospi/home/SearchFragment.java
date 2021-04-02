package com.gradproject.hospi.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gradproject.hospi.Document;
import com.gradproject.hospi.R;
import com.gradproject.hospi.RestAPI;
import com.gradproject.hospi.ResultSearchCategory;
import com.gradproject.hospi.home.search.ResultActivity;
import com.gradproject.hospi.utils.GpsTracker;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchFragment extends Fragment implements MapReverseGeoCoder.ReverseGeoCodingResultListener{
    private static final String OPEN_API_KEY = "07d563b8fc089510a0c926182cf35b1f";
    private static final String URL = "https://dapi.kakao.com/";
    private static final String REST_API_KEY = "KakaoAK 3e2f7063b5718fb7603bc5bbdcea189b";

    GpsTracker gpsTracker;
    MapView mapView;

    ImageButton gpsRefreshBtn;
    FrameLayout searchBox;
    TextView locationTxt;

    ViewGroup rootView;
    ViewGroup mapViewContainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsTracker = new GpsTracker(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView = new MapView(getContext());
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        // 현위치 기준 읍.면.동까지의 주소 불러오기
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        MapReverseGeoCoder reverseGeoCoder = new MapReverseGeoCoder(OPEN_API_KEY, mapPoint, SearchFragment.this, getActivity());
        reverseGeoCoder.startFindingAddress();
        getCategorySearchResult(mapPoint);

        mapViewContainer.addView(mapView);
        mapView.setZoomLevel(3, true); // 맵 줌레벨 설정
        mapView.setMapCenterPoint(mapView.getMapCenterPoint(), true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapViewContainer.removeView(mapView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container,false);

        locationTxt = rootView.findViewById(R.id.locationTxt);
        searchBox = rootView.findViewById(R.id.searchBox);
        gpsRefreshBtn = rootView.findViewById(R.id.gpsRefreshBtn);
        mapViewContainer = rootView.findViewById(R.id.mapView);

        searchBox.setOnClickListener(v -> startActivity(new Intent(getContext(), ResultActivity.class)));

        gpsRefreshBtn.setOnClickListener(v -> {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            mapView.setZoomLevel(3, true);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            mapView.setMapCenterPoint(mapPoint, true);
            mapView.removeAllPOIItems();
            getCategorySearchResult(mapPoint);
            endTrackingModeThread(5000);
        });

        endTrackingModeThread(5000);

        return rootView;
    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        // 주소 (읍, 면, 동)까지만 표시
        String strArr[] = s.split(" ");
        String address = "";

        for(int i=0; i<strArr.length; i++){
            if(strArr[i].endsWith("읍") || strArr[i].endsWith("면") || strArr[i].endsWith("동")){
                address += strArr[i];
                break;
            }else{
                address += strArr[i] + " ";
            }
        }
        Observable.just(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(locationTxt::setText);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        String msg = "위치정보를 찾는데 실패하였습니다.";
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        locationTxt.setText("위치정보 없음");
    }

    private void endTrackingModeThread(long millis){
        new Thread(() -> {
            try{
                Thread.sleep(millis);
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            }catch(Exception e){}
        }).start();
    }

    private void setMarker(Double longitude, Double latitude, String itemName){
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(itemName);
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        // 기본으로 제공하는 BluePin 마커 모양.
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(marker);
    }

    private void getCategorySearchResult(MapPoint mapPoint){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String longitude = String.valueOf(mapPoint.getMapPointGeoCoord().longitude);
        String latitude = String.valueOf(mapPoint.getMapPointGeoCoord().latitude);

        RestAPI api = retrofit.create(RestAPI.class);

        for(int i=0; i<5; i++){
            String page = String.valueOf(i);
            Call<ResultSearchCategory> call = api.getSearchCategory(REST_API_KEY, "HP8", longitude, latitude, "1000", page);

            call.enqueue(new Callback<ResultSearchCategory>() {
                @Override
                public void onResponse(Call<ResultSearchCategory> call, Response<ResultSearchCategory> response) {
                    if(response.isSuccessful()){
                        Log.d("rest", "Raw: " + response.raw());
                        Log.d("rest", "Body: " + response.body());
                        ResultSearchCategory result = response.body();
                        ArrayList<Document> nearbyHospitalList = (ArrayList) result.getDocuments();

                        for(int i=0; i<nearbyHospitalList.size(); i++){
                            Double longitude = Double.parseDouble(nearbyHospitalList.get(i).getX());
                            Double latitude = Double.parseDouble(nearbyHospitalList.get(i).getY());
                            String name = nearbyHospitalList.get(i).getPlaceName();

                            setMarker(longitude, latitude, name);
                        }
                    }else{
                        Log.d("rest", "Error");
                    }
                }

                @Override
                public void onFailure(Call<ResultSearchCategory> call, Throwable t) {
                    Log.d("rest", "통신 실패: " + t.getMessage());
                }
            });
        }
    }
}