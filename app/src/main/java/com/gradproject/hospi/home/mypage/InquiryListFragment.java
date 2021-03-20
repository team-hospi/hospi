package com.gradproject.hospi.home.mypage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gradproject.hospi.Inquiry;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;

import java.util.ArrayList;

public class InquiryListFragment extends Fragment implements OnBackPressedListener {
    RecyclerView inquiryRecyclerView;
    LinearLayoutManager layoutManager;
    InquiryAdapter inquiryAdapter = new InquiryAdapter();

    ImageButton backBtn;
    int pos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_inquiry_list, container,false);

        backBtn = rootView.findViewById(R.id.backBtn);
        inquiryRecyclerView = rootView.findViewById(R.id.inquiryList);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        inquiryRecyclerView.setLayoutManager(layoutManager);

        if(getArguments()!=null){
            pos = getArguments().getInt("pos", -1);
            if(pos!=-1){
                inquiryAdapter.items.remove(pos);
                inquiryAdapter.notifyItemRemoved(pos);
                inquiryAdapter.notifyItemRangeChanged(pos, inquiryAdapter.items.size());
            }
        }

        getInquiryList();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        inquiryAdapter.setOnItemClickListener(new OnInquiryItemClickListener() {
            @Override
            public void onItemClick(InquiryAdapter.ViewHolder holder, View view, int position) {
                Inquiry inquiry = inquiryAdapter.getItem(position);
                FragmentTransaction transaction = ((SettingActivity)getActivity()).getSupportFragmentManager().beginTransaction();
                InquiryDetailFragment inquiryDetailFragment = new InquiryDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("pos", position);
                bundle.putSerializable("inquiry", inquiry);
                inquiryDetailFragment.setArguments(bundle);
                transaction.replace(R.id.settingContainer, inquiryDetailFragment);
                transaction.commit();
            }
        });

        return rootView;
    }

    @Override
    public void onBackPressed() {
        getActivity().finish();
    }

    private void getInquiryList(){
        inquiryAdapter.items.clear(); // 기존 검색 결과 항목 모두 삭제
        inquiryAdapter.notifyDataSetChanged(); // 어댑터 갱신

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("inquiry_list")
                .whereEqualTo("id", firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Inquiry> tmpArrList = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("DB", document.getId() + " => " + document.getData());
                                Inquiry inquiry = document.toObject(Inquiry.class);
                                inquiry.setDocumentId(document.getId());
                                tmpArrList.add(inquiry);
                            }

                            for(int i=0; i<tmpArrList.size(); i++){
                                inquiryAdapter.addItem(tmpArrList.get(i));
                            }

                            inquiryRecyclerView.setAdapter(inquiryAdapter);
                        } else {
                            Log.d("DB", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}