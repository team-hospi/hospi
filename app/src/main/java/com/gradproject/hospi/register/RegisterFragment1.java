package com.gradproject.hospi.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.databinding.FragmentRegister1Binding;

import java.util.Objects;

public class RegisterFragment1 extends Fragment implements OnBackPressedListener {
    private FragmentRegister1Binding binding;
    RegisterActivity registerActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegister1Binding.inflate(inflater, container, false);

        registerActivity = (RegisterActivity) getActivity();

        // 다음 버튼
        binding.nextBtn.setOnClickListener(v -> {
            if(binding.agree.isChecked()){
                registerActivity.onFragmentChanged(1);
            }else{
                binding.radioErrorTxt.setVisibility(View.VISIBLE);
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
        Objects.requireNonNull(getActivity()).finish();
    }
}