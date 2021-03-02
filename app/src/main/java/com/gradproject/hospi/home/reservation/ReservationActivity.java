package com.gradproject.hospi.home.reservation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gradproject.hospi.R;

public class ReservationActivity extends AppCompatActivity {
    HopitalInfoFragment hopitalInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        hopitalInfoFragment = new HopitalInfoFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.Container, hopitalInfoFragment).commit();
    }
}