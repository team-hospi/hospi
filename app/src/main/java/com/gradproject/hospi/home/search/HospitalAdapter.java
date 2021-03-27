package com.gradproject.hospi.home.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gradproject.hospi.R;

import java.util.ArrayList;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.ViewHolder>
                            implements OnHospitalItemClickListener {
    public ArrayList<Hospital> items = new ArrayList<Hospital>();
    OnHospitalItemClickListener listener;

    public void addItem(Hospital item){
        items.add(item);
    }

    public void setItems(ArrayList<Hospital> items){
        this.items = items;
    }

    public Hospital getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, Hospital item){
        items.set(position, item);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.hospital_item, parent, false);

        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hospital item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(OnHospitalItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if(listener != null){
            listener.onItemClick(holder, view, position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView hospitalName, weekdayBusinessHours, saturdayBusinessHours;
        TextView holidayBusinessHours, addressTxt;

        public ViewHolder(View itemView, final OnHospitalItemClickListener listener){
            super(itemView);

            hospitalName = itemView.findViewById(R.id.hospitalName);
            weekdayBusinessHours = itemView.findViewById(R.id.weekdayBusinessHours);
            saturdayBusinessHours = itemView.findViewById(R.id.saturdayBusinessHours);
            holidayBusinessHours = itemView.findViewById(R.id.holidayBusinessHours);
            addressTxt = itemView.findViewById(R.id.addressTxt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(listener != null){
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
        }

        public void setItem(Hospital item){
            hospitalName.setText(item.getName());
            if(item.isStatus()){
                weekdayBusinessHours.setText("평일 " + item.getWeekdayOpen() + " ~ " + item.getWeekdayClose());
            }
            if(item.isSaturdayStatus()){
                saturdayBusinessHours.setText("토요일 " + item.getSaturdayOpen() + " ~ " + item.getSaturdayClose());
            }
            if(item.isHolidayStatus()){
                holidayBusinessHours.setText("공휴일 " + item.getHolidayOpen() + " ~ " + item.getHolidayClose());
            }
            addressTxt.setText(item.getAddress());
        }

    }
}
