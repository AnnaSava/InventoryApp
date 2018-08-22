package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryDbHelper;
import com.example.android.inventoryapp.data.InventoryContract.ProductEntry;
import android.support.design.widget.FloatingActionButton;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private InventoryDbHelper mDbHelper;

    private static final int PRODUCT_LOADER = 0;

    ProductCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = new InventoryDbHelper(this);

        insertProduct();

        ListView productListView = (ListView) findViewById(R.id.list);

        mCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    private void insertProduct() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Barbie Basics #1");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 14.95);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 100);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Mattel");
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, "+000 111 22 33");

        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY};

        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
