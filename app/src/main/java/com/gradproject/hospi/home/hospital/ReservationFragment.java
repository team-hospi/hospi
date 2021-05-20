package com.gradproject.hospi.home.hospital;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.databinding.FragmentReservationBinding;
import com.gradproject.hospi.utils.Loading;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import static com.gradproject.hospi.home.HomeActivity.user;
import static com.gradproject.hospi.home.hospital.HospitalActivity.hospital;
import static com.gradproject.hospi.home.hospital.HospitalActivity.reservedList;

public class ReservationFragment extends Fragment implements OnBackPressedListener {
    private static final String TAG = "ReservationFragment";
    private FragmentReservationBinding binding;

    private static final int WEEKDAY = 0;
    private static final int SATURDAY = 1;
    private static final int HOLIDAY = 2;

    final String[] week = {"일", "월", "화", "수", "목", "금", "토"};

    HospitalActivity hospitalActivity;
    FirebaseFirestore db;
    Calendar cal, selectCal;
    DatePicker datePicker;
    String date;
    String selectDepartment, selectTime=null;
    boolean isClickDateSetBtn = false;
    boolean isClickTimeSetBtn = false;
    Loading loading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        cal = Calendar.getInstance();
        selectCal = Calendar.getInstance();
        loading = new Loading(getContext());

        datePicker = new DatePicker(getContext());
        datePicker.setMinDate(cal.getTimeInMillis());
        Calendar tmpCal = Calendar.getInstance();
        tmpCal.add(Calendar.MONTH, 1);
        datePicker.setMaxDate(tmpCal.getTimeInMillis());
        datePicker.setBackgroundColor(Color.WHITE);
        datePicker.setElevation(3);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReservationBinding.inflate(inflater, container, false);

        hospitalActivity = (HospitalActivity) getActivity();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        binding.calendar.setLayoutParams(layoutParams);

        LinearLayout.LayoutParams datePickerLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        datePickerLayoutParams.gravity = Gravity.CENTER;
        datePickerLayoutParams.setMargins(0, 25, 0, 25);
        datePicker.setLayoutParams(datePickerLayoutParams);

        // 예약자 정보
        binding.userNameTxt.setText(user.getName());
        binding.userPhoneTxt.setText(user.getPhone());
        binding.userBirthTxt.setText(user.getBirth());

        // 병원 정보
        binding.hospitalNameTxt.setText(hospital.getName());
        binding.hospitalTelTxt.setText(hospital.getTel());
        binding.hospitalAddressTxt.setText(hospital.getAddress());

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        ArrayList<String> departmentArray = (ArrayList<String>)hospital.getDepartment();

