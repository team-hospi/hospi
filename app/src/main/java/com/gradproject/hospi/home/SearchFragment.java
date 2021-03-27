package com.gradproject.hospi.home;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
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

    FrameLayout searchBox;
    TextView locationTxt;

    LocationPoint currentPoint;
    String address = "위치정보 없음";



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView = new MapView(getContext());
        gpsTracker = new GpsTracker(getContext());
        currentPoint = new LocationPoint();

        // 현재 위치 좌표 설정
        currentPoint.latitude = gpsTracker.getLatitude();
        currentPoint.longitude = gpsTracker.getLongitude();

        // 현위치 기준 읍.면.동까지의 주소 불러오기
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(currentPoint.latitude, currentPoint.longitude);
        MapReverseGeoCoder reverseGeoCoder = new MapReverseGeoCoder(OPEN_API_KEY, mapPoint, this, getActivity());
        reverseGeoCoder.startFindingAddress();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container,false);

        locationTxt = rootView.findViewById(R.id.locationTxt);
        searchBox = rootView.findViewById(R.id.searchBox);

        locationTxt.setText("현위치: " + address);

        // TODO: 현위치 쓰레드 사용 수정 필요
        Observable.just("현위치: " + address)
                .throttleWithTimeout(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(locationTxt::setText);
/*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }

                if(getActivity() == null)
                    return;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        locationTxt.setText("현위치: " + address);
                    }
                });
            }
        }).start();
*/
        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ResultActivity.class));
            }
        });

        showDisplayCurrentLocation(rootView, currentPoint); // 현재 위치 기준으로 지도 설정
        getCategorySearchResult(rootView, mapView.getMapCenterPoint()); // 현위치 기준 1km 이내 병원 표시

        mapView.setMapViewEventListener(new MapView.MapViewEventListener() {
            @Override
            public void onMapViewInitialized(MapView mapView) {

            }

            @Override
            public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
                getCategorySearchResult(rootView, mapPoint);
            }

            @Override
            public void onMapViewZoomLevelChanged(MapView mapView, int i) {

            }

            @Override
            public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
                getCategorySearchResult(rootView, mapPoint);
            }
        });

        return rootView;
    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        // 주소 (읍, 면, 동)까지만 표시
        String strArr[] = s.split(" ");
        String tmp = "";

        for(int i=0; i<strArr.length; i++){
            if(strArr[i].endsWith("읍") || strArr[i].endsWith("면") || strArr[i].endsWith("동")){
                tmp += strArr[i];
                break;
            }else{
                tmp += strArr[i] + " ";
            }
        }
        address = tmp;
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        String msg = "위치정보를 찾는데 실패하였습니다.";
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void showDisplayCurrentLocation(ViewGroup rootView, LocationPoint point){
        ViewGroup mapViewContainer = (ViewGroup) rootView.findViewById(R.id.mapView);
        mapViewContainer.addView(mapView);
        mapView.setZoomLevel(2, false); // 맵 줌레벨 설정
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(point.latitude, point.longitude);
        mapView.setMapCenterPoint(mapPoint, false);
    }

    private void setMarker(ViewGroup rootView, Double longitude, Double latitude, String itemName){
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

    private LocationPoint getPointFromGeoCoder(String addr) {
        LocationPoint point = new LocationPoint();
        point.addr = addr;

        Geocoder geocoder = new Geocoder(getContext());
        List<Address> listAddress;
        try {
            listAddress = geocoder.getFromLocationName(addr, 1);
        } catch (IOException e) {
            e.printStackTrace();
            point.havePoint = false;
            return point;
        }

        if (listAddress.isEmpty()) {
            point.havePoint = false;
            return point;
        }

        point.havePoint = true;
        point.longitude = listAddress.get(0).getLongitude();
        point.latitude = listAddress.get(0).getLatitude();
        return point;
    }

    private void getCategorySearchResult(ViewGroup rootView, MapPoint mapPoint){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String longitude = String.valueOf(mapPoint.getMapPointGeoCoord().longitude);
        String latitude = String.valueOf(mapPoint.getMapPointGeoCoord().latitude);

        RestAPI api = retrofit.create(RestAPI.class);
        Call<ResultSearchCategory> call = api.getSearchCategory(REST_API_KEY, "HP8", longitude, latitude, "1000");

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

                        setMarker(rootView, longitude, latitude, name);
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