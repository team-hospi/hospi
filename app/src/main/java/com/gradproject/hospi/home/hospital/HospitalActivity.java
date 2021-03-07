package com.gradproject.hospi.home.hospital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.home.search.Hospital;

import java.util.List;

public class HospitalActivity extends AppCompatActivity {
    HospitalInfoDetailFragment hospitalInfoDetailFragment;
    ReservationFragment reservationFragment; ReservationFragment2 reservationFragment2;
    ReservationFragment3 reservationFragment3;
    InquiryFragment inquiryFragment;

    static Hospital hospital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        hospital = (Hospital) getIntent().getSerializableExtra("hospital");

        hospitalInfoDetailFragment = new HospitalInfoDetailFragment();
        reservationFragment = new ReservationFragment(); reservationFragment2 = new ReservationFragment2();
        reservationFragment3 = new ReservationFragment3();
        inquiryFragment = new InquiryFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.Container, hospitalInfoDetailFragment).commit();
    }

    public void onReservationFragmentChanged(int index){
        if(index == 0){
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, hospitalInfoDetailFragment).commit();
        }else if(index == 1){
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, reservationFragment).commit();
        }else if(index == 2){
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, reservationFragment2).commit();
        }else if(index == 3){
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, reservationFragment3).commit();
        }
    }

    public void onInquiryFragmentChanged(int index){
        if(index == 0){
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, hospitalInfoDetailFragment).commit();
        }else if(index == 1){
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, inquiryFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if(fragmentList!=null){
            for(Fragment fragment : fragmentList) {
                if (fragment instanceof OnBackPressedListener) {
                    ((OnBackPressedListener) fragment).onBackPressed();
                }
            }
        }
    }
}