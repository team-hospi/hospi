package com.gradproject.hospi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gradproject.hospi.home.HomeActivity;
import com.gradproject.hospi.register.RegisterActivity;
import com.gradproject.hospi.utils.Encrypt;
import com.gradproject.hospi.utils.Loading;

import io.grpc.okhttp.internal.Util;

public class LoginActivity extends AppCompatActivity {
    Loading loading;
    FirebaseAuth firebaseAuth;
    EditText inputEmail, inputPW;
    String id, pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.inputEmail);
        inputPW = findViewById(R.id.inputPW);

        firebaseAuth = FirebaseAuth.getInstance();

        loading = new Loading(LoginActivity.this, "로그인 중...");

        // 로그인 버튼
        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = inputEmail.getText().toString();
                pw = Encrypt.getEncrypt(id, inputPW.getText().toString());

                loading.start();

                if(!(id.equals("") || pw.equals(""))){
                    firebaseAuth.signInWithEmailAndPassword(id, pw)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>(){
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task){
                                    if(task.isSuccessful()){
                                        loading.end();
                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                        finish();
                                    } else {
                                        loading.end();
                                        loginFail();
                                    }
                                }
                            });
                }else{
                    loading.end();
                    loginFail();
                }
            }
        });

        // 아이디, 비밀번호 찾기 버튼
        TextView accountFindBtn = findViewById(R.id.accountFindBtn);
        accountFindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - 계정 찾기 화면 이동
            }
        });

        // 회원가입 버튼
        TextView registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }

    // 로그인 실패 팝업 띄우기
    private void loginFail(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                .setCancelable(false)
                .setMessage("아이디 및 비밀번호가 일치하지 않습니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}