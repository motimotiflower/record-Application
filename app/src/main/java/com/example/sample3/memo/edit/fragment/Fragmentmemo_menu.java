package com.example.sample3.memo.edit.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Button;
import androidx.fragment.app.Fragment;

import com.example.sample3.R;

public class Fragmentmemo_menu extends Fragment {
    private long targetMemoId = -1; // 編集中のメモID

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.memo_menu_fragment, container, false);


         //*****************新規作成*******************
         //ボタンの取得
         Button createbutton = view.findViewById(R.id.menu_fragment_buttonB);
         createbutton.setOnClickListener(v -> {

             //ダイアログの表示
             Fragmentmemo_MakeLog dialog = new Fragmentmemo_MakeLog();
             dialog.setCancelable(false); // 外タップで閉じない
             dialog.show(getParentFragmentManager(), "MakeLog");

         });


         //*************** 削除 *****************
         // ボタンを取得
         Button deleteButton = view.findViewById(R.id.menu_fragment_buttonA);
         // クリックリスナーを設定
         deleteButton.setOnClickListener(v -> {
             Log.d("Fragment_menu", "削除");

             // SharedPreferences から取得する場合
             targetMemoId = requireActivity()
                     .getSharedPreferences("MemoPrefs", getContext().MODE_PRIVATE)
                     .getLong("saved_memo_id", -1);

             if (targetMemoId == -1) {
                 Log.e("Fragmentmemo_menu", "削除対象IDがありません");
                 return;
             }

             // 確認ダイアログを表示
             Fragmentmemo_DeleteLog dialog = new Fragmentmemo_DeleteLog();
             Bundle args = new Bundle();
             args.putLong("memo_id", targetMemoId); // ここでIDを渡す
             dialog.setArguments(args);

             dialog.setCancelable(false); // 外タップで閉じない
             dialog.show(getParentFragmentManager(), "FragmentLog");
         });

         return view;
     }

}