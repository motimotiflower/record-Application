package com.example.sample3.db;

import static com.example.sample3.db.DBContract.MemoEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sample3.db.model.Cat;
import com.example.sample3.db.model.File;
import com.example.sample3.db.model.Memo;
import com.example.sample3.db.model.Todo;

import java.util.ArrayList;
import java.util.List;

// データベースをアプリから使用するために、 SQLiteOpenHelperを継承する
// SQLiteOpenHelperは、データベースやテーブルが存在する場合はそれを開き、存在しない場合は作成してくれる
public class DatabaseHelper extends SQLiteOpenHelper {

    // データベースのバージョン
    // テーブルの内容などを変更したら、この数字を変更する
    static final private int VERSION = 12;

    // データベース名
    static final private String DBNAME = "app.db";

    // コンストラクタ
    public DatabaseHelper(Context context) {
        // 親クラスのコンストラクタを呼ぶ
        super(context, DBNAME, null, VERSION);
    }

    //SQL 定義
    @Override
    // データベース作成時にテーブルを作成
    public void onCreate(SQLiteDatabase db) {

        //**************** メモテーブル *************
        // テーブルを作成
        db.execSQL(
                "CREATE TABLE " + MemoEntry.TABLE_NAME + " (" +
                        MemoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MemoEntry.COLUMN_NAME_CONTENTS + " TEXT DEFAULT '', " +
                        MemoEntry.COLUMN_NAME_UPDATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +

                        // 外部キーを追加（カテゴリとの紐づけ）
                        "cat_id INTEGER, " +
                        "file_id INTEGER, " +

                        "FOREIGN KEY(cat_id) REFERENCES " + DBContract.CatEntry.TABLE_NAME +
                        "(" + DBContract.CatEntry._ID + ") " +
                        "ON DELETE CASCADE ON UPDATE CASCADE," +

                        "FOREIGN KEY(file_id) REFERENCES " + DBContract.FileEntry.TABLE_NAME +
                        "(" + DBContract.FileEntry._ID + ") " +
                        "ON DELETE CASCADE ON UPDATE CASCADE" +

                        ");"
        );

        // トリガーを作成
        db.execSQL(
                "CREATE TRIGGER trigger_samp_memo_update AFTER UPDATE ON " + MemoEntry.TABLE_NAME +
                        " BEGIN " +
                        " UPDATE " + MemoEntry.TABLE_NAME + " SET up_date = DATETIME('now', 'localtime') WHERE rowid == NEW.rowid; " +
                        " END;");


        //************* タスクテーブル *****************
        //タスクテーブル作成
        db.execSQL(
                "CREATE TABLE " + DBContract.TodoEntry.TABLE_NAME + " (" +
                        DBContract.TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DBContract.TodoEntry.COLUMN_NAME_TITLE + " TEXT default '', " +
                        DBContract.TodoEntry.COLUMN_NAME_DONE + " INTEGER default 0, " +
                        DBContract.TodoEntry.COLUMN_NAME_UPDATE + " INTEGER DEFAULT (datetime(CURRENT_TIMESTAMP,'localtime'))," +

                        //外部キー
                        "cat_id INTEGER, " +
                        "file_id INTEGER," +
                        "parent_memo_id INTEGER," +

                        "FOREIGN KEY(cat_id) REFERENCES " + DBContract.CatEntry.TABLE_NAME +
                        "(" + DBContract.CatEntry._ID + ")" +
                        "ON DELETE CASCADE ON UPDATE CASCADE," +

                        "FOREIGN KEY(file_id) REFERENCES " + DBContract.FileEntry.TABLE_NAME +
                        "(" + DBContract.FileEntry._ID + ")" +
                        "ON DELETE CASCADE ON UPDATE CASCADE," +

                        "FOREIGN KEY(parent_memo_id) REFERENCES " + DBContract.CatEntry.TABLE_NAME +
                        "(" + DBContract.MemoEntry._ID + ")" +
                        "ON DELETE CASCADE ON UPDATE CASCADE" +

                ");"

        );

        // トリガー作成（更新時に up_date を更新）
        db.execSQL(
                "CREATE TRIGGER trigger_todo_update AFTER UPDATE ON " + DBContract.TodoEntry.TABLE_NAME +
                        " BEGIN " +
                        " UPDATE " + DBContract.TodoEntry.TABLE_NAME + " SET up_date = DATETIME('now', 'localtime') WHERE rowid == NEW.rowid; " +
                        " END;"
        );

        //****************** ファイルテーブル *****************
        db.execSQL(
                "CREATE TABLE " + DBContract.FileEntry.TABLE_NAME + "(" +
                        DBContract.FileEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DBContract.FileEntry.COLUMN_NAME_TITLE + " TEXT default '', " +
                        DBContract.FileEntry.COLUMN_NAME_UPDATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +

                        // 外部キーを追加（カテゴリとの紐づけ）
                        "cat_id INTEGER, " +
                        "FOREIGN KEY(cat_id) REFERENCES " + DBContract.CatEntry.TABLE_NAME +
                        "(" + DBContract.CatEntry._ID + ") " +
                        "ON DELETE CASCADE ON UPDATE CASCADE" +

                        ");"
        );


        //**************** カテゴリテーブル *************
        db.execSQL(
                "CREATE TABLE " + DBContract.CatEntry.TABLE_NAME + " (" +
                        DBContract.CatEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DBContract.CatEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL UNIQUE, " +
                        DBContract.CatEntry.COLUMN_NAME_UPDATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ");"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // トリガーがあるなら消す（念のため）
        db.execSQL("DROP TRIGGER IF EXISTS trigger_samp_memo_update");
        db.execSQL("DROP TRIGGER IF EXISTS trigger_todo_update");

        // テーブルを全て消して onCreate で作り直す（データは消える）
        db.execSQL("DROP TABLE IF EXISTS " + MemoEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.TodoEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.CatEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.FileEntry.TABLE_NAME);

        onCreate(db);
    }

    //************************************* メモの処理 **********************************************
    //メモを保存する処理
    public long insertMemo(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MemoEntry.COLUMN_NAME_CONTENTS, memo.getContent());
        values.put(MemoEntry.FILE_ID,memo.getFileId()); //FileIDをいれる

        long newRowId = db.insert(MemoEntry.TABLE_NAME, null, values);

        db.close();
        return newRowId;
    }

