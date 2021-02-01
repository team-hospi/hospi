package com.gradproject.hospi.home.mypage;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.LoginActivity;
import com.gradproject.hospi.R;
import com.gradproject.hospi.User;
import com.gradproject.hospi.utils.Loading;
import com.gradproject.hospi.utils.PhoneNumberHyphen;

import static android.app.Activity.RESULT_OK;

public class EditMyInfoFragment extends Fragment {
    private static final int REQUEST_WRITE_ADDRESS_ACTIVITY_CODE = 100; // 주소 입력 화면 식별 코드

    LinearLayout backBtn; // 뒤로가기 버튼
    FrameLayout changePhNumBtn, changeBirthBtn, changeAddressBtn; // 전화번호 변경, 생년월일 변경, 주소 변경 버튼
    Button changePwBtn, logoutBtn, withdrawalBtn; // 비밀번호 변경, 로그아웃, 회원탈퇴 버튼
    TextView emailTxt, nameTxt, phoneTxt, birthTxt, addressTxt;

    FirebaseFirestore db;
    FirebaseUser firebaseUser;

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

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if(firebaseUser != null){
            emailTxt.setText(firebaseUser.getEmail());
            nameTxt.setText(firebaseUser.getDisplayName());
            getUserInfo();
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
                changePhone();
            }
        });

        // 생년월일 변경 버튼
        changeBirthBtn = rootView.findViewById(R.id.changeBirthBtn);
        changeBirthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBirth();
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
                changePassword();
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

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(requestCode==REQUEST_WRITE_ADDRESS_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                String address = data.getStringExtra("address"); // 주소 받아와서 address에 저장
                addressTxt.setText(address); // 주소 설정

                DocumentReference washingtonRef = db.collection("user_list").document(firebaseUser.getEmail()); // 해당 이메일 유저 문서 열기
                washingtonRef
                        .update("address", address) // 주소 업데이트
                        .addOnSuccessListener(new OnSuccessListener<Void>() { // 업데이트에 성공 했을때 호출
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("update", "주소 정보 업데이트 성공");
                            }
                        });
            }
        }
    }

    private void getUserInfo(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); // 현재 로그인한 유저 정보 받기
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {
            // User is signed in
            String email = firebaseUser.getEmail(); // 현재 로그인한 유저 이메일 가져오기

            DocumentReference docRef = db.collection("user_list").document(email);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() { // 유저 정보 받아오는데 성공 할 경우
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    user = documentSnapshot.toObject(User.class); // user 인스턴스에 유저 정보 저장
                    phoneTxt.setText(user.getPhone());
                    birthTxt.setText(user.getBirth());
                    // 주소가 입력되었는지 검사
                    if(user.getAddress().equals("")){
                        addressTxt.setText("주소를 입력해주세요");
                    }else{
                        addressTxt.setText(user.getAddress());
                    }
                    Log.d("success", "유저 정보 받기 성공");
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
    }

    private void changePhone(){
        EditText phone = new EditText(getContext());
        phone.setBackgroundResource(R.drawable.edit_text);
        phone.setHint("전화번호");
        phone.setInputType(InputType.TYPE_CLASS_NUMBER);
        phone.setPadding(40,0,0,0);

        TextView err = new TextView(getContext());
        err.setText("전화번호를 입력해주세요.");
        err.setTextColor(getResources().getColor(R.color.red, null));
        err.setVisibility(View.INVISIBLE);
        err.setPadding(10,10,0,0);

        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(100, 0, 100, 0);
        phone.setLayoutParams(params);
        err.setLayoutParams(params);

        container.addView(phone);
        container.addView(err);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle("전화번호 변경")
                .setMessage("전화번호를 입력해주세요.")
                .setView(container)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int i) { /* empty */ }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { /* empty */ }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String num = PhoneNumberHyphen.phone(phone.getText().toString());

                if(num.equals("")){
                    err.setVisibility(View.VISIBLE);
                }else{
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    if(firebaseUser!=null){
                        DocumentReference washingtonRef = db.collection("user_list").document(firebaseUser.getEmail()); // 해당 이메일 유저 문서 열기
                        washingtonRef
                                .update("phone", num) // 전화번호 업데이트
                                .addOnSuccessListener(new OnSuccessListener<Void>() { // 업데이트에 성공 했을때 호출
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        phoneTxt = getActivity().findViewById(R.id.phoneTxt);
                                        phoneTxt.setText(num);
                                        user.setPhone(num);
                                        alertDialog.dismiss();
                                        Log.d("update", "전화번호 정보 업데이트 성공");
                                    }
                                });
                    }else{
                        Toast.makeText(getContext(), "로그인 정보가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut(); // 로그아웃
                        ActivityCompat.finishAffinity(getActivity()); // 모든 액티비티 종료
                        startActivity(new Intent(getContext(), LoginActivity.class)); // 다시 로그인 화면으로
                    }
                }
            }
        });
    }

    private void changeBirth(){
        String[] birth = user.getBirth().split("-");
        int cYear = Integer.parseInt(birth[0]);
        int cMonth = Integer.parseInt(birth[1])-1;
        int cDay = Integer.parseInt(birth[2]);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = year+"-"+(month+1)+"-"+dayOfMonth;

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if(firebaseUser!=null){
                    DocumentReference washingtonRef = db.collection("user_list").document(firebaseUser.getEmail()); // 해당 이메일 유저 문서 열기
                    washingtonRef
                            .update("birth", date) // 생년월일 업데이트
                            .addOnSuccessListener(new OnSuccessListener<Void>() { // 업데이트에 성공 했을때 호출
                                @Override
                                public void onSuccess(Void aVoid) {
                                    birthTxt = getActivity().findViewById(R.id.birthTxt);
                                    user.setBirth(date);
                                    birthTxt.setText(date);
                                    Log.d("update", "생년월일 정보 업데이트 성공");
                                }
                            });
                }else{
                    Toast.makeText(getContext(), "로그인 정보가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut(); // 로그아웃
                    ActivityCompat.finishAffinity(getActivity()); // 모든 액티비티 종료
                    startActivity(new Intent(getContext(), LoginActivity.class)); // 다시 로그인 화면으로
                }
            }
        },cYear, cMonth, cDay);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }

    private void changePassword(){
        Loading loading = new Loading(getContext(), "비밀번호 변경 메일 발송 중...");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null){
            FirebaseAuth auth = FirebaseAuth.getInstance();

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setCancelable(false)
                    .setMessage("비밀번호를 변경하시겠습니까?")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int i) {
                            loading.start();

                            auth.sendPasswordResetEmail(firebaseUser.getEmail())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                loading.end();

                                                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext())
                                                        .setCancelable(false)
                                                        .setMessage("설정하신 이메일로 비밀번호 변경 안내 메일이 발송되었습니다.")
                                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                            @Override public void onClick(DialogInterface dialog, int i) { /* empty */ }
                                                        });
                                                AlertDialog alertDialog2 = builder2.create();
                                                alertDialog2.show();
                                                Log.d("changePassword", "Email sent.");
                                            }else{
                                                loading.end();
                                                Toast.makeText(getContext(), "진행 중 오류가 발생하였습니다. 잠시 후 다시 진행하여 주십시오.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { /* empty */ }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else{
            Toast.makeText(getContext(), "로그인 정보가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signOut(); // 로그아웃
            ActivityCompat.finishAffinity(getActivity()); // 모든 액티비티 종료
            startActivity(new Intent(getContext(), LoginActivity.class)); // 다시 로그인 화면으로
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
                    public void onClick(DialogInterface dialog, int which) { /* empty */ }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}