package com.gradproject.hospi.home.mypage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.gradproject.hospi.R;

public class SettingActivity extends AppCompatActivity {

    // 마이페이지 각 목록과 연결되는 화면
    EditMyInfoFragment editMyInfoFragment; FavoriteFragment favoriteFragment;
    InquiryFragment inquiryFragment; NoticeFragment noticeFragment;
    PrescriptionFragment prescriptionFragment; TermsFragment termsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        editMyInfoFragment = new EditMyInfoFragment(); favoriteFragment = new FavoriteFragment();
        inquiryFragment = new InquiryFragment(); noticeFragment = new NoticeFragment();
        prescriptionFragment = new PrescriptionFragment(); termsFragment = new TermsFragment();

        String select = getIntent().getStringExtra("selectBtn");

        switch (select){
            case "myInfoEditBtn":
                getSupportFragmentManager().beginTransaction().replace(R.id.settingContainer, editMyInfoFragment).commit();
                break;
            case "favoritesBtn":
                getSupportFragmentManager().beginTransaction().replace(R.id.settingContainer, favoriteFragment).commit();
                break;
            case "prescriptionBtn":
                getSupportFragmentManager().beginTransaction().replace(R.id.settingContainer, prescriptionFragment).commit();
                break;
            case "inquiryDetailsBtn":
                getSupportFragmentManager().beginTransaction().replace(R.id.settingContainer, inquiryFragment).commit();
                break;
            case "termsBtn":
                getSupportFragmentManager().beginTransaction().replace(R.id.settingContainer, termsFragment).commit();
                break;
            case "noticeBtn":
                getSupportFragmentManager().beginTransaction().replace(R.id.settingContainer, noticeFragment).commit();
                break;
        }
    }
}