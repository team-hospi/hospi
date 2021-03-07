package com.gradproject.hospi.home.hospital;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;

public class InquiryFragment extends Fragment implements OnBackPressedListener {
    HospitalActivity hospitalActivity;

    Button nextBtn;
    LinearLayout backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_inquiry, container,false);

        hospitalActivity = (HospitalActivity) getActivity();
        backBtn = rootView.findViewById(R.id.backBtn);
        nextBtn = rootView.findViewById(R.id.nextBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hospitalActivity.onInquiryFragmentChanged(2);
            }
        });

        return rootView;
    }

    @Override
    public void onBackPressed() {
        hospitalActivity.onInquiryFragmentChanged(0);
    }
}