package com.gradproject.hospi.home.hospital;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.User;
import com.gradproject.hospi.home.LocationPoint;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.gradproject.hospi.home.HomeActivity.user;
import static com.gradproject.hospi.home.hospital.HospitalActivity.hospital;

public class HospitalInfoDetailFragment extends Fragment implements OnBackPressedListener {
    private static final String TAG ="HospitalInfoDetailFragment";

    HospitalActivity hospitalActivity;
    FirebaseFirestore db;

    TextView hospitalName, departmentTxt, weekdayBusinessHours;
    TextView saturdayBusinessHours, holidayBusinessHours, addressTxt;
    ImageButton backBtn;
    LinearLayout reservationBtn, inquiryBtn, callBtn, favoriteBtn;
    ImageView favoriteImg;

    ArrayList<String> favorites;
    boolean isFavorite = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        favorites = (ArrayList) user.getFavorites();
    }

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
        favoriteBtn = rootView.findViewById(R.id.favoriteBtn);
        favoriteImg = rootView.findViewById(R.id.favoriteImg);

        favoriteCheck(); // 찜한 병원인지 확인

        if(isFavorite){
            favoriteImg.setImageResource(R.drawable.ic_action_favorite);
        }

        // 뒤로가기 버튼
        backBtn.setOnClickListener(v -> onBackPressed());

        // 예약 버튼
        reservationBtn.setOnClickListener(v -> hospitalActivity.onReservationFragmentChanged(1));

        // 문의 버튼
        inquiryBtn.setOnClickListener(v -> hospitalActivity.onInquiryFragmentChanged(1));

        // 전화 버튼
        callBtn.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + hospital.getTel()))));

        // 찜 버튼
        favoriteBtn.setOnClickListener(v -> {
            if(isFavorite){
                String msg = "찜이 해제되었습니다.";
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                isFavorite = false;
                favoriteImg.setImageResource(R.drawable.ic_action_favorite_border);
                removeFavoriteList();
            }else{
                String msg = "찜이 설정되었습니다.";
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                isFavorite = true;
                favoriteImg.setImageResource(R.drawable.ic_action_favorite);
                addFavoriteList();
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

        open = hospital.getWeekdayOpen();
        close = hospital.getWeekdayClose();
        strArr[0] = "평일 " + open + " ~ " + close;

        if(hospital.isSaturdayStatus()){
            open = hospital.getSaturdayOpen();
            close = hospital.getSaturdayClose();
            strArr[1] = "토요일 " + open + " ~ " + close;
        }

        if(hospital.isHolidayStatus()){
            open = hospital.getHolidayOpen();
            close = hospital.getHolidayClose();
            strArr[2] = "공휴일 " + open + " ~ " + close;
        }

        return strArr;
    }

    public void favoriteCheck(){
        for(String str : favorites){
            if(str.equals(hospital.getId())){
                isFavorite = true;
            }
        }
    }

    public void addFavoriteList(){
        favorites.add(hospital.getId());
        user.setFavorites(favorites);
        DocumentReference documentReference = db.collection(User.DB_NAME).document(user.getDocumentId());
        documentReference
                .update("favorites", user.getFavorites())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

    public void removeFavoriteList(){
        for(int i=0; i<favorites.size(); i++){
            if(favorites.get(i).equals(hospital.getId())){
                favorites.remove(i);
            }
        }
        user.setFavorites(favorites);

        DocumentReference documentReference = db.collection(User.DB_NAME).document(user.getDocumentId());
        documentReference
                .update("favorites", user.getFavorites())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

    private void showHospitalLocation(ViewGroup rootView){
        LocationPoint point = getPointFromGeoCoder(hospital.getAddress());

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
        mapView.selectPOIItem(marker, true);
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