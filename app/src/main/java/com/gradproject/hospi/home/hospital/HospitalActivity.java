package com.gradproject.hospi.home.hospital;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.home.search.Hospital;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HospitalActivity extends AppCompatActivity {
    private static final String TAG = "HospitalActivity";

    HospitalInfoDetailFragment hospitalInfoDetailFragment;
    ReservationFragment reservationFragment;
    InquiryFragment inquiryFragment;

    FirebaseFirestore db;

    static Hospital hospital;
    static ArrayList<Reserved> reservedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        hospital = (Hospital) getIntent().getSerializableExtra("hospital");

        int code = getIntent().getIntExtra(HospitalInfoPopUp.HOSPITAL_INFO_POP_UP, -1);

        db = FirebaseFirestore.getInstance();

        new Thread(() -> {
            Query query = db.collection(Reserved.DB_NAME)
                    .whereEqualTo("hospitalId", hospital.getId());

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Reserved reserved = document.toObject(Reserved.class);
                        reservedList.add(reserved);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            });
        }).start();

        db.collection(Reserved.DB_NAME)
                .whereEqualTo("hospitalId", hospital.getId())
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    assert value != null;
                    for (QueryDocumentSnapshot doc : value) {
                        Reserved reserved = doc.toObject(Reserved.class);
                        HashMap<String, List<String>> reservedMap = (HashMap<String, List<String>>)reserved.getReservedMap();

                        for(int i=0; i<reservedList.size(); i++) {
                            if(reservedList.get(i).getHospitalId().equals(reserved.getHospitalId())
                                && reservedList.get(i).getDepartment().equals(reserved.getDepartment()))
                                reservedList.get(i).setReservedMap(reservedMap);
                        }
                    }
                });

        hospitalInfoDetailFragment = new HospitalInfoDetailFragment();
        reservationFragment = new ReservationFragment();
        inquiryFragment = new InquiryFragment();

        switch(code){
            case HospitalInfoPopUp.RESERVATION_CODE:
                onReservationFragmentChanged();
                break;
            case HospitalInfoPopUp.INQUIRY_CODE:
                onInquiryFragmentChanged();
                break;
            default:
                getSupportFragmentManager().beginTransaction().replace(R.id.Container, hospitalInfoDetailFragment).commit();
                break;
        }
    }

    public void onReservationFragmentChanged(int index){
        switch (index){
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.Container, hospitalInfoDetailFragment).commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.Container, reservationFragment).commit();
                break;
            default:
                Log.d(TAG, "잘못된 프래그먼트 인덱스 선택");
                break;
        }
    }

    public void onReservationFragmentChanged(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("popUp", true);
        reservationFragment.setArguments(bundle);
        transaction.replace(R.id.Container, reservationFragment);
        transaction.commit();
    }

    public void onInquiryFragmentChanged(int index){
        switch (index) {
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.Container, hospitalInfoDetailFragment).commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.Container, new InquiryFragment()).commit();
                break;
            default:
                Log.d(TAG, "잘못된 프래그먼트 인덱스 선택");
                break;
        }
    }

    public void onInquiryFragmentChanged(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("popUp", true);
        inquiryFragment.setArguments(bundle);
        transaction.replace(R.id.Container, inquiryFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for(Fragment fragment : fragmentList) {
            if (fragment instanceof OnBackPressedListener) {
                ((OnBackPressedListener) fragment).onBackPressed();
            }
        }
    }
}