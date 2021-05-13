package com.gradproject.hospi.home.mypage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.databinding.FragmentTermsBinding;

public class TermsFragment extends Fragment implements OnBackPressedListener {
    private FragmentTermsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTermsBinding.inflate(inflater, container, false);
        binding.backBtn.setOnClickListener(v -> onBackPressed());
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onBackPressed() {
        getActivity().finish();
    }
}