package com.gradproject.hospi.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.databinding.FragmentRegister4Binding;
import com.gradproject.hospi.utils.PatternCheck;
import com.gradproject.hospi.utils.PhoneNumberHyphen;

public class RegisterFragment4 extends Fragment implements OnBackPressedListener{
    private FragmentRegister4Binding binding;
    RegisterActivity registerActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegister4Binding.inflate(inflater, container, false);

        registerActivity = (RegisterActivity) getActivity();

        binding.nextBtn.setOnClickListener(v -> {
            String phone = binding.inputPhone.getText().toString();
            if(PatternCheck.isPhone(phone)){
                registerActivity.user.setPhone(PhoneNumberHyphen.phone(phone));
                registerActivity.onFragmentChanged(4);
            }else{
                binding.phoneErrorTxt.setVisibility(View.VISIBLE);
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
        registerActivity.onFragmentChanged(2);
    }
}