package com.gradproject.hospi.home.mypage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;
import com.gradproject.hospi.databinding.FragmentNoticeBinding;

import java.util.ArrayList;
import java.util.Objects;

import static com.gradproject.hospi.home.HomeActivity.user;

public class NoticeFragment extends Fragment implements OnBackPressedListener {
    private static final String TAG = "NoticeFragment";
    private FragmentNoticeBinding binding;

    LinearLayoutManager layoutManager;
    NoticeAdapter noticeAdapter = new NoticeAdapter();
    int pos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNoticeBinding.inflate(inflater, container, false);

        binding.noticeList.setLayoutManager(layoutManager);

        if(getArguments()!=null){
            pos = getArguments().getInt("pos", -1);
            if(pos!=-1){
                noticeAdapter.items.remove(pos);
                noticeAdapter.notifyItemRemoved(pos);
                noticeAdapter.notifyItemRangeChanged(pos, noticeAdapter.items.size());
            }

            if(getArguments().getBoolean("write", false)){
                noticeAdapter.notifyDataSetChanged();
            }
        }

        getNoticeList();

        if(user.isAdmin()){
            binding.writeBtn.setVisibility(View.VISIBLE);
        }

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        binding.writeBtn.setOnClickListener(v -> requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settingContainer, new NoticeWriteFragment())
                .commit());

        noticeAdapter.setOnItemClickListener((holder, view, position) -> {
            Notice notice = noticeAdapter.getItem(position);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            NoticeDetailFragment noticeDetailFragment = new NoticeDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            bundle.putSerializable("notice", notice);
            noticeDetailFragment.setArguments(bundle);
            transaction.replace(R.id.settingContainer, noticeDetailFragment);
            transaction.commit();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onBackPressed() {
        requireActivity().finish();
    }

    private void getNoticeList(){
        noticeAdapter.items.clear(); // 기존 항목 모두 삭제
        noticeAdapter.notifyDataSetChanged(); // 어댑터 갱신

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Notice.DB_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<Notice> tmpArrList = new ArrayList<>();

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Notice notice = document.toObject(Notice.class);
                            notice.setDocumentId(document.getId());
                            tmpArrList.add(notice);
                        }

                        tmpArrList.sort(new NoticeComparator());

                        for(int i=0; i<tmpArrList.size(); i++){
                            noticeAdapter.addItem(tmpArrList.get(i));
                        }

                        binding.noticeList.setAdapter(noticeAdapter);
                        binding.noticeList.setVisibility(View.VISIBLE);
                        binding.loadingLayout.setVisibility(View.GONE);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        String msg = "공지사항을 불러올 수 없습니다.";
                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}