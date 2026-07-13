package com.example.sample3.db;

import android.provider.BaseColumns;

// データベースのテーブル名・項目名を定義
public final class DBContract {

    // 誤ってインスタンス化しないようにコンストラクタをプライベート宣言
    private DBContract() {
    }

    // テーブルの内容を定義
    public static class MemoEntry implements BaseColumns {
        // BaseColumns インターフェースを実装することで、内部クラスは_IDを継承できる
        public static final String TABLE_NAME = "memoTbl";
        public static final String _ID = "memo_id";
        public static final String COLUMN_NAME_CONTENTS = "memo_text";
        public static final String FILE_ID = "file_id";
        public static final String COLUMN_NAME_UPDATE = "up_date";
    }

    public static class TodoEntry {
        public static final String TABLE_NAME = "todoTbl";
        public static final String _ID = "todo_id";
        public static final String COLUMN_NAME_TITLE = "todo_text";
        public static final String COLUMN_NAME_DONE = "todo_done"; // 完了フラグ 0:未完了, 1:完了
        public static final String FILE_ID = "file_id";
        public static final String COLUMN_NAME_UPDATE = "up_date";

    }

    public static class CatEntry {
        public static final String TABLE_NAME = "catTbl";
        public static final String _ID = "cat_id";
        public static final String COLUMN_NAME_TITLE = "cat_name";
        public static final String COLUMN_NAME_UPDATE = "up_date";

    }

    public static class FileEntry {

        public static final String TABLE_NAME = "fileTbl";
        public static final String _ID = "file_id";
        public static final String COLUMN_NAME_TITLE = "file_name";
        public static final String COLUMN_NAME_UPDATE = "up_date";

    }

}



