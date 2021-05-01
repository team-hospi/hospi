package com.gradproject.hospi.home.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.home.hospital.HospitalActivity;
import com.gradproject.hospi.home.search.Hospital;
import com.gradproject.hospi.home.search.HospitalAdapter;

import java.util.ArrayList;

import static com.gradproject.hospi.home.HomeActivity.user;

public class FavoriteFragment extends Fragment implements OnBackPressedListener {
    private static final String TAG = "FavoriteFragment";

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
        backBtn.setOnClickListener(v -> onBackPressed());

        hospitalAdapter.setOnItemClickListener((holder, view, position) -> {
            Hospital hospital = hospitalAdapter.getItem(position);
            Intent intent = new Intent(getContext(), HospitalActivity.class);
            intent.putExtra("hospital", hospital);
            startActivity(intent);
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

        for(String hospitalId : favorites){
            db.collection(Hospital.DB_NAME)
                    .whereEqualTo("id", hospitalId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Hospital hospital = document.toObject(Hospital.class);
                                hospitalAdapter.addItem(hospital);
                            }

                            favoriteRecyclerView.setAdapter(hospitalAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }

        if(favorites.size()==0){
            noFavoriteTxt.setVisibility(View.VISIBLE);
        }
    }
}