package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        // Find the columns of product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        // Read the product attributes from the Cursor for the current product
        String productName = cursor.getString(nameColumnIndex);
        float productPrice = cursor.getFloat(priceColumnIndex);
        final int productQuantity = cursor.getInt(quantityColumnIndex);
        final int id = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));

        // Update the TextViews with the attributes for the current product
        nameTextView.setText(productName);
        priceTextView.setText(Float.toString(productPrice));
        quantityTextView.setText(Integer.toString(productQuantity));

        Button quantityButton = (Button) view.findViewById(R.id.sale);
        quantityButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (productQuantity <= 0) {
                    Toast.makeText(v.getContext(), context.getString(R.string.quantity_zero), Toast.LENGTH_SHORT).show();
                } else {
                    Uri productUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity - 1);

                    int rowsUpdated = context.getContentResolver()
                            .update(productUri, values, null, null);

                    if (rowsUpdated != 0) {
                        Toast.makeText(context, R.string.quantity_decremented, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
