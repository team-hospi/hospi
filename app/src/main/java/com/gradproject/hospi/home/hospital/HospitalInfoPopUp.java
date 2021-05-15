package com.gradproject.hospi.home.hospital;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.R;
import com.gradproject.hospi.User;
import com.gradproject.hospi.databinding.HospitalInfoPopUpBinding;
import com.gradproject.hospi.home.search.Hospital;

import java.util.ArrayList;

import static com.gradproject.hospi.home.HomeActivity.user;

public class HospitalInfoPopUp extends AppCompatActivity {
    private static final String TAG ="HospitalInfoPopUp";
    public static final String HOSPITAL_INFO_POP_UP ="HospitalInfoPopUp";
    public static final int RESERVATION_CODE = 0;
    public static final int INQUIRY_CODE = 1;
    private HospitalInfoPopUpBinding binding;

    FirebaseFirestore db;
    Hospital hospital;
    ArrayList<String> favorites;
    boolean isFavorite = false;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HospitalInfoPopUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intent = new Intent(getApplicationContext(), HospitalActivity.class);

        db = FirebaseFirestore.getInstance();
        if(user.getFavorites() != null){
            favorites = (ArrayList) user.getFavorites();
        }
        hospital = (Hospital) getIntent().getSerializableExtra("hospital");
        intent.putExtra("hospital", hospital);

        favoriteCheck(); // 찜한 병원인지 확인

        if(isFavorite){
            binding.favoriteImg.setImageResource(R.drawable.ic_action_favorite);
        }

        // 닫기 버튼
        binding.closeBtn.setOnClickListener(v -> onBackPressed());

        // 예약 버튼
        binding.reservationBtn.setOnClickListener(v -> {
            intent.putExtra(HOSPITAL_INFO_POP_UP, RESERVATION_CODE);
            startActivity(intent);
        });

        // 문의 버튼
        binding.inquiryBtn.setOnClickListener(v -> {
            intent.putExtra(HOSPITAL_INFO_POP_UP, INQUIRY_CODE);
            startActivity(intent);
        });

        // 전화 버튼
        binding.callBtn.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + hospital.getTel()))));

        // 찜 버튼
        binding.favoriteBtn.setOnClickListener(v -> {
            if(isFavorite){
                String msg = "찜이 해제되었습니다.";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                isFavorite = false;
                binding.favoriteImg.setImageResource(R.drawable.ic_action_favorite_border);
                removeFavoriteList();
            }else{
                String msg = "찜이 설정되었습니다.";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                isFavorite = true;
                binding.favoriteImg.setImageResource(R.drawable.ic_action_favorite);
                addFavoriteList();
            }
        });

        binding.hospitalName.setText(hospital.getName());

        switch (hospital.getKind()){
            case "의원":
                binding.departmentTxt.setText(hospital.getDepartment().get(0));
                break;
            case "종합":
            case "대학":
                binding.departmentTxt.setText(hospital.getKind() + "병원");
                break;
        }

        String[] businessHoursArr = getBusinessHours();
        binding.weekdayBusinessHours.setText(businessHoursArr[0]);
        binding.saturdayBusinessHours.setText(businessHoursArr[1]);
        binding.holidayBusinessHours.setText(businessHoursArr[2]);
        binding.addressTxt.setText(hospital.getAddress());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private String[] getBusinessHours(){
        String[] strArr = {"", "토요일 휴무", "공휴일 휴무"};
        String open, close;

        open = hospital.getWeekdayOpen();
        close = hospital.getWeekdayClose();
        strArr[0] = "평일 " + open + " ~ " + close;

        if(hospital.isSaturdayStatus()){
            open = hospital.getSaturdayOpen();
            close = hospital.getSaturdayClose();
            strArr[1] = "토요일 " + open + " ~ " + close;
        }

        if(hospital.isHolidayStatus()){
            open = hospital.getHolidayOpen();
            close = hospital.getHolidayClose();
            strArr[2] = "공휴일 " + open + " ~ " + close;
        }

        return strArr;
    }

    public void favoriteCheck(){
        for(String str : favorites){
            if(str.equals(hospital.getId())){
                isFavorite = true;
            }
        }
    }

    public void addFavoriteList(){
        favorites.add(hospital.getId());
        user.setFavorites(favorites);
        DocumentReference documentReference = db.collection(User.DB_NAME).document(user.getDocumentId());
        documentReference
                .update("favorites", user.getFavorites())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

    public void removeFavoriteList(){
        for(int i=0; i<favorites.size(); i++){
            if(favorites.get(i).equals(hospital.getId())){
                favorites.remove(i);
            }
        }
        user.setFavorites(favorites);

        DocumentReference documentReference = db.collection(User.DB_NAME).document(user.getDocumentId());
        documentReference
                .update("favorites", user.getFavorites())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }
}