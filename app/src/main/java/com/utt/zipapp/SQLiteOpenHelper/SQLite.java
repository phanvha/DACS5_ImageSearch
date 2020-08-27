package com.utt.zipapp.SQLiteOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.utt.zipapp.Model.ItemsHistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SQLite extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";
    //data list bus stop
    private static final String DATABASE_NAME = "DBHISTORY";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE = "history";
    private static final String ID = "id";
    private static final String LINK = "link";
    private static final String DATETIME = "time";
    private static final String PERSON_ID = "person_id";
    private static final String CREATE_TABLE_SQL_HISTORY = "CREATE TABLE " + TABLE + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            LINK + " TEXT,"+
            DATETIME + " TEXT, " +
            PERSON_ID + " TEXT " +
            ")";

    public SQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    private static SQLite sInstance;
    public static SQLite getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SQLite(context.getApplicationContext());
        }
        return sInstance;
    }

    private SQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG, "onCreate: ");
        db.execSQL(CREATE_TABLE_SQL_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "onUpgrade: ");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
    public boolean insertListHistory(ItemsHistory data){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LINK, data.getLink());
        values.put(DATETIME, data.getDatetime());
        values.put(PERSON_ID,data.getPerson_id());
        long rowId = db.insert(TABLE, null, values);
        db.close();
        if (rowId != -1)
            return true;
        return false;
    }
//    public boolean insertListHistory(ItemsHistory data) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(LINK, data.getLink());
//        values.put(DATETIME, data.getDatetime());
//        long rowId = db.insert(TABLE, null, values);
//        db.close();
//        if (rowId != -1)
//            return true;
//        return false;
//    }

    //get data
    public List<ItemsHistory> getLink(String m) {
        SQLiteDatabase db = getReadableDatabase();
        ItemsHistory data = null;
        Cursor cursor = db.query(TABLE, new String[]{
                        LINK,
                        DATETIME,
                        PERSON_ID
                }, ID + " = ?",
                new String[]{m}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            data = new ItemsHistory(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
            cursor.close();
        }
        db.close();
        return Collections.singletonList(data);
    }

    //get all list
    public List<ItemsHistory> getListLink(String person_id) {
        SQLiteDatabase db = getReadableDatabase();
        List<ItemsHistory> words = new ArrayList<>();
        if (person_id!=null) {
            String sql = "SELECT * FROM " + TABLE + " WHERE person_id= '" + person_id + "'";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    words.add(new ItemsHistory(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3)
                    ));
                } while (cursor.moveToNext());
                cursor.close();
            }

            db.close();
        }else{
            String sql = "SELECT * FROM " + TABLE +" WHERE person_id=null";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    words.add(new ItemsHistory(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3)
                    ));
                } while (cursor.moveToNext());
                cursor.close();
            }

            db.close();
        }
        return words;
    }

    //get total
    public int getCountTotalLinkTB() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE;
        Cursor cursor = db.rawQuery(sql, null);
        int totalRows = cursor.getCount();
        cursor.close();
        return totalRows;
    }


    public int deleteAllLink() {
        SQLiteDatabase db = getReadableDatabase();
        int rowEffect = db.delete(TABLE, null, null);
        db.close();
        return rowEffect;
    }
    public int deleteSimpleLink(int id) {
        SQLiteDatabase db = getReadableDatabase();
        int rowEffect = db.delete(TABLE,  ID+"="+id, null);
        db.close();
        return rowEffect;
    }

}
