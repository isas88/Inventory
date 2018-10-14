package com.example.isas88.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.isas88.inventory.data.InventoryContract.*;

public class InventoryCurAdp extends CursorAdapter {

    public InventoryCurAdp(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find fields to populate in inflated template
        TextView InventoryName     = view.findViewById(R.id.name);
        TextView InventorySupplier = view.findViewById(R.id.summary);
        TextView InventoryQuantity = view.findViewById(R.id.quantity);
        Button   btn_sale          = view.findViewById(R.id.sale_btn);

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryDB.COLUMN_PRODUCT_NAME));
        String summary = cursor.getString(cursor.getColumnIndexOrThrow(InventoryDB.COLUMN_SUPPLIER_NAME));
        final String quantity = cursor.getString(cursor.getColumnIndexOrThrow(InventoryDB.COLUMN_QUANTITY));

        //get the current rows ID
        final long id = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryDB._ID));

        // Populate text fields of the list with extracted properties
        InventoryName.setText(name);
        InventorySupplier.setText(summary);
        InventoryQuantity.setText(quantity);

        //set gray color for the button if quantity is 0 and disable it
        if (Integer.parseInt(quantity)==0){
            btn_sale.setBackgroundColor(context.getResources().getColor(R.color.color_no_item));
            btn_sale.setEnabled(false);
        }else{
            //enable button
            btn_sale.setBackgroundColor(context.getResources().getColor(R.color.colorButton));
            btn_sale.setEnabled(true);
        }

        //sale button click event
        btn_sale.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view1) {
                MainActivity mainAct = (MainActivity) context;
                mainAct.saleButtonClick(id,quantity);
            }
        });

    }
}

