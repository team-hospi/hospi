package com.gradproject.hospi.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.R;
import com.gradproject.hospi.databinding.FragmentTreatmentHistoryBinding;

import java.util.ArrayList;
import java.util.Objects;

public class TreatmentHistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "TreatmentHistoryFragment";
    private FragmentTreatmentHistoryBinding binding;

    LinearLayoutManager layoutManager;
    PrescriptionAdapter prescriptionAdapter = new PrescriptionAdapter();

    FirebaseFirestore db;
    FirebaseUser firebaseUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTreatmentHistoryBinding.inflate(inflater, container, false);

        binding.treatmentList.setLayoutManager(layoutManager);
        binding.swipeRefresh.setOnRefreshListener(this);
        binding.swipeRefresh.setColorSchemeResources(R.color.main_color_dark);
        binding.refreshBtn.setOnClickListener(v -> {
            binding.loadingLayout.setVisibility(View.VISIBLE);
            binding.swipeRefresh.setVisibility(View.GONE);
            binding.nothingTreatmentView.setVisibility(View.GONE);
            showPrescriptionList();
        });

        showPrescriptionList();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRefresh() {
        showPrescriptionList();
        binding.swipeRefresh.setRefreshing(false);
    }

    private void showPrescriptionList(){
        prescriptionAdapter.items.clear(); // 기존 항목 모두 삭제
        prescriptionAdapter.notifyDataSetChanged(); // 어댑터 갱신
        db.collection(Prescription.DB_NAME)
                .whereEqualTo("id", firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Prescription> tmpArrList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Prescription prescription = document.toObject(Prescription.class);
                            tmpArrList.add(prescription);
                        }

                        if(tmpArrList.size()!=0) {
                            binding.swipeRefresh.setVisibility(View.VISIBLE);
                            binding.nothingTreatmentView.setVisibility(View.GONE);
                        }else{
                            binding.swipeRefresh.setVisibility(View.GONE);
                            binding.nothingTreatmentView.setVisibility(View.VISIBLE);
                        }

                        tmpArrList.sort(new PrescriptionComparator());

                        for(int i=0; i<tmpArrList.size(); i++){
                            prescriptionAdapter.addItem(tmpArrList.get(i));
                        }

                        binding.treatmentList.setAdapter(prescriptionAdapter);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    binding.loadingLayout.setVisibility(View.GONE);
                });
    }
}