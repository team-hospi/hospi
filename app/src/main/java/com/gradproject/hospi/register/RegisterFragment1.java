package com.gradproject.hospi.register;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gradproject.hospi.R;

public class RegisterFragment1 extends Fragment {
    RegisterActivity registerActivity;
    RadioButton agree, noAgree;
    TextView radioErrorTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_register1, container,false);

        registerActivity = (RegisterActivity) getActivity();
        agree = rootView.findViewById(R.id.agree);
        noAgree = rootView.findViewById(R.id.noAgree);
        radioErrorTxt = rootView.findViewById(R.id.radioErrorTxt);

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}