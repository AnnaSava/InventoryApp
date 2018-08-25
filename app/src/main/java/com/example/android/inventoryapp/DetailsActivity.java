package com.example.android.inventoryapp;

import android.Manifest;
import android.app.Activity;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.ProductEntry;

public class DetailsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri mCurrentProductUri;

    private TextView mNameTextView;

    private TextView mPriceTextView;

    private TextView mQuantityTextView;

    private TextView mSupplierNameTextView;

    private TextView mSupplierPhoneTextView;

    int mQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setTitle(getString(R.string.details_activity_title));

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        mNameTextView = (TextView) findViewById(R.id.product_name_view);
        mPriceTextView = (TextView) findViewById(R.id.product_price_view);
        mQuantityTextView = (TextView) findViewById(R.id.product_quantity_view);
        mSupplierNameTextView = (TextView) findViewById(R.id.supplier_name_view);
        mSupplierPhoneTextView = (TextView) findViewById(R.id.supplier_phone_view);

        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);

        Button incQuantityButton = (Button) findViewById(R.id.inc_quantity);
        // Set a click listener on that Button
        incQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateQuantity(1, R.string.quantity_incremented);
            }
        });

        Button decQuantityButton = (Button) findViewById(R.id.dec_quantity);
        // Set a click listener on that Button
        decQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateQuantity(-1, R.string.quantity_decremented);
            }
        });

        Button callSupplierButton = (Button) findViewById(R.id.call_supplier);
        // Set a click listener on that Button
        callSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String supplierPhone = String.format("tel: %s", mSupplierPhoneTextView.getText().toString());

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(supplierPhone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        Button deleteProductButton = (Button) findViewById(R.id.delete_product);
        // Set a click listener on that Button
        deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_edit:
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);

                // Set the URI on the data field of the intent
                intent.setData(mCurrentProductUri);

                // Launch the {@link EditorActivity} to display the data for the current product.
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateQuantity(int value, int messageResId) {
        int newQuantity = mQuantity + value;

        if (newQuantity < 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.quantity_zero), Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

        int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

        if (rowsAffected > 0) {
            mQuantity = newQuantity;
            mQuantityTextView.setText(Integer.toString(newQuantity));
            Toast.makeText(getApplicationContext(), getString(messageResId), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            mNameTextView.setText(name);
            mPriceTextView.setText(Float.toString(price));
            mQuantityTextView.setText(Integer.toString(quantity));
            mSupplierNameTextView.setText(supplierName);
            mSupplierPhoneTextView.setText("+" + supplierPhone);

            mQuantity = quantity;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameTextView.setText("");
        mPriceTextView.setText("");
        mQuantityTextView.setText("");
        mSupplierNameTextView.setText("");
        mSupplierPhoneTextView.setText("");
        mQuantity = 0;
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentProductUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }

            // Close the activity
            finish();
        }
    }
}
