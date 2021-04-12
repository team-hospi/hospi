package com.gradproject.hospi.home.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.gradproject.hospi.home.HomeActivity.user;
import static com.gradproject.hospi.home.hospital.HospitalActivity.hospital;
import static com.gradproject.hospi.home.hospital.HospitalActivity.reservedList;

public class ReservationFragment extends Fragment implements OnBackPressedListener {
    private static final String TAG = "ReservationFragment";

    private static final int WEEKDAY = 0;
    private static final int SATURDAY = 1;
    private static final int HOLIDAY = 2;

    HospitalActivity hospitalActivity;
    FirebaseFirestore db;

    Button reservationBtn;
    ImageButton backBtn;
    EditText additionalContentEdt;
    TextView dateTxt, timeTxt, departmentTxt;
    TextView userNameTxt, userPhoneTxt, userBirthTxt;
    TextView hospitalNameTxt, hospitalTelTxt, hospitalAddressTxt;
    FrameLayout dateSetBtn, timeSetBtn;
    LinearLayout calendar;
    Spinner department;
    TableLayout timeTable;
    ImageView calendarExpandImg, timeExpandImg;

    Calendar cal, selectCal;
    CalendarView calendarView;

    String date;
    String selectDepartment, selectTime;

    boolean isClickDateSetBtn = false;
    boolean isClickTimeSetBtn = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        cal = Calendar.getInstance();
        calendarView = new CalendarView(getContext());
        calendarView.setMinDate(cal.getTimeInMillis());
        Calendar tmpCal = Calendar.getInstance();
        tmpCal.add(Calendar.MONTH, 1);
        calendarView.setMaxDate(tmpCal.getTimeInMillis());

        selectCal = Calendar.getInstance();
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

        departmentTxt = rootView.findViewById(R.id.departmentTxt);
        department = rootView.findViewById(R.id.department);

        dateTxt = rootView.findViewById(R.id.dateTxt);
        dateSetBtn = rootView.findViewById(R.id.dateSetBtn);
        calendar = rootView.findViewById(R.id.calendar);
        calendarExpandImg = rootView.findViewById(R.id.calendarExpandImg);

        timeTxt = rootView.findViewById(R.id.timeTxt);
        timeSetBtn = rootView.findViewById(R.id.timeSetBtn);
        timeTable = rootView.findViewById(R.id.timeTable);
        timeExpandImg = rootView.findViewById(R.id.timeExpandImg);

        // 예약자 정보
        userNameTxt.setText(user.getName());
        userPhoneTxt.setText(user.getPhone());
        userBirthTxt.setText(user.getBirth());

        // 병원 정보
        hospitalNameTxt.setText(hospital.getName());
        hospitalTelTxt.setText(hospital.getTel());
        hospitalAddressTxt.setText(hospital.getAddress());

        backBtn.setOnClickListener(v -> onBackPressed());

        ArrayList<String> departmentArray =  (ArrayList)hospital.getDepartment();

