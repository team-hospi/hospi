package com.gradproject.hospi.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

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

import static android.app.Activity.RESULT_OK;

public class SearchFragment extends Fragment {
    FrameLayout hospitalMapBtn, coronaCheckBtn, searchBox;
    ViewGroup rootView;

    FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container,false);

        searchBox = rootView.findViewById(R.id.searchBox);
        hospitalMapBtn = rootView.findViewById(R.id.hospitalMapBtn);
        coronaCheckBtn = rootView.findViewById(R.id.coronaCheckBtn);

        searchBox.setOnClickListener(v -> startActivity(new Intent(getContext(), ResultActivity.class)));
        hospitalMapBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), HospitalMapActivity.class)));
        coronaCheckBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), CoronaCheckActivity.class)));

        return rootView;
    }
}