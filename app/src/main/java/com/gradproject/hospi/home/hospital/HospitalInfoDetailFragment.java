package com.gradproject.hospi.home.hospital;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.User;
import com.gradproject.hospi.databinding.FragmentHospitalInfoDetailBinding;
import com.gradproject.hospi.home.LocationPoint;
import com.gradproject.hospi.utils.StatusBar;

import net.daum.mf.map.api.CalloutBalloonAdapter;
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
    private FragmentHospitalInfoDetailBinding binding;

    HospitalActivity hospitalActivity;
    FirebaseFirestore db;
    ArrayList<String> favorites;
    boolean isFavorite = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        favorites = (ArrayList<String>) user.getFavorites();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHospitalInfoDetailBinding.inflate(inflater, container, false);

        StatusBar.updateStatusBarColor(requireActivity(), R.color.white);

        hospitalActivity = (HospitalActivity) getActivity();

        favoriteCheck(); // 찜한 병원인지 확인

        if(isFavorite){
            binding.favoriteImg.setImageResource(R.drawable.ic_action_favorite);
        }

        // 뒤로가기 버튼
        binding.backBtn.setOnClickListener(v -> onBackPressed());

        // 예약 버튼
        binding.reservationBtn.setOnClickListener(v -> hospitalActivity.onReservationFragmentChanged(1));

        // 문의 버튼
        binding.inquiryBtn.setOnClickListener(v -> hospitalActivity.onInquiryFragmentChanged(1));

        // 전화 버튼
        binding.callBtn.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + hospital.getTel()))));

        // 찜 버튼
        binding.favoriteBtn.setOnClickListener(v -> {
            if(isFavorite){
                String msg = "찜이 해제되었습니다.";
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                isFavorite = false;
                binding.favoriteImg.setImageResource(R.drawable.ic_action_favorite_border);
                removeFavoriteList();
            }else{
                String msg = "찜이 설정되었습니다.";
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                isFavorite = true;
                binding.favoriteImg.setImageResource(R.drawable.ic_action_favorite);
                addFavoriteList();
            }
        });

        binding.hospitalName.setText(hospital.getName());

        switch (hospital.getKind()){
            case "의원":
                binding.departmentTxt.setText(hospital.getDepartment().get(0));
                break;
            case "종합":
            case "대학":
                binding.departmentTxt.setText(hospital.getKind() + "병원");
                break;
        }

        String[] businessHoursArr = getBusinessHours();
        binding.weekdayBusinessHours.setText(businessHoursArr[0]);
        binding.saturdayBusinessHours.setText(businessHoursArr[1]);
        binding.holidayBusinessHours.setText(businessHoursArr[2]);

        binding.addressTxt.setText(hospital.getAddress());

        showHospitalLocation();

        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
            if (str.equals(hospital.getId())) {
                isFavorite = true;
                break;
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
        int size = favorites.size();
        for(int i=0; i<size; i++){
            if(favorites.get(i).equals(hospital.getId())){
                favorites.remove(i);
                size--;
                i--;
            }
        }
        user.setFavorites(favorites);

        DocumentReference documentReference = db.collection(User.DB_NAME).document(user.getDocumentId());
        documentReference
                .update("favorites", user.getFavorites())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

    private void showHospitalLocation(){
        LocationPoint point = getPointFromGeoCoder(hospital.getAddress());

        MapView mapView = new MapView(requireContext());
        mapView.setCalloutBalloonAdapter(new CustomBalloonAdapter(getLayoutInflater()));
        mapView.setZoomLevel(2, false);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(point.latitude, point.longitude);
        mapView.setMapCenterPoint(mapPoint, false);
        binding.mapView.addView(mapView);

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

    static class CustomBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        @SuppressLint("InflateParams")
        public CustomBalloonAdapter(LayoutInflater inflater) {
            mCalloutBalloon = inflater.inflate(R.layout.balloon_layout2, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            ((TextView)mCalloutBalloon.findViewById(R.id.hospitalNameTxt)).setText(hospital.getName());
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return getCalloutBalloon(poiItem);
        }
    }
}