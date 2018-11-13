package com.example.android.bookstoreapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.bookstoreapp.data.BookContract;
import com.example.android.bookstoreapp.data.BookContract.BookEntry;
import com.example.android.bookstoreapp.data.BookDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    //private BookDbHelper dbHelper;
    BookCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView booksList = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        booksList.setEmptyView(emptyView);

        mAdapter = new BookCursorAdapter(this, null);
        booksList.setAdapter(mAdapter);
/*
        booksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, l);

                intent.setData(currentUri);
                startActivity(intent);
            }
        });
*/

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }
    /*
    private void queryData(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {BookEntry._ID, BookEntry.COLUMN_BOOK_NAME, BookEntry.COLUMN_BOOK_PRICE, BookEntry.COLUMN_BOOK_QUANTITY, BookEntry.COLUMN_BOOK_SUPPLIER_NAME, BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};
        Cursor cursor = db.query(BookEntry.TABLE_NAME, projection, null, null, null, null, null, null);

        TextView displayView = (TextView) findViewById(R.id.list);

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
    */
/*
    private void insertBook(){
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        //Hardcoded Data
//        ContentValues bookOne = new ContentValues();
//        bookOne.put(BookEntry.COLUMN_BOOK_NAME, "The Power of Habit");
//        bookOne.put(BookEntry.COLUMN_BOOK_PRICE, "$16.00");
//        bookOne.put(BookEntry.COLUMN_BOOK_QUANTITY, 5);
//        bookOne.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "Amazon");
//        bookOne.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, "1234567890");
//
//        long newRowId = db.insert(BookEntry.TABLE_NAME, null, bookOne);
//
//        ContentValues bookTwo = new ContentValues();
//        bookTwo.put(BookEntry.COLUMN_BOOK_NAME, "Creative Confidence");
//        bookTwo.put(BookEntry.COLUMN_BOOK_PRICE, "$25.00");
//        bookTwo.put(BookEntry.COLUMN_BOOK_QUANTITY, 10);
//        bookTwo.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "Amazon");
//        bookTwo.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, "1234567890");


        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, "Creative Confidence");
        values.put(BookEntry.COLUMN_BOOK_PRICE, "$25.00");
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 10);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "Amazon");
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, "1234567890");
        //long newRowIdTwo = db.insert(BookEntry.TABLE_NAME, null, bookTwo);
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = new String[] {BookEntry._ID, BookEntry.COLUMN_BOOK_NAME, BookEntry.COLUMN_BOOK_PRICE, BookEntry.COLUMN_BOOK_QUANTITY};
        return new CursorLoader(this, BookEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
