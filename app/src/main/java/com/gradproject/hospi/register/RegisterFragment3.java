package com.gradproject.hospi.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.databinding.FragmentRegister3Binding;
import com.gradproject.hospi.utils.DateTimeFormat;

import java.time.LocalDate;

public class RegisterFragment3 extends Fragment implements OnBackPressedListener {
    private FragmentRegister3Binding binding;
    RegisterActivity registerActivity;
    LocalDate lDate; // 생년월일 저장

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegister3Binding.inflate(inflater, container, false);

        registerActivity = (RegisterActivity) getActivity();

        binding.birthDp.setMaxDate(System.currentTimeMillis()); // 현재 날짜를 최대로 지정

        binding.nextBtn.setOnClickListener(v -> {
            lDate = LocalDate.of(
                    binding.birthDp.getYear(), binding.birthDp.getMonth()+1, binding.birthDp.getDayOfMonth());
            registerActivity.user.setBirth(lDate.format(DateTimeFormat.date()));
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