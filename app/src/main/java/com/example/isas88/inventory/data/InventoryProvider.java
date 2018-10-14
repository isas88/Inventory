package com.example.isas88.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.isas88.inventory.data.InventoryContract.*;

public class InventoryProvider extends ContentProvider {

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    public InventoryDBHelper mDbHelper;

    /** URI matcher code for the content URI for the inventory table */
    private static final int INVENTORY = 100;

    /** URI matcher code for the content URI for a single item in the table */
    private static final int INVENTORY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //add content URIs to URI matcher
    static{
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,InventoryContract.PATH_INVENTORY,INVENTORY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,InventoryContract.PATH_INVENTORY + "/#",INVENTORY_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                cursor = database.query(InventoryDB.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case INVENTORY_ID:
                selection = InventoryDB._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryDB.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case INVENTORY:
                    return InventoryDB.CONTENT_LIST_TYPE;
                case INVENTORY_ID:
                    return InventoryDB.CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
            }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Figure out if the URI matcher can match the URI to a specific code
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case INVENTORY:
                long insertID = database.insert(InventoryDB.TABLE_NAME,
                         null, values);
                if (insertID == -1){
                    Log.e(LOG_TAG,"Failed to insert row for URI: " +uri);
                    return null;
                }else{
                    getContext().getContentResolver().notifyChange(uri,null);
                    return ContentUris.withAppendedId(uri,insertID);
                }

            default:
                return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;

        switch(match){
            case INVENTORY_ID:
                selection     = InventoryDB._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted   = database.delete(InventoryDB.TABLE_NAME,selection,selectionArgs);
                break;

            default:
                Log.e(LOG_TAG,"Failed to delete row for URI: " +uri);
                return -1;
        }
        if(rowsDeleted > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;

        switch(match){
            case INVENTORY_ID:
                selection       = InventoryDB._ID + "=?";
                selectionArgs   = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated     = database.update(InventoryDB.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                Log.e(LOG_TAG,"Failed to update row for URI: " +uri);
                return -1;
        }
        if (rowsUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;

    }
}
