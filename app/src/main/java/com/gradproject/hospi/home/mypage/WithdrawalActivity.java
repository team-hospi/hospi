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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gradproject.hospi.Inquiry;
import com.gradproject.hospi.LoginActivity;
import com.gradproject.hospi.User;
import com.gradproject.hospi.databinding.ActivityWithdrawalBinding;
import com.gradproject.hospi.home.Prescription;
import com.gradproject.hospi.home.hospital.Reservation;
import com.gradproject.hospi.home.hospital.Reserved;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.gradproject.hospi.home.HomeActivity.user;

public class WithdrawalActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener{
    private static final String TAG = "WithdrawalActivity";
    private ActivityWithdrawalBinding binding;

    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        binding.okBtn.setOnClickListener(v -> {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            onAuthStateChanged(firebaseAuth);
            firebaseUser.delete();
            db.collection(User.DB_NAME).document(user.getDocumentId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "유저 정보 삭제 성공");
                        withdrawalDialog();
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "유저 정보 삭제 실패", e));

            // TODO: 회원탈퇴시 모든 정보 삭제

            /*
            db.collection(Inquiry.DB_NAME)
                    .whereEqualTo("id", user.getEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                db.collection(Inquiry.DB_NAME).document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "유저 정보 삭제 성공"))
                                        .addOnFailureListener(e -> Log.w(TAG, "유저 정보 삭제 실패", e));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });

            db.collection(Reservation.DB_NAME)
                    .whereEqualTo("id", user.getEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
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
                    });

            db.collection(Prescription.DB_NAME)
                    .whereEqualTo("id", user.getEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                db.collection(Prescription.DB_NAME).document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "유저 정보 삭제 성공"))
                                        .addOnFailureListener(e -> Log.w(TAG, "유저 정보 삭제 실패", e));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });

            withdrawalDialog();
            */
        });
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){

        }else{
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
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    reserved = document.toObject(Reserved.class);
                    documentId = document.getId();
                }

                if(reserved != null && documentId != null){
                    HashMap<String, List<String>> tmpMap = (HashMap) reserved.getReservedMap();
                    if(tmpMap.containsKey(item.getReservationDate())){
                        ArrayList<String> tmpList = (ArrayList) tmpMap.get(item.getReservationDate());
                        for(int i=0; i<tmpList.size(); i++){
                            if(tmpList.get(i).equals(item.getReservationTime())){
                                tmpList.remove(i);
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
}