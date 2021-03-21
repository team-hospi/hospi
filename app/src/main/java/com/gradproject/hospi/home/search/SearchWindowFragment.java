package com.gradproject.hospi.home.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gradproject.hospi.R;
import com.gradproject.hospi.home.hospital.HospitalActivity;

import java.util.ArrayList;

public class SearchWindowFragment extends Fragment {
    private static final String COLLECTION_NAME = "hospitals";

    ImageButton backBtn, searchBtn, removeBtn;
    EditText searchEdt;
    TextView noSearchTxt;
    RecyclerView hospitalRecyclerView;
    LinearLayoutManager layoutManager;
    HospitalAdapter hospitalAdapter = new HospitalAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search_window, container,false);

        backBtn = rootView.findViewById(R.id.backBtn);
        searchBtn = rootView.findViewById(R.id.searchBtn);
        removeBtn = rootView.findViewById(R.id.removeBtn);
        searchEdt = rootView.findViewById(R.id.searchEdt);
        noSearchTxt = rootView.findViewById(R.id.noSearchTxt);
        hospitalRecyclerView = rootView.findViewById(R.id.hospitalList);

        hospitalRecyclerView.setLayoutManager(layoutManager);

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

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* empty */ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(searchEdt.getText().toString().equals("")){
                    removeBtn.setClickable(false);
                    removeBtn.setVisibility(View.INVISIBLE);
                }else{
                    removeBtn.setClickable(true);
                    removeBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { /* empty */ }
        });

        searchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchBtnProcess();
                return true;
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBtnProcess();
            }
        });

        hospitalAdapter.setOnItemClickListener(new OnHospitalItemClickListener() {
            @Override
            public void onItemClick(HospitalAdapter.ViewHolder holder, View view, int position) {
                Hospital hospital = hospitalAdapter.getItem(position);
                Intent intent = new Intent(getContext(), HospitalActivity.class);
                intent.putExtra("hospital", hospital);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void searchBtnProcess(){
        hospitalAdapter.items.clear(); // 기존 검색 결과 항목 모두 삭제
        hospitalAdapter.notifyDataSetChanged(); // 어댑터 갱신

        String searchStr = searchEdt.getText().toString().trim();

        if(!searchStr.equals("")){
            searchHospital(searchStr); // 검색
            noSearchTxt.setVisibility(View.INVISIBLE);
        }else{
            noSearchTxt.setVisibility(View.VISIBLE);
        }
    }

    private void searchHospital(String searchStr){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_NAME)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Hospital> tmpArrList = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("DB", document.getId() + " => " + document.getData());
                                Hospital hospital = document.toObject(Hospital.class);
                                if(hospital.getName().contains(searchStr)){
                                    tmpArrList.add(hospital);
                                }
                            }

                            if(tmpArrList.size()==0){
                                noSearchTxt.setVisibility(View.VISIBLE);
                            }else{
                                noSearchTxt.setVisibility(View.INVISIBLE);
                                for(int i=0; i<tmpArrList.size(); i++){
                                    hospitalAdapter.addItem(tmpArrList.get(i));
                                }
                            }

                            hospitalRecyclerView.setAdapter(hospitalAdapter);
                        } else {
                            Log.d("DB", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}