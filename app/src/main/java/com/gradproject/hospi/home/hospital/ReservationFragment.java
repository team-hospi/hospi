package com.gradproject.hospi.home.hospital;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;

import static com.gradproject.hospi.home.HomeActivity.user;
import static com.gradproject.hospi.home.hospital.HospitalActivity.hospital;

public class ReservationFragment extends Fragment implements OnBackPressedListener {
    HospitalActivity hospitalActivity;

    Button nextBtn, setDateBtn, setTimeBtn;
    ImageButton backBtn;
    TextView nameTxt, phoneTxt, birthTxt, hospitalNameTxt;
    TextView reservationDateTxt, reservationTimeTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_reservation, container,false);

        hospitalActivity = (HospitalActivity) getActivity();
        backBtn = rootView.findViewById(R.id.backBtn);
        nextBtn = rootView.findViewById(R.id.nextBtn);
        setDateBtn = rootView.findViewById(R.id.setDateBtn);
        setTimeBtn = rootView.findViewById(R.id.setTimeBtn);
        nameTxt = rootView.findViewById(R.id.nameTxt);
        phoneTxt = rootView.findViewById(R.id.phoneTxt);
        birthTxt = rootView.findViewById(R.id.birthTxt);
        hospitalNameTxt = rootView.findViewById(R.id.hospitalNameTxt);
        reservationDateTxt = rootView.findViewById(R.id.reservationDateTxt);
        reservationTimeTxt = rootView.findViewById(R.id.reservationTimeTxt);

        nameTxt.setText(user.getName());
        phoneTxt.setText(user.getPhone());
        birthTxt.setText(user.getBirth());
        hospitalNameTxt.setText(hospital.getName());

        backBtn.setOnClickListener(v -> onBackPressed());

        nextBtn.setOnClickListener(v -> hospitalActivity.onReservationFragmentChanged(2));

        setDateBtn.setOnClickListener(v -> {
            // TODO: 예약 날짜 설정
        });

        setTimeBtn.setOnClickListener(v -> {
            // TODO: 예약 시간 설정
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