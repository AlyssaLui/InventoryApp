package com.example.android.bookstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.bookstoreapp.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "myBooks.db";
    public static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + BookEntry.TABLE_NAME + " ( "
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_BOOK_PRICE + " TEXT, "
                + BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_BOOK_SUPPLIER_PHONE + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
