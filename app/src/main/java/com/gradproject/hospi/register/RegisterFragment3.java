package com.gradproject.hospi.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.databinding.FragmentRegister3Binding;
import com.gradproject.hospi.utils.DateTimeFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.GregorianCalendar;

public class RegisterFragment3 extends Fragment implements OnBackPressedListener {
    private FragmentRegister3Binding binding;
    RegisterActivity registerActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegister3Binding.inflate(inflater, container, false);

        registerActivity = (RegisterActivity) getActivity();

        binding.nextBtn.setOnClickListener(v -> rrNumSetProcess());

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onBackPressed() {
        registerActivity.onFragmentChanged(1);
    }

    private void rrNumSetProcess() {
        String rrNumFront = binding.rrNumFrontEdt.getText().toString().trim();
        String rrNumBack = binding.rrNumBackEdt.getText().toString().trim();
        String rrNum = rrNumFront + rrNumBack;

        if(rrNumCheck(rrNum)){
            String birth, sex;
            birth = sex = null;
            char tmpSex = rrNumBack.charAt(0);
            if(tmpSex == '1' || tmpSex == '2'){
                birth = "19" + rrNumFront.substring(0, 2)
                        + "-" + rrNumFront.substring(2, 4)
                        + "-" + rrNumFront.substring(4, 6);
                sex = (tmpSex=='1') ? "남자" : "여자";
            }else{
                birth = "20" + rrNumFront.substring(0, 2)
                        + "-" + rrNumFront.substring(2, 4)
                        + "-" + rrNumFront.substring(4, 6);
                sex = (tmpSex=='3') ? "남자" : "여자";
            }

            registerActivity.user.setBirth(birth);
            registerActivity.user.setSex(sex);
            registerActivity.onFragmentChanged(3);
            binding.rrNumErr.setVisibility(View.INVISIBLE);
        }else{
            binding.rrNumErr.setVisibility(View.VISIBLE);
        }
    }

    private boolean rrNumCheck(String rrNum) {
        // check~!!
        int[] chk = {2, 3, 4, 5, 6, 7, 0, 8, 9, 2, 3, 4, 5};

        // 곱셈 연산 후 누적합
        int tot=0;

        if (rrNum.length() != 13) {
            return false; // 입력 오류
        }

        for (int i=0; i<chk.length; i++) {
            if (i==6)
                continue;
            tot += chk[i] * Integer.parseInt(rrNum.substring(i, (i+1)));
        }

        //-- 여기까지 수행하면 ① 과 ② 를 모두 끝낸 상황이다.
        //   규칙에 맞게 곱셈 연산을 수행한 결과를 모두 더한 값은
        //   변수 tot 에 담겨 있는 상황이 된다.
        int su = 11 - tot%11;

        //-- 추가연산~!! (최종 결과 출력 전 추가 연산 필요)
        //   su에 대한 연산 결과가 두 자리로 나올 경우
        //   주민번호 마지막 자릿수와 비교할 수 없는 상황
        su = su % 10;

        //-- 여기까지 수행하면 ③ 과 ④ 를 모두 끝낸 상황이다.
        //   최종 연산 결과는 변수 su 에 담겨 있는 상황이 된다.

        // 최종 결과 출력
        return su == Integer.parseInt(rrNum.substring(12));
    }
}