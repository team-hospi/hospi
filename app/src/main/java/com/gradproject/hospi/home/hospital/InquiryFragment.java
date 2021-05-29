package com.gradproject.hospi.home.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.Inquiry;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.databinding.FragmentInquiryBinding;
import com.gradproject.hospi.utils.Loading;
import com.gradproject.hospi.utils.StatusBar;

import java.sql.Timestamp;

import static com.gradproject.hospi.home.HomeActivity.user;
import static com.gradproject.hospi.home.hospital.HospitalActivity.hospital;

public class InquiryFragment extends Fragment implements OnBackPressedListener {
    private static final String TAG ="InquiryFragment";
    private FragmentInquiryBinding binding;

    HospitalActivity hospitalActivity;
    Loading loading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loading = new Loading(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInquiryBinding.inflate(inflater, container, false);

        StatusBar.updateStatusBarColor(requireActivity(), R.color.white);

        hospitalActivity = (HospitalActivity) getActivity();

        binding.hospitalNameTxt.setText(hospital.getName());

        binding.closeBtn.setOnClickListener(v -> onBackPressed());

        binding.writeBtn.setOnClickListener(v -> {
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
                inquiryWriteProcess(title, content);
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
    public void onBackPressed() {
        if(getArguments()!=null){
            if(getArguments().getBoolean("popUp", false)){
                requireActivity().finish();
            }else{
                hospitalActivity.onInquiryFragmentChanged(0);
                binding.inquiryTitleEdt.setText("");
                binding.inquiryContentEdt.setText("");
            }
        }else{
            hospitalActivity.onInquiryFragmentChanged(0);
            binding.inquiryTitleEdt.setText("");
            binding.inquiryContentEdt.setText("");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void inquiryWriteProcess(String title, String content){
        loading.show();

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
    }

    private void writeSuccess(){
        loading.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setCancelable(false)
                .setMessage("문의가 등록되었습니다.")
                .setPositiveButton("확인", (dialogInterface, i) -> {
                    boolean isPopUp = false;

                    if(getArguments() != null){
                        isPopUp = getArguments().getBoolean("popUp", false);
                    }

                    requireActivity().finish();

                    if (!isPopUp) {
                        Intent intent = new Intent(getContext(), HospitalActivity.class);
                        intent.putExtra("hospital", hospital);
                        startActivity(intent);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void writeFail(){
        loading.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setCancelable(false)
                .setMessage("문의 등록에 실패하였습니다.\n잠시후 다시 시도해주세요.")
                .setPositiveButton("확인", (dialogInterface, i) -> { /* empty */ });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}