package com.gradproject.hospi.home.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gradproject.hospi.MainActivity;
import com.gradproject.hospi.R;

public class WriteAddressActivity extends AppCompatActivity {
    private static final int REQUEST_ADDRESS_SEARCH_ACTIVITY_CODE = 101;

    TextView addressTxt;
    EditText detailAddressEdt;
    Button okBtn;
    LinearLayout backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_address);

        addressTxt = findViewById(R.id.addressTxt);

        startActivityForResult(new Intent(getApplicationContext(), AddressSearchActivity.class), REQUEST_ADDRESS_SEARCH_ACTIVITY_CODE);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                Intent intent = new Intent();
                intent.putExtra("address", addressTxt.getText().toString() + " " + detailAddressEdt.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_ADDRESS_SEARCH_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                addressTxt.setText(data.getStringExtra("address"));
            } else {
                finish();
            }
        }
    }
}