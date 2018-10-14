package com.example.isas88.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.isas88.inventory.data.InventoryContract.*;

public class InventoryDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventoryData.db";
    private static final int DATABASE_VERSION = 1;

    public InventoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_Create_Inventory = "CREATE TABLE " + InventoryDB.TABLE_NAME + "("
                + InventoryDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryDB.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryDB.COLUMN_PRICE + " INTEGER NOT NULL, "
                + InventoryDB.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + InventoryDB.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + InventoryDB.COLUMN_SUPPLIER_PHONE + " TEXT DEFAULT '0' );";

        db.execSQL(SQL_Create_Inventory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
