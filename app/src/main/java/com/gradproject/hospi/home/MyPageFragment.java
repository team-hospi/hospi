package com.gradproject.hospi.home;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gradproject.hospi.databinding.FragmentMyPageBinding;
import com.gradproject.hospi.home.mypage.SettingActivity;

public class MyPageFragment extends Fragment{
    private FragmentMyPageBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyPageBinding.inflate(inflater, container, false);

        HomeActivity homeActivity = (HomeActivity) getActivity();

        if (homeActivity != null) {
            binding.nameTxt.setText(homeActivity.firebaseUser.getDisplayName());
        }
        binding.version.setText(getVersionInfo(requireContext()));

        binding.myInfoEditBtn.setOnClickListener(v -> startSelectedFragment("myInfoEditBtn"));
        binding.favoritesBtn.setOnClickListener(v -> startSelectedFragment("favoritesBtn"));
        binding.inquiryDetailsBtn.setOnClickListener(v -> startSelectedFragment("inquiryDetailsBtn"));
        binding.termsBtn.setOnClickListener(v -> startSelectedFragment("termsBtn"));
        binding.noticeBtn.setOnClickListener(v -> startSelectedFragment("noticeBtn"));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void startSelectedFragment(String select){
        Intent intent = new Intent(getContext(), SettingActivity.class);
        intent.putExtra("selectBtn", select);
        startActivity(intent);
    }

    public String getVersionInfo(Context context){
        String version = null;
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = i.versionName;
        } catch(PackageManager.NameNotFoundException ignored) { }
        return version;
    }
}