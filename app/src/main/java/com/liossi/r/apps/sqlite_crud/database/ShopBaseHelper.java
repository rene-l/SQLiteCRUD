package com.liossi.r.apps.sqlite_crud.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.liossi.r.apps.sqlite_crud.database.ProductDbSchema.ProductTable;

/**
 * Created by Rene on 21/06/2016.
 */
public class ShopBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "shopdemo.db";

    public ShopBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ProductTable.NAME + " (" +
                "_id integer primary key autoincrement, " +
                ProductTable.Cols.UUID + ", " +
                ProductTable.Cols.NAME + ", " +
                ProductTable.Cols.DESCRIPTION + ", " +
                ProductTable.Cols.PRICE + ", " +
                ProductTable.Cols.PHOTO +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
