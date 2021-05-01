package com.gradproject.hospi.home.mypage;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gradproject.hospi.Inquiry;
import com.gradproject.hospi.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder>
        implements OnNoticeItemClickListener {
    ArrayList<Notice> items = new ArrayList<>();
    OnNoticeItemClickListener listener;

    public void addItem(Notice item){
        items.add(item);
    }

    public void setItems(ArrayList<Notice> items){
        this.items = items;
    }

    public Notice getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, Notice item){
        items.set(position, item);
    }

    @NonNull
    @Override
    public NoticeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.notice_item, parent, false);

        return new NoticeAdapter.ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeAdapter.ViewHolder holder, int position) {
        Notice item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(OnNoticeItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onItemClick(NoticeAdapter.ViewHolder holder, View view, int position) {
        if(listener != null){
            listener.onItemClick(holder, view, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt, contentTxt, dateTxt;

        public ViewHolder(View itemView, final OnNoticeItemClickListener listener){
            super(itemView);

            titleTxt = itemView.findViewById(R.id.titleTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            contentTxt = itemView.findViewById(R.id.contentTxt);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();

                if(listener != null){
                    listener.onItemClick(ViewHolder.this, v, position);
                }
            });
        }

        public void setItem(Notice item){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new Date(item.getTimestamp()));

            dateTxt.setText(date);
            titleTxt.setText(item.getTitle());
            contentTxt.setText(item.getContent());
        }
    }
}
