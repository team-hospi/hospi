package com.gradproject.hospi.home.mypage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gradproject.hospi.OnBackPressedListener;
import com.gradproject.hospi.R;

import static com.gradproject.hospi.home.HomeActivity.user;

public class NoticeFragment extends Fragment implements OnBackPressedListener {
    ImageButton backBtn;
    FloatingActionButton writeBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_notice, container,false);

        backBtn = rootView.findViewById(R.id.backBtn);
        writeBtn = rootView.findViewById(R.id.writeBtn);

        if(user.isAdmin()){
            writeBtn.setVisibility(View.VISIBLE);
        }

        backBtn.setOnClickListener(v -> onBackPressed());

        writeBtn.setOnClickListener(v -> {
            // TODO: 공지 글쓰기 버튼
        });

        return rootView;
    }

    @Override
    public void onBackPressed() {
        getActivity().finish();
    }
}