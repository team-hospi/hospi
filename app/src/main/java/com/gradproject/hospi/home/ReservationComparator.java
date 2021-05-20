package com.gradproject.hospi.home;

import com.gradproject.hospi.home.hospital.Reservation;

import java.util.Comparator;

public class ReservationComparator implements Comparator<Reservation> {
    @Override
    public int compare(Reservation a, Reservation b){
        return Long.compare(b.getTimestamp(), a.getTimestamp());
    }
}
