package com.gradproject.hospi.home.mypage;

import com.gradproject.hospi.Inquiry;

import java.util.Comparator;

public class InquiryComparator implements Comparator<Inquiry> {
    @Override
    public int compare(Inquiry a, Inquiry b){
        if(a.getTimestamp()>b.getTimestamp()) return -1;
        if(a.getTimestamp()<b.getTimestamp()) return 1;
        return 0;
    }
}
