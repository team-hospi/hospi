package com.gradproject.hospi.home.mypage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.Inquiry;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.databinding.FragmentInquiryEditBinding;

public class InquiryEditFragment extends Fragment implements OnBackPressedListener {
    private static final String TAG = "InquiryEditFragment";
    private FragmentInquiryEditBinding binding;

    SettingActivity settingActivity;
    Inquiry inquiry;
    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingActivity = (SettingActivity) getActivity();
        inquiry = (Inquiry) getArguments().getSerializable("inquiry");
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInquiryEditBinding.inflate(inflater, container, false);

        binding.hospitalNameTxt.setText(inquiry.getHospitalName());
        binding.inquiryTitleEdt.setText(inquiry.getTitle());
        binding.inquiryContentEdt.setText(inquiry.getContent());

        binding.closeBtn.setOnClickListener(v -> onBackPressed());

        binding.updateBtn.setOnClickListener(v -> {
            String title = binding.inquiryTitleEdt.getText().toString();
            String content = binding.inquiryContentEdt.getText().toString();

            if(title.equals("") && content.equals("")) {
                binding.titleEmptyTxt.setVisibility(View.VISIBLE);
                binding.contentEmptyTxt.setVisibility(View.VISIBLE);
            }else if(title.equals("")){
                binding.titleEmptyTxt.setVisibility(View.VISIBLE);
            }else if(content.equals("")){
                binding.contentEmptyTxt.setVisibility(View.VISIBLE);
            }else{
                inquiryUpdateProcess(title, content);
            }
        });

        binding.inquiryTitleEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Empty */ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.titleEmptyTxt.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) { /* Empty */ }
        });

        binding.inquiryContentEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Empty */ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.contentEmptyTxt.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) { /* Empty */ }
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
        FragmentTransaction transaction = settingActivity.getSupportFragmentManager().beginTransaction();
        InquiryDetailFragment inquiryDetailFragment = settingActivity.inquiryDetailFragment;
        Bundle bundle = new Bundle();
        bundle.putSerializable("inquiry", inquiry);
        inquiryDetailFragment.setArguments(bundle);
        transaction.replace(R.id.settingContainer, inquiryDetailFragment);
        transaction.commit();
    }

    public void inquiryUpdateProcess(String title, String content){
        inquiry.setTitle(title);
        inquiry.setContent(content);

        DocumentReference documentReference = db.collection(Inquiry.DB_NAME).document(inquiry.getDocumentId());
        documentReference
                .set(inquiry)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                    updateSuccessPopUp();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    String msg = "문의 수정에 실패하였습니다.\n잠시 후 다시 진행해주세요.";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                });
    }

    public void updateSuccessPopUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("문의가 수정되었습니다.")
                .setPositiveButton("확인", (dialog, i) -> {
                    FragmentTransaction transaction = settingActivity.getSupportFragmentManager().beginTransaction();
                    InquiryDetailFragment inquiryDetailFragment = settingActivity.inquiryDetailFragment;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("inquiry", inquiry);
                    inquiryDetailFragment.setArguments(bundle);
                    transaction.replace(R.id.settingContainer, inquiryDetailFragment);
                    transaction.commit();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}