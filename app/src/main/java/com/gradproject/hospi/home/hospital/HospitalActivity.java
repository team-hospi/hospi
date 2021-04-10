package com.gradproject.hospi.home.hospital;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.home.search.Hospital;

import java.util.ArrayList;
import java.util.List;

public class HospitalActivity extends AppCompatActivity {
    private static final String TAG = "HospitalActivity";

    HospitalInfoDetailFragment hospitalInfoDetailFragment;
    ReservationFragment reservationFragment;
    ReservationSuccessFragment reservationSuccessFragment;
    InquiryFragment inquiryFragment;

    FirebaseFirestore db;

    static Hospital hospital;
    static ArrayList<Reserved> reservedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        hospital = (Hospital) getIntent().getSerializableExtra("hospital");

        db = FirebaseFirestore.getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Query query = db.collection(Reserved.DB_NAME)
                        .whereEqualTo("hospitalId", hospital.getId());

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Reserved reserved = document.toObject(Reserved.class);
                                reservedList.add(reserved);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        }).start();

        hospitalInfoDetailFragment = new HospitalInfoDetailFragment();
        reservationFragment = new ReservationFragment();
        reservationSuccessFragment = new ReservationSuccessFragment();
        inquiryFragment = new InquiryFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.Container, hospitalInfoDetailFragment).commit();
    }

    public void onReservationFragmentChanged(int index){
        switch (index){
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.Container, hospitalInfoDetailFragment).commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.Container, reservationFragment).commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.Container, reservationSuccessFragment).commit();
                break;
            default:
                Log.d(TAG, "잘못된 프래그먼트 인덱스 선택");
                break;
        }
    }

    public void onInquiryFragmentChanged(int index){
        switch (index) {
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.Container, hospitalInfoDetailFragment).commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.Container, inquiryFragment).commit();
                break;
            default:
                Log.d(TAG, "잘못된 프래그먼트 인덱스 선택");
                break;
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