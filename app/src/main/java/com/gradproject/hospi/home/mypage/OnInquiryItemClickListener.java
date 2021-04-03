package com.gradproject.hospi.home.mypage;

import android.view.View;

import com.gradproject.hospi.home.search.HospitalAdapter;

public interface OnInquiryItemClickListener {
    void onItemClick(InquiryAdapter.ViewHolder holder, View view, int position);
}
