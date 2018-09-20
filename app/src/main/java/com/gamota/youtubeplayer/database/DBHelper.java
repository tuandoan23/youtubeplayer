package com.gamota.youtubeplayer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.apkfuns.logutils.LogUtils;
import com.gamota.youtubeplayer.model.listvideomodel.Item;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "gamehub";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_VIDEO_ID = "id_video";
    public static final String FAVOURITE_TABLE = "favourite";
    public static final String RECENTLY_TABLE = "recently";
    public static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createFavouriteTableQuery = "CREATE TABLE IF NOT EXISTS " + FAVOURITE_TABLE + "( " +
                 COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_VIDEO_ID + " TEXT NOT NULL, " +
                COLUMN_DATA + " BLOB NOT NULL" +
                ")";
        String createRecentlyTableQuery = "CREATE TABLE IF NOT EXISTS " + RECENTLY_TABLE + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_DATA + " BLOB NOT NULL" +
                ")";
        sqLiteDatabase.execSQL(createFavouriteTableQuery);
        sqLiteDatabase.execSQL(createRecentlyTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertRecently(Item item){
        ArrayList<Item> items = getAllRecently();
        if (items.size() > 0) {
            if (!items.get(0).getId().getVideoId().equals(item.getId().getVideoId())) {
                SQLiteDatabase sqlDB = this.getWritableDatabase();
                Gson gson = new Gson();
                ContentValues values = new ContentValues();
                values.put(COLUMN_DATA, gson.toJson(item).getBytes());
                sqlDB.insert(RECENTLY_TABLE, null, values);
                sqlDB.close();
            }
        } else if (items.size() == 0){
            SQLiteDatabase sqlDB = this.getWritableDatabase();
            Gson gson = new Gson();
            ContentValues values = new ContentValues();
            values.put(COLUMN_DATA, gson.toJson(item).getBytes());
            sqlDB.insert(RECENTLY_TABLE, null, values);
            sqlDB.close();
        }
    }

    public void insertFavourite(Item item){
        boolean isFavourite = false;
        ArrayList<Item> items = getAllFavourite();
        for (int i = 0; i < items.size(); i++){
            if (item.getId().equals(items.get(i).getId())){
                isFavourite = true;
            }
        }
        if (!isFavourite) {
            SQLiteDatabase sqlDB = this.getWritableDatabase();
            Gson gson = new Gson();
            ContentValues values = new ContentValues();
            values.put(COLUMN_VIDEO_ID, item.getId().getVideoId());
            values.put(COLUMN_DATA, gson.toJson(item).getBytes());
            sqlDB.insert(FAVOURITE_TABLE, null, values);
            sqlDB.close();
        }
    }

    public ArrayList<Item> getAllRecently(){
        ArrayList<Item> items = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + RECENTLY_TABLE + " ORDER BY " + COLUMN_ID + " DESC";
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        Cursor cursor = sqlDB.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do {
                byte[] blob = cursor.getBlob(cursor.getColumnIndex(COLUMN_DATA));
                String json = new String(blob);
                Gson gson = new Gson();
                Item item = gson.fromJson(json, new TypeToken<Item>(){}.getType());
                items.add(item);
            } while (cursor.moveToNext());
        }
        sqlDB.close();
        return items;
    }

    public ArrayList<Item> getAllFavourite(){
        ArrayList<Item> items = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + FAVOURITE_TABLE + " ORDER BY " + COLUMN_ID + " DESC";
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        Cursor cursor = sqlDB.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do {
                byte[] blob = cursor.getBlob(cursor.getColumnIndex(COLUMN_DATA));
                String json = new String(blob);
                Gson gson = new Gson();
                Item item = gson.fromJson(json, new TypeToken<Item>(){}.getType());
                items.add(item);
            } while (cursor.moveToNext());
        }
        sqlDB.close();
        return items;
    }

    public void deleteFavourite(Item item){
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        Gson gson = new Gson();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATA, gson.toJson(item).getBytes());
        sqlDB.delete(FAVOURITE_TABLE,COLUMN_VIDEO_ID + " = ?", new String[] { String.valueOf(item.getId().getVideoId()) });
    }
}
