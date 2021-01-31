package com.gradproject.hospi.register;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.R;
import com.gradproject.hospi.utils.Encrypt;
import com.gradproject.hospi.utils.Loading;

public class RegisterFragment6 extends Fragment {
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
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    pw = Encrypt.getEncrypt(registerActivity.user.getEmail(), inputPW2.getText().toString());

                    firebaseAuth.createUserWithEmailAndPassword(registerActivity.user.getEmail(), pw)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        //아이디 생성이 완료 되었을 때
                                        db.collection("user_list")
                                                .document(registerActivity.user.getEmail())
                                                .set(registerActivity.user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        loading.end();
                                                        registerSuccess();
                                                    }
                                                });
                                    }else{
                                        //아이디 생성이 실패했을 경우
                                        loading.end();
                                        Toast.makeText(getContext(), "알 수 없는 오류로 인해 진행 할 수 없습니다.\n 잠시 후 다시 진행하여 주십시오.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
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
                registerActivity.onFragmentChanged(4);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // 회원가입 완료 팝업
    private void registerSuccess(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("회원가입이 완료되었습니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        getActivity().finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}