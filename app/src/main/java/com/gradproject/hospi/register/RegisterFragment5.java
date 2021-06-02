package com.gradproject.hospi.register;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.User;
import com.gradproject.hospi.databinding.FragmentRegister5Binding;
import com.gradproject.hospi.utils.PatternCheck;

import java.util.Objects;

public class RegisterFragment5 extends Fragment implements OnBackPressedListener {
    private static final String TAG = "RegisterFragment5";
    private FragmentRegister5Binding binding;

    RegisterActivity registerActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegister5Binding.inflate(inflater, container, false);

        registerActivity = (RegisterActivity) getActivity();

        binding.nextBtn.setOnClickListener(v -> {
            String email = binding.inputEmail.getText().toString().trim();

            if (!(PatternCheck.isEmail(email))) {
                binding.emailErrorTxt.setText("잘못된 이메일입니다.");
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
    public void checkDuplicateEmail(String email){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(User.DB_NAME)
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = 0;
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            count++;
                        }
                        if(count != 0){
                            // 이메일 중복
                            duplicateError();
                            Log.d(TAG, "중복");
                        }else{
                            // 이메일 중복 없음
                            registerActivity.user.setEmail(email);
                            registerActivity.onFragmentChanged(5);
                            Log.d(TAG, "사용가능한 이메일");
                        }
                    } else {
                        // 가져오는데 실패
                        duplicateError();
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    void duplicateError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setCancelable(false)
                .setMessage("이미 존재하는 이메일입니다.")
                .setPositiveButton("확인", (dialogInterface, i) -> { /* empty */ });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}