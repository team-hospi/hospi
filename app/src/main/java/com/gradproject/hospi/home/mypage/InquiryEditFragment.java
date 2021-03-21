package com.gradproject.hospi.home.mypage;

import android.content.DialogInterface;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.Inquiry;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;

public class InquiryEditFragment extends Fragment implements OnBackPressedListener {
    ImageButton closeBtn;
    Button updateBtn;
    EditText inquiryTitleEdt, inquiryContentEdt;
    TextView hospitalNameTxt, titleEmptyTxt, contentEmptyTxt;

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_inquiry_edit, container,false);

        closeBtn = rootView.findViewById(R.id.closeBtn);
        updateBtn = rootView.findViewById(R.id.updateBtn);
        inquiryTitleEdt = rootView.findViewById(R.id.inquiryTitleEdt);
        inquiryContentEdt = rootView.findViewById(R.id.inquiryContentEdt);
        hospitalNameTxt = rootView.findViewById(R.id.hospitalNameTxt);
        titleEmptyTxt = rootView.findViewById(R.id.titleEmptyTxt);
        contentEmptyTxt = rootView.findViewById(R.id.contentEmptyTxt);

        hospitalNameTxt.setText(inquiry.getHospital_name());
        inquiryTitleEdt.setText(inquiry.getTitle());
        inquiryContentEdt.setText(inquiry.getContent());

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = inquiryTitleEdt.getText().toString();
                String content = inquiryContentEdt.getText().toString();

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
            }
        });

        inquiryTitleEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Empty */ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                titleEmptyTxt.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) { /* Empty */ }
        });

        inquiryContentEdt.addTextChangedListener(new TextWatcher() {
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

        DocumentReference documentReference = db.collection("inquiry_list").document(inquiry.getDocumentId());
        documentReference
                .set(inquiry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DB", "DocumentSnapshot successfully updated!");
                        updateSuccessPopUp();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DB", "Error updating document", e);
                        String msg = "문의 수정에 실패하였습니다.\n잠시 후 다시 진행해주세요.";
                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void updateSuccessPopUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("문의가 수정되었습니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int i) {
                        FragmentTransaction transaction = settingActivity.getSupportFragmentManager().beginTransaction();
                        InquiryDetailFragment inquiryDetailFragment = settingActivity.inquiryDetailFragment;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("inquiry", inquiry);
                        inquiryDetailFragment.setArguments(bundle);
                        transaction.replace(R.id.settingContainer, inquiryDetailFragment);
                        transaction.commit();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}