package com.example.isas88.inventory;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.isas88.inventory.data.InventoryContract.*;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    Uri currInventory;
    EditText itemName;
    EditText itemPrice;
    EditText itemQuantity;
    EditText itemSupplier;
    EditText itemSupplierNo;
    Button deleteItem_btn;
    Button addItem_btn;
    Button decreaseItem_btn;
    private boolean inventoryChanged = false;

    private static final int INVENTORY_LOAD_ID = 1;

    // OnTouchListener that listens for any user touches on a View
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            inventoryChanged = true;
            return false;
        }
    };

    //to alert the user to discard or save the unsaved changes when navigated back from edit screen
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!inventoryChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_inventory);

        //Receive the Uri data from MainActivity for editing an inventory
        Intent UriData = getIntent();
        currInventory = UriData.getData();

        //initialize variables for each input item on the edit item page
        itemName = findViewById(R.id.edit_product_name);
        itemPrice = findViewById(R.id.edit_product_price);
        itemQuantity = findViewById(R.id.edit_product_quantity);
        itemSupplier = findViewById(R.id.edit_supplier_name);
        itemSupplierNo = findViewById(R.id.edit_supplier_phoneno);
        deleteItem_btn = findViewById(R.id.delete_button);
        addItem_btn = findViewById(R.id.increment_btn);
        decreaseItem_btn = findViewById(R.id.decrement_btn);

        //set title based on URI data passed or not
        if (currInventory == null) {
            setTitle(getResources().getString(R.string.title_add));
            deleteItem_btn.setVisibility(View.GONE);
        } else {
            setTitle(getResources().getString(R.string.title_edit));
        }

        getLoaderManager().initLoader(INVENTORY_LOAD_ID, null, this);

        deleteItem_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (currInventory != null) {
                    long deleteID = getContentResolver().delete(currInventory, null, null);
                    if (deleteID == -1) {
                        Toast.makeText(EditorActivity.this, getResources().getText(R.string.error_delete).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditorActivity.this, getResources().getText(R.string.delete_success).toString(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(EditorActivity.this, getResources().getText(R.string.delete_fail).toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        addItem_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int currQuantity;
                if (itemQuantity.getText().toString().isEmpty()) {
                    currQuantity = 0;
                } else {
                    currQuantity = Integer.valueOf(itemQuantity.getText().toString());
                }
                itemQuantity.setText(String.valueOf(++currQuantity));
                Toast.makeText(EditorActivity.this, getResources().getText(R.string.increment_save).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        decreaseItem_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int currQuantity;
                String strQuantity = itemQuantity.getText().toString();
                if (!strQuantity.isEmpty()) {
                    currQuantity = Integer.valueOf(itemQuantity.getText().toString());
                    if (currQuantity > 0) {
                        itemQuantity.setText(String.valueOf(--currQuantity));
                        Toast.makeText(EditorActivity.this, getResources().getText(R.string.decrement_save).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditorActivity.this, getResources().getText(R.string.item_na).toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditorActivity.this, getResources().getText(R.string.item_na).toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        FloatingActionButton fab = findViewById(R.id.call_supplier);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + itemSupplierNo.getText().toString()));
                if (ContextCompat.checkSelfPermission(EditorActivity.this, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                } else {
                    Toast.makeText(EditorActivity.this, getResources().getText(R.string.err_permission).toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        itemName.setOnTouchListener(mTouchListener);
        itemPrice.setOnTouchListener(mTouchListener);
        itemQuantity.setOnTouchListener(mTouchListener);
        itemSupplier.setOnTouchListener(mTouchListener);
        itemSupplierNo.setOnTouchListener(mTouchListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                saveItem();
                finish();
                return true;

            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!inventoryChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveItem() {

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String name = itemName.getText().toString().trim();
        String supplier = itemSupplier.getText().toString().trim();

        Long supplierNo;

        if (itemSupplierNo.getText().toString().isEmpty()) {
            supplierNo = Long.valueOf(0);
        } else {
            supplierNo = Long.parseLong(itemSupplierNo.getText().toString().trim());
        }

        if (name.isEmpty() || supplier.isEmpty() ||
                itemPrice.getText().toString().isEmpty() ||
                itemQuantity.getText().toString().isEmpty()) {
            sndToastError();
            return;
        }

        int price = Integer.parseInt(itemPrice.getText().toString().trim());
        int quantity = Integer.parseInt(itemQuantity.getText().toString().trim());

        // Create a ContentValues object where column names are the keys,
        // and inventory items from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryDB.COLUMN_PRODUCT_NAME, name);
        values.put(InventoryDB.COLUMN_PRICE, price);
        values.put(InventoryDB.COLUMN_QUANTITY, quantity);
        values.put(InventoryDB.COLUMN_SUPPLIER_NAME, supplier);
        values.put(InventoryDB.COLUMN_SUPPLIER_PHONE, supplierNo);

        Uri newUri = null;
        long updateID = 0;

        if (currInventory == null) {
            // Insert a new row for pet in the database, returning the ID of that new row.
            newUri = getContentResolver().insert(InventoryDB.CONTENT_URI, values);
        } else {
            updateID = getContentResolver().update(currInventory, values, null, null);
        }

        // Show a toast message depending on whether or not the insertion was successful
        if ((newUri == null) && (updateID <= 0)) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getResources().getText(R.string.error_save).toString(), Toast.LENGTH_SHORT).show();
        } else {
            //insert was successful
            if (currInventory == null) {
                // use new uri that is generated from insert
                Toast.makeText(this, getResources().getText(R.string.save_success).toString() + ContentUris.parseId(newUri), Toast.LENGTH_SHORT).show();
            } else {
                // use current item uri
                Toast.makeText(this, getResources().getText(R.string.save_success).toString() + ContentUris.parseId(currInventory), Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void sndToastError() {
        Toast.makeText(this, "Please enter all fields to save item", Toast.LENGTH_LONG).show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = {InventoryDB._ID, InventoryDB.COLUMN_PRODUCT_NAME, InventoryDB.COLUMN_PRICE, InventoryDB.COLUMN_QUANTITY, InventoryDB.COLUMN_SUPPLIER_NAME, InventoryDB.COLUMN_SUPPLIER_PHONE};
        if (currInventory != null) {
            //return new CursorLoader(currInventory,projection,null,null,null);
            return new CursorLoader(this, currInventory, projection, null, null, null);
        } else {
            return new CursorLoader(this, InventoryDB.CONTENT_URI, projection, null, null, null);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (currInventory != null && data.moveToFirst()) {
            int name_idx = data.getColumnIndex(InventoryDB.COLUMN_PRODUCT_NAME);
            int price_idx = data.getColumnIndex(InventoryDB.COLUMN_PRICE);
            int quantity_idx = data.getColumnIndex(InventoryDB.COLUMN_QUANTITY);
            int supplier_idx = data.getColumnIndex(InventoryDB.COLUMN_SUPPLIER_NAME);
            int supplierno_idx = data.getColumnIndex(InventoryDB.COLUMN_SUPPLIER_PHONE);

            itemName.setText(data.getString(name_idx));
            itemPrice.setText(String.valueOf(data.getString(price_idx)));
            itemQuantity.setText(String.valueOf(data.getString(quantity_idx)));
            itemSupplier.setText(data.getString(supplier_idx));
            itemSupplierNo.setText(String.valueOf(data.getString(supplierno_idx)));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
