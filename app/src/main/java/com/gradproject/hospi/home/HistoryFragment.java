package com.gradproject.hospi.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;
import com.gradproject.hospi.databinding.FragmentHistoryBinding;

public class HistoryFragment extends Fragment {
    private FragmentHistoryBinding binding;
    HistoryPagerAdapter historyPagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historyPagerAdapter = new HistoryPagerAdapter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);


        binding.historyPager.setSaveEnabled(false);

        binding.historyPager.setAdapter(historyPagerAdapter);
        new TabLayoutMediator(binding.historyTab, binding.historyPager, (tab, position) -> {
            switch(position){
                case 0:
                    tab.setText("예약 현황");
                    break;
                case 1:
                    tab.setText("접수 현황");
                    break;
                default:
                    tab.setText("진료 내역");
                    break;
            }

        }).attach();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}