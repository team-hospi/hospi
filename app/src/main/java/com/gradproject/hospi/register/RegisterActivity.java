package com.gradproject.hospi.register;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.gradproject.hospi.R;

public class RegisterActivity extends AppCompatActivity {

    // 회원가입 프래그먼트
    RegisterFragment1 registerFragment1; RegisterFragment2 registerFragment2;
    RegisterFragment3 registerFragment3; RegisterFragment4 registerFragment4;
    RegisterFragment5 registerFragment5; RegisterFragment6 registerFragment6;
    RegisterFragment7 registerFragment7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerFragment1 = new RegisterFragment1(); registerFragment2 = new RegisterFragment2();
        registerFragment3 = new RegisterFragment3(); registerFragment4 = new RegisterFragment4();
        registerFragment5 = new RegisterFragment5(); registerFragment6 = new RegisterFragment6();
        registerFragment7 = new RegisterFragment7();

        getSupportFragmentManager().beginTransaction().replace(R.id.registerContainer, registerFragment1).commit();
    }

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
            case 5:
                getSupportFragmentManager().beginTransaction().replace(R.id.registerContainer, registerFragment6).commit();
                break;
            case 6:
                getSupportFragmentManager().beginTransaction().replace(R.id.registerContainer, registerFragment7).commit();
                break;
        }
    }
}