package com.gradproject.hospi.register;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.User;
import com.gradproject.hospi.utils.Loading;

public class RegisterFragment6 extends Fragment implements OnBackPressedListener {
    private static final String TAG = "RegisterFragment6";

    RegisterActivity registerActivity;
    EditText inputPW, inputPW2; // 1: 비밀번호 2: 비밀번호 확인
    TextView pwErrorTxt;

    String pw; // 비밀번호 저장
    Loading loading;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_register6, container,false);

        registerActivity = (RegisterActivity) getActivity();
        inputPW = rootView.findViewById(R.id.inputPW);
        inputPW2 = rootView.findViewById(R.id.inputPW2);
        pwErrorTxt = rootView.findViewById(R.id.pwErrorTxt);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loading = new Loading(getContext(), "회원가입 완료 중...");

        Button nextBtn = rootView.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(v -> {
            if(inputPW.getText().toString().equals("") || inputPW2.getText().toString().equals("")){
                pwErrorTxt.setText("비밀번호를 입력해주세요.");
                pwErrorTxt.setVisibility(View.VISIBLE);
            }else if(!(inputPW.getText().toString().equals(inputPW2.getText().toString()))) {
                pwErrorTxt.setText("비밀번호가 일치하지 않습니다.");
                pwErrorTxt.setVisibility(View.VISIBLE);
            }else if(inputPW.getText().toString().length()<6){
                pwErrorTxt.setText("비밀번호는 6자리 이상이어야 합니다.");
                pwErrorTxt.setVisibility(View.VISIBLE);
            }else{
                loading.start();
                pw = inputPW2.getText().toString();

                firebaseAuth.createUserWithEmailAndPassword(registerActivity.user.getEmail(), pw)
                        .addOnCompleteListener(getActivity(), task -> {
                            if(task.isSuccessful()){
                                //아이디 생성이 완료 되었을 때
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(registerActivity.user.getName())
                                        .build();

                                firebaseAuth.getCurrentUser().updateProfile(profileUpdates)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            }
                                        });

                                db.collection(User.DB_NAME)
                                        .add(registerActivity.user)
                                        .addOnSuccessListener(documentReference -> {
                                            userDocumentIdUpdate();
                                            loading.end();
                                            registerSuccess();
                                        });
                            }else{
                                //아이디 생성이 실패했을 경우
                                loading.end();
                                Toast.makeText(getContext(), "알 수 없는 오류로 인해 진행 할 수 없습니다.\n 잠시 후 다시 진행하여 주십시오.", Toast.LENGTH_LONG).show();
                                firebaseAuth.getCurrentUser().delete();
                            }
                        });
            }
        });

        return rootView;
    }

    @Override
    public void onBackPressed() {
        registerActivity.onFragmentChanged(4);
    }

    // 회원가입 완료 팝업
    private void registerSuccess(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("회원가입이 완료되었습니다.")
                .setPositiveButton("확인", (dialogInterface, i) -> {
                    FirebaseAuth.getInstance().signOut();
                    getActivity().finish();
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
                        for (QueryDocumentSnapshot document : task.getResult()) {
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