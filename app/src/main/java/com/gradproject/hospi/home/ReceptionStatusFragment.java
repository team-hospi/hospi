package com.gradproject.hospi.home;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.R;
import com.gradproject.hospi.databinding.FragmentReceptionStatusBinding;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

public class ReceptionStatusFragment extends Fragment {
    private static final String TAG = "ReceptionStatusFragment";
    private FragmentReceptionStatusBinding binding;

    FirebaseFirestore db;
    HomeActivity homeActivity;
    Reception reception;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        homeActivity = (HomeActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReceptionStatusBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.loadingLayout.setVisibility(View.VISIBLE);
        binding.nothingReceptionView.setVisibility(View.GONE);
        binding.receptionView.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        showReceptionInfo();
    }

    @SuppressLint("SetTextI18n")
    private void showReceptionInfo(){
        db.collection(Reception.DB_NAME)
                .whereEqualTo("id", homeActivity.firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int minNum = 1440;
                        String docId = null;

                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Reception tmpRec = document.toObject(Reception.class);
                            String tmpStr = tmpRec.getReceptionTime();
                            String[] tmpTime = tmpStr.split(":");
                            int tmpNum = Integer.parseInt(tmpTime[0])*60 + Integer.parseInt(tmpTime[1]);
                            if(tmpNum<minNum){
                                minNum=tmpNum;
                                reception = tmpRec;
                                docId = document.getId();
                            }
                        }

                        if(docId != null){
                            realtimeCheck(docId);
                        }

                        if(reception != null){
                            String[] time = reception.getReceptionTime().split(":");
                            String[] date = reception.getReceptionDate().split("-");
                            LocalDate curDate = LocalDate.now();
                            LocalDate tmpDate = LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));

                            LocalTime curTime = LocalTime.now();
                            LocalTime tmpTime = LocalTime.of(Integer.parseInt(time[0]), Integer.parseInt(time[1]));

                            if(curDate.isEqual(tmpDate)
                                    && (curTime.isAfter(tmpTime.minusHours(1)))
                                    && (curTime.isBefore(tmpTime.plusHours(1)))){
                                binding.nothingReceptionView.setVisibility(View.GONE);
                                binding.receptionView.setVisibility(View.VISIBLE);
                                binding.departmentTxt.setText(reception.getDepartment());
                                binding.hospitalNameTxt.setText(reception.getHospitalName());
                                binding.patientTxt.setText(reception.getPatient());
                                binding.patientTxt2.setText(reception.getPatient());

                                String[] tmp = reception.getReceptionDate().split("-");

                                LocalDate lDate = LocalDate.of(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]), Integer.parseInt(tmp[2]));
                                binding.receptionDateTxt.setText(reception.getReceptionDate()
                                        + " ("
                                        + lDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREA)
                                        + ") "
                                        + reception.getReceptionTime());
                                binding.officeTxt.setText(reception.getOffice());
                                binding.doctorTxt.setText(reception.getDoctor());

                                updateStatus(reception);
                            }
                        }else{
                            binding.nothingReceptionView.setVisibility(View.VISIBLE);
                            binding.receptionView.setVisibility(View.GONE);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    binding.loadingLayout.setVisibility(View.GONE);
                });
    }

    private void realtimeCheck(String docId){
        if(docId != null){
            final DocumentReference docRef = db.collection(Reception.DB_NAME).document(docId);
            docRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());

                    Reception reception = snapshot.toObject(Reception.class);
                    if (reception != null) {
                        updateStatus(reception);
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            });
        }
    }

    private void updateStatus(Reception reception){
        switch (reception.getStatus()){
            case Reception.RECEIVED:
                String str = String.valueOf(reception.getWaitingNumber());
                binding.statusTxt.setText(str);
                binding.statusTxt.setTextColor(Color.BLACK);
                binding.waitingTxt.setVisibility(View.VISIBLE);
                break;
            case Reception.TREATMENT:
                binding.statusTxt.setText("진료 중");
                binding.statusTxt.setTextColor(Color.rgb(70, 201, 0));
                binding.waitingTxt.setVisibility(View.GONE);
                break;
            case Reception.TREATMENT_COMPLETE:
                binding.statusTxt.setText("진료 완료");
                binding.statusTxt.setTextColor(Color.BLACK);
                binding.waitingTxt.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
}