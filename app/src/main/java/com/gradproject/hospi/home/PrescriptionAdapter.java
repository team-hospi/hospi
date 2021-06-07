package com.gradproject.hospi.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.gradproject.hospi.databinding.TreatmentItemBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.ViewHolder>{

    public ArrayList<Prescription> items = new ArrayList<>();

    public void addItem(Prescription item){
        items.add(item);
    }

    public void setItems(ArrayList<Prescription> items){
        this.items = items;
    }

    @SuppressWarnings("unused")
    public Prescription getItem(int position){
        return items.get(position);
    }

    @SuppressWarnings("unused")
    public void setItem(int position, Prescription item){
        items.set(position, item);
    }

    @NonNull
    @Override
    public PrescriptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TreatmentItemBinding binding = TreatmentItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);

        return new PrescriptionAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionAdapter.ViewHolder holder, int position) {
        Prescription item = items.get(position);
        holder.setItem(item);
        holder.binding.prescriptionInfoBtn.setTag(holder.getAdapterPosition());
        holder.binding.prescriptionInfoBtn.setOnClickListener(v -> {
            if(item.getMedicine() != null){
                Intent intent = new Intent(v.getContext(), PrescriptionInfoPopUpActivity.class);
                intent.putExtra("prescription", item);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }else{
                notMedicineAlert(v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void notMedicineAlert(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext())
                .setCancelable(true)
                .setMessage("처방된 의약품이 존재하지 않습니다.")
                .setPositiveButton("확인", (dialogInterface, i) -> {});
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        final String[] week = {"일", "월", "화", "수", "목", "금", "토"};

        TreatmentItemBinding binding;

        public ViewHolder(TreatmentItemBinding binding){
            super(binding.getRoot());

            this.binding = binding;
        }

        public void setItem(Prescription item){
            binding.hospitalNameTxt.setText(item.getHospitalName());

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            long timestamp = item.getTimestamp();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);
            String date = sdfDate.format(timestamp) + " (" + week[cal.get(Calendar.DAY_OF_WEEK)-1] + ") " + sdfTime.format(timestamp);

            binding.treatmentDateTxt.setText(date);
            binding.departmentTxt.setText(item.getDepartment());
            binding.opinionTxt.setText(item.getOpinion());
        }
    }
}
