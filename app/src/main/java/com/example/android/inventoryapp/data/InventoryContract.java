package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Created by Sava on 06.08.2018.
 */

public class InventoryContract {

    private InventoryContract() {}

    public static final class ProductEntry implements BaseColumns {

        public final static String TABLE_NAME = "products";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PRODUCT_NAME ="name";

        public final static String COLUMN_PRODUCT_PRICE = "price";

        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";

        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE = "supplier_phone";
    }
}
