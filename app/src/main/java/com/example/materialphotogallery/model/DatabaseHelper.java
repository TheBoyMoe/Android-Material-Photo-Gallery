package com.example.materialphotogallery.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.example.materialphotogallery.common.Constants;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "database.db";
    private static final int SCHEMA = 1;
    private static volatile DatabaseHelper sDatabaseHelper = null;
    private SQLiteDatabase mDatabase = null;

    public synchronized static DatabaseHelper getInstance(Context context) {
        if (sDatabaseHelper == null) {
            sDatabaseHelper = new DatabaseHelper(context.getApplicationContext());
        }
        return sDatabaseHelper;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table
        db.execSQL("CREATE TABLE " + Constants.TABLE + "("
                + "_id INTEGER PRIMARY KEY, "
                + Constants.PHOTO_ID + " INTEGER, "
                + Constants.PHOTO_FILE_PATH + " TEXT, "
                + Constants.PHOTO_PREVIEW_PATH + " TEXT, "
                + Constants.PHOTO_THUMBNAIL_PATH + " TEXT"
                +  ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        throw new RuntimeException("onUpgrade not setup");
    }

    private SQLiteDatabase getDb(Context context) {
        if(mDatabase == null) {
            mDatabase = getInstance(context).getWritableDatabase();
        }
        return mDatabase;
    }

    // insert Item
    public void insertItem(Context context, ContentValues values) {
        // Timber.i("%s: inserting item into the dbase", Constants.LOG_TAG);
        SQLiteDatabase db = getDb(context);
        db.insert(Constants.TABLE, Constants.PHOTO_ID, values);
    }

    // load item
    public Cursor loadItem(Context context, long itemId) {
        // Timber.i("%s: loading item from the dbase", Constants.LOG_TAG);
        SQLiteDatabase db = getDb(context);
        return (db.rawQuery("SELECT * FROM " + Constants.TABLE + " where " + Constants.PHOTO_ID +"='" + itemId + "'", null));
    }

    // load items
    public Cursor loadItems(Context context) {
        // Timber.i("%s: loading items from the dbase", Constants.LOG_TAG);
        SQLiteDatabase db = getDb(context);
        return  (db.rawQuery("SELECT * FROM " + Constants.TABLE + " ORDER BY " + Constants.PHOTO_ID + " DESC", null));
    }

    // delete items
    public void deleteItems(Context context, String[] itemIds) {
        SQLiteDatabase db = getDb(context);
        String args = TextUtils.join(", ", itemIds);
        db.execSQL(String.format("DELETE FROM " + Constants.TABLE + " WHERE " + Constants.PHOTO_ID +  " IN (%s);", args));
    }

    // update item
    public void updateItem(Context context, ContentValues values){
        // Timber.i("%s: updating item in the dbase", Constants.LOG_TAG);
        SQLiteDatabase db = getDb(context);
        db.update(Constants.TABLE, values, Constants.PHOTO_ID + " = " + values.getAsString(Constants.PHOTO_ID), null);
    }

}
