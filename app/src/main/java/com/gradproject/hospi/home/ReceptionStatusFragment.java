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

public class ReceptionStatusFragment extends Fragment {
    private static final String TAG = "ReceptionStatusFragment";
    private FragmentReceptionStatusBinding binding;

    FirebaseFirestore db;
    Reception reception = null;
    HomeActivity homeActivity;

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
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            reception = document.toObject(Reception.class);
                            realtimeCheck(document.getId());
                        }

                        if(reception != null){
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
            case Reception.NOT_RECEIVED:
                binding.statusTxt.setText("미접수");
                binding.statusTxt.setTextColor(Color.RED);
                binding.waitingTxt.setVisibility(View.GONE);
                break;
            case Reception.RECEIVED:
                String str = reception.getWaitingNumber() + "명";
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