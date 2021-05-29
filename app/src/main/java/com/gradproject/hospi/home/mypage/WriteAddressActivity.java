package com.gradproject.hospi.home.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gradproject.hospi.R;
import com.gradproject.hospi.databinding.ActivityWriteAddressBinding;

public class WriteAddressActivity extends AppCompatActivity {
    private ActivityWriteAddressBinding binding;
    ActivityResultLauncher<Intent> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWriteAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    binding.addressTxt.setText(result.getData().getStringExtra("address"));
                }
            } else {
                finish();
            }
        });

        mGetContent.launch(new Intent(getApplicationContext(), AddressSearchActivity.class));

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
}