package com.gradproject.hospi.register;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.MainActivity;
import com.gradproject.hospi.R;

public class RegisterFragment5 extends Fragment {
    RegisterActivity registerActivity;
    TextView emailErrorTxt;
    EditText inputEmail;

    String email;
    String emailRegex = "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_register5, container,false);

        registerActivity = (RegisterActivity) getActivity();
        emailErrorTxt = rootView.findViewById(R.id.emailErrorTxt);
        inputEmail = rootView.findViewById(R.id.inputEmail);

        Button nextBtn = rootView.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = inputEmail.getText().toString().trim();

                if (email.equals("")) {
                    emailErrorTxt.setText("이메일을 입력해주세요.");
                    emailErrorTxt.setVisibility(View.VISIBLE);
                } else if (!(email.matches(emailRegex))) {
                    emailErrorTxt.setText("잘못된 이메일 형식입니다.");
                    emailErrorTxt.setVisibility(View.VISIBLE);
                }else{
                    checkDuplicateEmail(email);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                registerActivity.onFragmentChanged(3);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // 이메일 중복 체크
    public void checkDuplicateEmail(String str){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("user_list").document(str);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // 문서 발견
                        duplicateError();
                        Log.d("duplicate", "중복");
                    } else {
                        // 문서 발견 못함
                        registerActivity.user.setEmail(email);
                        registerActivity.onFragmentChanged(5);
                        Log.d("duplicate", "사용가능한 이메일");
                    }
                } else {
                    // 가져오는데 실패
                    duplicateError();
                    Log.d("duplicate", "get failed with ", task.getException());
                }
            }
        });
    }

    void duplicateError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("이미 존재하는 이메일입니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}