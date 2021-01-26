package com.gradproject.hospi.register;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gradproject.hospi.R;
import com.gradproject.hospi.utils.Encrypt;

public class RegisterFragment6 extends Fragment {
    RegisterActivity registerActivity;
    EditText inputPW, inputPW2; // 1: 비밀번호 2: 비밀번호 확인
    TextView pwErrorTxt;

    String pw; // 비밀번호 저장

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_register6, container,false);

        registerActivity = (RegisterActivity) getActivity();
        inputPW = rootView.findViewById(R.id.inputPW);
        inputPW2 = rootView.findViewById(R.id.inputPW2);
        pwErrorTxt = rootView.findViewById(R.id.pwErrorTxt);

        Button nextBtn = rootView.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputPW.getText().toString().equals("") || inputPW2.getText().toString().equals("")){
                    pwErrorTxt.setText("비밀번호를 입력해주세요.");
                    pwErrorTxt.setVisibility(View.VISIBLE);
                }else if(!(inputPW.getText().toString().equals(inputPW2.getText().toString()))){
                    pwErrorTxt.setText("비밀번호가 일치하지 않습니다.");
                    pwErrorTxt.setVisibility(View.VISIBLE);
                }else{
                    pw = Encrypt.getEncrypt(registerActivity.user.getEmail(), inputPW2.getText().toString());
                    registerActivity.user.setPassword(pw);
                    getActivity().finish();
                    registerSuccess();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                registerActivity.onFragmentChanged(4);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // 회원가입 완료 팝업
    private void registerSuccess(){
        startActivity(new Intent(getContext(), RegisterSuccessPopUp.class));
    }
}