package com.example.sample3.CatFile.select.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sample3.CatFile.edit.activity.CatEditActivity;
import com.example.sample3.CatFile.select.fragment.Fragment_CatList;
import com.example.sample3.R;
import com.example.sample3.db.DatabaseHelper;

public class CatSelectActivity extends AppCompatActivity {

    private int edit;

    //****************************************** onCreate() ***********************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cat_select_activity);

        //ファイルがない時作成する
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.DefaultCat();

        //リスナー
        Button makebutton = findViewById(R.id.button_cat_make);
        makebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatSelectActivity.this, CatEditActivity.class);
                startActivity(intent);
            }
        });

        //Fragment
        edit = getIntent().getIntExtra("edit",0);
        ShowCatListFragment();
    }

    //リスト更新
    @Override
    protected void onResume() {
        super.onResume();

        ShowCatListFragment();

    }

    private void ShowCatListFragment (){
        Fragment_CatList fragment = new Fragment_CatList();

        Bundle args = new Bundle();
        args.putInt("edit",edit);
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_CatSelect_List, fragment)
                .commit();
    }

    // Fragment から呼び出す
    public void returnResult(long catId) {
        Intent data = new Intent();
        data.putExtra("cat_id", catId);
        setResult(RESULT_OK, data);
        finish(); // 元の画面へ戻る
    }

}
