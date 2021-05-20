package com.gradproject.hospi.home;

import java.util.Comparator;

public class PrescriptionComparator implements Comparator<Prescription> {
    @Override
    public int compare(Prescription a, Prescription b){
        return Long.compare(b.getTimestamp(), a.getTimestamp());
    }
}
