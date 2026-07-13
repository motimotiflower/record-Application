package com.example.sample3.memo.home.adapter;

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

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {

    //削除ボタンが押されたときどれを削除すべきかFragmentやActivityに伝えるコールバック用のインターフェース
    public interface OnDeleteClickListener {
        void onDeleteClick(Memo memo);
    }

    public interface OnClickListener {
        void onClick(Memo memo);
    }

    private final List<Memo> memoList;
    private OnDeleteClickListener deleteClickListener;
    private OnClickListener clickListener;

    //コンストラクタで MemoAdapter にメモデータ(削除先も)を渡します。
    public MemoAdapter(List<Memo> memoList,OnDeleteClickListener dlistener,OnClickListener clistener) {
        this.memoList = memoList;
        this.deleteClickListener = dlistener;
        this.clickListener = clistener;

    }

    //メモ1つ分(memo_item)を読み込んで、ViewHolderに包む
    @NonNull
    @Override
    public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.memo_item, parent, false);
        return new MemoViewHolder(view);
    }

    //各アイテムに実際のデータをセットするメソッド
    @Override
    public void onBindViewHolder(@NonNull MemoViewHolder holder, int position) {
        Memo memo = memoList.get(position);
        holder.content.setText(memo.getContent());

        //削除ボタンにクリックリスナーをセット
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(memo);
            }
        });

        // アイテム全体のクリックで編集などを通知
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(memo);
            }
        });

    }


    //RecyclerView に「何件のアイテムを表示するか？」を教えるメソッド
    @Override
    public int getItemCount() {
        return memoList.size();
    }

    //再利用のため 各行の View（TextView）を保持する内部クラス
    static class MemoViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        ImageButton deleteButton;

        MemoViewHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.memoContent);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }
    }
}