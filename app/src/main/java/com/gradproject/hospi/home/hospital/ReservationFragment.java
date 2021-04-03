package com.gradproject.hospi.home.hospital;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;

import static com.gradproject.hospi.home.HomeActivity.user;
import static com.gradproject.hospi.home.hospital.HospitalActivity.hospital;

public class ReservationFragment extends Fragment implements OnBackPressedListener {
    HospitalActivity hospitalActivity;

    Button reservationBtn;
    ImageButton backBtn;
    EditText additionalContentEdt;
    TextView dateTxt, timeTxt, departmentTxt;
    TextView userNameTxt, userPhoneTxt, userBirthTxt;
    TextView hospitalNameTxt, hospitalTelTxt, hospitalAddressTxt;
    FrameLayout dateSetBtn, timeSetBtn, departmentSetBtn;
    LinearLayout calendar, time, department;
    ImageView calendarExpandImg, timeExpandImg, departmentExpandImg;

    boolean isClickDateSetBtn = false;
    boolean isClickTimeSetBtn = false;
    boolean isClickDepartmentSetBtn = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_reservation, container,false);

        hospitalActivity = (HospitalActivity) getActivity();
        reservationBtn = rootView.findViewById(R.id.reservationBtn);
        backBtn = rootView.findViewById(R.id.backBtn);
        additionalContentEdt = rootView.findViewById(R.id.additionalContentEdt);
        userNameTxt = rootView.findViewById(R.id.userNameTxt);
        userPhoneTxt = rootView.findViewById(R.id.userPhoneTxt);
        userBirthTxt = rootView.findViewById(R.id.userBirthTxt);
        hospitalNameTxt = rootView.findViewById(R.id.hospitalNameTxt);
        hospitalTelTxt = rootView.findViewById(R.id.hospitalTelTxt);
        hospitalAddressTxt = rootView.findViewById(R.id.hospitalAddressTxt);
        dateTxt = rootView.findViewById(R.id.dateTxt);
        dateSetBtn = rootView.findViewById(R.id.dateSetBtn);
        calendar = rootView.findViewById(R.id.calendar);
        calendarExpandImg = rootView.findViewById(R.id.calendarExpandImg);
        timeTxt = rootView.findViewById(R.id.timeTxt);
        timeSetBtn = rootView.findViewById(R.id.timeSetBtn);
        time = rootView.findViewById(R.id.time);
        timeExpandImg = rootView.findViewById(R.id.timeExpandImg);
        departmentTxt = rootView.findViewById(R.id.departmentTxt);
        departmentSetBtn = rootView.findViewById(R.id.departmentSetBtn);
        department = rootView.findViewById(R.id.department);
        departmentExpandImg = rootView.findViewById(R.id.departmentExpandImg);

        userNameTxt.setText(user.getName());
        userPhoneTxt.setText(user.getPhone());
        userBirthTxt.setText(user.getBirth());
        hospitalNameTxt.setText(hospital.getName());
        hospitalTelTxt.setText(hospital.getTel());
        hospitalAddressTxt.setText(hospital.getAddress());

        backBtn.setOnClickListener(v -> onBackPressed());

        dateSetBtn.setOnClickListener(v -> {
            if(!(isClickDateSetBtn)){
                CalendarView calendarView = new CalendarView(getContext());
                calendar.addView(calendarView);
                isClickDateSetBtn = true;
                calendarExpandImg.setImageResource(R.drawable.ic_action_expand_more);
            }else{
                calendar.removeAllViews();
                isClickDateSetBtn = false;
                calendarExpandImg.setImageResource(R.drawable.ic_action_expand_less);
            }
        });

        timeSetBtn.setOnClickListener(v -> {
            if(!(isClickTimeSetBtn)){
                TimePicker timePicker = new TimePicker(getContext());
                time.addView(timePicker);
                isClickTimeSetBtn = true;
                timeExpandImg.setImageResource(R.drawable.ic_action_expand_more);
            }else{
                time.removeAllViews();
                isClickTimeSetBtn = false;
                timeExpandImg.setImageResource(R.drawable.ic_action_expand_less);
            }
        });

        departmentSetBtn.setOnClickListener(v -> {
            if(!(isClickDepartmentSetBtn)){

                isClickDepartmentSetBtn = true;
                departmentExpandImg.setImageResource(R.drawable.ic_action_expand_more);
            }else{

                isClickDepartmentSetBtn = false;
                departmentExpandImg.setImageResource(R.drawable.ic_action_expand_less);
            }
        });

        reservationBtn.setOnClickListener(v -> {
            // TODO: 예약 구현
            hospitalActivity.onReservationFragmentChanged(2);
        });

        return rootView;
    }

    @Override
    public void onBackPressed(){
        hospitalActivity.onReservationFragmentChanged(0);
    }
}