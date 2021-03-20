package com.gradproject.hospi.home.mypage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.Inquiry;
import com.gradproject.hospi.R;

public class InquiryDetailActivity extends AppCompatActivity {
    Inquiry inquiry;
    FirebaseFirestore db;

    Toolbar toolbar;
    ImageButton backBtn;
    TextView hospitalNameTxt, titleTxt, contentTxt, answerTxt;

    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_detail);

        db = FirebaseFirestore.getInstance();
        inquiry = (Inquiry) getIntent().getSerializableExtra("inquiry");
        pos = getIntent().getIntExtra("pos", -1);

        toolbar = findViewById(R.id.toolbar);
        hospitalNameTxt = findViewById(R.id.hospitalNameTxt);
        titleTxt = findViewById(R.id.titleTxt);
        contentTxt = findViewById(R.id.contentTxt);
        answerTxt = findViewById(R.id.answerTxt);
        backBtn = findViewById(R.id.backBtn);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        hospitalNameTxt.setText(inquiry.getHospital_name());
        titleTxt.setText(inquiry.getTitle());
        contentTxt.setText(inquiry.getContent());

        if(inquiry.getAnswer().equals("")){
            answerTxt.setText("아직 답변이 등록되지 않았습니다.");
        }else{
            answerTxt.setText(inquiry.getAnswer());
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inquiry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.editBtn:
                editBtnProcess();
                return true;
            case R.id.delBtn:
                deletePopUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void editBtnProcess(){
        // TODO: 문의 수정 버튼
    }

    public void delBtnProcess(){
        db.collection("inquiry_list").document(inquiry.getDocumentId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DB", "DocumentSnapshot successfully deleted!");
                        deleteSuccessPopUp();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DB", "Error deleting document", e);
                        Toast.makeText(getApplicationContext(), "삭제에 실패하였습니다.\n잠시 후 다시 진행해주세요.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void deletePopUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(InquiryDetailActivity.this)
                .setCancelable(false)
                .setMessage("해당 문의를 삭제하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int i) {
                        delBtnProcess();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { /* empty */ }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteSuccessPopUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(InquiryDetailActivity.this)
                .setCancelable(false)
                .setMessage("삭제되었습니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int i) {
                        Intent intent = new Intent();
                        intent.putExtra("pos", pos);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}