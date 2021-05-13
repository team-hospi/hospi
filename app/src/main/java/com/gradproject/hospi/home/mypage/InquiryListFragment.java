package com.gradproject.hospi.home.mypage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.Inquiry;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.databinding.FragmentInquiryListBinding;

import java.util.ArrayList;
import java.util.Collections;

public class InquiryListFragment extends Fragment implements OnBackPressedListener {
    private static final String TAG = "InquiryListFragment";
    private FragmentInquiryListBinding binding;

    LinearLayoutManager layoutManager;
    InquiryAdapter inquiryAdapter = new InquiryAdapter();
    int pos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInquiryListBinding.inflate(inflater, container, false);

        binding.inquiryList.setLayoutManager(layoutManager);

        if(getArguments()!=null){
            pos = getArguments().getInt("pos", -1);
            if(pos!=-1){
                inquiryAdapter.items.remove(pos);
                inquiryAdapter.notifyItemRemoved(pos);
                inquiryAdapter.notifyItemRangeChanged(pos, inquiryAdapter.items.size());
            }
        }

        getInquiryList();

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        inquiryAdapter.setOnItemClickListener((holder, view, position) -> {
            Inquiry inquiry = inquiryAdapter.getItem(position);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            InquiryDetailFragment inquiryDetailFragment = new InquiryDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            bundle.putSerializable("inquiry", inquiry);
            inquiryDetailFragment.setArguments(bundle);
            transaction.replace(R.id.settingContainer, inquiryDetailFragment);
            transaction.commit();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onBackPressed() {
        getActivity().finish();
    }

    private void getInquiryList(){
        inquiryAdapter.items.clear(); // 기존 항목 모두 삭제
        inquiryAdapter.notifyDataSetChanged(); // 어댑터 갱신

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(Inquiry.DB_NAME)
                .whereEqualTo("id", firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<Inquiry> tmpArrList = new ArrayList<>();

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Inquiry inquiry = document.toObject(Inquiry.class);
                            inquiry.setDocumentId(document.getId());
                            tmpArrList.add(inquiry);
                        }

                        Collections.sort(tmpArrList, new InquiryComparator());

                        for(int i=0; i<tmpArrList.size(); i++){
                            inquiryAdapter.addItem(tmpArrList.get(i));
                        }

                        binding.inquiryList.setAdapter(inquiryAdapter);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        String msg = "문의 내역을 불러올 수 없습니다.";
                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}