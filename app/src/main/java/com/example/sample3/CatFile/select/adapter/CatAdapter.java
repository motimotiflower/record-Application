package com.example.sample3.CatFile.select.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sample3.R;
import com.example.sample3.db.model.Cat;

import java.util.List;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.ViewHolder> {
    private Context context;
    private List<Cat> catList;
    private OnItemClickListener listener;

    // --- クリックイベント用インターフェース ---
    public interface OnItemClickListener {
        void onItemClick(Cat cat);
        void onDeleteClick(Cat cat);
    }

    // --- コンストラクタ ---
    public CatAdapter(Context context, List<Cat> catList, OnItemClickListener listener) {
        this.context = context;
        this.catList = catList;
        this.listener = listener;
    }

    //何件分のデータを作るか
    @Override
    public int getItemCount() {
        return catList != null ? catList.size() : 0;
    }

    //表示される３件分のビューを作る
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.catfile_item, parent, false);
        return new ViewHolder(view);
    }

    //ビューに指定されたデータを入れる(入れ替える)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cat cat = catList.get(position);
        holder.textViewTitle.setText(cat.getTitle());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(cat);
        });

        // 削除ボタン
        holder.imageButtonDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(cat);
        });
    }


    //ビューを保持するクラス
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        ImageButton imageButtonDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text);
            imageButtonDelete = itemView.findViewById(R.id.delete_catfile);
        }
    }

    //データを更新したときにリサイクルビューを再描画する
    public void updateList(List<Cat> newList) {
        catList = newList;
        notifyDataSetChanged();
    }
}
