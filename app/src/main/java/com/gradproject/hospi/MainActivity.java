package com.gradproject.hospi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gradproject.hospi.databinding.ActivityMainBinding;
import com.gradproject.hospi.home.HomeActivity;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener{
    private static final int MULTIPLE_PERMISSION = 10235;
    private final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.setBtn.setOnClickListener(arg0 -> {
            if (!hasPermissions(this, PERMISSIONS)) {//권한이 없는 경우
                //권한요청
                ActivityCompat.requestPermissions(this, PERMISSIONS, MULTIPLE_PERMISSION);
            } else {
                onAuthStateChanged(firebaseAuth);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (hasPermissions(this, PERMISSIONS)) {
            onAuthStateChanged(firebaseAuth);
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }else{
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        finish();
    }

    //권한이 있는지 확인하는 메소드 작성
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //권한 요청에 대한 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MULTIPLE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                /*..권한이 있는경우 실행할 코드....*/
                onAuthStateChanged(firebaseAuth);
            } else {
                // 하나라도 거부한다면.
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("앱 권한");
                alertDialog.setMessage("해당 앱의 원할한 기능을 이용하시려면 애플리케이션 [정보]>[권한] 에서 모든 권한을 허용해 주십시오.");
                // 권한설정 클릭시 이벤트 발생
                alertDialog.setPositiveButton("권한설정",
                        (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                            startActivity(intent);
                            dialog.cancel();
                        });
                //취소
                alertDialog.setNegativeButton("취소",
                        (dialog, which) -> dialog.cancel());
                alertDialog.show();
            }
        }
    }
}