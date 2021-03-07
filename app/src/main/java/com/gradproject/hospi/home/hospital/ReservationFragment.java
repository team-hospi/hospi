package com.gradproject.hospi.home.hospital;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.home.search.Hospital;

import org.w3c.dom.Text;

public class ReservationFragment extends Fragment implements OnBackPressedListener {
    HospitalActivity hospitalActivity;

    Button nextBtn;
    LinearLayout backBtn;
    TextView nameTxt, phoneTxt, birthTxt, hospitalNameTxt;
    TextView reservationDateTxt, reservationTimeTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_reservation, container,false);

        hospitalActivity = (HospitalActivity) getActivity();
        backBtn = rootView.findViewById(R.id.backBtn);
        nextBtn = rootView.findViewById(R.id.nextBtn);
        nameTxt = rootView.findViewById(R.id.nameTxt);
        phoneTxt = rootView.findViewById(R.id.phoneTxt);
        birthTxt = rootView.findViewById(R.id.birthTxt);
        hospitalNameTxt = rootView.findViewById(R.id.hospitalNameTxt);
        reservationDateTxt = rootView.findViewById(R.id.reservationDateTxt);
        reservationTimeTxt = rootView.findViewById(R.id.reservationTimeTxt);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hospitalActivity.onReservationFragmentChanged(2);
            }
        });

        return rootView;
    }

    @Override
    public void onBackPressed(){
        hospitalActivity.onReservationFragmentChanged(0);
        reservationDateTxt.setText("날짜를 설정해주세요.");
        reservationTimeTxt.setText("시간을 설정해주세요.");
    }
}