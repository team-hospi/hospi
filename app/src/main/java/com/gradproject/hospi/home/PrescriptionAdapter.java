package com.gradproject.hospi.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.ViewHolder>{
    private static final String TAG = "PrescriptionAdapter";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ArrayList<Prescription> items = new ArrayList<>();

    public void addItem(Prescription item){
        items.add(item);
    }

    public void setItems(ArrayList<Prescription> items){
        this.items = items;
    }

    public Prescription getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, Prescription item){
        items.set(position, item);
    }

    @NonNull
    @Override
    public PrescriptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.treatment_item, parent, false);

        return new PrescriptionAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionAdapter.ViewHolder holder, int position) {
        Prescription item = items.get(position);
        holder.setItem(item);
        holder.prescriptionInfoBtn.setTag(holder.getAdapterPosition());
        holder.prescriptionInfoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PrescriptionInfoPopUpActivity.class);
            intent.putExtra("prescription", item);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        final String week[] = {"일", "월", "화", "수", "목", "금", "토"};

        TextView hospitalNameTxt, treatmentDateTxt, departmentTxt, opinionTxt;
        LinearLayout prescriptionInfoBtn;

        public ViewHolder(View itemView){
            super(itemView);
            hospitalNameTxt = itemView.findViewById(R.id.hospitalNameTxt);
            treatmentDateTxt = itemView.findViewById(R.id.treatmentDateTxt);
            departmentTxt = itemView.findViewById(R.id.departmentTxt);
            opinionTxt = itemView.findViewById(R.id.opinionTxt);
            prescriptionInfoBtn = itemView.findViewById(R.id.prescriptionInfoBtn);
        }

        public void setItem(Prescription item){
            hospitalNameTxt.setText(item.getHospitalName());

            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            long timestamp = item.getTimestamp();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);
            String date = sdfDate.format(timestamp) + " (" + week[cal.get(Calendar.DAY_OF_WEEK)-1] + ") " + sdfTime.format(timestamp);

            treatmentDateTxt.setText(date);
            departmentTxt.setText(item.getDepartment());
            opinionTxt.setText(item.getOpinion());
        }
    }
}
