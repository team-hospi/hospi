package com.gradproject.hospi.home.mypage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.LoginActivity;
import com.gradproject.hospi.R;
import com.gradproject.hospi.User;

import static com.gradproject.hospi.home.HomeActivity.user;

public class WithdrawalActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener{
    ImageButton backBtn;
    Button okBtn;

    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        db = FirebaseFirestore.getInstance();

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> onBackPressed());

        okBtn = findViewById(R.id.okBtn);
        okBtn.setOnClickListener(v -> {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            onAuthStateChanged(firebaseAuth);
            firebaseUser.delete();
            db.collection(User.DB_NAME).document(user.getDocumentId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        withdrawalDialog();
                        Log.d("withdrawal", "유저 정보 삭제 성공");
                    })
                    .addOnFailureListener(e -> Log.w("withdrawal", "유저 정보 삭제 실패", e));
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