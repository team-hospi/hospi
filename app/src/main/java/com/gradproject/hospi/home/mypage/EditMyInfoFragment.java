package com.gradproject.hospi.home.mypage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.LoginActivity;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.User;
import com.gradproject.hospi.databinding.FragmentEditMyInfoBinding;
import com.gradproject.hospi.utils.Loading;
import com.gradproject.hospi.utils.PhoneNumberHyphen;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;
import static com.gradproject.hospi.home.HomeActivity.user;

public class EditMyInfoFragment extends Fragment implements OnBackPressedListener{
    private static final String TAG = "EditMyInfoFragment";
    private static final int REQUEST_WRITE_ADDRESS_ACTIVITY_CODE = 100; // 주소 입력 화면 식별 코드
    private FragmentEditMyInfoBinding binding;

    Calendar cal;
    FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        cal = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditMyInfoBinding.inflate(inflater, container, false);

        binding.emailTxt.setText(user.getEmail());
        binding.nameTxt.setText(user.getName());
        binding.phoneTxt.setText(user.getPhone());
        binding.birthTxt.setText(user.getBirth());
        // 주소가 입력되었는지 검사
        if(user.getAddress().equals("")){
            binding.addressTxt.setText("주소를 입력해주세요");
        }else{
            binding.addressTxt.setText(user.getAddress());
        }

        // 뒤로가기 버튼
        binding.backBtn.setOnClickListener(v -> onBackPressed());

        // 전화번호 변경 버튼
        binding.changePhNumBtn.setOnClickListener(v -> changePhone());

        // 생년월일 변경 버튼
        binding.changeBirthBtn.setOnClickListener(v -> changeBirth());

        // 주소 변경 버튼
        binding.changeAddressBtn.setOnClickListener(v -> {
            // 주소 입력 화면으로 이동
            startActivityForResult(new Intent(getContext(), WriteAddressActivity.class),
                    REQUEST_WRITE_ADDRESS_ACTIVITY_CODE);
        });

        // 비밀번호 변경 버튼
        binding.changePwBtn.setOnClickListener(v -> changePassword());

        // 로그아웃 버튼
        binding.logoutBtn.setOnClickListener(v -> logoutDialog());

        // 회원탈퇴 버튼
        binding.withdrawalBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), WithdrawalActivity.class)));

        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_WRITE_ADDRESS_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                String address = data.getStringExtra("address"); // 주소 받아와서 address에 저장
                user.setAddress(address);
                binding.addressTxt.setText(address); // 주소 설정

                DocumentReference documentReference = db
                        .collection(User.DB_NAME)
                        .document(user.getDocumentId()); // 해당 이메일 유저 문서 열기
                documentReference
                        .update("address", address) // 주소 업데이트
                        .addOnSuccessListener(new OnSuccessListener<Void>() { // 업데이트에 성공 했을때 호출
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "주소 정보 업데이트 성공");
                            }
                        });
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
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
                .setPositiveButton("확인", (dialog, i) -> { /* empty */ })
                .setNegativeButton("취소", (dialog, which) -> { /* empty */ });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String phoneNum = PhoneNumberHyphen.phone(phone.getText().toString());

            if(phoneNum.equals("")){
                err.setVisibility(View.VISIBLE);
            }else{
                DocumentReference documentReference = db
                        .collection(User.DB_NAME)
                        .document(user.getDocumentId()); // 해당 이메일 유저 문서 열기
                // 업데이트에 성공 했을때 호출
                documentReference
                        .update("phone", phoneNum) // 전화번호 업데이트
                        .addOnSuccessListener(aVoid -> {
                            binding.phoneTxt.setText(phoneNum);
                            user.setPhone(phoneNum);
                            alertDialog.dismiss();
                            Log.d(TAG, "전화번호 정보 업데이트 성공");
                        });
            }
        });
    }

    private void changeBirth(){
        String[] birth = user.getBirth().split("-");
        int cYear = Integer.parseInt(birth[0]);
        int cMonth = Integer.parseInt(birth[1])-1;
        int cDay = Integer.parseInt(birth[2]);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            cal.set(year, month, dayOfMonth);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String date = df.format(cal.getTime());

            DocumentReference documentReference = db.collection(User.DB_NAME)
                    .document(user.getDocumentId()); // 해당 이메일 유저 문서 열기
            // 업데이트에 성공 했을때 호출
            documentReference
                    .update("birth", date) // 생년월일 업데이트
                    .addOnSuccessListener(aVoid -> {
                        user.setBirth(date);
                        binding.birthTxt.setText(date);
                        Log.d(TAG, "생년월일 정보 업데이트 성공");
                    });
        },cYear, cMonth, cDay);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }

    private void changePassword(){
        Loading loading = new Loading(getContext(), "비밀번호 변경 메일 발송 중...");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("비밀번호를 변경하시겠습니까?")
                .setPositiveButton("확인", (dialog, i) -> {
                    loading.start();

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                    firebaseAuth.sendPasswordResetEmail(user.getEmail())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    loading.end();

                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext())
                                            .setCancelable(false)
                                            .setMessage("이메일로 비밀번호 변경 안내 메일이 발송되었습니다.\n비밀번호 변경 후 다시 로그인해주세요.")
                                            .setPositiveButton("확인", (dialog1, i1) -> {
                                                firebaseAuth.signOut();
                                                ActivityCompat.finishAffinity(getActivity());
                                                startActivity(new Intent(getContext(), LoginActivity.class));
                                            });
                                    AlertDialog alertDialog2 = builder2.create();
                                    alertDialog2.show();
                                    Log.d(TAG, "Email sent.");
                                }else{
                                    loading.end();
                                    Toast.makeText(getContext(), "진행 중 오류가 발생하였습니다. 잠시 후 다시 진행하여 주십시오.", Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .setNegativeButton("취소", (dialog, which) -> { /* empty */ });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void logoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("확인", (dialog, i) -> {
                    FirebaseAuth.getInstance().signOut(); // 로그아웃
                    ActivityCompat.finishAffinity(getActivity()); // 모든 액티비티 종료
                    startActivity(new Intent(getContext(), LoginActivity.class)); // 다시 로그인 화면으로
                })
                .setNegativeButton("취소", (dialog, which) -> { /* empty */ });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}