package com.example.sample3.Todo.home.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sample3.R;
import com.example.sample3.Todo.home.adapter.TodoAdapter;
import com.example.sample3.db.DBContract;
import com.example.sample3.db.DatabaseHelper;
import com.example.sample3.db.model.Todo;

import java.util.ArrayList;
import java.util.List;

public class TodoHomeListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private List<Todo> todoList;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.todo_home_list_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_TodoHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DatabaseHelper(getContext());
        todoList = loadTodosFromDB();

        adapter = new TodoAdapter(todoList, dbHelper);
        recyclerView.setAdapter(adapter);

        loadTodosFromDB(); // 初期表示

        return view;
    }

    // DBからTodoリストを読み込む
    private List<Todo> loadTodosFromDB() {
        List<Todo> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DBContract.TodoEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DBContract.TodoEntry._ID + " DESC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.TodoEntry._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TodoEntry.COLUMN_NAME_TITLE));
                int done = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.TodoEntry.COLUMN_NAME_DONE));

                list.add(new Todo(id, title, done == 1));
            }
            cursor.close();
        }

        db.close();
        return list;
    }

    // 外部からデータ更新したときに呼ぶ
    public void refreshList() {
        loadTodosFromDB();
    }

    //SQLite からメモを再読み込みして RecyclerView を更新
    public void loadTodos() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        List<Todo> newList = dbHelper.getAllTodos();

        todoList.clear();
        todoList.addAll(newList);
        adapter.notifyDataSetChanged();
    }

}
