package com.gradproject.hospi.home;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.gradproject.hospi.R;
import com.gradproject.hospi.home.mypage.SettingActivity;

public class MyPageFragment extends Fragment {
    TextView version, nameTxt;
    Button myInfoEditBtn, favoritesBtn, prescriptionBtn,
            inquiryDetailsBtn, termsBtn, noticeBtn;

    FirebaseAuth firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_page, container,false);

        firebaseUser = FirebaseAuth.getInstance();
        version = rootView.findViewById(R.id.version);
        nameTxt = rootView.findViewById(R.id.nameTxt);
        myInfoEditBtn = rootView.findViewById(R.id.myInfoEditBtn);
        favoritesBtn = rootView.findViewById(R.id.favoritesBtn);
        prescriptionBtn = rootView.findViewById(R.id.prescriptionBtn);
        inquiryDetailsBtn = rootView.findViewById(R.id.inquiryDetailsBtn);
        termsBtn = rootView.findViewById(R.id.termsBtn);
        noticeBtn = rootView.findViewById(R.id.noticeBtn);

        nameTxt.setText(firebaseUser.getCurrentUser().getDisplayName());
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