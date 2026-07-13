package com.example.sample3.memo.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sample3.R;
import com.example.sample3.db.DatabaseHelper;
import com.example.sample3.memo.edit.activity.MemoEditActivity;
import com.example.sample3.memo.home.adapter.MemoAdapter;
import com.example.sample3.db.model.Memo;
import java.util.List;

public class MemoHomeListFragment extends Fragment {

    private RecyclerView recyclerView;
    private MemoAdapter adapter;
    private List<Memo> memoList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.memohome_list_fragment, container, false);

        //レイアウト内の RecyclerView を取得してグリッド表示にするため2列に設定
        recyclerView = view.findViewById(R.id.recyclerView_TodoHome);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        //SQLiteからメモを取得する
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        memoList = dbHelper.getAllMemos();


        //RecyclerView にアダプターをセット → 各メモが画面に表示される。
        adapter = new MemoAdapter(memoList,

            // 削除処理
                // 削除ボタン
                memo -> {
                    // SharedPreferencesの保存IDをクリア
                    // 確認ダイアログを表示
                    new AlertDialog.Builder(requireContext())
                            .setTitle("削除確認")
                            .setMessage("このメモを削除してもよろしいですか？")
                            .setNegativeButton("はい", (dialog, which) -> {
                                // DBから削除
                                dbHelper.deleteMemoById(memo.getId());

                                // SharedPreferencesの保存IDをクリア
                                SharedPreferences prefs = requireContext().getSharedPreferences("MemoPrefs", Context.MODE_PRIVATE);
                                long savedId = prefs.getLong("saved_memo_id", -1);
                                if (savedId == memo.getId()) {
                                    prefs.edit().putLong("saved_memo_id", -1).apply();
                                }

                                // RecyclerView 更新
                                loadMemos();
                            })
                            .setPositiveButton("いいえ", (dialog, which) -> {
                                dialog.dismiss(); // キャンセル
                            })
                            .show();
                },

                // メモをタップしたらデータをセットして編集画面へ
                memo -> {
                    Intent intent = new Intent(getActivity(), MemoEditActivity.class);
                    intent.putExtra("memo_id", memo.getId());
                    intent.putExtra("memo_title", memo.getTitle());
                    intent.putExtra("memo_content", memo.getContent());
                    startActivity(intent);
                }

        );


        recyclerView.setAdapter(adapter);

        // 初回読み込み
        loadMemos();

        return view;
    }

    //SQLite からメモを再読み込みして RecyclerView を更新
    public void loadMemos() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        List<Memo> newList = dbHelper.getAllMemos();

        memoList.clear();
        memoList.addAll(newList);
        adapter.notifyDataSetChanged();
    }

}