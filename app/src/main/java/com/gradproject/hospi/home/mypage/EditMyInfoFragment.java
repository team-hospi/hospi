package com.gradproject.hospi.home.mypage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.gradproject.hospi.LoginActivity;
import com.gradproject.hospi.R;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class EditMyInfoFragment extends Fragment {
    private static final int REQUEST_WRITE_ADDRESS_ACTIVITY_CODE = 100;

    LinearLayout backBtn; // 뒤로가기 버튼
    FrameLayout changePhNumBtn, changeBirthBtn, changeAddressBtn; // 전화번호 변경, 생년월일 변경, 주소 변경 버튼
    Button changePwBtn, logoutBtn, withdrawalBtn; // 비밀번호 변경, 로그아웃, 회원탈퇴 버튼
    TextView addressTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_edit_my_info, container,false);

        addressTxt = rootView.findViewById(R.id.addressTxt);
        addressTxt.setText(SettingActivity.address);

        backBtn = rootView.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        changePhNumBtn = rootView.findViewById(R.id.changePhNumBtn);
        changePhNumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - 전화번호 변경
            }
        });

        changeBirthBtn = rootView.findViewById(R.id.changeBirthBtn);
        changeBirthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - 생년월일 변경
            }
        });

        changeAddressBtn = rootView.findViewById(R.id.changeAddressBtn);
        changeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), WriteAddressActivity.class), REQUEST_WRITE_ADDRESS_ACTIVITY_CODE);
            }
        });

        changePwBtn = rootView.findViewById(R.id.changePwBtn);
        changePwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - 비밀번호 변경
            }
        });

        logoutBtn = rootView.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getContext().getSharedPreferences("account", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("id");
                editor.remove("pw");
                editor.commit();

                ActivityCompat.finishAffinity(getActivity());
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        withdrawalBtn = rootView.findViewById(R.id.withdrawalBtn);
        withdrawalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - 회원탈퇴
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_WRITE_ADDRESS_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                String address = data.getStringExtra("address");
                addressTxt.setText(address);
                // TODO - 받아온 주소 서버 저장
            }
        }
    }
}