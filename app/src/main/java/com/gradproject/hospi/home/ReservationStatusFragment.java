package com.gradproject.hospi.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.R;
import com.gradproject.hospi.home.hospital.Reservation;

import java.util.ArrayList;
import java.util.Collections;

public class ReservationStatusFragment extends Fragment {
    private static final String TAG = "ReservationStatusFragment";

    RecyclerView reservationRecyclerView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_reservation_status, container, false);

        reservationRecyclerView = rootView.findViewById(R.id.reservationList);

        reservationRecyclerView.setLayoutManager(layoutManager);
        showReservationList();

        return rootView;
    }

    private void showReservationList(){
        db.collection(Reservation.DB_NAME)
                .whereEqualTo("id", firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Reservation> tmpArrList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Reservation reservation = document.toObject(Reservation.class);
                            tmpArrList.add(reservation);
                        }

                        Collections.sort(tmpArrList, new ReservationComparator());

                        for(int i=0; i<tmpArrList.size(); i++){
                            reservationAdapter.addItem(tmpArrList.get(i));
                        }

                        reservationRecyclerView.setAdapter(reservationAdapter);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}