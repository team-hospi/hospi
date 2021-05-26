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
import com.gradproject.hospi.databinding.FragmentReservationStatusBinding;
import com.gradproject.hospi.home.hospital.Reservation;

import java.util.ArrayList;
import java.util.Objects;

public class ReservationStatusFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ReservationStatusFragment";
    private FragmentReservationStatusBinding binding;

    LinearLayoutManager layoutManager;
    ReservationAdapter reservationAdapter = new ReservationAdapter();

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
        binding = FragmentReservationStatusBinding.inflate(inflater, container, false);

        binding.reservationList.setLayoutManager(layoutManager);
        binding.swipeRefresh.setOnRefreshListener(this);
        binding.swipeRefresh.setColorSchemeResources(R.color.main_color_dark);
        binding.refreshBtn.setOnClickListener(v -> {
            binding.loadingLayout.setVisibility(View.VISIBLE);
            binding.swipeRefresh.setVisibility(View.GONE);
            binding.nothingReservationView.setVisibility(View.GONE);
            showReservationList();
        });

        showReservationList();

        return binding.getRoot();
    }

    @Override
    public void onRefresh() {
        showReservationList();
        binding.swipeRefresh.setRefreshing(false);
    }

    private void showReservationList(){
        reservationAdapter.items.clear(); // 기존 항목 모두 삭제
        reservationAdapter.notifyDataSetChanged(); // 어댑터 갱신
        db.collection(Reservation.DB_NAME)
                .whereEqualTo("id", firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Reservation> tmpArrList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Reservation reservation = document.toObject(Reservation.class);
                            tmpArrList.add(reservation);
                        }

                        if(tmpArrList.size()!=0) {
                            binding.swipeRefresh.setVisibility(View.VISIBLE);
                            binding.nothingReservationView.setVisibility(View.GONE);
                        }else{
                            binding.swipeRefresh.setVisibility(View.GONE);
                            binding.nothingReservationView.setVisibility(View.VISIBLE);
                        }

                        tmpArrList.sort(new ReservationComparator());

                        for(int i=0; i<tmpArrList.size(); i++){
                            reservationAdapter.addItem(tmpArrList.get(i));
                        }

                        binding.reservationList.setAdapter(reservationAdapter);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    binding.loadingLayout.setVisibility(View.GONE);
                });
    }
}