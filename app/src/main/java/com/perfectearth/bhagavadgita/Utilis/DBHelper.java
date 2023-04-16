package com.perfectearth.bhagavadgita.Utilis;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "my_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "my_table";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_JSON_DATA = "json_data";
    String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_JSON_DATA + " TEXT, " +
            " INTEGER DEFAULT 0);";



    public DBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // You can implement any necessary database upgrade logic here
    }

    public boolean insertData(String jsonData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_JSON_DATA, jsonData);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public List<String> getAllData() {
        List<String> jsonDataList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                String jsonData = cursor.getString(cursor.getColumnIndex(COLUMN_JSON_DATA));
                jsonDataList.add(jsonData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return jsonDataList;
    }
    public List<String> getALLID() {
        List<String> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_JSON_DATA};
        Cursor cursor = db.query(TABLE_NAME, projection, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                try {
                    JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex(COLUMN_JSON_DATA)));
                    if (jsonObject.has("id")) {
                        JSONObject objectWithId = new JSONObject();
                        objectWithId.put("id", jsonObject.get("id"));
                        dataList.add(objectWithId.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dataList;
    }
    public boolean deleteDataByTextValue(String textValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            try {
                JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex(COLUMN_JSON_DATA)));
                if (jsonObject.has("id") && jsonObject.getString("id").equals(textValue)) {
                    int result = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)))});
                    if (result <= 0) {
                        return false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();
        return true;
    }


}
