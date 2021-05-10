package com.gradproject.hospi.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.gradproject.hospi.R;

import java.util.ArrayList;

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
            // TODO: 처방 정보 팝업 intent 사용
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
            // TODO: 타임스탬프 날짜 변환 구현
            // treatmentDateTxt.setText();
            departmentTxt.setText(item.getDepartment());
            opinionTxt.setText(item.getOpinion());
        }
    }
}
