package com.gradproject.hospi.home.mypage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gradproject.hospi.R;

public class WriteAddressActivity extends AppCompatActivity {
    TextView addressTxt;
    EditText detailAddressEdt;
    Button okBtn;
    LinearLayout backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_address);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addressTxt = findViewById(R.id.addressTxt);
        addressTxt.setText(getIntent().getStringExtra("address"));

        detailAddressEdt = findViewById(R.id.detailAddressEdt);
        detailAddressEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(detailAddressEdt.getText().toString().equals("")){
                    okBtn.setEnabled(false);
                    okBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                }else{
                    okBtn.setEnabled(true);
                    okBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        okBtn = findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - 주소 입력 완료 버튼
            }
        });
    }
}