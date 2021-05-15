package com.gradproject.hospi.register;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.User;
import com.gradproject.hospi.databinding.FragmentRegister5Binding;

public class RegisterFragment5 extends Fragment implements OnBackPressedListener {
    private static final String TAG = "RegisterFragment5";
    private FragmentRegister5Binding binding;

    RegisterActivity registerActivity;
    String email;
    String emailRegex = "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegister5Binding.inflate(inflater, container, false);

        registerActivity = (RegisterActivity) getActivity();

        binding.nextBtn.setOnClickListener(v -> {
            email = binding.inputEmail.getText().toString().trim();

            if (email.equals("")) {
                binding.emailErrorTxt.setText("이메일을 입력해주세요.");
                binding.emailErrorTxt.setVisibility(View.VISIBLE);
            } else if (!(email.matches(emailRegex))) {
                binding.emailErrorTxt.setText("잘못된 이메일 형식입니다.");
                binding.emailErrorTxt.setVisibility(View.VISIBLE);
            }else{
                checkDuplicateEmail(email);
            }
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
        registerActivity.onFragmentChanged(3);
    }

    // 이메일 중복 체크
    public void checkDuplicateEmail(String str){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection(User.DB_NAME).document(str);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // 문서 발견
                    duplicateError();
                    Log.d(TAG, "중복");
                } else {
                    // 문서 발견 못함
                    registerActivity.user.setEmail(email);
                    registerActivity.onFragmentChanged(5);
                    Log.d(TAG, "사용가능한 이메일");
                }
            } else {
                // 가져오는데 실패
                duplicateError();
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    void duplicateError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("이미 존재하는 이메일입니다.")
                .setPositiveButton("확인", (dialogInterface, i) -> { /* empty */ });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}