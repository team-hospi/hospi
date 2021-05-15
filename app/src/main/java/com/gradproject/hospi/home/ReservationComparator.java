package com.gradproject.hospi.home;

import com.gradproject.hospi.home.hospital.Reservation;

import java.util.Comparator;

public class ReservationComparator implements Comparator<Reservation> {
    @Override
    public int compare(Reservation a, Reservation b){
        if(a.getTimestamp()>b.getTimestamp()) return -1;
        if(a.getTimestamp()<b.getTimestamp()) return 1;
        return 0;
    }
}
