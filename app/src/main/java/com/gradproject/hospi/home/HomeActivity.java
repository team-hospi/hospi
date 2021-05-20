package com.gradproject.hospi.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gradproject.hospi.BackPressHandler;
import com.gradproject.hospi.LoginActivity;
import com.gradproject.hospi.R;
import com.gradproject.hospi.User;
import com.gradproject.hospi.databinding.ActivityHomeBinding;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener{
    private static final String TAG = "HomeActivity";

    private final BackPressHandler backPressHandler = new BackPressHandler(this);

    SearchFragment searchFragment;
    HistoryFragment historyFragment;
    MyPageFragment myPageFragment;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    public static User user;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.gradproject.hospi.databinding.ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        searchFragment = new SearchFragment();
        historyFragment = new HistoryFragment();
        myPageFragment = new MyPageFragment();

        onAuthStateChanged(firebaseAuth);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, searchFragment).commit();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.search:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, searchFragment).commit();
                    return true;
                case R.id.history:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, historyFragment).commit();
                    return true;
                case R.id.mypage:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, myPageFragment).commit();
                    return true;
            }

            return false;
        });
    }

    @Override
    public void onBackPressed() {
        backPressHandler.onBackPressed();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            new Thread(() -> db.collection(User.DB_NAME)
                    .whereEqualTo("email", firebaseUser.getEmail()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                setUserInfo(document.getId());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    })).start();
        }else{
            final String msg = "로그인 정보가 존재하지 않습니다.";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signOut(); // 로그아웃
            ActivityCompat.finishAffinity(this); // 모든 액티비티 종료
            startActivity(new Intent(getApplicationContext(), LoginActivity.class)); // 다시 로그인 화면으로
        }
    }

    public void setUserInfo(String documentId){
        DocumentReference docRef = db.collection(User.DB_NAME).document(documentId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            user = documentSnapshot.toObject(User.class);
            if (user != null) {
                user.setDocumentId(documentId);
            }
            setUserToken();
            Log.d(TAG, "유저 정보 받기 성공");
        }).addOnFailureListener(e -> {
            final String msg = "알 수 없는 오류로 인해 유저 정보를 받아오지 못했습니다.";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        });
    }

    public void setUserToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    DocumentReference documentReference = db.collection(User.DB_NAME).document(user.getDocumentId());
                    documentReference
                            .update("token", token)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                            .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));

                    // Log and toast
                    Log.d(TAG, token);
                });
    }
}