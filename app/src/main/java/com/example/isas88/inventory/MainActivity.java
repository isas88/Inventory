package com.example.isas88.inventory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.isas88.inventory.data.InventoryContract.*;
import com.example.isas88.inventory.data.InventoryDBHelper;

public class MainActivity extends AppCompatActivity {

    private InventoryDBHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.add_inventory);
        FloatingActionButton fabClear = findViewById(R.id.clear_inventory);

        mDbHelper = new InventoryDBHelper(this);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //insert dummy value

                // Gets the database in write mode
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                // Create a ContentValues object where column names are the keys,
                ContentValues values = new ContentValues();
                values.put(InventoryDB.COLUMN_PRODUCT_NAME, getResources().getString(R.string.test_product_name));
                values.put(InventoryDB.COLUMN_PRICE, getResources().getString(R.string.test_price));
                values.put(InventoryDB.COLUMN_QUANTITY, getResources().getInteger(R.integer.test_quantity));
                values.put(InventoryDB.COLUMN_SUPPLIER_NAME, getResources().getString(R.string.test_supplier_name));
                values.put(InventoryDB.COLUMN_SUPPLIER_PHONE, getResources().getString(R.string.test_supplier_phone));

                db.insert(InventoryDB.TABLE_NAME, null, values);
                displayDatabaseInfo();

            }
        });

        fabClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Gets the database in write mode to delete the records
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                db.delete(InventoryDB.TABLE_NAME, null, null);
                displayDatabaseInfo();
            }
        });
    }

        protected void onStart() {
            super.onStart();
            displayDatabaseInfo();
        }

        private void displayDatabaseInfo() {
            // Create and/or open a database to read from it
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            String[] projection = {
                    InventoryDB._ID,
                    InventoryDB.COLUMN_PRODUCT_NAME,
                    InventoryDB.COLUMN_PRICE,
                    InventoryDB.COLUMN_QUANTITY,
                    InventoryDB.COLUMN_SUPPLIER_NAME,
                    InventoryDB.COLUMN_SUPPLIER_PHONE};

            try (Cursor cursor = db.query(
                    InventoryDB.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null);
            ) {

                TextView displayView = findViewById(R.id.text_view_inventory);

                displayView.setText("The inventory table contains " + cursor.getCount() + " rows.\n\n");
                displayView.append(InventoryDB._ID + " - " +
                        InventoryDB.COLUMN_PRODUCT_NAME + " - " +
                        InventoryDB.COLUMN_PRICE + " - " +
                        InventoryDB.COLUMN_QUANTITY + " - " +
                        InventoryDB.COLUMN_SUPPLIER_NAME + " - " +
                        InventoryDB.COLUMN_SUPPLIER_PHONE + "\n");

                // Figure out the index of each column
                int idColumnIndex = cursor.getColumnIndex(InventoryDB._ID);
                int nameColumnIndex = cursor.getColumnIndex(InventoryDB.COLUMN_PRODUCT_NAME);
                int priceColumnIndex = cursor.getColumnIndex(InventoryDB.COLUMN_PRICE);
                int quantityColumnIndex = cursor.getColumnIndex(InventoryDB.COLUMN_QUANTITY);
                int supplierColumnIndex = cursor.getColumnIndex(InventoryDB.COLUMN_SUPPLIER_NAME);

                // Iterate through all the returned rows in the cursor
                while (cursor.moveToNext()) {

                    int currentID = cursor.getInt(idColumnIndex);
                    String currentName = cursor.getString(nameColumnIndex);
                    String currentPrice = cursor.getString(priceColumnIndex);
                    int currentQuantity = cursor.getInt(quantityColumnIndex);
                    String currentSupplier = cursor.getString(supplierColumnIndex);

                    // Display the values from each column of the current row in the cursor in the TextView
                    displayView.append(("\n" + currentID + " - " +
                            currentName + " - " +
                            currentPrice + " - " +
                            currentQuantity + " - " +
                            currentSupplier));
                }
            } finally {
                Log.e("Cur","Error when using cursor");
            }
        }
}