package com.example.sample3.CatFile.edit.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sample3.R;
import com.example.sample3.db.DatabaseHelper;
import com.example.sample3.db.model.File;
import com.example.sample3.CatFile.edit.fragment.Fragment_TextView_File;

public class FileEditActivity extends AppCompatActivity {

    //*************** メンバ ***************
    private DatabaseHelper dbHelper;
    private long savedFileId = -1; // 編集中のメモID（フィールド化）
    private Toast currentToast; //toastの連続表示からのエラーを防ぐための変数
    private Fragment_TextView_File fragment;

    //****************************************** onCreate() ***********************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_edit_activity);
        dbHelper = new DatabaseHelper(this);

        //前画面から渡されたIntentのデータをここで受け取る
        Intent intent = getIntent();
        savedFileId = getIntent().getLongExtra("file_id", -1);

        if (savedInstanceState == null) {
            fragment = new Fragment_TextView_File(); // クラス変数に代入
        }

        //--- 編集モードなら既存データを読み込む ---
        if (savedFileId != -1) {
            // DBから指定IDのファイルを取得
            File file = dbHelper.getFileById(savedFileId);
            if (file != null) {
                //Fragmentに渡す値をここでBundleのargsに入れる
                Bundle args = new Bundle();
                args.putString("title", file.getTitle());
                fragment.setArguments(args);
            }
        }

        //TextViewのFragmentの表示
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_file_textview, fragment)
                .commit();

        // 決定ボタンでタスクを保存（上書き or 新規）
        Button decideButton = findViewById(R.id.button_file_edit_decide);
        decideButton.setOnClickListener(v -> {

            // 連打防止
            decideButton.setEnabled(false);//ボタンの無効化
            decideButton.postDelayed(() -> decideButton.setEnabled(true), 800);

            Fragment_TextView_File fragment = (Fragment_TextView_File) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container_file_textview);


            if (fragment != null) {

                // Fragment からタイトルや完了フラグを取得
                String fileTitle = fragment.getInputTitle();
                String toastMessage;

                //空白または空文字チェックを追加
                if (fileTitle == null || fileTitle.trim().isEmpty()) {
                    if (currentToast != null) currentToast.cancel();
                    currentToast = Toast.makeText(this, "タイトルを入力してください", Toast.LENGTH_SHORT);
                    currentToast.show();
                    return; // 保存処理を中断
                }

                //新規作成・更新の判断
                if (savedFileId == -1) {
                    // 新規作成
                    File file = new File(0, fileTitle); //ここでのIDは仮でOK
                    long newId = dbHelper.insertFile(file);

                    if (newId != -1) {
                        savedFileId = newId; // DB が決めたIDを保持
                        toastMessage = "新規作成しました (ID: " + savedFileId + ")";
                    } else {
                        toastMessage = "新規作成に失敗しました";
                    }

                } else {
                    // 更新
                    File file = new File(savedFileId, fileTitle); // ID をセットして更新
                    int count = dbHelper.updateFile(file);
                    toastMessage = (count > 0) ? "更新しました (ID: " + savedFileId + ")" : "更新に失敗しました";
                }

                // SharedPreferences に保存
                SharedPreferences prefs = getSharedPreferences("filePrefs", MODE_PRIVATE);
                prefs.edit().putLong("saved_file_id", savedFileId).apply();


                // 前の Toast をキャンセルしてから表示
                if (currentToast != null) currentToast.cancel();
                currentToast = Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT);
                currentToast.show();

                // 保存完了後、前画面に戻る（自動更新のため）
                setResult(RESULT_OK);
                finish();

            }
        });

    }
}