        department.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, departmentArray));
        department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectDepartment = departmentArray.get(position);
                closeDateSelect();
                openDateSelect();
                closeTimeSelect();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        openDateSelect();
        cal = Calendar.getInstance();
        cal.setTimeInMillis(calendarView.getDate());
        selectCal.setTimeInMillis(calendarView.getDate());
        date = cal.get(Calendar.MONTH)+1 + "." + cal.get(Calendar.DATE) + "(" + getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK)) + ")";
        dateTxt.setText(date);

        dateSetBtn.setOnClickListener(v -> {
            if (!(isClickDateSetBtn)) {
                openDateSelect();
            } else {
                closeDateSelect();
            }
        });

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectCal.set(year, month, dayOfMonth);

            date = month+1 + "." + dayOfMonth + "(" + getDayOfWeek(selectCal.get(Calendar.DAY_OF_WEEK)) + ")";
            dateTxt.setText(date);

            closeTimeSelect();
            closeDateSelect();

            openTimeSelect();
        });

        timeSetBtn.setOnClickListener(v -> {
            if (!(isClickTimeSetBtn)) {
                openTimeSelect();
            } else {
                closeTimeSelect();
            }
        });

        reservationBtn.setOnClickListener(v -> {
            reservationProcess();
        });

        return rootView;
    }

    @Override
    public void onBackPressed() {
        hospitalActivity.onReservationFragmentChanged(0);
    }

    public void reservationProcess(){
        Reservation reservation = new Reservation();
        reservation.setId(user.getEmail());    // 유저 이메일 설정
        reservation.setHospitalId(hospital.getId());     // 병원 아이디 설정
        reservation.setDepartment(selectDepartment);     // 진료과 설정
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(selectCal.getTime());
        reservation.setReservationDate(date);      // 예약 날짜 설정
        reservation.setReservationTime(selectTime);    // 예약 시간 설정
        reservation.setAdditionalContent(additionalContentEdt.getText().toString());  // 추가 입력한 내용 설정
        long timestamp = new Timestamp(System.currentTimeMillis()).getTime();
        reservation.setTimestamp(timestamp);    // 현재 시간 타임스탬프 설정
        reservation.setReservationStatus(Reservation.CONFIRMING_RESERVATION);     // 예약 신청됨 상태로 설정

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("예약 신청이 완료되었습니다.\n병원에서 예약 확정이 되는대로 알려드리겠습니다.")
                .setPositiveButton("확인", (dialogInterface, i) -> {
                    getActivity().finish();
                    Intent intent = new Intent(getContext(), HospitalActivity.class);
                    intent.putExtra("hospital", hospital);
                    startActivity(intent);
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void reservationFail(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
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
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    db.collection(Reservation.DB_NAME).document(document.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "Error deleting document", e);
                            });
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    public void reservedListUpdate(Reservation reservation){
        Log.d(TAG, "reservedListUpdate 실행");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(selectCal.getTime());
        Reserved reserved = null;
        HashMap<String, List<String>> reservedMap;

        for(int i=0; i<reservedList.size(); i++){
            Log.d(TAG, "reservedList 루프 시작 i="+i);
            reserved = reservedList.get(i);
            reservedMap = (HashMap)reserved.getReservedMap();
            if(reserved.getDepartment().equals(selectDepartment)){
                if(reservedMap.containsKey(date)){
                    Log.d(TAG, "reservedMap 키 찾음");
                    reservedMap.get(date).add(selectTime);
                    reserved.setReservedMap(reservedMap);
                    break;
                }else{
                    Log.d(TAG, "reservedMap 키 못찾음");
                    ArrayList<String> tmpArr = new ArrayList<>();
                    tmpArr.add(selectTime);
                    reservedMap.put(date, tmpArr);
                    reserved.setReservedMap(reservedMap);
                }
            }else{
                Log.d(TAG, "일치하는 department 존재하지 않음 -> reserved 설정");
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
            Log.d(TAG, "reserved가 null임 -> reserved 값을 설정해줌");
            reserved = new Reserved();
            reserved.setHospitalId(hospital.getId());
            reserved.setDepartment(selectDepartment);
            ArrayList<String> tmpArr = new ArrayList<>();
            tmpArr.add(selectTime);
            HashMap<String, List<String>> tmpMap = new HashMap<>();
            tmpMap.put(date, tmpArr);
            reserved.setReservedMap(tmpMap);
        }

        if(reserved != null){
            Log.d(TAG, "reserved가 null이 아님 :" + reserved.toString());
            Query query = db.collection(Reserved.DB_NAME)
                    .whereEqualTo("hospitalId", reserved.getHospitalId())
                    .whereEqualTo("department", reserved.getDepartment());

            Reserved finalReserved = reserved;

            query.get().addOnCompleteListener(task -> {
                Log.d(TAG, "query 찾음");

                if (task.isSuccessful()) {
                    if(task.getResult().size()!=0){
                        Log.d(TAG, "task 성공 -> " + task.getResult().size());
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
                    Log.d(TAG, "task 종료");
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    reservationFailProcess(reservation);
                    reservationFail();
                }
            });
        }else{
            Log.d(TAG, "reserved가 null임");
            reservationFailProcess(reservation);
            reservationFail();
        }
    }

    public void openDateSelect(){
        calendar.addView(calendarView);
        isClickDateSetBtn = true;
        calendarExpandImg.setImageResource(R.drawable.ic_action_expand_more);
    }

    public void closeDateSelect(){
        calendar.removeAllViews();
        isClickDateSetBtn = false;
        calendarExpandImg.setImageResource(R.drawable.ic_action_expand_less);
    }

    public void openTimeSelect(){
        setTimeTable(selectCal.get(Calendar.DAY_OF_WEEK));
        isClickTimeSetBtn = true;
        timeExpandImg.setImageResource(R.drawable.ic_action_expand_more);
    }

    public void closeTimeSelect(){
        timeTxt.setText("시간 설정");
        timeTable.removeAllViews();
        isClickTimeSetBtn = false;
        timeExpandImg.setImageResource(R.drawable.ic_action_expand_less);
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
                if(hospital.isStatus()){
                    timeTable(WEEKDAY);
                }
                break;
            case 7:
                if(hospital.isSaturdayStatus()){
                    timeTable(SATURDAY);
                }
                break;
            default:
                if(hospital.isHolidayStatus()){
                    timeTable(HOLIDAY);
                }
                break;
        }
    }

    // 평일: 0, 토요일: 1, 일요일 및 공휴일: 2
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
        Button button[] = new Button[timeList.size()];

        for(int i=0; i<timeList.size(); i++){
            if(i%4==0){
                timeTable.addView(tableRow);
                tableRow = new TableRow(getContext());
            }
            String tmp = timeList.get(i);

            button[i] = new Button(getContext());
            button[i].setText(timeList.get(i));
            button[i].setOnClickListener(v -> {
                selectTime = tmp;
                timeTxt.setText(tmp.substring(0,2) + "시 " + tmp.substring(3,5) + "분");
                timeTable.removeAllViews();
                isClickTimeSetBtn = false;
                timeExpandImg.setImageResource(R.drawable.ic_action_expand_less);
            });

            for(Reserved reserved : reservedList){
                if(selectDepartment.equals(reserved.getDepartment())){
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String date = df.format(selectCal.getTime());

                    HashMap<String, List<String>> reservedMap = (HashMap)reserved.getReservedMap();
                    ArrayList<String> dateArray = (ArrayList)reservedMap.get(date);

                    if(dateArray != null){
                        for(String time : dateArray){
                            if(timeList.get(i).equals(time)){
                                button[i].setEnabled(false);
                            }
                        }
                    }
                }
            }

            tableRow.addView(button[i]);
        }

        timeTable.addView(tableRow);
    }

    // 예약 시간 30분 간격으로 점심시간 제외해서 구하는 메서드
    public ArrayList<String> reservationTimeMaker(String open, String close, String lunch){

        ArrayList<String> timeList = new ArrayList<>();

        int openHr = Integer.parseInt(open.substring(0,2));
        int openMin = Integer.parseInt(open.substring(3,5));
        int closeHr = Integer.parseInt(close.substring(0,2));
        int closeMin = Integer.parseInt(close.substring(3,5));
        int lunchHr = Integer.parseInt(lunch.substring(0,2));
        int lunchMin = Integer.parseInt(lunch.substring(3,5));

        while(openHr<lunchHr){
            String time;

            if(openMin==30){
                time = openHr + ":" + openMin;
                timeList.add(time);
                openMin=0;
                openHr++;
            }else{
                time = openHr + ":" + openMin +"0";
                timeList.add(time);
                openMin = 30;
            }
        }

        if(lunchMin==30){
            openMin=0;
            String time = openHr + ":" + openMin + "0";
            timeList.add(time);
        }

        int tmp = lunchHr+1;

        while(tmp<closeHr){
            String time;
            if(lunchMin==30){
                time = tmp + ":" + lunchMin;
                timeList.add(time);
                lunchMin=0;
                tmp++;
            }else{
                time = tmp + ":" + lunchMin +"0";
                timeList.add(time);
                lunchMin = 30;
            }
        }

        if(closeMin==30){
            lunchMin=0;
            String time = tmp + ":" + lunchMin + "0";
            timeList.add(time);
        }

        return timeList;
    }
}