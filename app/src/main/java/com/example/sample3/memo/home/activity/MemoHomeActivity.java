package com.example.sample3.memo.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sample3.memo.home.fragment.MemoHomeListFragment;
import com.example.sample3.R;
import com.example.sample3.memo.edit.activity.MemoEditActivity;

public class MemoHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_home_activity);

        //新しいメモボタン押したら移動
        Button changeButton = findViewById(R.id.listmemo_make);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemoHomeActivity.this, MemoEditActivity.class);
                startActivity(intent);
            }
        });

        //memo_list(fragment)を表示
        MemoHomeListFragment fragment = new MemoHomeListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_memohome1, fragment)
                .commit();
    }

    //リスト更新
    @Override
    protected void onResume() {
        super.onResume();

        MemoHomeListFragment fragment = (MemoHomeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container_memohome1);

        if (fragment != null) {
            fragment.loadMemos(); //リストを更新
        }
    }

}
