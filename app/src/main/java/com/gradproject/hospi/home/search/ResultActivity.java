package com.gradproject.hospi.home.search;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gradproject.hospi.R;
import com.gradproject.hospi.databinding.ActivityResultBinding;

public class ResultActivity extends AppCompatActivity {
    SearchWindowFragment searchWindowFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityResultBinding binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        searchWindowFragment = new SearchWindowFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.resultContainer, searchWindowFragment).commit();
    }
}