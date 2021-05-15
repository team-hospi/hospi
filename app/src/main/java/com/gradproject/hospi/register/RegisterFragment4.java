package com.gradproject.hospi.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.databinding.FragmentRegister4Binding;
import com.gradproject.hospi.utils.PhoneNumberHyphen;

public class RegisterFragment4 extends Fragment implements OnBackPressedListener {
    private FragmentRegister4Binding binding;
    RegisterActivity registerActivity;
    String phone; // 전화번호

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegister4Binding.inflate(inflater, container, false);

        registerActivity = (RegisterActivity) getActivity();

        // 다음 버튼
        binding.nextBtn.setOnClickListener(v -> {
            phone = PhoneNumberHyphen.phone(binding.inputPhone.getText().toString()); // 자동 하이픈 입력 후 전화번호 저장
            if(phone.equals("")){
                binding.phoneErrorTxt.setVisibility(View.VISIBLE);
            }else{
                registerActivity.user.setPhone(phone);
                registerActivity.onFragmentChanged(4);
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