package com.gradproject.hospi.home;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.TestLooperManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gradproject.hospi.R;
import com.gradproject.hospi.home.mypage.SettingActivity;

public class MyPageFragment extends Fragment {
    TextView version;

    Button myInfoEditBtn, favoritesBtn, prescriptionBtn,
            inquiryDetailsBtn, termsBtn, noticeBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_page, container,false);

        version = rootView.findViewById(R.id.version);
        myInfoEditBtn = rootView.findViewById(R.id.myInfoEditBtn);
        favoritesBtn = rootView.findViewById(R.id.favoritesBtn);
        prescriptionBtn = rootView.findViewById(R.id.prescriptionBtn);
        inquiryDetailsBtn = rootView.findViewById(R.id.inquiryDetailsBtn);
        termsBtn = rootView.findViewById(R.id.termsBtn);
        noticeBtn = rootView.findViewById(R.id.noticeBtn);

        version.setText(getVersionInfo(getContext()));

        myInfoEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectedFragment("myInfoEditBtn");
            }
        });

        favoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectedFragment("favoritesBtn");
            }
        });

        prescriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectedFragment("prescriptionBtn");
            }
        });

        inquiryDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectedFragment("inquiryDetailsBtn");
            }
        });

        termsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectedFragment("termsBtn");
            }
        });

        noticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectedFragment("noticeBtn");
            }
        });

        return rootView;
    }

    public void startSelectedFragment(String select){
        Intent intent = new Intent(getContext(), SettingActivity.class);
        intent.putExtra("selectBtn", select);
        startActivity(intent);
    }

    public String getVersionInfo(Context context){
        String version = null;
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = i.versionName;
        } catch(PackageManager.NameNotFoundException e) { }
        return version;
    }
}