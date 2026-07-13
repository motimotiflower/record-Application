package com.example.sample3.CatFile.edit.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sample3.R;

public class Fragment_TextView_File extends Fragment {
    private EditText editTextTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.file_edit_textview_fragment, container, false);
        editTextTitle = view.findViewById(R.id.FileEditText_input);

        //Activityから渡された値をここで取得
        if (getArguments() != null) {
            String title = getArguments().getString("title", "");
            editTextTitle.setText(title);
        }

        return view;
    }


    //Fragmentから文字を読み込んで返す
    public String getInputTitle() {
        if (editTextTitle != null) {
            return editTextTitle.getText().toString().trim();
        }
        return "";
    }

    // 必要なら、編集用に初期値を設定するメソッド
    public void setFile(String title) {
        if (editTextTitle != null) editTextTitle.setText(title);
    }

}
