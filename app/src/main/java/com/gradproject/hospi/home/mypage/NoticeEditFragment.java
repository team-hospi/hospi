package com.gradproject.hospi.home.mypage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.Inquiry;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;

public class NoticeEditFragment extends Fragment implements OnBackPressedListener {
    private static final String TAG = "NoticeEditFragment";

    ImageButton closeBtn;
    Button updateBtn;
    EditText titleEdt, contentEdt;
    TextView titleEmptyTxt, contentEmptyTxt;

    SettingActivity settingActivity;
    Notice notice;
    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingActivity = (SettingActivity) getActivity();
        notice = (Notice) getArguments().getSerializable("notice");
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_notice_edit, container,false);

        closeBtn = rootView.findViewById(R.id.closeBtn);
        updateBtn = rootView.findViewById(R.id.updateBtn);
        titleEdt = rootView.findViewById(R.id.titleEdt);
        contentEdt = rootView.findViewById(R.id.contentEdt);
        titleEmptyTxt = rootView.findViewById(R.id.titleEmptyTxt);
        contentEmptyTxt = rootView.findViewById(R.id.contentEmptyTxt);

        titleEdt.setText(notice.getTitle());
        contentEdt.setText(notice.getContent());

        closeBtn.setOnClickListener(v -> onBackPressed());

        updateBtn.setOnClickListener(v -> {
            String title = titleEdt.getText().toString();
            String content = contentEdt.getText().toString();

            if(title.equals("") && content.equals("")) {
                titleEmptyTxt.setVisibility(View.VISIBLE);
                contentEmptyTxt.setVisibility(View.VISIBLE);
            }else if(title.equals("")){
                titleEmptyTxt.setVisibility(View.VISIBLE);
            }else if(content.equals("")){
                contentEmptyTxt.setVisibility(View.VISIBLE);
            }else{
                inquiryUpdateProcess(title, content);
            }
        });

        titleEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Empty */ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                titleEmptyTxt.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) { /* Empty */ }
        });

        contentEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Empty */ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contentEmptyTxt.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) { /* Empty */ }
        });

        return rootView;
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
                    String msg = "공지사항 수정에 실패하였습니다.\n잠시 후 다시 진행해주세요.";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                });
    }

    public void updateSuccessPopUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
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