package com.gradproject.hospi.register;

import android.graphics.Color;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.User;
import com.gradproject.hospi.databinding.FragmentRegister5Binding;
import com.gradproject.hospi.utils.GMailSender;
import com.gradproject.hospi.utils.Loading;
import com.gradproject.hospi.utils.PatternCheck;

import java.util.Objects;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class RegisterFragment5 extends Fragment implements OnBackPressedListener {
    private static final String TAG = "RegisterFragment5";
    private FragmentRegister5Binding binding;
    RegisterActivity registerActivity;
    String sendCode;
    String email;
    String pw; // 비밀번호 저장
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

        binding.sendVerifyMailBtn.setOnClickListener(v -> {
            email = binding.inputEmail.getText().toString().trim();

            if (!(PatternCheck.isEmail(email))) {
                binding.emailErrorTxt.setText("잘못된 이메일입니다.");
                binding.emailErrorTxt.setVisibility(View.VISIBLE);
            }else{
                checkDuplicateEmail();
            }
        });

        binding.verifyCompleteBtn.setOnClickListener(v -> {
            String code = binding.verifyCodeEdt.getText().toString();
            if(code.equals(sendCode)){
                registerActivity.user.setEmail(email);
                binding.verifyErrorTxt.setText("인증에 성공하였습니다.");
                binding.verifyErrorTxt.setVisibility(View.VISIBLE);
                binding.verifyErrorTxt.setTextColor(Color.BLUE);
                binding.nextBtn.setEnabled(true);
                binding.verifyCompleteBtn.setEnabled(false);
                binding.verifyCodeEdt.setEnabled(false);
            }else{
                binding.verifyErrorTxt.setText("인증번호가 일치하지 않습니다.");
                binding.verifyErrorTxt.setVisibility(View.VISIBLE);
                binding.verifyErrorTxt.setTextColor(Color.RED);
            }
        });

        binding.inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.emailErrorTxt.setVisibility(View.INVISIBLE);
                binding.verifyErrorTxt.setVisibility(View.INVISIBLE);
                binding.verifyCodeEdt.setEnabled(false);
                binding.verifyCodeEdt.setBackgroundResource(R.drawable.edit_text_disable);
                binding.verifyCompleteBtn.setEnabled(false);
                binding.nextBtn.setEnabled(false);
                binding.verifyCodeEdt.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

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
        registerActivity.onFragmentChanged(3);
    }

    // 이메일 인증
    private void sendVerifyMail(){
        try {
            GMailSender gMailSender = new GMailSender();
            gMailSender.sendMail(email);
            sendCode = gMailSender.getEmailCode();
            Log.d(TAG, "송신 완료");
            registerActivity.runOnUiThread(() -> {
                Toast.makeText(getContext(), "인증번호가 발송되었습니다.", Toast.LENGTH_LONG).show();
                binding.verifyCodeEdt.setEnabled(true);
                binding.verifyCodeEdt.setBackgroundResource(R.drawable.edit_text);
                binding.verifyCompleteBtn.setEnabled(true);
            });
        } catch (SendFailedException e) {
            registerActivity.runOnUiThread(() -> {
                binding.emailErrorTxt.setText("이메일 형식이 잘못되었습니다.");
                binding.emailErrorTxt.setVisibility(View.VISIBLE);
            });
        } catch (MessagingException e) {
            registerActivity.runOnUiThread(() ->
                    Toast.makeText(getContext(), "인터넷 연결을 확인해주십시오.", Toast.LENGTH_LONG).show());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            registerActivity.runOnUiThread(() -> binding.sendVerifyMailBtn.setEnabled(true));
        }
    }

    // 이메일 중복 체크
    public void checkDuplicateEmail(){
        binding.sendVerifyMailBtn.setEnabled(false);
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
                            binding.sendVerifyMailBtn.setEnabled(true);
                            Log.d(TAG, "중복");
                        }else{
                            // 이메일 중복 없음
                            new Thread(this::sendVerifyMail).start();
                            Log.d(TAG, "사용가능한 이메일");
                        }
                    } else {
                        // 가져오는데 실패
                        Toast.makeText(getContext(), "오류가 발생하였습니다.\n잠시 후 다시 시도해주십시오.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        binding.sendVerifyMailBtn.setEnabled(true);
                    }
                });
    }

    // 회원가입 완료 팝업
    private void registerSuccess(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setCancelable(false)
                .setMessage("회원가입이 완료되었습니다.")
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