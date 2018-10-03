package com.example.isas88.inventory.data;

import android.provider.BaseColumns;

public class InventoryContract {

    //empty constructor
    public InventoryContract() {
    }

    public static final class InventoryDB implements BaseColumns {

        //define table and field names
        public final static String TABLE_NAME = "inventory";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "Product Name";
        public final static String COLUMN_PRICE = "Price";
        public final static String COLUMN_QUANTITY = "Quantity";
        public final static String COLUMN_SUPPLIER_NAME = "Supplier Name";
        public final static String COLUMN_SUPPLIER_PHONE = "Supplier Phone Number";

    }

}

