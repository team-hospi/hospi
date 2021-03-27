package com.gradproject.hospi.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gradproject.hospi.BackPressHandler;
import com.gradproject.hospi.LoginActivity;
import com.gradproject.hospi.R;
import com.gradproject.hospi.User;

public class HomeActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener{
    private BackPressHandler backPressHandler = new BackPressHandler(this);

    SearchFragment searchFragment;
    HistoryFragment historyFragment;
    MyPageFragment myPageFragment;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        searchFragment = new SearchFragment();
        historyFragment = new HistoryFragment();
        myPageFragment = new MyPageFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, searchFragment).commit();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        onAuthStateChanged(firebaseAuth);
    }

    @Override
    public void onBackPressed() {
        backPressHandler.onBackPressed();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            db.collection(User.DB_NAME)
                    .whereEqualTo("email", firebaseUser.getEmail()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    setUserInfo(document.getId());
                                }
                            } else {
                                Log.d("DB", "Error getting documents: ", task.getException());
                            }
                        }
                    });

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
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                user.setDocumentId(documentId);
                Log.d("success", "유저 정보 받기 성공");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                final String msg = "알 수 없는 오류로 인해 유저 정보를 받아오지 못했습니다.";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}