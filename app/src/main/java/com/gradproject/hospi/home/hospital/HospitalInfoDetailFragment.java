package com.gradproject.hospi.home.hospital;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.home.search.Hospital;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.List;

import static com.gradproject.hospi.home.hospital.HospitalActivity.hospital;


public class HospitalInfoDetailFragment extends Fragment implements OnBackPressedListener {
    HospitalActivity hospitalActivity;

    TextView hospitalName, departmentTxt, weekdayBusinessHours;
    TextView saturdayBusinessHours, holidayBusinessHours, addressTxt;
    LinearLayout backBtn, reservationBtn, inquiryBtn, callBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_hospital_info_detail, container,false);

        hospitalActivity = (HospitalActivity) getActivity();
        hospitalName = rootView.findViewById(R.id.hospitalName);
        departmentTxt = rootView.findViewById(R.id.departmentTxt);
        weekdayBusinessHours = rootView.findViewById(R.id.weekdayBusinessHours);
        saturdayBusinessHours = rootView.findViewById(R.id.saturdayBusinessHours);
        holidayBusinessHours = rootView.findViewById(R.id.holidayBusinessHours);
        addressTxt = rootView.findViewById(R.id.addressTxt);
        backBtn = rootView.findViewById(R.id.backBtn);
        reservationBtn = rootView.findViewById(R.id.reservationBtn);
        inquiryBtn = rootView.findViewById(R.id.inquiryBtn);
        callBtn = rootView.findViewById(R.id.callBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        reservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hospitalActivity.onReservationFragmentChanged(1);
            }
        });

        inquiryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hospitalActivity.onInquiryFragmentChanged(1);
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + hospital.getTel())));
            }
        });

        hospitalName.setText(hospital.getName());

        switch (hospital.getKind()){
            case "의원":
                departmentTxt.setText(hospital.getDepartment().get(0));
                break;
            case "종합":
            case "대학":
                departmentTxt.setText(hospital.getKind() + "병원");
                break;
        }

        String[] businessHoursArr = getBusinessHours();
        weekdayBusinessHours.setText(businessHoursArr[0]);
        saturdayBusinessHours.setText(businessHoursArr[1]);
        holidayBusinessHours.setText(businessHoursArr[2]);

        addressTxt.setText(hospital.getAddress());

        showHospitalLocation(rootView);

        return rootView;
    }

    @Override
    public void onBackPressed() {
        getActivity().finish();
    }

    private String[] getBusinessHours(){
        String[] strArr = {"", "토요일 휴무", "공휴일 휴무"};
        String open, close;

        open = hospital.getWeekday_open();
        close = hospital.getWeekday_close();
        strArr[0] = "평일 " + open + " ~ " + close;

        if(hospital.isSaturday_status()){
            open = hospital.getSaturday_open();
            close = hospital.getSaturday_close();
            strArr[1] = "토요일 " + open + " ~ " + close;
        }

        if(hospital.isHoliday_status()){
            open = hospital.getHoliday_open();
            close = hospital.getHoliday_close();
            strArr[2] = "공휴일 " + open + " ~ " + close;
        }

        return strArr;
    }

    private void showHospitalLocation(ViewGroup rootView){
        Point point = getPointFromGeoCoder(hospital.getAddress());

        MapView mapView = new MapView(getContext());
        mapView.setZoomLevel(2, false);
        ViewGroup mapViewContainer = (ViewGroup) rootView.findViewById(R.id.mapView);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(point.latitude, point.longitude);
        mapView.setMapCenterPoint(mapPoint, false);
        mapViewContainer.addView(mapView);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(hospital.getName());
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