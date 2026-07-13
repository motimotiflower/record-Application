package com.example.sample3.CatFile.edit.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sample3.CatFile.edit.fragment.Fragment_TextView_Cat;
import com.example.sample3.R;
import com.example.sample3.db.DatabaseHelper;
import com.example.sample3.db.model.Cat;

public class CatEditActivity extends AppCompatActivity {

    //*************** メンバ ***************
    private DatabaseHelper dbHelper;
    private long savedCatId = -1; // 編集中のメモID（フィールド化）
    private Toast currentToast; //toastの連続表示からのエラーを防ぐための変数
    private Fragment_TextView_Cat fragment;

    //****************************************** onCreate() ***********************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cat_edit_activity);
        dbHelper = new DatabaseHelper(this);

        //前画面から渡されたIntentのデータをここで受け取る
        Intent intent = getIntent();
        savedCatId = getIntent().getLongExtra("cat_id", -1);

        if (savedInstanceState == null) {
            fragment = new Fragment_TextView_Cat(); // クラス変数に代入
        }

        //--- 編集モードなら既存データを読み込む ---
        if (savedCatId != -1) {
            // DBから指定IDのファイルを取得
            Cat cat = dbHelper.getCatById(savedCatId);
            if (cat != null) {
                //Fragmentに渡す値をここでBundleのargsに入れる
                Bundle args = new Bundle();
                args.putString("title", cat.getTitle());
                fragment.setArguments(args);
            }
        }

        //TextViewのFragmentの表示
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_cat_textview, fragment)
                .commit();

        // 決定ボタンでタスクを保存（上書き or 新規）
        Button decideButton = findViewById(R.id.button_cat_edit_decide);
        decideButton.setOnClickListener(v -> {

            // 連打防止
            decideButton.setEnabled(false);//ボタンの無効化
            decideButton.postDelayed(() -> decideButton.setEnabled(true), 800);

            Fragment_TextView_Cat fragment = (Fragment_TextView_Cat) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container_cat_textview);


            if (fragment != null) {

                // Fragment からタイトルや完了フラグを取得
                String catTitle = fragment.getInputTitle();
                String toastMessage;

                //空白または空文字チェックを追加
                if (catTitle == null || catTitle.trim().isEmpty()) {
                    if (currentToast != null) currentToast.cancel();
                    currentToast = Toast.makeText(this, "タイトルを入力してください", Toast.LENGTH_SHORT);
                    currentToast.show();
                    return; // 保存処理を中断
                }

                //新規作成・更新の判断
                if (savedCatId == -1) {
                    // 新規作成
                    Cat cat = new Cat(0, catTitle); //ここでのIDは仮でOK
                    long newId = dbHelper.insertCat(cat);

                    if (newId != -1) {
                        savedCatId = newId; // DB が決めたIDを保持
                        toastMessage = "新規作成しました (ID: " + savedCatId + ")";
                    } else {
                        toastMessage = "新規作成に失敗しました";
                    }

                } else {
                    // 更新
                    Cat cat = new Cat(savedCatId, catTitle); // ID をセットして更新
                    int count = dbHelper.updateCat(cat);
                    toastMessage = (count > 0) ? "更新しました (ID: " + savedCatId + ")" : "更新に失敗しました";
                }

                // SharedPreferences に保存
                SharedPreferences prefs = getSharedPreferences("catPrefs", MODE_PRIVATE);


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
