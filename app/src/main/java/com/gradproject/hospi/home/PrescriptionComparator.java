package com.gradproject.hospi.home;

import java.util.Comparator;

public class PrescriptionComparator implements Comparator<Prescription> {
    @Override
    public int compare(Prescription a, Prescription b){
        if(a.getTimestamp()>b.getTimestamp()) return -1;
        if(a.getTimestamp()<b.getTimestamp()) return 1;
        return 0;
    }
}
