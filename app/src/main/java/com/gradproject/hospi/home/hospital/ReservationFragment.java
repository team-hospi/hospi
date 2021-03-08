package com.gradproject.hospi.home.hospital;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gradproject.hospi.LoginActivity;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.User;

import static com.gradproject.hospi.home.hospital.HospitalActivity.hospital;

public class ReservationFragment extends Fragment implements OnBackPressedListener {
    HospitalActivity hospitalActivity;
    User user;
    FirebaseUser firebaseUser;

    Button nextBtn, setDateBtn, setTimeBtn;
    LinearLayout backBtn;
    TextView nameTxt, phoneTxt, birthTxt, hospitalNameTxt;
    TextView reservationDateTxt, reservationTimeTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_reservation, container,false);

        hospitalActivity = (HospitalActivity) getActivity();
        backBtn = rootView.findViewById(R.id.backBtn);
        nextBtn = rootView.findViewById(R.id.nextBtn);
        setDateBtn = rootView.findViewById(R.id.setDateBtn);
        setTimeBtn = rootView.findViewById(R.id.setTimeBtn);
        nameTxt = rootView.findViewById(R.id.nameTxt);
        phoneTxt = rootView.findViewById(R.id.phoneTxt);
        birthTxt = rootView.findViewById(R.id.birthTxt);
        hospitalNameTxt = rootView.findViewById(R.id.hospitalNameTxt);
        reservationDateTxt = rootView.findViewById(R.id.reservationDateTxt);
        reservationTimeTxt = rootView.findViewById(R.id.reservationTimeTxt);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hospitalActivity.onReservationFragmentChanged(2);
            }
        });

        setDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        getUserInfo(); // 유저 정보 받아옴과 동시에 텍스트뷰 설정
        hospitalNameTxt.setText(hospital.getName());

        return rootView;
    }

    @Override
    public void onBackPressed(){
        hospitalActivity.onReservationFragmentChanged(0);
        reservationDateTxt.setText("날짜를 설정해주세요.");
        reservationTimeTxt.setText("시간을 설정해주세요.");
    }

    private void getUserInfo(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); // 현재 로그인한 유저 정보 받기

        if (firebaseUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("user_list")
                    .whereEqualTo("email", firebaseUser.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("DB", document.getId() + " => " + document.getData());
                                    DocumentReference docRef = db.collection("user_list")
                                            .document(document.getId());
                                    docRef
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() { // 유저 정보 받아오는데 성공 할 경우
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    user = documentSnapshot.toObject(User.class); // user 인스턴스에 유저 정보 저장
                                                    nameTxt.setText(user.getName());
                                                    phoneTxt.setText(user.getPhone());
                                                    birthTxt.setText(user.getBirth());
                                                    // 주소가 입력되었는지 검사
                                                    if(user.getAddress().equals("")){
                                                        // 주소가 없는 경우
                                                    }else{
                                                        // 주소가 있는 경우
                                                    }
                                                    Log.d("success", "유저 정보 받기 성공");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() { // 유저 정보 받아오는데 실패 할 경우
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(),
                                                    "알 수 없는 오류로 인해 유저 정보를 받아오지 못했습니다.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                Log.d("DB", "Error getting documents: ", task.getException());
                            }
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