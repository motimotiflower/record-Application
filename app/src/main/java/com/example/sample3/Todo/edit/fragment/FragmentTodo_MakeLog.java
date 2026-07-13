package com.example.sample3.Todo.edit.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.sample3.Todo.edit.activity.TodoEditActivity;

public class FragmentTodo_MakeLog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setTitle("確認")
                .setMessage("保存をしていなければ今のデータが失われますが、新規作成しますか？")
                .setNegativeButton("はい", (dialog, which) -> {
                    Log.d("FragmentTodo_NewDialog", "新規作成決定");

                    Intent intent = new Intent(requireActivity(), TodoEditActivity.class);
                    intent.putExtra("Todo_id", -1L);       // 新規作成
                    intent.putExtra("Todo_content", "");   // 空の内容
                    requireActivity().startActivity(intent);

                    requireActivity().finish();
                })
                .setPositiveButton("いいえ", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create();
    }
}