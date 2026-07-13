package com.example.sample3.CatFile.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class CatHomeMemoListFragment extends Fragment {

    //メンバ
    private static  final String ARG_FILE_ID = "file_id";
    private long FileId;

    private RecyclerView recyclerView;
    private MemoAdapter adapter;
    private List<Memo> memoList;

    public static CatHomeMemoListFragment newInstance(long FileId){
        CatHomeMemoListFragment fragment = new CatHomeMemoListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_FILE_ID, FileId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            FileId = getArguments().getLong(ARG_FILE_ID);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cat_home_memo_list_fragment, container, false);

        //レイアウト内の RecyclerView を取得してグリッド表示にするため3列に設定
        recyclerView = view.findViewById(R.id.cat_home_memo_RecycleView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        //************ SQLiteからメモを取得する ***************
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        //テキストからファイルの文字を読み込む
        TextView textViewCat_catHome = getActivity().findViewById(R.id.textViewFile_catHome);
        String FileName = textViewCat_catHome.getText().toString();

        //file_id読み込み
        FileId = dbHelper.getFileIdByFileName(FileName);

        memoList = dbHelper.getMemoByFileId(FileId);

        //RecyclerView にアダプターをセット → 各メモが画面に表示される。***********
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
        List<Memo> newList = dbHelper.getMemoByFileId(FileId);

        memoList.clear();
        memoList.addAll(newList);
        adapter.notifyDataSetChanged();
    }

}