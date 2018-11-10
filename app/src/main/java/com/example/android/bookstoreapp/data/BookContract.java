package com.example.android.bookstoreapp.data;


import android.provider.BaseColumns;

public class BookContract {

    //Private constructor to avoid accidentally creating a contract
    private BookContract(){}
    public static final class BookEntry implements BaseColumns{
        public final static String TABLE_NAME = "books";

        public static final String _ID = "book_id";
        public static final String COLUMN_BOOK_NAME = "name";
        public static final String COLUMN_BOOK_PRICE = "price";
        public static final String COLUMN_BOOK_QUANTITY = "quantity";
        public static final String COLUMN_BOOK_SUPPLIER_NAME = "supplierName";
        public static final String COLUMN_BOOK_SUPPLIER_PHONE = "supplierPhone";

    }
}
