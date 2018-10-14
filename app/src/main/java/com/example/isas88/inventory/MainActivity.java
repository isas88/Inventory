package com.example.isas88.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.isas88.inventory.data.InventoryContract.*;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 1;

    InventoryCurAdp mAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.add_inventory);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView inventoryListView = findViewById(R.id.list_view_inventory);

        //set empty view tp the list view
        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);

        //setup Adapter to the list view
        mAdapter = new InventoryCurAdp(this,null);
        inventoryListView.setAdapter(mAdapter);
        
        //initialize loader
        getLoaderManager().initLoader(INVENTORY_LOADER,null,this);

        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editInventory = new Intent(MainActivity.this,EditorActivity.class);
                Uri currentItem = ContentUris.withAppendedId(InventoryDB.CONTENT_URI, id);
                editInventory.setData(currentItem);
                startActivity(editInventory);
            }
        });

    }

    protected void onStart() {
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {InventoryDB._ID,InventoryDB.COLUMN_PRODUCT_NAME,InventoryDB.COLUMN_SUPPLIER_NAME,InventoryDB.COLUMN_PRICE,InventoryDB.COLUMN_QUANTITY,InventoryDB.COLUMN_SUPPLIER_PHONE};
        return new CursorLoader(this,InventoryDB.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void saleButtonClick(long id, String quantity){

        int val_Quantity = 0;
        long update_ID;

        if (!quantity.isEmpty()){
            val_Quantity = Integer.parseInt(quantity);
        }

        //decrement quantity value and update the database
        if (val_Quantity > 0){
            --val_Quantity;
            Uri uriQuantity = ContentUris.withAppendedId(InventoryDB.CONTENT_URI, id);
            ContentValues values = new ContentValues();
            values.put(InventoryDB.COLUMN_QUANTITY, String.valueOf(val_Quantity));
            update_ID = getContentResolver().update(uriQuantity,values,null,null);

            //display toast messages
            if(update_ID==-1){
                Toast.makeText(this, getResources().getText(R.string.error_update).toString(), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, getResources().getText(R.string.sale_success).toString() +id, Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, getResources().getText(R.string.sale_fail).toString(), Toast.LENGTH_SHORT).show();
        }

    }
}