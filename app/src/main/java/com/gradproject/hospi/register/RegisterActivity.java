package com.gradproject.hospi.register;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.User;
import com.gradproject.hospi.databinding.ActivityRegisterBinding;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    // 회원가입 프래그먼트
    RegisterFragment1 registerFragment1; RegisterFragment2 registerFragment2;
    RegisterFragment3 registerFragment3; RegisterFragment4 registerFragment4;
    RegisterFragment5 registerFragment5;

    User user; // 회원가입 정보 임시 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegisterBinding binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = new User();

        registerFragment1 = new RegisterFragment1(); registerFragment2 = new RegisterFragment2();
        registerFragment3 = new RegisterFragment3(); registerFragment4 = new RegisterFragment4();
        registerFragment5 = new RegisterFragment5();

        getSupportFragmentManager().beginTransaction().replace(R.id.registerContainer, registerFragment1).commit();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for(Fragment fragment : fragmentList) {
            if (fragment instanceof OnBackPressedListener) {
                ((OnBackPressedListener) fragment).onBackPressed();
            }
        }
    }

    // 프래그먼트 화면 이동
    public void onFragmentChanged(int index){
        switch(index){
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.registerContainer, registerFragment1).commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.registerContainer, registerFragment2).commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.registerContainer, registerFragment3).commit();
                break;
            case 3:
                getSupportFragmentManager().beginTransaction().replace(R.id.registerContainer, registerFragment4).commit();
                break;
            case 4:
                getSupportFragmentManager().beginTransaction().replace(R.id.registerContainer, registerFragment5).commit();
                break;
        }
    }
}