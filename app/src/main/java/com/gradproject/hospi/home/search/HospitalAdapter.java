package com.gradproject.hospi.home.search;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gradproject.hospi.databinding.HospitalItemBinding;

import java.util.ArrayList;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.ViewHolder>
                            implements OnHospitalItemClickListener {
    public ArrayList<Hospital> items = new ArrayList<>();
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

    @SuppressWarnings("unused")
    public void setItem(int position, Hospital item){
        items.set(position, item);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HospitalItemBinding binding = HospitalItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding, this);
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
        HospitalItemBinding binding;

        public ViewHolder(HospitalItemBinding binding, final OnHospitalItemClickListener listener){
            super(binding.getRoot());

            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();

                if(listener != null){
                    listener.onItemClick(ViewHolder.this, v, position);
                }
            });
        }

        @SuppressLint("SetTextI18n")
        public void setItem(Hospital item){
            binding.hospitalName.setText(item.getName());
            if(item.isStatus()){
                binding.weekdayBusinessHours.setText("평일 " + item.getWeekdayOpen() + " ~ " + item.getWeekdayClose());
            }
            if(item.isSaturdayStatus()){
                binding.saturdayBusinessHours.setText("토요일 " + item.getSaturdayOpen() + " ~ " + item.getSaturdayClose());
            }
            if(item.isHolidayStatus()){
                binding.holidayBusinessHours.setText("공휴일 " + item.getHolidayOpen() + " ~ " + item.getHolidayClose());
            }
            binding.addressTxt.setText(item.getAddress());
        }

    }
}
