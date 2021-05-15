package com.gradproject.hospi.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.databinding.FragmentRegister2Binding;

public class RegisterFragment2 extends Fragment implements OnBackPressedListener {
    private FragmentRegister2Binding binding;
    RegisterActivity registerActivity;
    String name; // 이름 저장

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegister2Binding.inflate(inflater, container, false);

        registerActivity = (RegisterActivity) getActivity();

        registerActivity.user.setAdmin(false); // 일반 사용자로 설정

        binding.nextBtn.setOnClickListener(v -> {
            name = binding.inputName.getText().toString();

            if(name.equals("")){
                binding.nameErrorTxt.setVisibility(View.VISIBLE); // 빈칸 에러 출력
            }else{
                registerActivity.user.setName(name); // user 인스턴스에 이름 저장
                registerActivity.onFragmentChanged(2); // 다음 화면 이동
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
        registerActivity.onFragmentChanged(0);
    }
}