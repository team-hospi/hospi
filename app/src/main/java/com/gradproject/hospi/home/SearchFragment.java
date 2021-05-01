package com.gradproject.hospi.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.R;
import com.gradproject.hospi.home.hospital.HospitalMapActivity;
import com.gradproject.hospi.home.search.ResultActivity;
import com.gradproject.hospi.utils.GpsTracker;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchFragment extends Fragment implements MapReverseGeoCoder.ReverseGeoCodingResultListener{
    private static final String OPEN_API_KEY = "07d563b8fc089510a0c926182cf35b1f";

    FrameLayout hospitalMapBtn, coronaCheckBtn;

    MapPoint mapPoint;
    GpsTracker gpsTracker;
    FrameLayout searchBox;
    TextView locationTxt;
    ViewGroup rootView;

    FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsTracker = new GpsTracker(getContext());
        db = FirebaseFirestore.getInstance();

        // 현위치 기준 읍.면.동까지의 주소 불러오기
        mapPoint = MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        MapReverseGeoCoder reverseGeoCoder = new MapReverseGeoCoder(OPEN_API_KEY, mapPoint, SearchFragment.this, getActivity());
        reverseGeoCoder.startFindingAddress();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container,false);

        locationTxt = rootView.findViewById(R.id.locationTxt);
        searchBox = rootView.findViewById(R.id.searchBox);
        hospitalMapBtn = rootView.findViewById(R.id.hospitalMapBtn);
        coronaCheckBtn = rootView.findViewById(R.id.coronaCheckBtn);

        searchBox.setOnClickListener(v -> startActivity(new Intent(getContext(), ResultActivity.class)));
        hospitalMapBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), HospitalMapActivity.class)));
        coronaCheckBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), CoronaCheckActivity.class)));

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
}