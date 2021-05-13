package com.gradproject.hospi.home.mypage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.databinding.FragmentNoticeWriteBinding;
import com.gradproject.hospi.utils.Loading;

import java.sql.Timestamp;

public class NoticeWriteFragment extends Fragment implements OnBackPressedListener {
    private static final String TAG ="NoticeWriteFragment";
    private FragmentNoticeWriteBinding binding;

    SettingActivity settingActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNoticeWriteBinding.inflate(inflater, container, false);

        settingActivity = (SettingActivity) getActivity();

        binding.closeBtn.setOnClickListener(v -> onBackPressed());

        binding.writeBtn.setOnClickListener(v -> {
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
                inquiryWriteProcess(title, content);
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
        settingActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.settingContainer, settingActivity.noticeFragment).commit();
        binding.titleEdt.setText("");
        binding.contentEdt.setText("");
    }

    public void inquiryWriteProcess(String title, String content){
        Loading loading = new Loading(getContext(), "공지사항 등록 중...");
        loading.start();

        long timestamp = new Timestamp(System.currentTimeMillis()).getTime();

        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setTimestamp(timestamp);
        notice.setDocumentId(null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Notice.DB_NAME)
                .add(notice)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    writeSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    writeFail();
                });

        loading.end();
    }

    private void writeSuccess(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("공지사항이 등록되었습니다.")
                .setPositiveButton("확인", (dialog, i) -> {
                    FragmentTransaction transaction = settingActivity.getSupportFragmentManager().beginTransaction();
                    NoticeFragment noticeFragment = settingActivity.noticeFragment;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("write", true);
                    noticeFragment.setArguments(bundle);
                    transaction.replace(R.id.settingContainer, noticeFragment);
                    transaction.commit();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void writeFail(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("문의 등록에 실패하였습니다.\n잠시후 다시 시도해주세요.")
                .setPositiveButton("확인", (dialogInterface, i) -> { /* empty */ });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}