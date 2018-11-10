package com.example.android.bookstoreapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.bookstoreapp.data.BookContract;
import com.example.android.bookstoreapp.data.BookContract.BookEntry;
import com.example.android.bookstoreapp.data.BookDbHelper;

public class MainActivity extends AppCompatActivity {

    private BookDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new BookDbHelper(this);
        insertBook();
        queryData();
    }

    private void queryData(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {BookEntry._ID, BookEntry.COLUMN_BOOK_NAME, BookEntry.COLUMN_BOOK_PRICE, BookEntry.COLUMN_BOOK_QUANTITY, BookEntry.COLUMN_BOOK_SUPPLIER_NAME, BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};
        Cursor cursor = db.query(BookEntry.TABLE_NAME, projection, null, null, null, null, null, null);

        TextView displayView = (TextView) findViewById(R.id.text_view_book);

        try{
            displayView.setText("Rows: " + cursor.getCount());

            int idColIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int priceColIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int sNameColIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int sPhoneColIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            while(cursor.moveToNext()){
                int currentId = cursor.getInt(idColIndex);
                String currentName = cursor.getString(nameColIndex);
                String currentPrice = cursor.getString(priceColIndex);
                int currentQuantity = cursor.getInt(quantityColIndex);
                String currentSName = cursor.getString(sNameColIndex);
                int currentSPhone = cursor.getInt(sPhoneColIndex);

                displayView.append("\n" + currentId + " - " + currentName + " - " + currentPrice + " - " + currentQuantity + " - " + currentSName + " - " + currentSPhone);
            }
        } finally {
            cursor.close();
        }
    }

    private void insertBook(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Hardcoded Data
        ContentValues bookOne = new ContentValues();
        bookOne.put(BookEntry.COLUMN_BOOK_NAME, "The Power of Habit");
        bookOne.put(BookEntry.COLUMN_BOOK_PRICE, "$16.00");
        bookOne.put(BookEntry.COLUMN_BOOK_QUANTITY, 5);
        bookOne.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "Amazon");
        bookOne.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, "1234567890");

        long newRowId = db.insert(BookEntry.TABLE_NAME, null, bookOne);

        ContentValues bookTwo = new ContentValues();
        bookTwo.put(BookEntry.COLUMN_BOOK_NAME, "Creative Confidence");
        bookTwo.put(BookEntry.COLUMN_BOOK_PRICE, "$25.00");
        bookTwo.put(BookEntry.COLUMN_BOOK_QUANTITY, 10);
        bookTwo.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "Amazon");
        bookTwo.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, "1234567890");

        long newRowIdTwo = db.insert(BookEntry.TABLE_NAME, null, bookTwo);
    }
}
