package com.gradproject.hospi.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.databinding.FragmentRegister3Binding;
import com.gradproject.hospi.utils.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RegisterFragment3 extends Fragment implements OnBackPressedListener {
    private FragmentRegister3Binding binding;
    RegisterActivity registerActivity;
    DateTimeFormatter dateFormat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegister3Binding.inflate(inflater, container, false);

        registerActivity = (RegisterActivity) getActivity();

        binding.nextBtn.setOnClickListener(v -> {
            String date = binding.birthEdt.getText().toString().trim();
            nextBtnProcess(date);
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

    private void nextBtnProcess(String date){
        boolean isRadioChecked = radioCheck();
        boolean isCompareDateChecked = compareDate(date);

        if(isRadioChecked && isCompareDateChecked){
            int id = binding.sexRg.getCheckedRadioButtonId();
            RadioButton rb = binding.getRoot().findViewById(id);
            registerActivity.user.setSex(rb.getText().toString());
            LocalDate birthDate = LocalDate.parse(date, dateFormat);
            registerActivity.user.setBirth(birthDate.format(DateTimeFormat.date()));
            registerActivity.onFragmentChanged(3);
        }
    }

    public boolean radioCheck(){
        if(binding.man.isChecked() || binding.woman.isChecked()){
            return true;
        }else{
            binding.selSexErr.setVisibility(View.VISIBLE);
            return false;
        }
    }

    public boolean dateCheck(String date) {
        if (date == null || date.equals("")) {
            binding.birthErr.setVisibility(View.VISIBLE);
            return false;
        }

        try {
            dateFormat.parse(date);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            binding.birthErr.setVisibility(View.VISIBLE);
            return false;
        }
    }

    public boolean compareDate(String date){
        if (dateCheck(date)) {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = LocalDate.parse(date, dateFormat);

            if(endDate.isAfter(startDate)){
                return true;
            }else{
                binding.birthErr.setVisibility(View.VISIBLE);
                return false;
            }
        }else{
            binding.birthErr.setVisibility(View.VISIBLE);
            return false;
        }
    }
}