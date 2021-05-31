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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.Locale;

public class RegisterFragment3 extends Fragment implements OnBackPressedListener {
    private FragmentRegister3Binding binding;
    RegisterActivity registerActivity;
    String date; // 생년월일 저장

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegister3Binding.inflate(inflater, container, false);

        registerActivity = (RegisterActivity) getActivity();

        // TODO: 생년월일 로직 구현

        binding.nextBtn.setOnClickListener(v -> {
            if(binding.man.isChecked()){
                registerActivity.user.setSex(binding.man.getText().toString());



                registerActivity.user.setBirth(date);
                registerActivity.onFragmentChanged(3);
            }else if(binding.woman.isChecked()){
                registerActivity.user.setSex(binding.woman.getText().toString());



                registerActivity.user.setBirth(date);
                registerActivity.onFragmentChanged(3);
            }else{
                binding.selSexErr.setVisibility(View.VISIBLE);
            }
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

    public boolean dateCheck(String checkDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.KOREA);

        try {
            dateTimeFormatter.parse(checkDate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}