package com.gradproject.hospi.home;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gradproject.hospi.utils.GpsTracker;
import com.gradproject.hospi.R;
import com.gradproject.hospi.home.search.ResultActivity;
import com.gradproject.hospi.utils.CurrentAddress;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchFragment extends Fragment{
    private GpsTracker gpsTracker;
    MapView mapView;

    FrameLayout searchBox;
    TextView locationTxt;

    LocationPoint currentPoint;
    String address;

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
        address = CurrentAddress.getCurrentAddress(getContext(), currentPoint.latitude, currentPoint.longitude);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container,false);

        locationTxt = rootView.findViewById(R.id.locationTxt);

        if(address.equals(CurrentAddress.NO_LOCATION_INFORMATION)){
            locationTxt.setText(address);
        }else{
            locationTxt.setText("현위치: " + address);
        }

        searchBox = rootView.findViewById(R.id.searchBox);
        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ResultActivity.class));
            }
        });

        showDisplayCurrentLocation(rootView, currentPoint); // 현재 위치 기준으로 지도 설정
        // TODO: 현위치 기준 1km 이내 병원 표시

        return rootView;
    }

    private void showDisplayCurrentLocation(ViewGroup rootView, LocationPoint point){
        ViewGroup mapViewContainer = (ViewGroup) rootView.findViewById(R.id.mapView);
        mapViewContainer.addView(mapView);
        mapView.setZoomLevel(2, false); // 맵 줌레벨 설정
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(point.latitude, point.longitude);
        mapView.setMapCenterPoint(mapPoint, false);
    }

    private void setMarker(ViewGroup rootView, LocationPoint point, String itemName){
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(point.latitude, point.longitude);
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
}