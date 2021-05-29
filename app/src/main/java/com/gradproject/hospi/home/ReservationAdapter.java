package com.gradproject.hospi.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.databinding.ReservationItemBinding;
import com.gradproject.hospi.home.hospital.HospitalActivity;
import com.gradproject.hospi.home.hospital.Reservation;
import com.gradproject.hospi.home.hospital.Reserved;
import com.gradproject.hospi.home.search.Hospital;
import com.gradproject.hospi.utils.DateTimeFormat;
import com.gradproject.hospi.utils.Loading;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@SuppressWarnings("unused")
public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder>{
    private static final String TAG = "ReservationAdapter";

    final String cancelComment = "예약자에 의한 취소";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Loading loading;

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
        ReservationItemBinding binding = ReservationItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        loading = new Loading(binding.getRoot().getContext());

        return new ReservationAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationAdapter.ViewHolder holder, int position) {
        Reservation item = items.get(position);
        holder.setItem(item);
        holder.binding.reservationCancelBtn.setTag(holder.getAdapterPosition());
        holder.binding.reservationCancelBtn.setOnClickListener(v -> reservationCancelDialog(v, item));
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
        loading.show();
        Query query = db.collection(Reservation.DB_NAME)
                .whereEqualTo("id", item.getId())
                .whereEqualTo("hospitalId", item.getHospitalId())
                .whereEqualTo("timestamp", item.getTimestamp());

        query.get().addOnCompleteListener(task -> {
            String id = null;
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
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
                                loading.dismiss();
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
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    reserved = document.toObject(Reserved.class);
                    documentId = document.getId();
                }

                if(reserved != null){
                    HashMap<String, List<String>> tmpMap = (HashMap<String, List<String>>) reserved.getReservedMap();
                    if(tmpMap.containsKey(item.getReservationDate())){
                        ArrayList<String> tmpList = (ArrayList<String>) tmpMap.get(item.getReservationDate());
                        int size = 0;
                        if (tmpList != null) {
                            size = tmpList.size();
                        }
                        for(int i=0; i<size; i++){
                            if(tmpList.get(i).equals(item.getReservationTime())){
                                tmpList.remove(i);
                                i--;
                                size--;
                            }
                        }

                        tmpMap.put(item.getReservationDate(), tmpList);

                        DocumentReference documentReference = db.collection(Reserved.DB_NAME).document(documentId);
                        documentReference
                                .update("reservedMap", tmpMap)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    cancelSuccess(v);
                                })
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Error updating document", e);
                                    loading.dismiss();
                                });
                    }
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private void cancelSuccess(View v){
        loading.dismiss();
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
        ReservationItemBinding binding;
        Loading loading;

        public ViewHolder(ReservationItemBinding binding){
            super(binding.getRoot());
            this.binding = binding;
            loading = new Loading(binding.getRoot().getContext());
        }

        @SuppressLint("SetTextI18n")
        public void setItem(Reservation item){
            binding.hospitalNameTxt.setText(item.getHospitalName());

            if(item.getCancelComment() != null){
                binding.cancelCommentTxt.setText(item.getCancelComment());
            }

            String[] date = item.getReservationDate().split("-");
            String time = item.getReservationTime();

            LocalDate lDate = LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
            binding.reservationDateTxt.setText(lDate.format(DateTimeFormat.date())
                    + " ("
                    + lDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREA)
                    + ") "
                    + time);

            switch (item.getReservationStatus()){
                case Reservation.RESERVATION_CONFIRMED:
                    binding.reservationStatusTxt.setText("예약 확정");
                    binding.reservationStatusTxt.setTextColor(Color.BLUE);
                    binding.costLayout.setVisibility(View.VISIBLE);
                    binding.cost.setText(item.getPredictCost());
                    break;
                case Reservation.CONFIRMING_RESERVATION:
                    binding.reservationStatusTxt.setText("확인 중");
                    binding.reservationStatusTxt.setTextColor(Color.rgb(70, 201, 0));
                    binding.costLayout.setVisibility(View.GONE);
                    break;
                case Reservation.TREATMENT_COMPLETE:
                    binding.reservationStatusTxt.setText("진료 완료");
                    binding.reservationStatusTxt.setTextColor(Color.rgb(0, 131, 26));
                    binding.reservationCancelBtn.setVisibility(View.GONE);
                    binding.reserveInfo.setVisibility(View.GONE);
                    binding.costLayout.setVisibility(View.GONE);
                    break;
                default:
                    binding.reservationStatusTxt.setText("취소됨");
                    binding.reservationStatusTxt.setTextColor(Color.RED);
                    binding.reserveInfo.setVisibility(View.GONE);
                    binding.cancelInfo.setVisibility(View.VISIBLE);
                    binding.reservationCancelBtn.setVisibility(View.GONE);
                    binding.costLayout.setVisibility(View.GONE);
                    break;
            }

            binding.hospitalInfoBtn.setOnClickListener(v -> showHospitalInfo(v, item.getHospitalId()));
        }

        public void showHospitalInfo(View v, String id){
            loading.show();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Hospital.DB_NAME)
                    .whereEqualTo("id", id)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Hospital hospital = null;
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                hospital = document.toObject(Hospital.class);
                            }

                            if(hospital != null){
                                Intent intent = new Intent(v.getContext(), HospitalActivity.class);
                                intent.putExtra("hospital", hospital);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                v.getContext().startActivity(intent);
                            }

                            loading.dismiss();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }
    }
}
