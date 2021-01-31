package com.gradproject.hospi.home.mypage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.LoginActivity;
import com.gradproject.hospi.R;
import com.gradproject.hospi.User;

import static android.app.Activity.RESULT_OK;

public class EditMyInfoFragment extends Fragment {
    private static final int REQUEST_WRITE_ADDRESS_ACTIVITY_CODE = 100; // 주소 입력 화면 식별 코드

    LinearLayout backBtn; // 뒤로가기 버튼
    FrameLayout changePhNumBtn, changeBirthBtn, changeAddressBtn; // 전화번호 변경, 생년월일 변경, 주소 변경 버튼
    Button changePwBtn, logoutBtn, withdrawalBtn; // 비밀번호 변경, 로그아웃, 회원탈퇴 버튼
    TextView emailTxt, nameTxt, phoneTxt, birthTxt, addressTxt;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    String email;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_edit_my_info, container,false);

        emailTxt = rootView.findViewById(R.id.emailTxt);
        nameTxt = rootView.findViewById(R.id.nameTxt);
        phoneTxt = rootView.findViewById(R.id.phoneTxt);
        birthTxt = rootView.findViewById(R.id.birthTxt);
        addressTxt = rootView.findViewById(R.id.addressTxt);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); // 현재 로그인한 유저 정보 받기
        if (firebaseUser != null) {
            // User is signed in
            email = firebaseUser.getEmail(); // 현재 로그인한 유저 이메일 가져오기

            emailTxt.setText(email);
            DocumentReference docRef = db.collection("user_list").document(email);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() { // 유저 정보 받아오는데 성공 할 경우
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    user = documentSnapshot.toObject(User.class); // user 인스턴스에 유저 정보 저장
                    nameTxt.setText(user.getName());
                    phoneTxt.setText(user.getPhone());
                    birthTxt.setText(user.getBirth());

                    // 주소가 입력되었는지 검사
                    if(user.getAddress().equals("")){
                        addressTxt.setText("주소를 입력해주세요");
                    }else{
                        addressTxt.setText(user.getAddress());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() { // 유저 정보 받아오는데 실패 할 경우
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "알 수 없는 오류로 인해 유저 정보를 받아오지 못했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(getContext(), "로그인 정보가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signOut(); // 로그아웃
            ActivityCompat.finishAffinity(getActivity()); // 모든 액티비티 종료
            startActivity(new Intent(getContext(), LoginActivity.class)); // 다시 로그인 화면으로
        }

        // 뒤로가기 버튼
        backBtn = rootView.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        // 전화번호 변경 버튼
        changePhNumBtn = rootView.findViewById(R.id.changePhNumBtn);
        changePhNumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - 전화번호 변경
            }
        });

        // 생년월일 변경 버튼
        changeBirthBtn = rootView.findViewById(R.id.changeBirthBtn);
        changeBirthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - 생년월일 변경
            }
        });

        // 주소 변경 버튼
        changeAddressBtn = rootView.findViewById(R.id.changeAddressBtn);
        changeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 주소 입력 화면으로 이동
                startActivityForResult(new Intent(getContext(), WriteAddressActivity.class), REQUEST_WRITE_ADDRESS_ACTIVITY_CODE);
            }
        });

        // 비밀번호 변경 버튼
        changePwBtn = rootView.findViewById(R.id.changePwBtn);
        changePwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - 비밀번호 변경
            }
        });

        // 로그아웃 버튼
        logoutBtn = rootView.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog();
            }
        });

        // 회원탈퇴 버튼
        withdrawalBtn = rootView.findViewById(R.id.withdrawalBtn);
        withdrawalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), WithdrawalActivity.class));
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_WRITE_ADDRESS_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                String address = data.getStringExtra("address"); // 주소 받아와서 address에 저장
                addressTxt.setText(address); // 주소 설정

                DocumentReference washingtonRef = db.collection("user_list").document(user.getEmail()); // 해당 이메일 유저 문서 열기
                washingtonRef
                        .update("address", address) // 주소 업데이트
                        .addOnSuccessListener(new OnSuccessListener<Void>() { // 업데이트에 성공 했을때 호출
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("getAddress", "주소 정보 업데이트 성공");
                            }
                        });
            }
        }
    }

    private void logoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int i) {
                        FirebaseAuth.getInstance().signOut(); // 로그아웃
                        ActivityCompat.finishAffinity(getActivity()); // 모든 액티비티 종료
                        startActivity(new Intent(getContext(), LoginActivity.class)); // 다시 로그인 화면으로
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}