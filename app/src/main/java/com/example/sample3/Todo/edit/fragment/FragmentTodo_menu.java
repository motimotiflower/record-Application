package com.example.sample3.Todo.edit.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

import com.example.sample3.R;

public class FragmentTodo_menu extends Fragment {
    private long targetTodoId = -1; // 編集中のメモID

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_menu_fragment, container, false);


        //*****************新規作成*******************
        //ボタンの取得
        Button createbutton = view.findViewById(R.id.menu_fragment_buttonB);
        createbutton.setOnClickListener(v -> {

            //ダイアログの表示
            FragmentTodo_MakeLog dialog = new FragmentTodo_MakeLog();
            dialog.setCancelable(false); // 外タップで閉じない
            dialog.show(getParentFragmentManager(), "MakeLog");

        });

        return view;
    }

}