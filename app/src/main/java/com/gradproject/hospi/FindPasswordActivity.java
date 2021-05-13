package com.gradproject.hospi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.gradproject.hospi.databinding.ActivityFindPasswordBinding;

public class FindPasswordActivity extends AppCompatActivity {
    private final String TAG = "FindPasswordActivity";
    private ActivityFindPasswordBinding binding;

    String email;
    String emailRegex = "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFindPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.nextBtn.setOnClickListener(v -> {
            email = binding.inputEmail.getText().toString().trim();

            if (email.equals("")) {
                binding.emailErrorTxt.setText("이메일을 입력해주세요.");
                binding.emailErrorTxt.setVisibility(View.VISIBLE);
            } else if (!(email.matches(emailRegex))) {
                binding.emailErrorTxt.setText("잘못된 이메일 형식입니다.");
                binding.emailErrorTxt.setVisibility(View.VISIBLE);
            }else{
                sendEmail(email);
            }
        });
    }

    // 이메일 중복 체크
    public void sendEmail(String str){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(str)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(FindPasswordActivity.this)
                                .setCancelable(false)
                                .setMessage("입력하신 이메일로 비밀번호 변경 안내 메일이 발송되었습니다.")
                                .setPositiveButton("확인", (dialog, i) -> finish());
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        Log.d(TAG, "Email sent.");
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(FindPasswordActivity.this)
                                .setCancelable(false)
                                .setMessage("존재하지 않는 이메일입니다.")
                                .setPositiveButton("확인", (dialog, i) -> { /* empty */ });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        Log.d(TAG, "Email sent error.");
                    }
                });
    }
}