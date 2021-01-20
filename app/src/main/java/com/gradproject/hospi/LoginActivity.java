package com.gradproject.hospi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    static final String ID = "test";   // 임시 아이디
    static final String PW = "af5570f5a1810b7af78caf4bc70a660f0df51e42baf91d4de5b2328de0e83dfc";   // 임시 비밀번호: 1234

    EditText inputID, inputPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputID = findViewById(R.id.inputID);
        inputPW = findViewById(R.id.inputPW);

        Button loginBtn = findViewById(R.id.loginBtn);  // 로그인 버튼
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = inputID.getText().toString();
                String pw = Utils.getEncrypt(id, inputPW.getText().toString());
                if(!processedLogin(id, pw)){
                    loginFail();
                }else{
                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG).show();
                    //startActivity();
                    //finish();
                }
            }
        });

        TextView accountFindBtn = findViewById(R.id.accountFindBtn);
        accountFindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - 계정 찾기 화면 이동
            }
        });

        TextView registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - 회원가입 화면 이동
            }
        });
    }
    private Boolean processedLogin(String id, String pw){
        // TODO - 로그인 처리 로직 구현
        if(id.equals(ID)){
            if(pw.equals(PW)){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    private void loginFail(){
        Intent loginFail = new Intent(getApplicationContext(), LoginFailPopUp.class);
        startActivity(loginFail);
    }
}