package com.example.sample3.Todo.edit.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sample3.R;

public class Fragment_Textview_Todo extends Fragment {

    private EditText editTextTitle;
    private CheckBox checkBoxDone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.todo_edit_textview_fragment, container, false);

        editTextTitle = view.findViewById(R.id.CatEditText_input);
        //checkBoxDone = view.findViewById(R.id.checkbox_todo);

        return view;
    }

    // Activity からタイトルを取得するメソッド
    public String getInputTitle() {
        if (editTextTitle != null) {
            return editTextTitle.getText().toString().trim();
        }
        return "";
    }

    // Activity から完了フラグを取得するメソッド
    public boolean isDone() {
        if (checkBoxDone != null) {
            return checkBoxDone.isChecked();
        }
        return false;
    }

    // 必要なら、編集用に初期値を設定するメソッド
    public void setTodo(String title, boolean done) {
        if (editTextTitle != null) editTextTitle.setText(title);
        if (checkBoxDone != null) checkBoxDone.setChecked(done);
    }
}