package com.gradproject.hospi.home.mypage;

import java.util.Comparator;

public class NoticeComparator implements Comparator<Notice> {
    @Override
    public int compare(Notice a, Notice b){
        if(a.getTimestamp()>b.getTimestamp()) return -1;
        if(a.getTimestamp()<b.getTimestamp()) return 1;
        return 0;
    }
}
