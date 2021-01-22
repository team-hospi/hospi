package com.gradproject.hospi.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gradproject.hospi.R;

import org.w3c.dom.Text;

public class RegisterFragment4 extends Fragment {
    RegisterActivity registerActivity;
    EditText inputPhone;
    TextView phoneErrorTxt;

    String phone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_register4, container,false);

        registerActivity = (RegisterActivity) getActivity();
        inputPhone = rootView.findViewById(R.id.inputPhone);
        phoneErrorTxt = rootView.findViewById(R.id.phoneErrorTxt);

        Button nextBtn = rootView.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = inputPhone.getText().toString();
                if(phone.equals("")){
                    phoneErrorTxt.setVisibility(View.VISIBLE);
                }else{
                    registerActivity.user.setPhone(phone);
                    registerActivity.onFragmentChanged(4);
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
                registerActivity.onFragmentChanged(2);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}