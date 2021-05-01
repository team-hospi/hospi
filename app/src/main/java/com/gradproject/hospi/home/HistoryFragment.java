package com.gradproject.hospi.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.gradproject.hospi.R;

public class HistoryFragment extends Fragment {
    ViewPager2 historyPager;
    HistoryPagerAdapter historyPagerAdapter;
    TabLayout historyTab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historyPagerAdapter = new HistoryPagerAdapter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_history, container,false);

        historyPager = rootView.findViewById(R.id.historyPager);
        historyTab = rootView.findViewById(R.id.historyTab);

        historyPager.setSaveEnabled(false);

        historyPager.setAdapter(historyPagerAdapter);
        new TabLayoutMediator(historyTab, historyPager, (tab, position) -> {
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

        return rootView;
    }
}