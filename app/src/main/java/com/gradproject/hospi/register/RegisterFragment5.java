package com.gradproject.hospi.register;

import static com.gradproject.hospi.utils.PatternCheck.isEmail;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.User;
import com.gradproject.hospi.databinding.FragmentRegister5Binding;
import com.gradproject.hospi.utils.Loading;

import java.util.Objects;

public class RegisterFragment5 extends Fragment implements OnBackPressedListener {
    private static final String TAG = "RegisterFragment5";
    private FragmentRegister5Binding binding;
    RegisterActivity registerActivity;
    Loading loading;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerActivity = (RegisterActivity) getActivity();
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loading = new Loading(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegister5Binding.inflate(inflater, container, false);

        binding.inputPW2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!(binding.inputEmail.getText().toString().equals("") || binding.inputPW.getText().toString().equals(""))){
                    binding.nextBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.nextBtn.setOnClickListener(v -> {
            String email = binding.inputEmail.getText().toString();
            checkDuplicateEmail(email);
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

    // 회원가입 진행
    public void signUpProcess(){
        loading.show();
        String pw = binding.inputPW2.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(registerActivity.user.getEmail(), pw)
                .addOnCompleteListener(requireActivity(), task -> {
                    if(task.isSuccessful()){
                        //아이디 생성이 완료 되었을 때
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(registerActivity.user.getName())
                                .build();

                        Objects.requireNonNull(firebaseAuth.getCurrentUser()).updateProfile(profileUpdates)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                });

                        db.collection(User.DB_NAME)
                                .add(registerActivity.user)
                                .addOnSuccessListener(documentReference -> {
                                    userDocumentIdUpdate();

                                    firebaseAuth.getCurrentUser()
                                            .sendEmailVerification()
                                            .addOnCompleteListener(task12 -> {
                                                if(task12.isSuccessful()){
                                                    Log.d(TAG, "인증메일 발송");
                                                }
                                            });

                                    loading.dismiss();
                                    registerSuccess();
                                });
                    }else{
                        //아이디 생성이 실패했을 경우
                        loading.dismiss();
                        Toast.makeText(getContext(), "알 수 없는 오류로 인해 진행 할 수 없습니다.\n 잠시 후 다시 진행하여 주십시오.", Toast.LENGTH_LONG).show();
                    }
                });
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
                            binding.emailErrorTxt.setText("이미 존재하는 이메일입니다.");
                            binding.emailErrorTxt.setVisibility(View.VISIBLE);
                            Log.d(TAG, "중복");
                        }else{
                            // 이메일 중복 없음
                            if(isEmail(email)){
                                registerActivity.user.setEmail(email);
                                checkPassword();
                                Log.d(TAG, "사용가능한 이메일");
                            }else{
                                binding.emailErrorTxt.setText("올바르지 않은 이메일입니다.");
                                binding.emailErrorTxt.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        // 가져오는데 실패
                        Toast.makeText(getContext(), "오류가 발생하였습니다.\n잠시 후 다시 시도해주십시오.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    // 비밀번호 확인
    public void checkPassword(){
        if(binding.inputPW.getText().toString().equals("") || binding.inputPW2.getText().toString().equals("")){
            binding.pwErrorTxt.setText("비밀번호를 입력해주세요.");
            binding.pwErrorTxt.setVisibility(View.VISIBLE);
        }else if(!(binding.inputPW.getText().toString().equals(binding.inputPW2.getText().toString()))) {
            binding.pwErrorTxt.setText("비밀번호가 일치하지 않습니다.");
            binding.pwErrorTxt.setVisibility(View.VISIBLE);
        }else if(binding.inputPW.getText().toString().length()<6){
            binding.pwErrorTxt.setText("비밀번호는 6자리 이상이어야 합니다.");
            binding.pwErrorTxt.setVisibility(View.VISIBLE);
        }else{
            signUpProcess();
        }
    }

    // 회원가입 완료 팝업
    private void registerSuccess(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setCancelable(false)
                .setMessage("가입하신 이메일로 발송된 메일을 통해 인증을 하시면 회원가입이 완료됩니다.")
                .setPositiveButton("확인", (dialogInterface, i) -> {
                    FirebaseAuth.getInstance().signOut();
                    requireActivity().finish();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void userDocumentIdUpdate(){
        db.collection(User.DB_NAME)
                .whereEqualTo("email", registerActivity.user.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            DocumentReference documentReference = db.collection(User.DB_NAME).document(document.getId());
                            documentReference
                                    .update("documentId", document.getId())
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}