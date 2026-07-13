package com.example.sample3.Todo.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sample3.Todo.home.fragment.TodoHomeListFragment;
import com.example.sample3.R;
import com.example.sample3.Todo.edit.activity.TodoEditActivity;

public class TodoHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_home_activity);

        //新しいメモボタン押したら移動
        Button changeButton = findViewById(R.id.listtodo_make);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TodoHomeActivity.this, TodoEditActivity.class);
                startActivity(intent);
            }
        });

        // Fragment 表示
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_todohome1, new TodoHomeListFragment())
                .commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        TodoHomeListFragment fragment = (TodoHomeListFragment)
                getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container_todohome1);

        if (fragment != null) {
            fragment.refreshList(); // DBから再取得して表示
        }
    }

}
