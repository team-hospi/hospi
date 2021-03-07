package com.gradproject.hospi.home.hospital;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;

public class ReservationFragment2 extends Fragment implements OnBackPressedListener {
    HospitalActivity hospitalActivity;

    Button nextBtn;
    LinearLayout backBtn;
    EditText additionalContentEdt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_reservation2, container,false);

        hospitalActivity = (HospitalActivity) getActivity();
        backBtn = rootView.findViewById(R.id.backBtn);
        nextBtn = rootView.findViewById(R.id.nextBtn);
        additionalContentEdt = rootView.findViewById(R.id.additionalContentEdt);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hospitalActivity.onReservationFragmentChanged(3);
            }
        });

        return rootView;
    }

    @Override
    public void onBackPressed(){
        hospitalActivity.onReservationFragmentChanged(1);
        additionalContentEdt.setText("");
    }
}