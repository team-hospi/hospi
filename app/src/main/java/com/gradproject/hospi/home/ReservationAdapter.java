package com.gradproject.hospi.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.R;
import com.gradproject.hospi.home.hospital.HospitalActivity;
import com.gradproject.hospi.home.hospital.Reservation;
import com.gradproject.hospi.home.hospital.Reserved;
import com.gradproject.hospi.home.search.Hospital;
import com.gradproject.hospi.utils.Loading;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder>{
    private static final String TAG = "ReservationAdapter";

    final String cancelComment = "예약자에 의한 취소";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ArrayList<Reservation> items = new ArrayList<>();

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

        return new ReservationAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationAdapter.ViewHolder holder, int position) {
        Reservation item = items.get(position);
        holder.setItem(item);
        holder.reservationCancelBtn.setTag(holder.getAdapterPosition());
        holder.reservationCancelBtn.setOnClickListener(v -> reservationCancelDialog(v, item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void reservationCancelDialog(View v, Reservation item){
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext())
                .setMessage("해당 예약을 취소하시겠습니까?")
                .setPositiveButton("예", (dialogInterface, i) -> reservationCancelProcess(v, item))
                .setNegativeButton("아니오", (dialog, which) -> {});
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void reservationCancelProcess(View v, Reservation item){
        Query query = db.collection(Reservation.DB_NAME)
                .whereEqualTo("id", item.getId())
                .whereEqualTo("hospitalId", item.getHospitalId())
                .whereEqualTo("timestamp", item.getTimestamp());

        query.get().addOnCompleteListener(task -> {
            String id = null;
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    id = document.getId();
                }

                if(id != null){
                    DocumentReference documentReference = db.collection(Reservation.DB_NAME).document(id);
                    documentReference
                            .update(
                                    "cancelComment", cancelComment,
                                    "reservationStatus", Reservation.RESERVATION_CANCELED
                            )
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                reservedDeleteProcess(v, item);
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "Error updating document", e);
                                final String msg = "알 수 없는 오류로 인해 취소되지 않았습니다.\n잠시 후 다시 시도해주세요.";
                                Toast.makeText(v.getContext(), msg, Toast.LENGTH_LONG).show();
                            });
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private void reservedDeleteProcess(View v, Reservation item){
        Query query = db.collection(Reserved.DB_NAME)
                .whereEqualTo("hospitalId", item.getHospitalId())
                .whereEqualTo("department", item.getDepartment());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Reserved reserved=null;
                String documentId = null;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    reserved = document.toObject(Reserved.class);
                    documentId = document.getId();
                }

                if(reserved != null && documentId != null){
                    HashMap<String, List<String>> tmpMap = (HashMap) reserved.getReservedMap();
                    if(tmpMap.containsKey(item.getReservationDate())){
                        ArrayList<String> tmpList = (ArrayList) tmpMap.get(item.getReservationDate());
                        for(int i=0; i<tmpList.size(); i++){
                            if(tmpList.get(i).equals(item.getReservationTime())){
                                tmpList.remove(i);
                            }
                        }

                        tmpMap.put(item.getReservationDate(), tmpList);

                        DocumentReference documentReference = db.collection(Reserved.DB_NAME).document(documentId);
                        documentReference
                                .update("reservedMap", tmpMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                        cancelSuccess(v);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });
                    }
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private void cancelSuccess(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext())
                .setCancelable(false)
                .setMessage("예약 취소가 정상적으로 처리되었습니다.")
                .setPositiveButton("확인", (dialogInterface, i) -> {
                    int pos = (int) v.getTag();
                    items.get(pos).setCancelComment(cancelComment);
                    items.get(pos).setReservationStatus(Reservation.RESERVATION_CANCELED);
                    notifyDataSetChanged();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        final String week[] = {"일", "월", "화", "수", "목", "금", "토"};

        ImageButton hospitalInfoBtn;
        TextView hospitalNameTxt, reservationDateTxt, reservationStatusTxt, cancelCommentTxt;
        Button reservationCancelBtn;
        LinearLayout reserveInfo, cancelInfo;

        public ViewHolder(View itemView){
            super(itemView);
            hospitalInfoBtn = itemView.findViewById(R.id.hospitalInfoBtn);
            hospitalNameTxt = itemView.findViewById(R.id.hospitalNameTxt);
            reservationDateTxt = itemView.findViewById(R.id.reservationDateTxt);
            reservationStatusTxt = itemView.findViewById(R.id.reservationStatusTxt);
            reservationCancelBtn = itemView.findViewById(R.id.reservationCancelBtn);
            reserveInfo = itemView.findViewById(R.id.reserveInfo);
            cancelInfo = itemView.findViewById(R.id.cancelInfo);
            cancelCommentTxt = itemView.findViewById(R.id.cancelCommentTxt);
        }

        public void setItem(Reservation item){
            hospitalNameTxt.setText(item.getHospitalName());
            if(item.getCancelComment() != null){
                cancelCommentTxt.setText(item.getCancelComment());
            }
            String date = item.getReservationDate();
            String time = item.getReservationTime();
            Calendar cal = Calendar.getInstance();
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));
            int day = Integer.parseInt(date.substring(8, 10));
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month-1);
            cal.set(Calendar.DATE, day);
            reservationDateTxt.setText(date + " (" + week[cal.get(Calendar.DAY_OF_WEEK)-1] + ") " + time);

            switch (item.getReservationStatus()){
                case Reservation.RESERVATION_CONFIRMED:
                    reservationStatusTxt.setText("예약 확정");
                    reservationStatusTxt.setTextColor(Color.BLUE);
                    break;
                case Reservation.CONFIRMING_RESERVATION:
                    reservationStatusTxt.setText("예약 확인 중");
                    reservationStatusTxt.setTextColor(Color.rgb(70, 201, 0));
                    break;
                default:
                    reservationStatusTxt.setText("예약 취소됨");
                    reservationStatusTxt.setTextColor(Color.RED);
                    reserveInfo.setVisibility(View.GONE);
                    cancelInfo.setVisibility(View.VISIBLE);
                    reservationCancelBtn.setVisibility(View.GONE);
                    break;
            }

            hospitalInfoBtn.setOnClickListener(v -> showHospitalInfo(v, item.getHospitalId()));
        }

        public void showHospitalInfo(View v, String id){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Hospital.DB_NAME)
                    .whereEqualTo("id", id)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Hospital hospital = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                hospital = document.toObject(Hospital.class);
                            }

                            if(hospital != null){
                                Intent intent = new Intent(v.getContext(), HospitalActivity.class);
                                intent.putExtra("hospital", hospital);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                v.getContext().startActivity(intent);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }
    }
}
