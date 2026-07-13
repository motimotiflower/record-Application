package com.example.sample3.CatFile.home.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sample3.R;
import com.example.sample3.db.model.Memo;

import java.util.List;

public class CatHomeMemoAdapter extends RecyclerView.Adapter<CatHomeMemoAdapter.ViewHolder> {

    private List<Memo> memoList;

    public CatHomeMemoAdapter(List<Memo> memoList) {
        this.memoList = memoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.memo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Memo memo = memoList.get(position);
        holder.textView.setText(memo.getTitle());
    }

    @Override
    public int getItemCount() {
        return memoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.memoContent);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }
    }
}
