package com.gradproject.hospi.register;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.databinding.FragmentRegister3Binding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RegisterFragment3 extends Fragment implements OnBackPressedListener {
    private FragmentRegister3Binding binding;
    RegisterActivity registerActivity;
    Calendar cal; // 생년월일 저장

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cal = Calendar.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegister3Binding.inflate(inflater, container, false);

        registerActivity = (RegisterActivity) getActivity();

        binding.birthDp.setMaxDate(System.currentTimeMillis()); // 현재 날짜를 최대로 지정

        binding.nextBtn.setOnClickListener(v -> {
            int month = binding.birthDp.getMonth();
            cal.set(binding.birthDp.getYear(), month, binding.birthDp.getDayOfMonth());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String date = df.format(cal.getTime());
            registerActivity.user.setBirth(date);
            registerActivity.onFragmentChanged(3);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onBackPressed() {
        registerActivity.onFragmentChanged(1);
    }
}