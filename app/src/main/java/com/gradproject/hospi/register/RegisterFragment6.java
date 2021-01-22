package com.gradproject.hospi.register;

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
import android.widget.Toast;

import com.gradproject.hospi.R;
import com.gradproject.hospi.Utils;

public class RegisterFragment6 extends Fragment {
    RegisterActivity registerActivity;
    EditText inputPW, inputPW2; // 1: 비밀번호 2: 비밀번호 확인
    TextView pwErrorTxt, pwErrorTxt2; // 1: 중복체크 2: 빈칸 체크

    String pw;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_register6, container,false);

        registerActivity = (RegisterActivity) getActivity();
        inputPW = rootView.findViewById(R.id.inputPW);
        inputPW2 = rootView.findViewById(R.id.inputPW2);
        pwErrorTxt = rootView.findViewById(R.id.pwErrorTxt);
        pwErrorTxt2 = rootView.findViewById(R.id.pwErrorTxt2);

        Button nextBtn = rootView.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputPW.getText().toString().equals("") || inputPW2.getText().toString().equals("")){
                    pwErrorTxt2.setVisibility(View.VISIBLE);
                    pwErrorTxt.setVisibility(View.INVISIBLE);
                }else if(!(inputPW.getText().toString().equals(inputPW2.getText().toString()))){
                    pwErrorTxt.setVisibility(View.VISIBLE);
                    pwErrorTxt2.setVisibility(View.INVISIBLE);
                }else{
                    pw = Utils.getEncrypt(registerActivity.user.getEmail(), inputPW2.getText().toString());
                    registerActivity.user.setPassword(pw);
                    String check = "사용자 정보\n" +
                            "이메일: " + registerActivity.user.getEmail() + "\n" +
                            "비밀번호: " + inputPW2.getText().toString() + "\n" +
                            "암호화된 비밀번호: " + registerActivity.user.getPassword() + "\n" +
                            "이름: " + registerActivity.user.getName() + "\n" +
                            "휴대폰 번호: " + registerActivity.user.getPhone() + "\n" +
                            "생년월일: " + registerActivity.user.getBirth() + "\n" +
                            "성별: " + registerActivity.user.getSex();
                    Toast.makeText(getContext(), check, Toast.LENGTH_LONG).show();
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
}