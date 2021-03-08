package com.gradproject.hospi.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;

public class RegisterFragment1 extends Fragment implements OnBackPressedListener {
    RegisterActivity registerActivity;
    RadioButton agree, noAgree;  // 동의, 미동의
    TextView radioErrorTxt; // 미동의 및 체크 안했을때 표시할 경고 메시지

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_register1, container,false);

        registerActivity = (RegisterActivity) getActivity();
        agree = rootView.findViewById(R.id.agree);
        noAgree = rootView.findViewById(R.id.noAgree);
        radioErrorTxt = rootView.findViewById(R.id.radioErrorTxt);

        // 다음 버튼
        Button nextBtn = rootView.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(agree.isChecked()){
                    registerActivity.onFragmentChanged(1);
                }else{
                    radioErrorTxt.setVisibility(View.VISIBLE);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onBackPressed() {
        getActivity().finish();
    }
}