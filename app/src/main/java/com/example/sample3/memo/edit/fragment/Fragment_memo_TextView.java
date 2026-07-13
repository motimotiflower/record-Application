package com.example.sample3.memo.edit.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.sample3.R;

//フラグメントクラスの継承
public class Fragment_memo_TextView extends Fragment {

    //メンバ
    private EditText editTextInput;

    // Fragmentで表示するViewを作成するメソッド
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.memoedit_fragment_b, container, false);

        // EditText を取得
        editTextInput = view.findViewById(R.id.editText_input);

        // onCreateView の時点で arguments(外部からFragmentに値を渡すための引数) にデータがあればセット
        if (getArguments() != null) {
            String content = getArguments().getString("memo_content", "");
            editTextInput.setText(content);
        }

        // fragment_b_memoeditのレイアウトをここでViewとして作成
        return view;
    }

    // Activity から直接セットする場合のメソッド
    public void setInputText(String text) {
        if (editTextInput != null) {
            editTextInput.setText(text);
        }
    }

    //入力文字の取得するためのメソッド
    public String getInputText() {
        return editTextInput.getText().toString();
    }

}

