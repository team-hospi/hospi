package com.gradproject.hospi.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.gradproject.hospi.R;
import com.gradproject.hospi.home.search.ResultActivity;
import com.gradproject.hospi.register.RegisterActivity;

public class SearchFragment extends Fragment {
    FrameLayout searchBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container,false);

        searchBox = rootView.findViewById(R.id.searchBox);
        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ResultActivity.class));
            }
        });

        return rootView;
    }
}