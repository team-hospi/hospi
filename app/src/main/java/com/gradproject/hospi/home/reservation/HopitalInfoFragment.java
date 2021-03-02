package com.gradproject.hospi.home.reservation;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gradproject.hospi.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.List;


public class HopitalInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_hopital_info, container,false);

        LinearLayout backBtn = rootView.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        showHospitalLocation("경기도 부천시 소사동 2", "부천성모병원", rootView);

        return rootView;
    }

    private void showHospitalLocation(String addr, String hospitalName, ViewGroup rootView){
        Point point = getPointFromGeoCoder(addr);

        MapView mapView = new MapView(getContext());
        mapView.setZoomLevel(2, false);
        ViewGroup mapViewContainer = (ViewGroup) rootView.findViewById(R.id.mapView);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(point.latitude, point.longitude);
        mapView.setMapCenterPoint(mapPoint, false);
        mapViewContainer.addView(mapView);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(hospitalName);
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        // 기본으로 제공하는 BluePin 마커 모양.
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(marker);
    }

    private Point getPointFromGeoCoder(String addr) {
        Point point = new Point();
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

    class Point {
        // 위도
        public double longitude;
        // 경도
        public double latitude;
        public String addr;
        // 포인트를 받았는지 여부
        public boolean havePoint;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("x : ");
            builder.append(longitude);
            builder.append(" y : ");
            builder.append(latitude);
            builder.append(" addr : ");
            builder.append(addr);

            return builder.toString();
        }
    }
}