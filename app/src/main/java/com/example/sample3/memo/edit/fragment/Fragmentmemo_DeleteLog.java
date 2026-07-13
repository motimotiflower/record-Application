package com.example.sample3.memo.edit.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.sample3.R;
import com.example.sample3.db.DatabaseHelper;
import com.example.sample3.memo.home.activity.MemoHomeActivity;
import com.example.sample3.memo.home.fragment.MemoHomeListFragment;

public class Fragmentmemo_DeleteLog extends DialogFragment {

    //***********メンバ*************
    private static final String PREFS_NAME = "MemoPrefs";
    private static final String KEY_MEMO_ID = "saved_memo_id";
    private long memoId = -1; // 削除対象ID

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // 引数から編集中のIDを取得
        if (getArguments() != null) {
            memoId = getArguments().getLong("memo_id", -1);
            Log.d("DeleteLog", "削除対象ID=" + memoId);
        }

        return new AlertDialog.Builder(requireContext())
                .setTitle("確認")
                .setMessage("このメモを削除しますか？")

                //****************** はいが押されたとき *******************
                .setNegativeButton("はい", (dialog, which) -> {
                    Log.d("Fragment_log", "はいが押されました");

                    if (memoId != -1) {

                        //削除処理
                        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                        int deletedRows = dbHelper.deleteMemoById(memoId);
                        Log.d("Fragmentmemo_DeleteLog", "削除ID=" + memoId + ", 削除行数=" + deletedRows);

                        Toast.makeText(requireContext(), "メモを削除しました", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("Fragmentmemo_DeleteLog", "削除対象IDが無効です");
                    }

                    // RecyclerView 一覧を更新
                        MemoHomeListFragment listFragment = (MemoHomeListFragment)
                                requireActivity()
                                        .getSupportFragmentManager()
                                        .findFragmentById(R.id.fragment_container_memohome1);

                        if (listFragment != null) {
                            listFragment.loadMemos();
                        }

                        // Toastで削除完了通知
                        Toast.makeText(requireContext(), "メモを削除しました", Toast.LENGTH_SHORT).show();

                        // MemoHomeActivity に遷移
                        Intent intent = new Intent(requireActivity(), MemoHomeActivity.class);
                        startActivity(intent);

                        // 現在のアクティビティを終了（オプション）
                        requireActivity().finish();

                })

                //**************いいえが押されたとき********************
                .setPositiveButton("いいえ", (dialog, which) -> {
                    Log.d("Fragmentmemo_DeleteLog", "削除キャンセル");
                    dialog.dismiss();
                })
                .create();
    }
}
