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
    TextView emailErrorTxt, emailErrorTxt2, emailErrorTxt3; // 1: 중복 체크 2: 공백 체크 3: 올바른 이메일인지 체크
    EditText inputEmail;

    String email;
    String tmpEamil = "test@test"; // 확인용 임시 이메일

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_register5, container,false);

        registerActivity = (RegisterActivity) getActivity();
        emailErrorTxt = rootView.findViewById(R.id.emailErrorTxt);
        emailErrorTxt2 = rootView.findViewById(R.id.emailErrorTxt2);
        emailErrorTxt3 = rootView.findViewById(R.id.emailErrorTxt3);
        inputEmail = rootView.findViewById(R.id.inputEmail);

        Button nextBtn = rootView.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = inputEmail.getText().toString();

                if(email.equals(tmpEamil)){
                    emailErrorTxt.setVisibility(View.VISIBLE);
                    emailErrorTxt2.setVisibility(View.INVISIBLE);
                    emailErrorTxt3.setVisibility(View.INVISIBLE);
                }else if(email.equals("")){
                    emailErrorTxt.setVisibility(View.INVISIBLE);
                    emailErrorTxt2.setVisibility(View.VISIBLE);
                    emailErrorTxt3.setVisibility(View.INVISIBLE);
                }else if(!(email.contains("@"))){
                    emailErrorTxt.setVisibility(View.INVISIBLE);
                    emailErrorTxt2.setVisibility(View.INVISIBLE);
                    emailErrorTxt3.setVisibility(View.VISIBLE);
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
}