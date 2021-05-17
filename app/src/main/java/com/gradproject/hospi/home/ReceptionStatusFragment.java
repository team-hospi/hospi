package com.gradproject.hospi.home;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.databinding.FragmentReceptionStatusBinding;

import java.util.ArrayList;
import java.util.Calendar;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReceptionStatusBinding.inflate(inflater, container, false);

        showReceptionInfo();

        return binding.getRoot();
    }

    private void showReceptionInfo(){
        db.collection(Reception.DB_NAME)
                .whereEqualTo("id", homeActivity.firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int minNum = 1440;
                        String docId = null;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Reception tmpRec = document.toObject(Reception.class);
                            String tmpStr = tmpRec.getReceptionDate();
                            String[] tmpTime = tmpStr.substring(tmpStr.length()-5).split(":");
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
                            String tmpDate = reception.getReceptionDate();
                            String[] time = tmpDate.substring(tmpDate.length()-5).split(":");
                            String[] date = tmpDate.substring(0, 10).split("-");
                            Calendar curCal = Calendar.getInstance();
                            Calendar tmpCal = Calendar.getInstance();
                            tmpCal.set(Integer.parseInt(date[0]), Integer.parseInt(date[1])-1, Integer.parseInt(date[2]));

                            int curMin = curCal.get(Calendar.MINUTE) + curCal.get(Calendar.HOUR_OF_DAY)*60;
                            int tmpMin = Integer.parseInt(time[0])*60 + Integer.parseInt(time[1]);

                            if((curCal.get(Calendar.YEAR) == tmpCal.get(Calendar.YEAR))
                                    && (curCal.get(Calendar.MONTH) == tmpCal.get(Calendar.MONTH))
                                    && (curCal.get(Calendar.DATE) == tmpCal.get(Calendar.DATE))
                                    && (curMin>=tmpMin-60) && (curMin<=tmpMin+60)){
                                binding.nothingReceptionView.setVisibility(View.GONE);
                                binding.receptionView.setVisibility(View.VISIBLE);

                                binding.departmentTxt.setText(reception.getDepartment());
                                binding.hospitalNameTxt.setText(reception.getHospitalName());
                                binding.patientTxt.setText(reception.getPatient());
                                binding.patientTxt2.setText(reception.getPatient());
                                binding.receptionDateTxt.setText(reception.getReceptionDate());
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
                    updateStatus(reception);

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
                binding.statusTxt.setText("진료중");
                binding.statusTxt.setTextColor(Color.GREEN);
                binding.waitingTxt.setVisibility(View.GONE);
                break;
            case Reception.TREATMENT_COMPLETE:
                binding.statusTxt.setText("진료완료");
                binding.statusTxt.setTextColor(Color.BLACK);
                binding.waitingTxt.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
}