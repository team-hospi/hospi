package com.gradproject.hospi.register;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.User;
import com.gradproject.hospi.databinding.FragmentRegister6Binding;
import com.gradproject.hospi.utils.Loading;

import java.util.Objects;

public class RegisterFragment6 extends Fragment implements OnBackPressedListener {
    private static final String TAG = "RegisterFragment6";
    private FragmentRegister6Binding binding;

    RegisterActivity registerActivity;
    String pw; // 비밀번호 저장
    Loading loading;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegister6Binding.inflate(inflater, container, false);

        registerActivity = (RegisterActivity) getActivity();

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loading = new Loading(getContext());

        binding.nextBtn.setOnClickListener(v -> {
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
                loading.show();
                pw = binding.inputPW2.getText().toString();

                firebaseAuth.createUserWithEmailAndPassword(registerActivity.user.getEmail(), pw)
                        .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
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
                                            loading.dismiss();
                                            registerSuccess();
                                        });
                            }else{
                                //아이디 생성이 실패했을 경우
                                loading.dismiss();
                                Toast.makeText(getContext(), "알 수 없는 오류로 인해 진행 할 수 없습니다.\n 잠시 후 다시 진행하여 주십시오.", Toast.LENGTH_LONG).show();
                                Objects.requireNonNull(firebaseAuth.getCurrentUser()).delete();
                            }
                        });
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
        registerActivity.onFragmentChanged(4);
    }

    // 회원가입 완료 팝업
    private void registerSuccess(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setCancelable(false)
                .setMessage("회원가입이 완료되었습니다.")
                .setPositiveButton("확인", (dialogInterface, i) -> {
                    FirebaseAuth.getInstance().signOut();
                    Objects.requireNonNull(getActivity()).finish();
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