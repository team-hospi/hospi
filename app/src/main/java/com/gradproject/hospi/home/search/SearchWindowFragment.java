package com.gradproject.hospi.home.search;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.R;
import com.gradproject.hospi.home.hospital.HospitalActivity;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class SearchWindowFragment extends Fragment {
    ImageButton backBtn, voiceInputBtn, removeBtn;
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
        voiceInputBtn = rootView.findViewById(R.id.voiceInputBtn);
        removeBtn = rootView.findViewById(R.id.removeBtn);
        searchEdt = rootView.findViewById(R.id.searchEdt);
        noSearchTxt = rootView.findViewById(R.id.noSearchTxt);
        hospitalRecyclerView = rootView.findViewById(R.id.hospitalList);

        hospitalRecyclerView.setLayoutManager(layoutManager);

        backBtn.setOnClickListener(v -> getActivity().finish());

        removeBtn.setOnClickListener(v -> searchEdt.setText(""));

        voiceInputBtn.setOnClickListener(v -> {
            // TODO: 퍼미션 수정 필요
            if ( Build.VERSION.SDK_INT >= 23 ){
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO
                },1);
            }

            startActivityForResult(new Intent(getContext(), SpeechRecognitionPopUp.class), 1);
        });

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* empty */ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(searchEdt.getText().toString().equals("")){
                    voiceInputBtn.setVisibility(View.VISIBLE);
                    removeBtn.setVisibility(View.INVISIBLE);
                }else{
                    voiceInputBtn.setVisibility(View.INVISIBLE);
                    removeBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { /* empty */ }
        });

        searchEdt.setOnEditorActionListener((v, actionId, event) -> {
            searchProcess();
            return true;
        });

        hospitalAdapter.setOnItemClickListener((holder, view, position) -> {
            Hospital hospital = hospitalAdapter.getItem(position);
            Intent intent = new Intent(getContext(), HospitalActivity.class);
            intent.putExtra("hospital", hospital);
            startActivity(intent);
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1) {
            if (resultCode == RESULT_OK) {
                searchEdt.setText(data.getStringExtra("result"));
                searchProcess();
            }
        }
    }

    private void searchProcess(){
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

        db.collection(Hospital.DB_NAME)
                .get()
                .addOnCompleteListener(task -> {
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
                });
    }
}