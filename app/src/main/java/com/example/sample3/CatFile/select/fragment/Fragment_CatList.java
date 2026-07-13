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

import com.example.sample3.CatFile.edit.activity.CatEditActivity;
import com.example.sample3.CatFile.select.activity.CatSelectActivity;
import com.example.sample3.CatFile.select.adapter.CatAdapter;
import com.example.sample3.R;
import com.example.sample3.db.DatabaseHelper;
import com.example.sample3.db.model.Cat;

import java.util.List;
import android.util.Log;

public class Fragment_CatList extends Fragment {

    //変数
    private RecyclerView recyclerView;
    private CatAdapter adapter;
    private List<Cat> catList;
    private DatabaseHelper dbHelper;

    //**** onCreateView *****************************************
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.cat_select_list_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_CatSelect_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));

        dbHelper = new DatabaseHelper(getContext());
        catList = loadCatsFromDB();

        //adapterに画面の状況、データ、タップ時のリスナーを渡す
        adapter = new CatAdapter(
                getContext(),
                catList,
                new CatAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Cat cat) {

                        int edit = getArguments().getInt("edit", 0);
                        Log.d("Fragment_CatList", "タップした catId = " + cat.getId());

                        //アイテムクリックでCatHomeに値を渡す
                        if( edit == 1 ) {
                            ((CatSelectActivity) requireActivity()).returnResult(cat.getId());
                        }
                        // アイテム全体クリック → 編集画面
                        else{
                            Intent intent = new Intent(getContext(), CatEditActivity.class);
                            intent.putExtra("cat_id", cat.getId());
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onDeleteClick(Cat cat) {

                        // ファイル件数チェック
                        if (catList.size() <= 1) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("削除できません")
                                    .setMessage("カテゴリーは1件以上必要です。")
                                    .setPositiveButton("OK", null)
                                    .show();
                            return; // 削除中止
                        }

                        new AlertDialog.Builder(getContext())
                                .setTitle("削除の確認")
                                .setMessage("「" + cat.getTitle() + "」を削除しますか？")

                                .setPositiveButton("削除", (dialog, which) -> {
                                    // 削除ボタンクリック → DBから削除して更新
                                    dbHelper.deleteCatById(cat.getId());
                                    adapter.updateList(dbHelper.getAllCats());
                                })

                                .setNegativeButton("キャンセル", (dialog, which) -> dialog.dismiss())
                                .show();
                }
                }

        );

        //アダプターから変換したデータを表示
        recyclerView.setAdapter(adapter);

        loadCatsFromDB(); // 初期表示

        return view;
    }
    //*********************************************************************

    @Override
    public void onResume() {
        super.onResume();
        adapter.updateList(dbHelper.getAllCats());
    }

    private List<Cat> loadCatsFromDB() {
        return dbHelper.getAllCats();
    }

}
