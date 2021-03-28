package com.gradproject.hospi.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.utils.PhoneNumberHyphen;

public class RegisterFragment4 extends Fragment implements OnBackPressedListener {
    RegisterActivity registerActivity;
    EditText inputPhone; // 전화번호 입력받기
    TextView phoneErrorTxt; // 전화번호 빈칸 에러메시지

    String phone; // 전화번호

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_register4, container,false);

        registerActivity = (RegisterActivity) getActivity();
        inputPhone = rootView.findViewById(R.id.inputPhone);
        phoneErrorTxt = rootView.findViewById(R.id.phoneErrorTxt);

        // 다음 버튼
        Button nextBtn = rootView.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(v -> {
            phone = PhoneNumberHyphen.phone(inputPhone.getText().toString()); // 자동 하이픈 입력 후 전화번호 저장
            if(phone.equals("")){
                phoneErrorTxt.setVisibility(View.VISIBLE);
            }else{
                registerActivity.user.setPhone(phone);
                registerActivity.onFragmentChanged(4);
            }
        });

        return rootView;
    }

    @Override
    public void onBackPressed() {
        registerActivity.onFragmentChanged(2);
    }
}