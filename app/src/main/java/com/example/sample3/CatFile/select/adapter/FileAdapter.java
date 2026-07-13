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
import com.example.sample3.db.model.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private Context context;
    private List<File> fileList;
    private OnItemClickListener listener;

    // --- クリックイベント用インターフェース ---
    public interface OnItemClickListener {
        void onItemClick(File file);
        void onDeleteClick(File file);
    }

    // --- コンストラクタ ---
    public FileAdapter(Context context, List<File> fileList, OnItemClickListener listener) {
        this.context = context;
        this.fileList = fileList;
        this.listener = listener;
    }

    //何件分のデータを作るか
    @Override
    public int getItemCount() {
        return fileList != null ? fileList.size() : 0;
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
        File file = fileList.get(position);
        holder.textViewTitle.setText(file.getTitle());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(file);
        });

        // 削除ボタン
        holder.imageButtonDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(file);
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
    public void updateList(List<File> newList) {
        fileList = newList;
        notifyDataSetChanged();
    }
}