    // メモ更新
    public int updateMemo(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MemoEntry.COLUMN_NAME_CONTENTS, memo.getContent());
        values.put(MemoEntry.FILE_ID, memo.getFileId());

        String selection = MemoEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(memo.getId()) };

        int count = db.update(MemoEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
        return count;
    }

    // IDでメモ削除
    public int deleteMemoById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = MemoEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int deletedRows = db.delete(MemoEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        return deletedRows;
    }

    //ファイルごとのメモを取得
    public List<Memo> getMemoByFileId(long fileId) {
        List<Memo> memoList = new ArrayList<>();            //検索結果を格納
        SQLiteDatabase db = this.getReadableDatabase();     //DBを読み込む
        //SELECT 新しい順にファイルIDに対応して検索する ?は後で入れる値
        Cursor cursor = db.query(
                MemoEntry.TABLE_NAME,
                null,
                MemoEntry.FILE_ID + " = ?",
                new String[]{String.valueOf(fileId)},
                null,null,MemoEntry._ID + " DESC"
        );

        //最初の行にレコードがあれば1つずつ読み込んで進む
        if (cursor.moveToFirst()) {
            do {
                Memo memo = new Memo();
                memo.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MemoEntry._ID)));
                memo.setContent(cursor.getString(cursor.getColumnIndexOrThrow(MemoEntry.COLUMN_NAME_CONTENTS)));
                memo.setFileId(cursor.getLong(cursor.getColumnIndexOrThrow(MemoEntry.FILE_ID)));
                memoList.add(memo);
            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();
        return memoList;
    }

    //データベースから全てのメモを取得しMemo オブジェクトのリストとして返す
    public List<Memo> getAllMemos() {
        List<Memo> memoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //SQL文
        Cursor cursor = db.query(
                MemoEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MemoEntry.COLUMN_NAME_UPDATE + " DESC"  // 新しい順に表示
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MemoEntry._ID));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(MemoEntry.COLUMN_NAME_CONTENTS));
            memoList.add(new Memo(id, content));
        }

        cursor.close();
        db.close();
        return memoList;
    }

    //******************************************* タスクの処理 ********************************************

    public long insertTodo(Todo todo) {
        //DBを開いて書き込めるようにする
        SQLiteDatabase db = this.getWritableDatabase();

        //新規作成や更新するためにフラグメントから取得した値をセット
        ContentValues values = new ContentValues();
        values.put(DBContract.TodoEntry.COLUMN_NAME_TITLE, todo.getTitle());
        values.put(DBContract.TodoEntry.COLUMN_NAME_DONE, todo.isDone() ? 1 : 0);
        values.put(DBContract.TodoEntry.FILE_ID, todo.getFileId());

        long newRowId = db.insert(DBContract.TodoEntry.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    public int updateTodo(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.TodoEntry.COLUMN_NAME_TITLE, todo.getTitle());
        values.put(DBContract.TodoEntry.COLUMN_NAME_DONE, todo.isDone() ? 1 : 0);

        String selection = DBContract.TodoEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(todo.getId()) };

        int count = db.update(DBContract.TodoEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
        return count;
    }

    public int deleteTodoById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DBContract.TodoEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int deletedRows = db.delete(DBContract.TodoEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        return deletedRows;
    }

    public List<Todo> getAllTodos() {
        List<Todo> todoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DBContract.TodoEntry.TABLE_NAME, null, null, null, null, null,
                DBContract.TodoEntry.COLUMN_NAME_UPDATE + " DESC");

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DBContract.TodoEntry._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TodoEntry.COLUMN_NAME_TITLE));
            boolean done = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.TodoEntry.COLUMN_NAME_DONE)) == 1;
            todoList.add(new Todo(id, title, done));
        }

        cursor.close();
        db.close();
        return todoList;
    }

    //ファイルごとのメモを取得
    public List<Todo> getTodoByFileId(long fileId) {
        List<Todo> todoList = new ArrayList<>();            //検索結果を格納
        SQLiteDatabase db = this.getReadableDatabase();     //DBを読み込む

        //SELECT 新しい順にファイルIDに対応して検索する ?は後で入れる値
        Cursor cursor = db.query(
                DBContract.TodoEntry.TABLE_NAME,
                null,
                DBContract.TodoEntry.FILE_ID + " = ?",
                new String[]{String.valueOf(fileId)},
                null,null, DBContract.TodoEntry._ID + " DESC"
        );

        //最初の行にレコードがあれば1つずつ読み込んで進む
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DBContract.TodoEntry._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TodoEntry.COLUMN_NAME_TITLE));
                boolean done = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.TodoEntry.COLUMN_NAME_DONE)) == 1;
                long fileIdFromDB = cursor.getLong(cursor.getColumnIndexOrThrow(DBContract.TodoEntry.FILE_ID));

                // コンストラクタで一気にセット
                Todo todo = new Todo(id, title, done, fileIdFromDB);
                todoList.add(todo);

            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();
        return todoList;
    }

    //******************************** カテゴリの処理 *************************************

    // カテゴリ追加
    public long insertCat(Cat cat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.CatEntry.COLUMN_NAME_TITLE, cat.getTitle());

        long newRowId = db.insert(DBContract.CatEntry.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    // カテゴリ更新
    public int updateCat(Cat cat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.CatEntry.COLUMN_NAME_TITLE, cat.getTitle());

        String selection = DBContract.CatEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(cat.getId()) };

        int count = db.update(DBContract.CatEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
        return count;
    }

    // カテゴリ削除
    public int deleteCatById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DBContract.CatEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int deletedRows = db.delete(DBContract.CatEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        return deletedRows;
    }

    // 全カテゴリ取得
    public List<Cat> getAllCats() {
        List<Cat> catList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DBContract.CatEntry.TABLE_NAME,
                null, null, null, null, null,
                DBContract.CatEntry.COLUMN_NAME_TITLE + " ASC"); // 名前順に並べる

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DBContract.CatEntry._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.CatEntry.COLUMN_NAME_TITLE));
            catList.add(new Cat(id, title));
        }

        cursor.close();
        db.close();
        return catList;
    }

    //カテゴリ名からカテゴリIDの取得
    public long getCatIdByCatName(String catName) {
        SQLiteDatabase db = this.getReadableDatabase();
        long catId = -1;

        Cursor cursor = db.query(
                DBContract.CatEntry.TABLE_NAME , // ← カテゴリテーブル名に変更してください
                new String[]{DBContract.CatEntry._ID},
                DBContract.CatEntry.COLUMN_NAME_TITLE + " = ?", // ← 実際のカラム名に合わせて
                new String[]{catName},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            catId = cursor.getLong(cursor.getColumnIndexOrThrow(DBContract.CatEntry._ID));
        }
        cursor.close();
        db.close();
        return catId;
    }

    //カテゴリIDから名前を取得
    public String getCatNameById(long catId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String catName = "";

        Cursor cursor = db.query(
                DBContract.CatEntry.TABLE_NAME, // ←カテゴリテーブル名
                new String[]{DBContract.CatEntry.COLUMN_NAME_TITLE}, // ← カテゴリ名カラム名
                DBContract.CatEntry._ID + " = ?",                 // ← 主キー
                new String[]{String.valueOf(catId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            catName = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.CatEntry.COLUMN_NAME_TITLE));
        }

        cursor.close();
        db.close();
        return catName;
    }

    // カテゴリIDで1件取得
    public Cat getCatById(long catId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cat cat = null;

        Cursor cursor = db.query(
                DBContract.CatEntry.TABLE_NAME,
                null,
                DBContract.CatEntry._ID + " = ?",
                new String[]{String.valueOf(catId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DBContract.CatEntry._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.CatEntry.COLUMN_NAME_TITLE));
            cat = new Cat(id, title);
        }

        cursor.close();
        db.close();
        return cat;
    }

    //デフォのカテゴリ作成
    public void DefaultCat() {
        SQLiteDatabase db = getWritableDatabase();

        // カテゴリ件数を確認
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DBContract.CatEntry.TABLE_NAME, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        // カテゴリがない場合のみデフォルト作成
        if (count == 0) {
            ContentValues values = new ContentValues();
            values.put(DBContract.CatEntry._ID, 1);  // ID固定
            values.put(DBContract.CatEntry.COLUMN_NAME_TITLE, "カテゴリ1");
            db.insert(DBContract.CatEntry.TABLE_NAME, null, values);
        }
    }

    //******************************* ファイル処理 ****************************************
    //ファイル追加
    public long insertFile(File file) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.FileEntry.COLUMN_NAME_TITLE, file.getTitle());

        long newRowId = db.insert(DBContract.FileEntry.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    //ファイル更新
    public int updateFile(File file) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.FileEntry.COLUMN_NAME_TITLE, file.getTitle());

        String selection = DBContract.FileEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(file.getId()) };

        int count = db.update(DBContract.FileEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
        return count;
    }

    //ファイル削除
    public int deleteFileById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DBContract.FileEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int deletedRows = db.delete(DBContract.FileEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        return deletedRows;
    }

    // 全ファイル取得
    public List<File> getAllFiles() {
        List<File> fileList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DBContract.FileEntry.TABLE_NAME,
                null, null, null, null, null,
                DBContract.FileEntry.COLUMN_NAME_TITLE + " ASC"); // 名前順に並べる

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DBContract.FileEntry._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.FileEntry.COLUMN_NAME_TITLE));
            fileList.add(new File(id, title));
        }

        cursor.close();
        db.close();
        return fileList;
    }

    //ファイル名からファイルIDの取得
    public long getFileIdByFileName(String fileName) {
        SQLiteDatabase db = this.getReadableDatabase();
        long fileId = -1;

        Cursor cursor = db.query(
                DBContract.FileEntry.TABLE_NAME , // ← ファイルテーブル名に変更してください
                new String[]{DBContract.FileEntry._ID},
                DBContract.FileEntry.COLUMN_NAME_TITLE + " = ?", // ← 実際のカラム名に合わせて
                new String[]{fileName},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            fileId = cursor.getLong(cursor.getColumnIndexOrThrow(DBContract.FileEntry._ID));
        }
        cursor.close();
        db.close();
        return fileId;
    }

    //ファイルIDから名前を取得
    public String getFileNameById(long fileId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String fileName = "";

        Cursor cursor = db.query(
                DBContract.FileEntry.TABLE_NAME, // ←ファイルテーブル名
                new String[]{DBContract.FileEntry.COLUMN_NAME_TITLE}, // ← ファイル名カラム名
                DBContract.FileEntry._ID + " = ?",                 // ← 主キー
                new String[]{String.valueOf(fileId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            fileName = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.FileEntry.COLUMN_NAME_TITLE));
        }

        cursor.close();
        db.close();
        return fileName;
    }

    // ファイルIDで1件取得
    public File getFileById(long fileId) {
        SQLiteDatabase db = this.getReadableDatabase();
        File file = null;

        Cursor cursor = db.query(
                DBContract.FileEntry.TABLE_NAME,
                null,
                DBContract.FileEntry._ID + " = ?",
                new String[]{String.valueOf(fileId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DBContract.FileEntry._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.FileEntry.COLUMN_NAME_TITLE));
            file = new File(id, title);
        }

        cursor.close();
        return file;
    }

    //デフォのファイル作成
    public void DefaultFile() {
        SQLiteDatabase db = getWritableDatabase();

        // ファイル件数を確認
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DBContract.FileEntry.TABLE_NAME, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        // ファイルがない場合のみデフォルト作成
        if (count == 0) {
            ContentValues values = new ContentValues();
            values.put(DBContract.FileEntry._ID, 1);  // ID固定
            values.put(DBContract.FileEntry.COLUMN_NAME_TITLE, "ファイル1");
            db.insert(DBContract.FileEntry.TABLE_NAME, null, values);
        }
    }

    //自動でIDが増える動作に合わせてIDに穴があるとき用
    public long getNearestFileId(long baseId, int offset) {
        SQLiteDatabase db = this.getReadableDatabase();

        long nearestId = -1;
        Cursor cursor = null;

        try {
            String table = DBContract.FileEntry.TABLE_NAME;

            //小さいID
            if (offset > 0) {
                cursor = db.rawQuery(
                        "SELECT MIN(" + DBContract.FileEntry._ID + ") FROM " + table +
                                " WHERE " + DBContract.FileEntry._ID + " > ?",
                        new String[]{String.valueOf(baseId)}
                );
                if (cursor.moveToFirst() && !cursor.isNull(0)) {
                    nearestId = cursor.getLong(0);
                } else {
                    // 先頭に戻る
                    cursor = db.rawQuery(
                            "SELECT MIN(" + DBContract.FileEntry._ID + ") FROM " + table, null);
                    if (cursor.moveToFirst() && !cursor.isNull(0)) {
                        nearestId = cursor.getLong(0);
                    }
                }
            }

            //大きいID
            else if (offset < 0) {
                cursor = db.rawQuery(
                        "SELECT MAX(" + DBContract.FileEntry._ID + ") FROM " + table +
                                " WHERE " + DBContract.FileEntry._ID + " < ?",
                        new String[]{String.valueOf(baseId)}
                );
                if (cursor.moveToFirst() && !cursor.isNull(0)) {
                    nearestId = cursor.getLong(0);
                } else {
                    // 後ろに戻る
                    cursor = db.rawQuery(
                            "SELECT MAX(" + DBContract.FileEntry._ID + ") FROM " + table, null);
                    if (cursor.moveToFirst() && !cursor.isNull(0)) {
                        nearestId = cursor.getLong(0);
                    }
                }
            }

        } finally {
            if (cursor != null) cursor.close();
        }

        return nearestId;
    }

}

