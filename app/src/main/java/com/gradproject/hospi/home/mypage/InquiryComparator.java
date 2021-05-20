package com.gradproject.hospi.home.mypage;

import com.gradproject.hospi.Inquiry;

import java.util.Comparator;

public class InquiryComparator implements Comparator<Inquiry> {
    @Override
    public int compare(Inquiry a, Inquiry b){
        return Long.compare(b.getTimestamp(), a.getTimestamp());
    }
}
