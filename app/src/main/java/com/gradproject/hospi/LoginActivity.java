package com.gradproject.hospi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.gradproject.hospi.databinding.ActivityLoginBinding;
import com.gradproject.hospi.home.HomeActivity;
import com.gradproject.hospi.register.RegisterActivity;
import com.gradproject.hospi.utils.Loading;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity{
    private ActivityLoginBinding binding;
    Loading loading;
    FirebaseAuth firebaseAuth;
    String id, pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        loading = new Loading(LoginActivity.this);

        // 로그인 버튼
        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> {
            id = binding.inputEmail.getText().toString();
            pw = binding.inputPW.getText().toString();

            loading.show();

            if(!(id.equals("") || pw.equals(""))){
                firebaseAuth.signInWithEmailAndPassword(id, pw)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            loading.dismiss();
                            if(task.isSuccessful()){
                                if(Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()){
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    finish();
                                }else{
                                    String msg = "발송된 메일을 통해 인증을 완료하여 주십시오.";
                                    loginFail(msg);
                                    Log.d("LoginError", "1");
                                }
                            } else {
                                String msg = "아이디 및 비밀번호가 일치하지 않습니다.";
                                loginFail(msg);
                                Log.d("LoginError", "2");
                            }
                        });
            }else{
                loading.dismiss();
                String msg = "아이디 및 비밀번호가 일치하지 않습니다.";
                loginFail(msg);
                Log.d("LoginError", "3");
            }
        });

        // 비밀번호 찾기 버튼
        TextView accountFindBtn = findViewById(R.id.accountFindBtn);
        accountFindBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), FindPasswordActivity.class)));

        // 회원가입 버튼
        TextView registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));
    }

    // 로그인 실패 팝업 띄우기
    private void loginFail(String msg){
        loading.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton("확인", (dialogInterface, i) -> { /* empty */ });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}