package com.example.sample3.CatFile.select.fragment;

import android.content.Intent;
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

import com.example.sample3.CatFile.edit.activity.FileEditActivity;
import com.example.sample3.CatFile.select.adapter.FileAdapter;
import com.example.sample3.R;
import com.example.sample3.db.DatabaseHelper;
import com.example.sample3.db.model.File;

import java.util.List;

public class Fragment_FileList extends Fragment {

    //変数
    private RecyclerView recyclerView;
    private FileAdapter adapter;
    private List<File> fileList;
    private DatabaseHelper dbHelper;

    //**** onCreateView *****************************************
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.file_select_list_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_FileSelect_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));

        dbHelper = new DatabaseHelper(getContext());
        fileList = loadFilesFromDB();

        //adapterに画面の状況、データ、タップ時のリスナーを渡す
        adapter = new FileAdapter(
                getContext(),
                fileList,
                new FileAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(File file) {
                        // アイテム全体クリック → 編集画面
                        Intent intent = new Intent(getContext(), FileEditActivity.class);
                        intent.putExtra("file_id", file.getId());
                        startActivity(intent);
                    }
                    @Override
                    public void onDeleteClick(File file) {

                        // ファイル件数チェック
                        if (fileList.size() <= 1) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("削除できません")
                                    .setMessage("ファイルは1件以上必要です。")
                                    .setPositiveButton("OK", null)
                                    .show();
                            return; // 削除中止
                        }

                        new AlertDialog.Builder(getContext())
                                .setTitle("削除の確認")
                                .setMessage("「" + file.getTitle() + "」を削除しますか？")

                                .setPositiveButton("削除", (dialog, which) -> {
                                    // 削除ボタンクリック → DBから削除して更新
                                    dbHelper.deleteFileById(file.getId());
                                    adapter.updateList(dbHelper.getAllFiles());
                                })

                                .setNegativeButton("キャンセル", (dialog, which) -> dialog.dismiss())
                                .show();
                }
                }

        );

        //アダプターから変換したデータを表示
        recyclerView.setAdapter(adapter);

        loadFilesFromDB(); // 初期表示

        return view;
    }
    //*********************************************************************

    @Override
    public void onResume() {
        super.onResume();
        adapter.updateList(dbHelper.getAllFiles());
    }

    private List<File> loadFilesFromDB() {
        return dbHelper.getAllFiles();
    }

}
