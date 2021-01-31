package com.gradproject.hospi.home.mypage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.LoginActivity;
import com.gradproject.hospi.R;

public class WithdrawalActivity extends AppCompatActivity {
    LinearLayout backBtn;
    Button okBtn;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        db = FirebaseFirestore.getInstance();

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        okBtn = findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); // 현재 로그인한 유저 정보 받기
                if (firebaseUser != null) {
                    // User is signed in
                    String email = firebaseUser.getEmail(); // 현재 로그인한 유저 이메일 가져오기
                    db.collection("user_list").document(email)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseAuth.getInstance().getCurrentUser().delete();
                                    withdrawalDialog();
                                    Log.d("withdrawal", "유저 정보 삭제 성공");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("withdrawal", "유저 정보 삭제 실패", e);
                                }
                            });
                }else{
                    Toast.makeText(getApplicationContext(), "로그인 정보가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut(); // 로그아웃
                    ActivityCompat.finishAffinity(WithdrawalActivity.this); // 모든 액티비티 종료
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class)); // 다시 로그인 화면으로
                }
            }
        });
    }

    private void withdrawalDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawalActivity.this)
                .setCancelable(false)
                .setMessage("회원탈퇴가 완료 되었습니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int i) {
                        ActivityCompat.finishAffinity(WithdrawalActivity.this); // 모든 액티비티 종료
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class)); // 다시 로그인 화면으로
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}