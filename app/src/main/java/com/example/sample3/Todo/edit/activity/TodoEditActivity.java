package com.example.sample3.Todo.edit.activity;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.example.sample3.Todo.home.fragment.TodoHomeListFragment;
import com.example.sample3.Todo.edit.fragment.Fragment_Textview_Todo;
import com.example.sample3.R;
import com.example.sample3.Todo.edit.fragment.FragmentTodo_menu;
import com.example.sample3.db.DatabaseHelper;
import com.example.sample3.db.model.Todo;

public class TodoEditActivity extends AppCompatActivity {

    //*************** メンバ ***************
    private DatabaseHelper dbHelper;
    private long savedTodoId = -1; // 編集中のメモID（フィールド化）
    private Toast currentToast; //toastの連続表示からのエラーを防ぐための変数
    private boolean isFragmentVisible = false; // ← これを追加

    private static final String KEY_TODO_ID = "saved_todo_id";
    private long currentFileId = -1; //メモが属するファイルのID

    private String initialTitle = "";
    private boolean initialDone = false;


    //****************************************** onCreate() ***********************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_edit_activity);
        dbHelper = new DatabaseHelper(this);

        //前画面から渡されたIntentのデータをここで受け取る
        Intent intent = getIntent();
        if (intent != null) {
            savedTodoId = intent.getLongExtra("todo_id", -1);
            initialTitle = intent.getStringExtra("todo_title");
            initialDone = intent.getBooleanExtra("todo_done", false);
            currentFileId = intent.getLongExtra("file_id", -1); //ファイルID
        }

        //TextViewのFragmentの表示
        if (savedInstanceState == null) {
            Fragment_Textview_Todo fragment = new Fragment_Textview_Todo();

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_todo1,fragment)
                    .commit();
        }



        // 決定ボタンでタスクを保存（上書き or 新規）
        Button decideTodoButton = findViewById(R.id.button_todoedit_decide);
        decideTodoButton.setOnClickListener(v -> {

            // 連打防止
            decideTodoButton.setEnabled(false);//ボタンの無効化
            decideTodoButton.postDelayed(() -> decideTodoButton.setEnabled(true), 800);

            Fragment_Textview_Todo fragment = (Fragment_Textview_Todo) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container_todo1);

            if (fragment != null) {

                // Fragment からタイトルや完了フラグを取得
                String todoTitle = fragment.getInputTitle();
                boolean todoDone = fragment.isDone();

                String toastMessage;

                //新規作成・更新の判断
                if (savedTodoId == -1) {
                    // 新規作成
                    Todo todo = new Todo(0, todoTitle, todoDone,currentFileId); //ここでのIDは仮でOK
                    long newId = dbHelper.insertTodo(todo);

                    if (newId != -1) {
                        savedTodoId = newId; // DB が決めたIDを保持
                        toastMessage = "新規作成しました (ID: " + savedTodoId + ")";

                    } else {
                        toastMessage = "新規作成に失敗しました";
                    }

                } else {
                    // 更新
                    Todo todo = new Todo(savedTodoId, todoTitle, todoDone,currentFileId); // ID をセットして更新
                    int count = dbHelper.updateTodo(todo);
                    toastMessage = (count > 0) ? "更新しました (ID: " + savedTodoId + ")" : "更新に失敗しました";
                }

                // SharedPreferences に保存
                SharedPreferences prefs = getSharedPreferences("TodoPrefs", MODE_PRIVATE);
                prefs.edit().putLong("saved_todo_id", savedTodoId).apply();


                // 前の Toast をキャンセルしてから表示
                if (currentToast != null) currentToast.cancel();
                currentToast = Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT);
                currentToast.show();
            }
        });

        //********************************
        //imageButtonを押したとき、フラグメントを見て状況に応じて表示する
        ImageButton toggleFragmentButton = findViewById(R.id.imageButton_todoedit);
        toggleFragmentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container_todo2);

                if (currentFragment == null) {
                    // 表示：フラグメントを追加
                    fragmentManager.beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragment_container_todo2, FragmentTodo_menu.class, null)
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


    @Override
    protected void onResume() {
        super.onResume();

        TodoHomeListFragment fragment = (TodoHomeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container_todohome1);

        if (fragment != null) {
            fragment.refreshList(); // DBの最新データでリストを更新
        }
    }

}