        binding.department.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, departmentArray));
        binding.department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectDepartment = departmentArray.get(position);
                closeDateSelect();
                closeTimeSelect();
                openTimeSelect();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        cal = Calendar.getInstance();
        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        selectCal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        date = cal.get(Calendar.MONTH)+1 + "." + cal.get(Calendar.DATE) + "(" + week[cal.get(Calendar.DAY_OF_WEEK)-1] + ")";
        binding.dateTxt.setText(date);

        binding.dateSetBtn.setOnClickListener(v -> {
            if (!(isClickDateSetBtn)) {
                openDateSelect();
            } else {
                closeDateSelect();
            }
        });

        datePicker.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {
            selectCal.set(year, monthOfYear, dayOfMonth);

            date = monthOfYear+1 + "." + dayOfMonth + "(" + week[selectCal.get(Calendar.DAY_OF_WEEK)-1] + ")";
            binding.dateTxt.setText(date);

            closeTimeSelect();
            closeDateSelect();

            openTimeSelect();
        });

        binding.timeSetBtn.setOnClickListener(v -> {
            if (!(isClickTimeSetBtn)) {
                openTimeSelect();
            } else {
                closeTimeSelect();
            }
        });

        binding.reservationBtn.setOnClickListener(v -> {
            if(selectTime != null){
                reservationProcess();
            }else{
                notSelectedTimeAlert();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        if(getArguments()!=null){
            if(getArguments().getBoolean("popUp", false)){
                Objects.requireNonNull(getActivity()).finish();
            }else{
                hospitalActivity.onReservationFragmentChanged(0);
            }
        }else{
            hospitalActivity.onReservationFragmentChanged(0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void notSelectedTimeAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setCancelable(false)
                .setMessage("시간을 설정해주세요.")
                .setPositiveButton("확인", (dialogInterface, i) -> {});
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void reservationProcess(){
        loading.show();
        Reservation reservation = new Reservation();
        reservation.setId(user.getEmail());    // 유저 이메일 설정
        reservation.setHospitalId(hospital.getId());     // 병원 아이디 설정
        reservation.setHospitalName(hospital.getName()); // 병원 이름 설정
        reservation.setDepartment(selectDepartment);     // 진료과 설정
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(selectCal.getTime());
        reservation.setReservationDate(date);      // 예약 날짜 설정
        reservation.setReservationTime(selectTime);    // 예약 시간 설정
        reservation.setAdditionalContent(binding.additionalContentEdt.getText().toString());  // 추가 입력한 내용 설정
        long timestamp = new Timestamp(System.currentTimeMillis()).getTime();
        reservation.setTimestamp(timestamp);    // 현재 시간 타임스탬프 설정
        reservation.setReservationStatus(Reservation.CONFIRMING_RESERVATION);     // 예약 신청됨 상태로 설정
        reservation.setCancelComment(null);

        db.collection(Reservation.DB_NAME)
                .add(reservation)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    reservedListUpdate(reservation);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    reservationFail();
                });
    }

    private void reservationSuccess(){
        loading.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setCancelable(false)
                .setMessage("예약 신청이 완료되었습니다.\n병원에서 예약 확정이 되는대로 알려드리겠습니다.")
                .setPositiveButton("확인", (dialogInterface, i) -> {
                    boolean isPopUp = false;

                    if(getArguments() != null){
                        isPopUp = getArguments().getBoolean("popUp", false);
                    }

                    Objects.requireNonNull(getActivity()).finish();

                    if (!isPopUp) {
                        Intent intent = new Intent(getContext(), HospitalActivity.class);
                        intent.putExtra("hospital", hospital);
                        startActivity(intent);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void reservationFail(){
        loading.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setCancelable(false)
                .setMessage("예약에 실패하였습니다.\n잠시 후 다시 진행해주세요.")
                .setPositiveButton("확인", (dialogInterface, i) -> { /* empty */ });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void reservationFailProcess(Reservation reservation){
        Query query = db.collection(Reservation.DB_NAME)
                .whereEqualTo("id", reservation.getId())
                .whereEqualTo("hospitalId", reservation.getHospitalId())
                .whereEqualTo("timestamp", reservation.getTimestamp());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    db.collection(Reservation.DB_NAME).document(document.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                            .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    public void reservedListUpdate(Reservation reservation){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(selectCal.getTime());
        Reserved reserved = null;
        HashMap<String, List<String>> reservedMap;

        for(int i=0; i<reservedList.size(); i++){
            reserved = reservedList.get(i);
            reservedMap = (HashMap<String, List<String>>)reserved.getReservedMap();
            if(reserved.getDepartment().equals(selectDepartment)){
                if(reservedMap.containsKey(date)){
                    Objects.requireNonNull(reservedMap.get(date)).add(selectTime);
                    reserved.setReservedMap(reservedMap);
                    break;
                }else{
                    ArrayList<String> tmpArr = new ArrayList<>();
                    tmpArr.add(selectTime);
                    reservedMap.put(date, tmpArr);
                    reserved.setReservedMap(reservedMap);
                }
            }else{
                reserved.setHospitalId(hospital.getId());
                reserved.setDepartment(selectDepartment);
                ArrayList<String> tmpArr = new ArrayList<>();
                tmpArr.add(selectTime);
                HashMap<String, List<String>> tmpMap = new HashMap<>();
                tmpMap.put(date, tmpArr);
                reserved.setReservedMap(tmpMap);
            }
        }

        if(reserved==null){
            reserved = new Reserved();
            reserved.setHospitalId(hospital.getId());
            reserved.setDepartment(selectDepartment);
            ArrayList<String> tmpArr = new ArrayList<>();
            tmpArr.add(selectTime);
            HashMap<String, List<String>> tmpMap = new HashMap<>();
            tmpMap.put(date, tmpArr);
            reserved.setReservedMap(tmpMap);
        }

        Query query = db.collection(Reserved.DB_NAME)
                .whereEqualTo("hospitalId", reserved.getHospitalId())
                .whereEqualTo("department", reserved.getDepartment());

        Reserved finalReserved = reserved;

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(Objects.requireNonNull(task.getResult()).size()!=0){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        DocumentReference documentReference = db.collection(Reserved.DB_NAME).document(document.getId());

                        documentReference
                                .update("reservedMap", finalReserved.getReservedMap())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    reservationSuccess();
                                })
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Error updating document", e);
                                    reservationFailProcess(reservation);
                                    reservationFail();
                                });
                    }
                }else{
                    db.collection(Reserved.DB_NAME)
                            .add(finalReserved)
                            .addOnSuccessListener(documentReference -> {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                reservationSuccess();
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "Error adding document", e);
                                reservationFailProcess(reservation);
                                reservationFail();
                            });
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
                reservationFailProcess(reservation);
                reservationFail();
            }
        });
    }

    public void openDateSelect(){
        binding.calendar.addView(datePicker);
        isClickDateSetBtn = true;
        binding.calendarExpandImg.setImageResource(R.drawable.ic_action_expand_more);
    }

    public void closeDateSelect(){
        binding.calendar.removeAllViews();
        isClickDateSetBtn = false;
        binding.calendarExpandImg.setImageResource(R.drawable.ic_action_expand_less);
    }

    public void openTimeSelect(){
        setTimeTable(selectCal.get(Calendar.DAY_OF_WEEK));
        isClickTimeSetBtn = true;
        binding.timeExpandImg.setImageResource(R.drawable.ic_action_expand_more);
    }

    public void closeTimeSelect(){
        binding.timeTxt.setText("시간 설정");
        binding.timeTable.removeAllViews();
        isClickTimeSetBtn = false;
        binding.timeExpandImg.setImageResource(R.drawable.ic_action_expand_less);
    }

    public void setTimeTable(int date){
        switch(date){
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                if(hospital.isStatus()){
                    timeTable(WEEKDAY);
                }else{
                    notPossibleReservation();
                }
                break;
            case 7:
                if(hospital.isSaturdayStatus()){
                    timeTable(SATURDAY);
                }else{
                    notPossibleReservation();
                }
                break;
            default:
                if(hospital.isHolidayStatus()){
                    timeTable(HOLIDAY);
                }else{
                    notPossibleReservation();
                }
                break;
        }
    }

    private void notPossibleReservation(){
        TextView textView = new TextView(getContext());
        TableRow tableRow = new TableRow(getContext());
        textView.setText("예약이 불가한 날짜입니다. 다른 날짜를 선택해주세요.");
        textView.setTextColor(Color.RED);
        tableRow.addView(textView);
        binding.timeTable.addView(tableRow);
    }

    // 평일: 0, 토요일: 1, 일요일 및 공휴일: 2
    @SuppressLint("SetTextI18n")
    public void timeTable(int dateNum){
        String open, close, lunch;

        switch(dateNum){
            case WEEKDAY:
                open = hospital.getWeekdayOpen();
                close = hospital.getWeekdayClose();
                lunch = hospital.getLunchTime();
                break;
            case SATURDAY:
                open = hospital.getSaturdayOpen();
                close = hospital.getSaturdayClose();
                lunch = hospital.getLunchTime();
                break;
            case HOLIDAY:
                open = hospital.getHolidayOpen();
                close = hospital.getHolidayClose();
                lunch = hospital.getLunchTime();
                break;
            default:
                Toast.makeText(getContext(), "타임테이블 생성 에러 발생", Toast.LENGTH_LONG).show();
                return;
        }

        ArrayList<String> timeList = reservationTimeMaker(open, close, lunch);

        TableRow tableRow = new TableRow(getContext());
        Button[] button = new Button[timeList.size()];

        for(int i=0; i<timeList.size(); i++){
            if(i%4==0){
                binding.timeTable.addView(tableRow);
                tableRow = new TableRow(getContext());
            }
            String tmp = timeList.get(i);

            button[i] = new Button(getContext());
            button[i].setText(timeList.get(i));
            button[i].setBackgroundResource(R.drawable.button_clickable_true);
            button[i].setOnClickListener(v -> {
                selectTime = tmp;
                StringTokenizer st = new StringTokenizer(tmp, ":");
                String hour = st.nextToken();
                String min = st.nextToken();
                binding.timeTxt.setText(hour + "시 " + min + "분");
                binding.timeTable.removeAllViews();
                isClickTimeSetBtn = false;
                binding.timeExpandImg.setImageResource(R.drawable.ic_action_expand_less);
            });

            for(Reserved reserved : reservedList){
                if(selectDepartment.equals(reserved.getDepartment())){
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String date = df.format(selectCal.getTime());

                    HashMap<String, List<String>> reservedMap = (HashMap<String, List<String>>)reserved.getReservedMap();
                    ArrayList<String> dateArray = (ArrayList<String>)reservedMap.get(date);

                    if(dateArray != null){
                        for(String time : dateArray){
                            if(timeList.get(i).equals(time)){
                                button[i].setClickable(false);
                                button[i].setBackgroundResource(R.drawable.button_clickable_false);
                            }
                        }
                    }
                }
            }

            Calendar curCal = Calendar.getInstance();
            if(curCal.get(Calendar.YEAR) == selectCal.get(Calendar.YEAR)
                    && curCal.get(Calendar.MONTH) == selectCal.get(Calendar.MONTH)
                    && curCal.get(Calendar.DAY_OF_MONTH) == selectCal.get(Calendar.DAY_OF_MONTH)){
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String[] time = sdf.format(curCal.getTime()).split(":");
                String[] selTime = button[i].getText().toString().split(":");
                int curMin = Integer.parseInt(time[0]) * 60 + Integer.parseInt(time[1]);
                int selMin = Integer.parseInt(selTime[0]) * 60 + Integer.parseInt(selTime[1]);

                if(selMin-30<=curMin){
                    button[i].setClickable(false);
                    button[i].setBackgroundResource(R.drawable.button_clickable_false);
                }
            }

            TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            buttonLayoutParams.leftMargin = 10;
            buttonLayoutParams.rightMargin = 10;
            buttonLayoutParams.bottomMargin = 20;
            tableRow.addView(button[i], buttonLayoutParams);
        }

        binding.timeTable.addView(tableRow);
    }

    // 예약 시간 30분 간격으로 점심시간 제외해서 구하는 메서드
    public ArrayList<String> reservationTimeMaker(String open, String close, String lunch){

        ArrayList<String> timeList = new ArrayList<>();

        String[] openArr = open.split(":");
        String[] closeArr = close.split(":");
        String[] lunchArr = lunch.split(":");

        int openMin = Integer.parseInt(openArr[0]) * 60 + Integer.parseInt(openArr[1]);
        int closeMin = Integer.parseInt(closeArr[0]) * 60 + Integer.parseInt(closeArr[1]);
        int lunchMin = Integer.parseInt(lunchArr[0]) * 60 + Integer.parseInt(lunchArr[1]);
        int lunchEndMin = lunchMin + 60;

        while(openMin<closeMin){
            if(openMin%60 == 0){
                timeList.add(openMin / 60 +":"+"00");
            }else{
                timeList.add(openMin / 60 +":"+"30");
            }
            openMin+=30;
        }

        ArrayList<String> tmpList = new ArrayList<>();

        while(lunchMin<lunchEndMin){
            if(lunchMin%60 == 0){
                tmpList.add(lunchMin / 60 +":"+"00");
            }else{
                tmpList.add(lunchMin / 60 +":"+"30");
            }
            lunchMin+=30;
        }

        for(int i=0; i<timeList.size(); i++){
            for(int j=0; j<tmpList.size(); j++){
                if(timeList.get(i).equals(tmpList.get(j))){
                    timeList.remove(i);
                }
            }
        }

        return timeList;
    }
}