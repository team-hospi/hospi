package com.gradproject.hospi.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gradproject.hospi.GpsTracker;
import com.gradproject.hospi.MainActivity;
import com.gradproject.hospi.R;
import com.gradproject.hospi.home.search.ResultActivity;

public class SearchFragment extends Fragment {
    private GpsTracker gpsTracker;

    FrameLayout searchBox;
    TextView locationTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container,false);

        locationTxt = rootView.findViewById(R.id.locationTxt);

        gpsTracker = new GpsTracker(getContext());

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        String address = "";
        String[] addrArr = MainActivity.getCurrentAddress(getContext() ,latitude, longitude).split(" ");

        for(int i=addrArr.length-2; i>0; i--){
            address = addrArr[i].concat(" " + address);
        }

        locationTxt.setText("현위치: " + address);

        searchBox = rootView.findViewById(R.id.searchBox);
        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ResultActivity.class));
            }
        });

        return rootView;
    }
}