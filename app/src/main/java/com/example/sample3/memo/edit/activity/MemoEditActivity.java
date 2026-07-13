package com.example.sample3.memo.edit.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.sample3.db.DatabaseHelper;
import com.example.sample3.memo.edit.fragment.Fragment_memo_TextView;
import com.example.sample3.memo.edit.fragment.Fragmentmemo_menu;
import com.example.sample3.R;
import com.example.sample3.db.model.Memo;

public class MemoEditActivity extends AppCompatActivity {

    //*************** メンバ ***************

    private boolean isFragmentVisible = false; // 状態を管理する変数
    private Toast currentToast; //toastの連続表示からのエラーを防ぐための変数

    //DBに使用
    private static final String PREFS_NAME = "MemoPrefs";
    private static final String KEY_MEMO_ID = "saved_memo_id";


    private long savedMemoId = -1; // 編集中のメモID（フィールド化）
    private long currentFileId = -1; //メモが属するファイルのID

    private DatabaseHelper dbHelper;


    //****************************************** onCreate() ***********************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_edit_activity);

        dbHelper = new DatabaseHelper(this);

        //前画面から渡されたIntentのデータをここで受け取る
        Intent intent = getIntent();
        savedMemoId = intent.getLongExtra("memo_id", -1);
        String memoContent = intent.getStringExtra("memo_content");
        currentFileId = intent.getLongExtra("file_id", -1);

        //この画面を開いたときメモのIDの確認
        Log.i("テスト","ID: " + savedMemoId );

        //FragmentBの表示
        if (savedInstanceState == null) {
            Fragment_memo_TextView fragment = new Fragment_memo_TextView();

            // Bundleでデータを渡す
            Bundle args = new Bundle();
            args.putString("memo_content", memoContent);
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_memo1, fragment)
                    .commit();
        }

        //********************************
        // 決定ボタンでメモを保存（上書き or 新規）
        Button decideButton = findViewById(R.id.button_memoedit_decide);
        decideButton.setOnClickListener(v -> {

            // 連打防止：一時的にボタンを無効化
            decideButton.setEnabled(false);
            decideButton.postDelayed(() -> decideButton.setEnabled(true), 800);

            Fragment_memo_TextView fragment = (Fragment_memo_TextView) getSupportFragmentManager().findFragmentById(R.id.fragment_container_memo1);

            if (fragment != null) {

                //fragmentから文字を取得
                String inputText = fragment.getInputText();
                String toastMessage;

                if (savedMemoId == -1) {
                    // 新規作成
                    Memo memo = new Memo();
                    memo.setContent(inputText);
                    memo.setFileId(currentFileId);

                    long newRowId = dbHelper.insertMemo(memo);

                    if (newRowId != -1) {
                        savedMemoId = newRowId; // DB が自動割り振りした ID を使用
                        toastMessage = "新規作成しました (ID: " + newRowId + ")";

                    } else {
                        toastMessage = "新規作成に失敗しました";
                    }

                } else {
                    // 編集
                    Memo memo = new Memo();
                    memo.setId(savedMemoId);
                    memo.setContent(inputText);
                    memo.setFileId(currentFileId); //更新時もfile_idを保持

                    int count = dbHelper.updateMemo(memo);
                    toastMessage = (count > 0)
                            ? "更新しました (ID: " + savedMemoId + ")"
                            : "更新に失敗しました";
                }

                // 新規作成 or 編集後にIDをSharedPreferencesに保存
                SharedPreferences prefs = getSharedPreferences("MemoPrefs", MODE_PRIVATE);
                prefs.edit().putLong(KEY_MEMO_ID, savedMemoId).apply();

                // 前の Toast をキャンセルしてから新しく表示
                if (currentToast != null) currentToast.cancel();
                currentToast = Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT);
                currentToast.show();

            }

        });

        //********************************
        //imageButtonを押したとき、フラグメントを見て状況に応じて表示する
        ImageButton toggleFragmentButton = findViewById(R.id.imageButton_memoedit);
        toggleFragmentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container_memo2);

                if (currentFragment == null) {
                    // 表示：フラグメントを追加
                    fragmentManager.beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragment_container_memo2, Fragmentmemo_menu.class, null)
                            .commit();
                    isFragmentVisible = true;
                } else {
                    // 非表示：フラグメントを削除
                    fragmentManager.beginTransaction()
                            .remove(currentFragment)
                            .commit();
                    isFragmentVisible = false;
                }
            }

        });

    }
    //*************************************** onCreate()おわり **********************************************


}