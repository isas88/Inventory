package com.example.isas88.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {

    //empty constructor
    public InventoryContract() {
    }

    //needed for Content URI
    public final static String PATH_INVENTORY = "inventory";
    public final static String CONTENT_AUTHORITY= "com.example.isas88.inventory";
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" +CONTENT_AUTHORITY);

    public static final class InventoryDB implements BaseColumns {

        //define table and field names
        public final static String TABLE_NAME = "inventory";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "Product_Name";
        public final static String COLUMN_PRICE = "Price";
        public final static String COLUMN_QUANTITY = "Quantity";
        public final static String COLUMN_SUPPLIER_NAME = "Supplier_Name";
        public final static String COLUMN_SUPPLIER_PHONE = "Supplier_Phone_Number";
        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_INVENTORY);

        //MIME Type
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

    }

}

