package com.gradproject.hospi.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.databinding.FragmentReceptionStatusBinding;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ReceptionStatusFragment extends Fragment {
    private static final String TAG = "ReceptionStatusFragment";
    private FragmentReceptionStatusBinding binding;

    FirebaseFirestore db;
    HomeActivity homeActivity;
    Reception reception;
    Observable<String> ob;
    Disposable disposable;
    int seconds;

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

        binding.refreshBtn.setOnClickListener(v -> {
            binding.loadingLayout.setVisibility(View.VISIBLE);
            binding.nothingReceptionView.setVisibility(View.GONE);
            binding.receptionView.setVisibility(View.GONE);
            showReceptionInfo();
        });

        binding.autoRefreshBtn.setOnClickListener(v -> {
            binding.secTxt.setText("갱신중");
            if(disposable != null){
                disposable.dispose();
            }
            showReceptionInfo();
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        showReceptionInfo();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(disposable != null){
            disposable.dispose();
        }
    }

    private void autoRefreshStatus(){
        seconds = 10;
        ob = Observable.interval(1, TimeUnit.SECONDS)
                .flatMap(o -> {
                    String timerStr;
                    if(seconds >= 0) {
                        timerStr = seconds + "초";
                        seconds--;
                    }else{
                        timerStr = "갱신중";
                        showReceptionInfo();
                        disposable.dispose();
                    }
                    return Observable.just(timerStr);
                });

        disposable = ob.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(binding.secTxt::setText);
    }

    @SuppressLint("SetTextI18n")
    private void showReceptionInfo(){
        db.collection(Reception.DB_NAME)
                .whereEqualTo("id", homeActivity.firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        LocalDate curDate = LocalDate.now();
                        LocalTime curTime = LocalTime.now();
                        LocalTime minTime = LocalTime.of(23, 59, 59);
                        String docId = null;

                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Reception r = document.toObject(Reception.class);
                            String[] tmpTimeArr = r.getReceptionTime().split(":");
                            String[] tmpDateArr = r.getReceptionDate().split("-");
                            LocalTime tmpTime = LocalTime.of(Integer.parseInt(tmpTimeArr[0]), Integer.parseInt(tmpTimeArr[1]));
                            LocalDate tmpDate = LocalDate.of(Integer.parseInt(tmpDateArr[0]), Integer.parseInt(tmpDateArr[1]), Integer.parseInt(tmpDateArr[2]));
                            if(minTime.isAfter(tmpTime) && curDate.isEqual(tmpDate)){
                                minTime = tmpTime;
                                docId = document.getId();
                                reception = r;
                            }
                        } // for end
                        Log.d(TAG, docId + " => " + minTime);

                        try{
                            String[] timeArr = reception.getReceptionTime().split(":");
                            String[] dateArr = reception.getReceptionDate().split("-");
                            LocalTime rTime = LocalTime.of(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]));
                            LocalDate rDate = LocalDate.of(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2]));

                            if(curDate.isEqual(rDate)
                                    && curTime.isAfter(rTime.minusHours(1))){
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

                                binding.statusTxt.setText(String.valueOf(reception.getWaitingNumber()));
                                autoRefreshStatus();
                            }else{
                                binding.nothingReceptionView.setVisibility(View.VISIBLE);
                                binding.receptionView.setVisibility(View.GONE);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            binding.nothingReceptionView.setVisibility(View.VISIBLE);
                            binding.receptionView.setVisibility(View.GONE);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        binding.nothingReceptionView.setVisibility(View.VISIBLE);
                        binding.receptionView.setVisibility(View.GONE);
                    }
                    binding.loadingLayout.setVisibility(View.GONE);
                });
    }
}