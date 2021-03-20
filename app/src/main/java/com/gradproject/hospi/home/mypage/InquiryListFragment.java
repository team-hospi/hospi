package com.gradproject.hospi.home.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.gradproject.hospi.R;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class InquiryListFragment extends Fragment {
    RecyclerView inquiryRecyclerView;
    LinearLayoutManager layoutManager;
    InquiryAdapter inquiryAdapter = new InquiryAdapter();

    ImageButton backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_inquiry_list, container,false);

        backBtn = rootView.findViewById(R.id.backBtn);

        inquiryRecyclerView = rootView.findViewById(R.id.inquiryList);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        inquiryRecyclerView.setLayoutManager(layoutManager);

        getInquiryList();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        inquiryAdapter.setOnItemClickListener(new OnInquiryItemClickListener() {
            @Override
            public void onItemClick(InquiryAdapter.ViewHolder holder, View view, int position) {
                Inquiry inquiry = inquiryAdapter.getItem(position);
                Intent intent = new Intent(getContext(), InquiryDetailActivity.class);
                intent.putExtra("pos", position);
                intent.putExtra("inquiry", inquiry);
                startActivityForResult(intent, 0);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==0){
            if (resultCode==RESULT_OK) {
                int pos = data.getIntExtra("pos", -1);
                if(pos!=-1){
                    inquiryAdapter.items.remove(pos);
                    inquiryAdapter.notifyItemRemoved(pos);
                    inquiryAdapter.notifyItemRangeChanged(pos, inquiryAdapter.items.size());
                }
            }
        }
    }

    private void getInquiryList(){
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