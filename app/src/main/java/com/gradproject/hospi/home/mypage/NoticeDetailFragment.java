package com.gradproject.hospi.home.mypage;

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

import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.Inquiry;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;

import static com.gradproject.hospi.home.HomeActivity.user;

public class NoticeDetailFragment extends Fragment implements OnBackPressedListener {
    private static final String TAG = "NoticeDetailFragment";

    Notice notice;
    FirebaseFirestore db;
    SettingActivity settingActivity;

    Toolbar toolbar;
    ImageButton backBtn;
    TextView titleTxt, contentTxt;

    int pos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingActivity = (SettingActivity) getActivity();
        db = FirebaseFirestore.getInstance();
        if(getArguments()!=null){
            notice = (Notice) getArguments().getSerializable("notice");
            pos = getArguments().getInt("pos", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_notice_detail, container,false);

        toolbar = rootView.findViewById(R.id.toolbar);
        titleTxt = rootView.findViewById(R.id.titleTxt);
        contentTxt = rootView.findViewById(R.id.contentTxt);
        backBtn = rootView.findViewById(R.id.backBtn);

        setHasOptionsMenu(true);
        settingActivity.setSupportActionBar(toolbar);
        settingActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        backBtn.setOnClickListener(v -> onBackPressed());

        titleTxt.setText(notice.getTitle());
        contentTxt.setText(notice.getContent());

        return rootView;
    }

    @Override
    public void onBackPressed() {
        settingActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.settingContainer, settingActivity.noticeFragment).commit();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(user.isAdmin()){
            inflater.inflate(R.menu.menu_notice, menu);
        }
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
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        NoticeEditFragment noticeEditFragment = new NoticeEditFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("notice", notice);
        noticeEditFragment.setArguments(bundle);
        transaction.replace(R.id.settingContainer, noticeEditFragment);
        transaction.commit();
    }

    public void delBtnProcess(){
        db.collection(Notice.DB_NAME).document(notice.getDocumentId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    deleteSuccessPopUp();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting document", e);
                    String msg = "삭제에 실패하였습니다.\n잠시 후 다시 진행해주세요.";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                });
    }

    public void deletePopUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("해당 공지사항을 삭제하시겠습니까?")
                .setPositiveButton("확인", (dialog, i) -> delBtnProcess())
                .setNegativeButton("취소", (dialog, which) -> { /* empty */ });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteSuccessPopUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("삭제되었습니다.")
                .setPositiveButton("확인", (dialog, i) -> {
                    FragmentTransaction transaction = settingActivity.getSupportFragmentManager().beginTransaction();
                    NoticeFragment noticeFragment = settingActivity.noticeFragment;
                    Bundle bundle = new Bundle();
                    bundle.putInt("pos", pos);
                    noticeFragment.setArguments(bundle);
                    transaction.replace(R.id.settingContainer, noticeFragment);
                    transaction.commit();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}