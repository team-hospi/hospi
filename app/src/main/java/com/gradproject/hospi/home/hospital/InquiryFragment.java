package com.gradproject.hospi.home.hospital;

import android.content.Intent;
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

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.Inquiry;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.utils.Loading;

import java.sql.Timestamp;

import static com.gradproject.hospi.home.HomeActivity.user;
import static com.gradproject.hospi.home.hospital.HospitalActivity.hospital;

public class InquiryFragment extends Fragment implements OnBackPressedListener {
    private static final String TAG ="InquiryFragment";

    HospitalActivity hospitalActivity;

    Button writeBtn;
    ImageButton closeBtn;
    EditText inquiryTitleEdt, inquiryContentEdt;
    TextView hospitalNameTxt, titleEmptyTxt, contentEmptyTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_inquiry, container,false);

        hospitalActivity = (HospitalActivity) getActivity();
        closeBtn = rootView.findViewById(R.id.closeBtn);
        writeBtn = rootView.findViewById(R.id.writeBtn);
        inquiryTitleEdt = rootView.findViewById(R.id.inquiryTitleEdt);
        inquiryContentEdt = rootView.findViewById(R.id.inquiryContentEdt);
        hospitalNameTxt = rootView.findViewById(R.id.hospitalNameTxt);
        titleEmptyTxt = rootView.findViewById(R.id.titleEmptyTxt);
        contentEmptyTxt = rootView.findViewById(R.id.contentEmptyTxt);

        hospitalNameTxt.setText(hospital.getName());

        closeBtn.setOnClickListener(v -> onBackPressed());

        writeBtn.setOnClickListener(v -> {
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
                inquiryWriteProcess(title, content);
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
        if(getArguments()!=null){
            if(getArguments().getBoolean("popUp", false)){
                getActivity().finish();
            }else{
                hospitalActivity.onInquiryFragmentChanged(0);
                inquiryTitleEdt.setText("");
                inquiryContentEdt.setText("");
            }
        }else{
            hospitalActivity.onInquiryFragmentChanged(0);
            inquiryTitleEdt.setText("");
            inquiryContentEdt.setText("");
        }
    }

    public void inquiryWriteProcess(String title, String content){
        Loading loading = new Loading(getContext(), "문의 등록 중...");
        loading.start();

        long timestamp = new Timestamp(System.currentTimeMillis()).getTime();

        Inquiry inquiry = new Inquiry(user.getEmail(), hospital.getId(), hospital.getName(),
                timestamp, title, content, "", false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Inquiry.DB_NAME)
                .add(inquiry)
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
                .setMessage("문의가 등록되었습니다.")
                .setPositiveButton("확인", (dialogInterface, i) -> {
                    if(getArguments().getBoolean("popUp", false)){
                        getActivity().finish();
                    }else{
                        getActivity().finish();
                        Intent intent = new Intent(getContext(), HospitalActivity.class);
                        intent.putExtra("hospital", hospital);
                        startActivity(intent);
                    }
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