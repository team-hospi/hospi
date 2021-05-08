package com.gradproject.hospi.register;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RegisterFragment3 extends Fragment implements OnBackPressedListener {
    RegisterActivity registerActivity;
    DatePicker birthDp; // 생년월일 받아오기
    Calendar cal; // 생년월일 저장

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cal = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_register3, container,false);

        registerActivity = (RegisterActivity) getActivity();
        birthDp = rootView.findViewById(R.id.birthDp);

        birthDp.setMaxDate(System.currentTimeMillis()); // 현재 날짜를 최대로 지정

        Button nextBtn = rootView.findViewById(R.id.nextBtn); // 다음 버튼
        nextBtn.setOnClickListener(v -> {
            int month = birthDp.getMonth();
            cal.set(birthDp.getYear(), month, birthDp.getDayOfMonth());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String date = df.format(cal.getTime());
            registerActivity.user.setBirth(date);
            registerActivity.onFragmentChanged(3);
        });

        return rootView;
    }

    @Override
    public void onBackPressed() {
        registerActivity.onFragmentChanged(1);
    }
}