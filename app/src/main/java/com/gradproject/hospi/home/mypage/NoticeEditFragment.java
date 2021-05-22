package com.gradproject.hospi.home.mypage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.databinding.FragmentNoticeEditBinding;
import com.gradproject.hospi.utils.Loading;

public class NoticeEditFragment extends Fragment implements OnBackPressedListener {
    private static final String TAG = "NoticeEditFragment";
    private FragmentNoticeEditBinding binding;

    SettingActivity settingActivity;
    Notice notice;
    FirebaseFirestore db;
    Loading loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingActivity = (SettingActivity) getActivity();

        if (getArguments() != null) {
            notice = (Notice) getArguments().getSerializable("notice");
        }
        db = FirebaseFirestore.getInstance();
        loading = new Loading(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNoticeEditBinding.inflate(inflater, container, false);

        binding.titleEdt.setText(notice.getTitle());
        binding.contentEdt.setText(notice.getContent());

        binding.closeBtn.setOnClickListener(v -> onBackPressed());

        binding.updateBtn.setOnClickListener(v -> {
            String title = binding.titleEdt.getText().toString();
            String content = binding.contentEdt.getText().toString();

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

        binding.titleEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Empty */ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.titleEmptyTxt.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) { /* Empty */ }
        });

        binding.contentEdt.addTextChangedListener(new TextWatcher() {
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
        NoticeDetailFragment noticeDetailFragment = settingActivity.noticeDetailFragment;
        Bundle bundle = new Bundle();
        bundle.putSerializable("notice", notice);
        noticeDetailFragment.setArguments(bundle);
        transaction.replace(R.id.settingContainer, noticeDetailFragment);
        transaction.commit();
    }

    public void inquiryUpdateProcess(String title, String content){
        loading.show();
        notice.setTitle(title);
        notice.setContent(content);

        DocumentReference documentReference = db.collection(Notice.DB_NAME).document(notice.getDocumentId());
        documentReference
                .set(notice)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                    updateSuccessPopUp();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    loading.dismiss();
                    String msg = "공지사항 수정에 실패하였습니다.\n잠시 후 다시 진행해주세요.";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                });
    }

    public void updateSuccessPopUp(){
        loading.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setCancelable(false)
                .setMessage("공지사항이 수정되었습니다.")
                .setPositiveButton("확인", (dialog, i) -> {
                    FragmentTransaction transaction = settingActivity.getSupportFragmentManager().beginTransaction();
                    NoticeDetailFragment noticeDetailFragment = settingActivity.noticeDetailFragment;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("notice", notice);
                    noticeDetailFragment.setArguments(bundle);
                    transaction.replace(R.id.settingContainer, noticeDetailFragment);
                    transaction.commit();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}