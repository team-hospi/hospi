package com.gradproject.hospi.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;

public class RegisterFragment3 extends Fragment implements OnBackPressedListener {
    RegisterActivity registerActivity;
    DatePicker birthDp; // 생년월일 받아오기
    String date; // 생년월일 저장

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_register3, container,false);

        registerActivity = (RegisterActivity) getActivity();
        birthDp = rootView.findViewById(R.id.birthDp);

        birthDp.setMaxDate(System.currentTimeMillis()); // 현재 날짜를 최대로 지정

        Button nextBtn = rootView.findViewById(R.id.nextBtn); // 다음 버튼
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int month = birthDp.getMonth() + 1;
                date = birthDp.getYear() + "-" + month + "-" + birthDp.getDayOfMonth();
                registerActivity.user.setBirth(date);
                registerActivity.onFragmentChanged(3);
            }
        });

        return rootView;
    }

    @Override
    public void onBackPressed() {
        registerActivity.onFragmentChanged(1);
    }
}