package com.gradproject.hospi.home;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gradproject.hospi.R;
import com.gradproject.hospi.home.hospital.Reservation;

import java.util.ArrayList;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder>
        implements OnReservationItemClickListener {
    public ArrayList<Reservation> items = new ArrayList<>();
    OnReservationItemClickListener listener;

    public void addItem(Reservation item){
        items.add(item);
    }

    public void setItems(ArrayList<Reservation> items){
        this.items = items;
    }

    public Reservation getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, Reservation item){
        items.set(position, item);
    }

    @NonNull
    @Override
    public ReservationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.reservation_item, parent, false);

        return new ReservationAdapter.ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationAdapter.ViewHolder holder, int position) {
        Reservation item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(OnReservationItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onItemClick(ReservationAdapter.ViewHolder holder, View view, int position) {
        if(listener != null){
            listener.onItemClick(holder, view, position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout hospitalInfoBtn;
        TextView hospitalNameTxt, reservationDateTxt, reservationStatusTxt;
        Button reservationCancelBtn;

        public ViewHolder(View itemView, final OnReservationItemClickListener listener){
            super(itemView);

            hospitalInfoBtn = itemView.findViewById(R.id.hospitalInfoBtn);
            hospitalNameTxt = itemView.findViewById(R.id.hospitalNameTxt);
            reservationDateTxt = itemView.findViewById(R.id.reservationDateTxt);
            reservationStatusTxt = itemView.findViewById(R.id.reservationStatusTxt);
            reservationCancelBtn = itemView.findViewById(R.id.reservationCancelBtn);

            hospitalInfoBtn.setOnClickListener(v -> {
                int position = getAdapterPosition();

                if(listener != null){
                    listener.onItemClick(ReservationAdapter.ViewHolder.this, v, position);
                }
            });

            reservationCancelBtn.setOnClickListener(v -> {
                //TODO: 예약 취소 구현
            });
        }

        public void setItem(Reservation item){
            hospitalNameTxt.setText(getHospitalName(item.getHospitalId()));
            String date = item.getReservationDate();
            String time = item.getReservationTime();
            //TODO: 요일 설정 해야함
            reservationDateTxt.setText(date + " (" + getDayOfWeek(1) + ") " + time);

            switch (item.getReservationStatus()){
                case Reservation.RESERVATION_CONFIRMED:
                    reservationStatusTxt.setText("예약 확정");
                    reservationStatusTxt.setTextColor(Color.BLUE);
                    break;
                case Reservation.CONFIRMING_RESERVATION:
                    reservationStatusTxt.setText("예약 확인 중");
                    reservationStatusTxt.setTextColor(Color.GREEN);
                    break;
                default:
                    reservationStatusTxt.setText("예약 취소됨");
                    reservationStatusTxt.setTextColor(Color.RED);
                    break;
            }
        }

        public String getHospitalName(String id){
            //TODO: 병원 이름 구하기
            return "병원이름";
        }

        public String getDayOfWeek(int date) {
            switch (date) {
                case 1:
                    return "일";
                case 2:
                    return "월";
                case 3:
                    return "화";
                case 4:
                    return "수";
                case 5:
                    return "목";
                case 6:
                    return "금";
                case 7:
                    return "토";
                default:
                    return null;
            }
        }
    }
}
