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

import com.gradproject.hospi.R;

public class RegisterFragment5 extends Fragment {
    RegisterActivity registerActivity;
    TextView emailErrorTxt;
    EditText inputEmail;

    String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_register5, container,false);

        registerActivity = (RegisterActivity) getActivity();
        emailErrorTxt = rootView.findViewById(R.id.emailErrorTxt);
        inputEmail = rootView.findViewById(R.id.inputEmail);

        Button nextBtn = rootView.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = inputEmail.getText().toString();

                if(checkDuplicateEmail(email)){
                    emailErrorTxt.setText("이미 등록된 이메일 입니다.");
                    emailErrorTxt.setVisibility(View.VISIBLE);
                }else if(email.equals("")){
                    emailErrorTxt.setText("이메일을 입력해주세요.");
                    emailErrorTxt.setVisibility(View.VISIBLE);
                }else if(!(email.contains("@"))){
                    emailErrorTxt.setText("잘못된 이메일 형식입니다.");
                    emailErrorTxt.setVisibility(View.VISIBLE);
                }else{
                    registerActivity.user.setEmail(email);
                    registerActivity.onFragmentChanged(5);
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
                registerActivity.onFragmentChanged(3);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // 이메일 중복 체크
    public boolean checkDuplicateEmail(String str){
        String tmpEamil = "test@test"; // 확인용 임시 이메일

        if(str.equals(tmpEamil)){
            return true;
        }else{
            return false;
        }
    }
}