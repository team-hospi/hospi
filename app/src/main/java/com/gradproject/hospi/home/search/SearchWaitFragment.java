package com.gradproject.hospi.home.search;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gradproject.hospi.R;
import com.gradproject.hospi.Utils;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SearchWaitFragment extends Fragment {
    ImageView searchBtn, removeBtn;
    LinearLayout backBtn;
    EditText searchEdt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search_wait, container,false);

        backBtn = rootView.findViewById(R.id.backBtn);
        searchBtn = rootView.findViewById(R.id.searchBtn);
        removeBtn = rootView.findViewById(R.id.removeBtn);
        searchEdt = rootView.findViewById(R.id.searchEdt);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEdt.setText("");
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - 검색 결과 출력
                Toast.makeText(getContext(), "미구현", Toast.LENGTH_LONG).show();
            }
        });

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Utils.blankCheck(searchEdt.getText().toString())){
                    removeBtn.setClickable(false);
                    removeBtn.setVisibility(View.INVISIBLE);
                }else{
                    removeBtn.setClickable(true);
                    removeBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
    }

}