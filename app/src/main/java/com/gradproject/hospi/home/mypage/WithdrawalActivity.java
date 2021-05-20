package com.gradproject.hospi.home.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.Inquiry;
import com.gradproject.hospi.LoginActivity;
import com.gradproject.hospi.User;
import com.gradproject.hospi.databinding.ActivityWithdrawalBinding;
import com.gradproject.hospi.home.Prescription;
import com.gradproject.hospi.home.hospital.Reservation;
import com.gradproject.hospi.home.hospital.Reserved;
import com.gradproject.hospi.utils.Loading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.gradproject.hospi.home.HomeActivity.user;

public class WithdrawalActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener{
    private static final String TAG = "WithdrawalActivity";

    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    Loading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWithdrawalBinding binding = ActivityWithdrawalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        loading = new Loading(WithdrawalActivity.this);

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        binding.okBtn.setOnClickListener(this::okBtnProcess);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            String msg = "유저 정보가 존재하지 않습니다.";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            ActivityCompat.finishAffinity(WithdrawalActivity.this); // 모든 액티비티 종료
            startActivity(new Intent(getApplicationContext(), LoginActivity.class)); // 다시 로그인 화면으로
        }
    }

    private void reservedDeleteProcess(Reservation item){
        Query query = db.collection(Reserved.DB_NAME)
                .whereEqualTo("hospitalId", item.getHospitalId())
                .whereEqualTo("department", item.getDepartment());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Reserved reserved=null;
                String documentId = null;
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    reserved = document.toObject(Reserved.class);
                    documentId = document.getId();
                }

                if(reserved != null){
                    HashMap<String, List<String>> tmpMap = (HashMap<String, List<String>>) reserved.getReservedMap();
                    if(tmpMap.containsKey(item.getReservationDate())){
                        ArrayList<String> tmpList = (ArrayList<String>) tmpMap.get(item.getReservationDate());

                        int size = 0;
                        if (tmpList != null) {
                            size = tmpList.size();
                        }
                        for(int i=0; i<size; i++){
                            if(tmpList.get(i).equals(item.getReservationTime())){
                                tmpList.remove(i);
                                i--;
                                size--;
                            }
                        }

                        tmpMap.put(item.getReservationDate(), tmpList);

                        DocumentReference documentReference = db.collection(Reserved.DB_NAME).document(documentId);
                        documentReference
                                .update("reservedMap", tmpMap)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                    }
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private void withdrawalDialog(){
        loading.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawalActivity.this)
                .setCancelable(false)
                .setMessage("회원탈퇴가 완료 되었습니다.")
                .setPositiveButton("확인", (dialog, i) -> {
                    ActivityCompat.finishAffinity(WithdrawalActivity.this); // 모든 액티비티 종료
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class)); // 다시 로그인 화면으로
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void okBtnProcess(View v) {
        loading.show();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        onAuthStateChanged(firebaseAuth);
        firebaseUser.delete();

        Thread[] delThr = new Thread[4];

        delThr[0] = new Thread(() -> db.collection(User.DB_NAME).document(user.getDocumentId())
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "유저 정보 삭제 성공"))
                .addOnFailureListener(e -> Log.w(TAG, "유저 정보 삭제 실패", e)));

        delThr[1] = new Thread(() -> db.collection(Inquiry.DB_NAME)
                .whereEqualTo("id", firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            db.collection(Inquiry.DB_NAME).document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "유저 정보 삭제 성공"))
                                    .addOnFailureListener(e -> Log.w(TAG, "유저 정보 삭제 실패", e));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }));

        delThr[2] = new Thread(() -> db.collection(Reservation.DB_NAME)
                .whereEqualTo("id", firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Reservation item = document.toObject(Reservation.class);
                            db.collection(Reservation.DB_NAME).document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "유저 정보 삭제 성공");
                                        reservedDeleteProcess(item);
                                    })
                                    .addOnFailureListener(e -> Log.w(TAG, "유저 정보 삭제 실패", e));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }));

        delThr[3] = new Thread(() -> db.collection(Prescription.DB_NAME)
                .whereEqualTo("id", firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            db.collection(Prescription.DB_NAME).document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "유저 정보 삭제 성공"))
                                    .addOnFailureListener(e -> Log.w(TAG, "유저 정보 삭제 실패", e));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }));

        for (Thread thread : delThr) {
            thread.start();
            try {
                thread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        withdrawalDialog();
    }
}