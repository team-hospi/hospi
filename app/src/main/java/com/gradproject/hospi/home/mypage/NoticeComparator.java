package com.gradproject.hospi.home.mypage;

import java.util.Comparator;

public class NoticeComparator implements Comparator<Notice> {
    @Override
    public int compare(Notice a, Notice b){
        return Long.compare(b.getTimestamp(), a.getTimestamp());
    }
}
