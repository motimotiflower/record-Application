package com.example.sample3.CatFile.select.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sample3.CatFile.edit.activity.FileEditActivity;
import com.example.sample3.CatFile.select.fragment.Fragment_CatList;
import com.example.sample3.CatFile.select.fragment.Fragment_FileList;
import com.example.sample3.R;
import com.example.sample3.db.DatabaseHelper;

public class FileSelectActivity extends AppCompatActivity {

    //****************************************** onCreate() ***********************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_select_activity);

        //ファイルがない時作成する
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.DefaultFile();

        //リスナー
        Button makebutton = findViewById(R.id.button_file_make);
        makebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FileSelectActivity.this, FileEditActivity.class);
                startActivity(intent);
            }
        });

        //Fragment
        ShowFileListFragment();
    }

    //リスト更新
    @Override
    protected void onResume() {
        super.onResume();

        ShowFileListFragment();

    }

    private void ShowFileListFragment (){
        Fragment_FileList fragment = new Fragment_FileList();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_FileSelect_List, fragment)
                .commit();
    }
}
