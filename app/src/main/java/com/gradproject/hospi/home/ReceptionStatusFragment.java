package com.gradproject.hospi.home;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gradproject.hospi.R;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ReceptionStatusFragment extends Fragment {
    private static final String TAG = "ReceptionStatusFragment";

    LinearLayout receptionView, nothingReceptionView;
    TextView hospitalNameTxt, departmentTxt, patientTxt, patientTxt2,
             waitingTxt, statusTxt, receptionDateTxt, officeTxt, doctorTxt;

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_reception_status, container, false);

        receptionView = rootView.findViewById(R.id.receptionView);
        nothingReceptionView = rootView.findViewById(R.id.nothingReceptionView);
        departmentTxt = rootView.findViewById(R.id.departmentTxt);
        hospitalNameTxt = rootView.findViewById(R.id.hospitalNameTxt);
        patientTxt = rootView.findViewById(R.id.patientTxt);
        patientTxt2 = rootView.findViewById(R.id.patientTxt2);
        waitingTxt = rootView.findViewById(R.id.waitingTxt);
        statusTxt = rootView.findViewById(R.id.statusTxt);
        receptionDateTxt = rootView.findViewById(R.id.receptionDateTxt);
        officeTxt = rootView.findViewById(R.id.officeTxt);
        doctorTxt = rootView.findViewById(R.id.doctorTxt);

        showReceptionInfo();

        return rootView;
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
                            nothingReceptionView.setVisibility(View.GONE);
                            receptionView.setVisibility(View.VISIBLE);

                            departmentTxt.setText(reception.getDepartment());
                            hospitalNameTxt.setText(reception.getHospitalName());
                            patientTxt.setText(reception.getPatient());
                            patientTxt2.setText(reception.getPatient());
                            receptionDateTxt.setText(reception.getReceptionDate());
                            officeTxt.setText(reception.getOffice());
                            doctorTxt.setText(reception.getDoctor());

                            updateStatus(reception);
                        }else{
                            nothingReceptionView.setVisibility(View.VISIBLE);
                            receptionView.setVisibility(View.GONE);
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
                statusTxt.setText("미접수");
                statusTxt.setTextColor(Color.RED);
                waitingTxt.setVisibility(View.GONE);
                break;
            case Reception.RECEIVED:
                String str = reception.getWaitingNumber() + "명";
                statusTxt.setText(str);
                statusTxt.setTextColor(Color.BLACK);
                waitingTxt.setVisibility(View.VISIBLE);
                break;
            case Reception.TREATMENT:
                statusTxt.setText("진료중");
                statusTxt.setTextColor(Color.GREEN);
                waitingTxt.setVisibility(View.GONE);
                break;
            case Reception.TREATMENT_COMPLETE:
                statusTxt.setText("진료완료");
                statusTxt.setTextColor(Color.BLACK);
                waitingTxt.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
}