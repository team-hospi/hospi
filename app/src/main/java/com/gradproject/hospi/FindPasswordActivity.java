package com.gradproject.hospi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FindPasswordActivity extends AppCompatActivity {
    TextView emailErrorTxt;
    EditText inputEmail;

    String email;
    String emailRegex = "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        emailErrorTxt = findViewById(R.id.emailErrorTxt);
        inputEmail = findViewById(R.id.inputEmail);

        Button nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = inputEmail.getText().toString().trim();

                if (email.equals("")) {
                    emailErrorTxt.setText("이메일을 입력해주세요.");
                    emailErrorTxt.setVisibility(View.VISIBLE);
                } else if (!(email.matches(emailRegex))) {
                    emailErrorTxt.setText("잘못된 이메일 형식입니다.");
                    emailErrorTxt.setVisibility(View.VISIBLE);
                }else{
                    sendEmail(email);
                }
            }
        });
    }

    // 이메일 중복 체크
    public void sendEmail(String str){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(str)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(FindPasswordActivity.this)
                                    .setCancelable(false)
                                    .setMessage("입력하신 이메일로 비밀번호 변경 안내 메일이 발송되었습니다.")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override public void onClick(DialogInterface dialog, int i) {
                                            finish();
                                        }
                                    });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            Log.d("changePassword", "Email sent.");
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(FindPasswordActivity.this)
                                    .setCancelable(false)
                                    .setMessage("존재하지 않는 이메일입니다.")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override public void onClick(DialogInterface dialog, int i) { /* empty */ }
                                    });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            Log.d("changePassword", "Email sent error.");
                        }
                    }
                });
    }
}