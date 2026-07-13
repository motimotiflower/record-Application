package com.example.sample3.Main;

//画面遷移のインポート
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sample3.CatFile.home.activity.CatHomeActivity;
import com.example.sample3.R;
import com.example.sample3.Todo.home.activity.TodoHomeActivity;
import com.example.sample3.Todo.home.fragment.TodoHomeListFragment;
import com.example.sample3.db.DatabaseHelper;
import com.example.sample3.memo.home.activity.MemoHomeActivity;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper _helper = null; //Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home_activity);

        //画面遷移
        setupButton();

        //タスク表示
        TodoHomeListFragment  fragment = new TodoHomeListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_Main_todo, fragment)
                .commit();


        //DatabaseHelperオブジェクトの生成
        _helper = new DatabaseHelper(MainActivity.this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    @Override
    protected void onDestroy() {
        //DatabaseHelperオブジェクトの解散
        _helper.close();
        super.onDestroy();
    }

    private void setupButton(){

        //memo_home(メモ一覧画面)へ
        Button memolistButton = findViewById(R.id.memo_list);
        memolistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MemoHomeActivity.class);
                startActivity(intent);
            }
        });

        //cat_home(カテゴリホーム画面)へ
        Button memoeditButton = findViewById(R.id.record);
        memoeditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CatHomeActivity.class);
                startActivity(intent);
            }
        });

        //todo_list(タスクホーム画面)へ
        Button todolistButton = findViewById(R.id.todo_list);
        todolistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TodoHomeActivity.class);
                startActivity(intent);
            }
        });

    }





}