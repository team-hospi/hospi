package com.gradproject.hospi.register;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gradproject.hospi.R;

import org.w3c.dom.Text;

public class RegisterFragment3 extends Fragment {
    RegisterActivity registerActivity;
    DatePicker birthDp;
    RadioGroup sexRg;
    TextView radioErrorTxt;

    int radioId;
    String sex, date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_register3, container,false);

        registerActivity = (RegisterActivity) getActivity();
        birthDp = rootView.findViewById(R.id.birthDp);
        sexRg = rootView.findViewById(R.id.sexRg);
        radioErrorTxt = rootView.findViewById(R.id.radioErrorTxt);

        Button nextBtn = rootView.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioId = sexRg.getCheckedRadioButtonId();
                if(radioId==-1){
                    radioErrorTxt.setVisibility(View.VISIBLE);
                }else{
                    RadioButton sexRb = rootView.findViewById(radioId);
                    sex = sexRb.getText().toString();
                    int month = birthDp.getMonth() + 1;
                    date = birthDp.getYear() + "-" + month + "-" + birthDp.getDayOfMonth();
                    registerActivity.user.setSex(sex);
                    registerActivity.user.setBirth(date);
                    registerActivity.onFragmentChanged(3);
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
                registerActivity.onFragmentChanged(1);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}