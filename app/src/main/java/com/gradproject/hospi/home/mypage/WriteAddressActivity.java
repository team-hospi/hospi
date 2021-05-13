package com.gradproject.hospi.home.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gradproject.hospi.R;
import com.gradproject.hospi.databinding.ActivityWriteAddressBinding;

public class WriteAddressActivity extends AppCompatActivity {
    private static final int REQUEST_ADDRESS_SEARCH_ACTIVITY_CODE = 101;
    private ActivityWriteAddressBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWriteAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startActivityForResult(
                new Intent(getApplicationContext(), AddressSearchActivity.class),
                REQUEST_ADDRESS_SEARCH_ACTIVITY_CODE);

        binding.backBtn.setOnClickListener(v -> finish());

        binding.detailAddressEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(binding.detailAddressEdt.getText().toString().equals("")){
                    binding.okBtn.setEnabled(false);
                    binding.okBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                }else{
                    binding.okBtn.setEnabled(true);
                    binding.okBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.okBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("address",
                    binding.addressTxt.getText().toString()
                    + " "
                    + binding.detailAddressEdt.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_ADDRESS_SEARCH_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                binding.addressTxt.setText(data.getStringExtra("address"));
            } else {
                finish();
            }
        }
    }
}