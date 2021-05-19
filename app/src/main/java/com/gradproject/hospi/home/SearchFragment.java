package com.gradproject.hospi.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.databinding.FragmentSearchBinding;
import com.gradproject.hospi.home.hospital.HospitalMapActivity;
import com.gradproject.hospi.home.search.ResultActivity;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;

    FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        binding.searchBox.setOnClickListener(v -> startActivity(new Intent(getContext(), ResultActivity.class)));
        binding.hospitalMapBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), HospitalMapActivity.class)));
        binding.pharmacyMapBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), PharmacyMapActivity.class)));
        binding.coronaCheckBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), CoronaCheckActivity.class)));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}