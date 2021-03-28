package com.gradproject.hospi.home.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;

import static com.gradproject.hospi.home.hospital.HospitalActivity.hospital;

public class ReservationSuccessFragment extends Fragment implements OnBackPressedListener {
    ImageButton closeBtn;

    HospitalActivity hospitalActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_reservation_success, container,false);

        hospitalActivity = (HospitalActivity) getActivity();
        closeBtn = rootView.findViewById(R.id.closeBtn);

        closeBtn.setOnClickListener(v -> onBackPressed());

        return rootView;
    }

    @Override
    public void onBackPressed() {
        getActivity().finish();
        Intent intent = new Intent(getContext(), HospitalActivity.class);
        intent.putExtra("hospital", hospital);
        startActivity(intent);
    }
}