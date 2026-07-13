package com.example.sample3.CatFile.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sample3.R;
import com.example.sample3.Todo.edit.activity.TodoEditActivity;
import com.example.sample3.Todo.home.adapter.TodoAdapter;
import com.example.sample3.db.DatabaseHelper;
import com.example.sample3.db.model.Todo;

import java.util.List;

public class CatHomeTodoListFragment extends Fragment {

    //メンバ
    private static  final String ARG_FILE_ID = "file_id";
    private long FileId;

    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private List<Todo> todoList;
    private DatabaseHelper dbHelper;


    public static CatHomeTodoListFragment newInstance(long FileId){
        CatHomeTodoListFragment fragment = new CatHomeTodoListFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.cat_home_memo_list_fragment, container, false);

        recyclerView = view.findViewById(R.id.cat_home_memo_RecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DatabaseHelper(getContext());

        // ************ ファイル名から file_id を取得 ************
        TextView textViewCat_catHome = getActivity().findViewById(R.id.textViewFile_catHome);
        String FileName = textViewCat_catHome.getText().toString();
        FileId = dbHelper.getFileIdByFileName(FileName);


        // ************ SQLiteからタスクを取得 ************
        todoList = dbHelper.getTodoByFileId(FileId);


        //************** Adapter **************
        //Adapterに渡すのはtodolistとdbHelperとリスナー設定の3つ
        adapter = new TodoAdapter(
                todoList,
                dbHelper,
                // 編集処理（クリックで編集画面へ）
                todo -> {
                    Intent intent = new Intent(getActivity(), TodoEditActivity.class);
                    intent.putExtra("todo_id", todo.getId());
                    intent.putExtra("todo_title", todo.getTitle());
                    intent.putExtra("todo_done", todo.isDone());
                    startActivity(intent);
                }
        );

        recyclerView.setAdapter(adapter);
        loadTodos(); // 初期表示

        return view;
    }

    // DBからtodolistを読み込む
    public void loadTodos() {
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            List<Todo> newList = dbHelper.getTodoByFileId(FileId);
            todoList.clear();
            todoList.addAll(newList);
            adapter.notifyDataSetChanged();
        }

    // 外部からデータ更新したときに呼ぶ
    public void refreshList() {
        loadTodos();
    }

}
