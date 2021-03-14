package com.gradproject.hospi.home.mypage;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gradproject.hospi.Inquiry;
import com.gradproject.hospi.R;

public class InquiryDetailActivity extends AppCompatActivity {
    Inquiry inquiry;

    LinearLayout backBtn;
    TextView hospitalNameTxt, titleTxt, contentTxt, answerTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_detail);

        inquiry = (Inquiry) getIntent().getSerializableExtra("inquiry");

        hospitalNameTxt = findViewById(R.id.hospitalNameTxt);
        titleTxt = findViewById(R.id.titleTxt);
        contentTxt = findViewById(R.id.contentTxt);
        answerTxt = findViewById(R.id.answerTxt);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        hospitalNameTxt.setText(inquiry.getHospital_name());
        titleTxt.setText(inquiry.getTitle());
        contentTxt.setText(inquiry.getContent());

        if(inquiry.getAnswer().equals("")){
            answerTxt.setText("아직 답변이 등록되지 않았습니다.");
        }else{
            answerTxt.setText(inquiry.getAnswer());
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}