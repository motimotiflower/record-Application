package com.example.sample3.CatFile.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import com.example.sample3.CatFile.home.fragment.CatHomeMemoListFragment;
//import com.example.sample3.CatFile.home.fragment.CatHomeTodoListFragment;
import com.example.sample3.CatFile.home.fragment.CatHomeTodoListFragment;
import com.example.sample3.CatFile.select.activity.CatSelectActivity;
import com.example.sample3.CatFile.select.activity.FileSelectActivity;
import com.example.sample3.R;
import com.example.sample3.Todo.edit.activity.TodoEditActivity;
import com.example.sample3.db.DatabaseHelper;
import com.example.sample3.memo.edit.activity.MemoEditActivity;
import com.example.sample3.db.model.Cat;
import com.example.sample3.db.model.File;

import java.util.List;

public class CatHomeActivity extends AppCompatActivity {

    //メンバ
    private long FileId = 1;
    private long CatId = 1;
    DatabaseHelper dbHelper;
    TextView textViewFile_catHome;
    TextView textViewCat_catHome;
    private ActivityResultLauncher<Intent> catSelectLauncher;
    private int CatSelect = 0;


    //****************************** onCreate ***********************************
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cat_home_activity);

        dbHelper= new DatabaseHelper(this);
        textViewFile_catHome = findViewById(R.id.textViewFile_catHome);
        textViewCat_catHome = findViewById(R.id.textViewCat_catHome);

        //ファイルやカテゴリがない時作成する
        dbHelper.DefaultFile();
        dbHelper.DefaultCat();

        updateFileDisplay();
        updateCatDisplay();

        setupButtons();                 //画面遷移ボタンの設定
        showMemoListFragment(FileId);   //メモのセット

    }


    //*********************** Buttonのリスナ設定 ***********************
    private void setupButtons(){

        //画面遷移
        //メモ編集画面へ
        ImageButton memoButton = findViewById(R.id.imageButton_cat_home_memo_make);
        memoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatHomeActivity.this, MemoEditActivity.class);
                intent.putExtra("file_id", FileId);
                startActivity(intent);
            }
        });

        //タスク
        ImageButton todoButton = findViewById(R.id.imageButton_cat_home_todo_make);
        todoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatHomeActivity.this, TodoEditActivity.class);
                intent.putExtra("file_id", FileId);
                startActivity(intent);
            }
        });

        //カテゴリ
        Button catButton = findViewById(R.id.cat_home_cat_select);
        catButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatHomeActivity.this, CatSelectActivity.class);
                startActivity(intent);
            }
        });

        //ファイル
        Button fileButton = findViewById(R.id.cat_home_file_select);
        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatHomeActivity.this, FileSelectActivity.class);
                startActivity(intent);
            }
        });

        //ファイル切り替え
        findViewById(R.id.RSelectButton).setOnClickListener(v -> {
            changeFile(+1);
        });

        findViewById(R.id.LSelectButton).setOnClickListener(v -> {
            changeFile(-1);
        });

        // ActivityResultLauncherの登録
        catSelectLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        long catId = result.getData().getLongExtra("cat_id", -1);

                        if (catId != -1) {
                            // 値を受け取って更新
                            updateCatDisplay(catId);
                        } else {
                            Log.w("CatSelect", "cat_idが見つかりません");
                        }

                    } else {
                        Log.w("CatSelect", "RESULT_OKでないか、dataがnullです");
                    }
                }
        );

        //カテゴリの切り替え
        findViewById(R.id.textViewCat_catHome).setOnClickListener(v -> {
            Intent intent = new Intent(CatHomeActivity.this,CatSelectActivity.class);
            intent.putExtra("edit",1);
            catSelectLauncher.launch(intent);
            CatSelect = 1;
        });


    }

    //************ ファイルの切り替え ***************
    private void changeFile(int offset) {
        long nearestId = dbHelper.getNearestFileId(FileId, offset);

        if (nearestId == -1) {
            Log.w("FileSwitch", "近いファイルが見つかりません");
            return;
        }

        File file = dbHelper.getFileById(nearestId);
        if (file == null) {
            Log.w("FileSwitch", "getFileById() でファイルが見つかりません ID=" + nearestId);
            return;
        }

        FileId = nearestId;
        updateFileDisplay();

        showMemoListFragment(FileId);
        showTodoListFragment(FileId);

        Log.i("テスト", "切り替え: 新しいFileId=" + FileId);
    }

    //************** ファイル名表示 *****************
    private void updateFileDisplay(){
        //ファイル名を取得
        String fileName = dbHelper.getFileNameById(FileId);
        // TextView に表示
        textViewFile_catHome.setText(fileName);
        Log.i("テスト","FileID = " + FileId + ":" + fileName );
    }

    //************** カテゴリ名表示 *****************

    //画面を開いたとき
    private void updateCatDisplay(){

        //存在するカテゴリを探してカテゴリ名表示
        List<Cat> catList = dbHelper.getAllCats();
        if (!catList.isEmpty()) {
            CatId = catList.get(0).getId();
        }

        //カテゴリ名を取得
        String catName = dbHelper.getCatNameById(CatId);
        // TextView に表示
        textViewCat_catHome.setText(catName);
        Log.i("テスト","CatID = " + CatId + ":" + catName );
    }

    //値を受け取ってセットする
    private void updateCatDisplay(long catId) {
        CatId = catId;
        String catName = dbHelper.getCatNameById(CatId);
        textViewCat_catHome.setText(catName);
        Log.i("テスト", "選択されたCatID = " + CatId + ":" + catName);
    }

    //****************** メモ表示 ******************
    private  void showMemoListFragment(long fileId){
        CatHomeMemoListFragment fragment = CatHomeMemoListFragment.newInstance(fileId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_catHome_memo, fragment)
                .commit();
    }

    //****************** タスク表示 ******************
    private  void showTodoListFragment(long fileId){
        CatHomeTodoListFragment fragment = CatHomeTodoListFragment.newInstance(fileId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_catHome_todo, fragment)
                .commit();
    }

    //リスト更新
    @Override
    protected void onResume() {
        super.onResume();

        //リスト再読み込み
        showMemoListFragment(FileId);
        showTodoListFragment(FileId);
    }


}
