package com.gradproject.hospi.home.mypage;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.Inquiry;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;

public class InquiryDetailFragment extends Fragment implements OnBackPressedListener {
    Inquiry inquiry;
    FirebaseFirestore db;
    SettingActivity settingActivity;

    Toolbar toolbar;
    ImageButton backBtn;
    TextView hospitalNameTxt, titleTxt, contentTxt, answerTxt;

    int pos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingActivity = (SettingActivity) getActivity();
        db = FirebaseFirestore.getInstance();
        if(getArguments()!=null){
            inquiry = (Inquiry) getArguments().getSerializable("inquiry");
            pos = getArguments().getInt("pos", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_inquiry_detail, container,false);

        toolbar = rootView.findViewById(R.id.toolbar);
        hospitalNameTxt = rootView.findViewById(R.id.hospitalNameTxt);
        titleTxt = rootView.findViewById(R.id.titleTxt);
        contentTxt = rootView.findViewById(R.id.contentTxt);
        answerTxt = rootView.findViewById(R.id.answerTxt);
        backBtn = rootView.findViewById(R.id.backBtn);

        setHasOptionsMenu(true);
        settingActivity.setSupportActionBar(toolbar);
        settingActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);

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

        return rootView;
    }

    @Override
    public void onBackPressed() {
        settingActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.settingContainer, settingActivity.inquiryListFragment).commit();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_inquiry, menu);
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
        if(!(inquiry.isCheckedAnswer())){
            FragmentTransaction transaction = ((SettingActivity)getActivity()).getSupportFragmentManager().beginTransaction();
            InquiryEditFragment inquiryEditFragment = new InquiryEditFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("inquiry", inquiry);
            inquiryEditFragment.setArguments(bundle);
            transaction.replace(R.id.settingContainer, inquiryEditFragment);
            transaction.commit();
        }else{
            String msg = "답변 완료된 문의는 수정하실 수 없습니다.";
            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        }
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
                        String msg = "삭제에 실패하였습니다.\n잠시 후 다시 진행해주세요.";
                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void deletePopUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("삭제되었습니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int i) {
                        FragmentTransaction transaction = settingActivity.getSupportFragmentManager().beginTransaction();
                        InquiryListFragment inquiryListFragment = settingActivity.inquiryListFragment;
                        Bundle bundle = new Bundle();
                        bundle.putInt("pos", pos);
                        inquiryListFragment.setArguments(bundle);
                        transaction.replace(R.id.settingContainer, inquiryListFragment);
                        transaction.commit();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}