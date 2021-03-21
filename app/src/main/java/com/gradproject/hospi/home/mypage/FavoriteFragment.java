package com.gradproject.hospi.home.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.home.hospital.HospitalActivity;
import com.gradproject.hospi.home.search.Hospital;
import com.gradproject.hospi.home.search.HospitalAdapter;
import com.gradproject.hospi.home.search.OnHospitalItemClickListener;

import java.util.ArrayList;

import static com.gradproject.hospi.home.HomeActivity.user;

public class FavoriteFragment extends Fragment implements OnBackPressedListener {
    ImageButton backBtn;
    TextView noFavoriteTxt;

    RecyclerView favoriteRecyclerView;
    LinearLayoutManager layoutManager;
    HospitalAdapter hospitalAdapter = new HospitalAdapter();

    FirebaseFirestore db;
    ArrayList<String> favorites;

    @Override
    public void onResume() {
        super.onResume();

        loadFavoriteHospital();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        favorites = (ArrayList) user.getFavorites();
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_favorite, container,false);

        favoriteRecyclerView = rootView.findViewById(R.id.favoriteList);
        noFavoriteTxt = rootView.findViewById(R.id.noFavoriteTxt);

        favoriteRecyclerView.setLayoutManager(layoutManager);

        backBtn = rootView.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

    @Override
    public void onBackPressed() {
        getActivity().finish();
    }

    public void loadFavoriteHospital(){
        hospitalAdapter.items.clear(); // 기존 항목 모두 삭제
        hospitalAdapter.notifyDataSetChanged(); // 어댑터 갱신

        for(String hospital_id : favorites){
            db.collection("hospitals")
                    .whereEqualTo("id", hospital_id)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("DB", document.getId() + " => " + document.getData());
                                    Hospital hospital = document.toObject(Hospital.class);
                                    hospitalAdapter.addItem(hospital);
                                }

                                favoriteRecyclerView.setAdapter(hospitalAdapter);
                            } else {
                                Log.d("DB", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

        if(favorites.size()==0){
            noFavoriteTxt.setVisibility(View.VISIBLE);
        }
    }
}