package com.gradproject.hospi.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class HistoryPagerAdapter extends FragmentStateAdapter {
    private ArrayList<Fragment> items;

    public HistoryPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
        items = new ArrayList<>();
        items.add(new ReservationStatusFragment());
        items.add(new ReceptionStatusFragment());
        items.add(new TreatmentHistoryFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
