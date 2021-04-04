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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;

import java.util.Calendar;

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

    Calendar cal, selectCal;
    CalendarView calendarView;
    TableLayout tableLayout;
    TableRow tableRow[];

    String date;

    boolean isClickDateSetBtn = true;
    boolean isClickTimeSetBtn = false;
    boolean isClickDepartmentSetBtn = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cal = Calendar.getInstance();
        calendarView = new CalendarView(getContext());
        calendarView.setMinDate(cal.getTimeInMillis());
        Calendar tmpCal = Calendar.getInstance();
        tmpCal.add(Calendar.MONTH, 1);
        calendarView.setMaxDate(tmpCal.getTimeInMillis());

        tableLayout = new TableLayout(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_reservation, container, false);

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

        // 예약자 정보
        userNameTxt.setText(user.getName());
        userPhoneTxt.setText(user.getPhone());
        userBirthTxt.setText(user.getBirth());

        // 병원 정보
        hospitalNameTxt.setText(hospital.getName());
        hospitalTelTxt.setText(hospital.getTel());
        hospitalAddressTxt.setText(hospital.getAddress());

        backBtn.setOnClickListener(v -> onBackPressed());

        calendar.addView(calendarView);
        cal = Calendar.getInstance();
        cal.setTimeInMillis(calendarView.getDate());
        date = cal.get(Calendar.MONTH)+1 + "." + cal.get(Calendar.DATE) + "(" + getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK)) + ")";
        dateTxt.setText(date);

        dateSetBtn.setOnClickListener(v -> {
            if (!(isClickDateSetBtn)) {
                calendar.addView(calendarView);
                isClickDateSetBtn = true;
                calendarExpandImg.setImageResource(R.drawable.ic_action_expand_more);
            } else {
                calendar.removeAllViews();
                isClickDateSetBtn = false;
                calendarExpandImg.setImageResource(R.drawable.ic_action_expand_less);
            }
        });

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectCal = Calendar.getInstance();
            selectCal.set(year, month, dayOfMonth);
            date = month+1 + "." + dayOfMonth + "(" + getDayOfWeek(selectCal.get(Calendar.DAY_OF_WEEK)) + ")";
            dateTxt.setText(date);
        });

        timeSetBtn.setOnClickListener(v -> {
            if (!(isClickTimeSetBtn)) {
                setTimeTable(selectCal.get(Calendar.DAY_OF_WEEK));
                isClickTimeSetBtn = true;
                timeExpandImg.setImageResource(R.drawable.ic_action_expand_more);
            } else {

                isClickTimeSetBtn = false;
                timeExpandImg.setImageResource(R.drawable.ic_action_expand_less);
            }
        });

        departmentSetBtn.setOnClickListener(v -> {
            if (!(isClickDepartmentSetBtn)) {

                isClickDepartmentSetBtn = true;
                departmentExpandImg.setImageResource(R.drawable.ic_action_expand_more);
            } else {

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
    public void onBackPressed() {
        hospitalActivity.onReservationFragmentChanged(0);
    }

    public String getDayOfWeek(int date) {
        switch (date) {
            case 1:
                return "일";
            case 2:
                return "월";
            case 3:
                return "화";
            case 4:
                return "수";
            case 5:
                return "목";
            case 6:
                return "금";
            case 7:
                return "토";
            default:
                return null;
        }
    }

    public void setTimeTable(int date){
        switch(date){
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                weekdayTimeTable(hospital.isStatus());
                break;
            case 7:
                saturdayTimeTable(hospital.isSaturdayStatus());
                break;
            default:
                holidayTimeTable(hospital.isHolidayStatus());
                break;
        }
    }

    public void weekdayTimeTable(boolean isStatus){
        if(isStatus){
            int openHr = Integer.parseInt(hospital.getWeekdayOpen().substring(0,2));
            int openMin = Integer.parseInt(hospital.getWeekdayOpen().substring(3));
            int closeHr = Integer.parseInt(hospital.getWeekdayClose().substring(0,2));
            int closeMin = Integer.parseInt(hospital.getWeekdayClose().substring(3));
        }
    }

    public void saturdayTimeTable(boolean isStatus){
        if(isStatus){

        }
    }

    public void holidayTimeTable(boolean isStatus){
        if(isStatus){

        }
    }
}