package com.gradproject.hospi.home.mypage;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gradproject.hospi.databinding.NoticeItemBinding;

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

    @SuppressWarnings("unused")
    public void setItem(int position, Notice item){
        items.set(position, item);
    }

    @NonNull
    @Override
    public NoticeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NoticeItemBinding binding = NoticeItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);

        return new NoticeAdapter.ViewHolder(binding, this);
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
        NoticeItemBinding binding;

        public ViewHolder(NoticeItemBinding binding, final OnNoticeItemClickListener listener){
            super(binding.getRoot());

            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();

                if(listener != null){
                    listener.onItemClick(ViewHolder.this, v, position);
                }
            });
        }

        public void setItem(Notice item){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new Date(item.getTimestamp()));

            binding.dateTxt.setText(date);
            binding.titleTxt.setText(item.getTitle());
            binding.contentTxt.setText(item.getContent());
        }
    }
}
