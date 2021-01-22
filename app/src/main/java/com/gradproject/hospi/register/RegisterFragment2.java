package com.gradproject.hospi.register;

import android.os.Bundle;
import android.util.Log;
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

public class RegisterFragment2 extends Fragment {
    RegisterActivity registerActivity;
    EditText inputName;
    TextView nameErrorTxt;

    String name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_register2, container,false);

        registerActivity = (RegisterActivity) getActivity();
        inputName = rootView.findViewById(R.id.inputName);
        nameErrorTxt = rootView.findViewById(R.id.nameErrorTxt);

        Button nextBtn = rootView.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = inputName.getText().toString();

                if(name.equals("")){
                    nameErrorTxt.setVisibility(View.VISIBLE);
                }else{
                    registerActivity.user.setName(name);
                    registerActivity.onFragmentChanged(2);
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
                registerActivity.onFragmentChanged(0);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}