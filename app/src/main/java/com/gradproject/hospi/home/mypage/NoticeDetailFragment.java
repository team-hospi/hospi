package com.gradproject.hospi.home.mypage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.databinding.FragmentNoticeDetailBinding;
import com.gradproject.hospi.utils.Loading;
import com.gradproject.hospi.utils.StatusBar;

import java.text.SimpleDateFormat;
import java.util.Objects;

import static com.gradproject.hospi.home.HomeActivity.user;

public class NoticeDetailFragment extends Fragment implements OnBackPressedListener {
    private static final String TAG = "NoticeDetailFragment";
    private FragmentNoticeDetailBinding binding;

    Loading loading;
    Notice notice;
    FirebaseFirestore db;
    SettingActivity settingActivity;
    int pos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loading = new Loading(getContext());
        settingActivity = (SettingActivity) getActivity();
        db = FirebaseFirestore.getInstance();
        if(getArguments()!=null){
            notice = (Notice) getArguments().getSerializable("notice");
            pos = getArguments().getInt("pos", -1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNoticeDetailBinding.inflate(inflater, container, false);

        StatusBar.updateStatusBarColor(requireActivity(), R.color.list_background);

        setHasOptionsMenu(true);
        settingActivity.setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(settingActivity.getSupportActionBar()).setDisplayShowTitleEnabled(false);

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd. HH:mm");

        binding.dateTxt.setText(sdf.format(notice.getTimestamp()));
        binding.titleTxt.setText(notice.getTitle());
        binding.contentTxt.setText(notice.getContent());

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        if(item.getItemId()==R.id.editBtn){
            editBtnProcess();
            return true;
        }else if(item.getItemId()==R.id.delBtn){
            deletePopUp();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    public void editBtnProcess(){
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        NoticeEditFragment noticeEditFragment = new NoticeEditFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("notice", notice);
        noticeEditFragment.setArguments(bundle);
        transaction.replace(R.id.settingContainer, noticeEditFragment);
        transaction.commit();
    }

    public void delBtnProcess(){
        loading.show();
        db.collection(Notice.DB_NAME).document(notice.getDocumentId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    deleteSuccessPopUp();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting document", e);
                    loading.dismiss();
                    String msg = "삭제에 실패하였습니다.\n잠시 후 다시 진행해주세요.";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                });
    }

    public void deletePopUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setCancelable(false)
                .setMessage("해당 공지사항을 삭제하시겠습니까?")
                .setPositiveButton("확인", (dialog, i) -> delBtnProcess())
                .setNegativeButton("취소", (dialog, which) -> { /* empty */ });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteSuccessPopUp(){
        loading.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
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