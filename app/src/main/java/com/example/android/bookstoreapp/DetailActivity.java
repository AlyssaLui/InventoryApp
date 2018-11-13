package com.example.android.bookstoreapp;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private TextView mNameText;
    private TextView mPriceText;
    private TextView mQuantityText;
    private TextView mSNameText;
    private TextView mSPhoneText;

    private Button orderButton;
    private Button decreaseButton;
    private Button increaseButton;

    private Uri mCurrentUri;

    private static final int EXISTING_BOOK_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent i = getIntent();
        mCurrentUri = i.getData();

        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);

        mNameText = (TextView) findViewById(R.id.book_name);
        mPriceText = (TextView) findViewById(R.id.book_price);
        mQuantityText = (TextView) findViewById(R.id.book_quantity);
        mSNameText = (TextView) findViewById(R.id.book_supplier_name);
        mSPhoneText = (TextView) findViewById(R.id.book_supplier_phone);
        orderButton = (Button) findViewById(R.id.order_button);
        decreaseButton = (Button) findViewById(R.id.decreaseQuantity);
        increaseButton = (Button) findViewById(R.id.increaseQuantity);




    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookEntry._ID, BookEntry.COLUMN_BOOK_NAME, BookEntry.COLUMN_BOOK_PRICE, BookEntry.COLUMN_BOOK_QUANTITY, BookEntry.COLUMN_BOOK_SUPPLIER_NAME, BookEntry.COLUMN_BOOK_SUPPLIER_PHONE };

        return new CursorLoader(this, mCurrentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int sNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int sPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
            final int bookId = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry._ID));

            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            final int quantity = cursor.getInt(quantityColumnIndex);
            String sName = cursor.getString(sNameColumnIndex);
            final String sPhone = cursor.getString(sPhoneColumnIndex);

            mNameText.setText(name);
            mPriceText.setText(price);
            mQuantityText.setText(String.valueOf(quantity));
            mSNameText.setText(sName);
            mSPhoneText.setText(sPhone);

            decreaseButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int updatedQuantity = quantity;
                    if(updatedQuantity >= 0) {
                        updatedQuantity -= 1;
                        ContentValues values = new ContentValues();
                        values.put(BookEntry.COLUMN_BOOK_QUANTITY, updatedQuantity);
                        Uri newUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);
                        getContentResolver().update(newUri, values, null, null);
                    }
                }
            });

            increaseButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int updatedQuantity = quantity;
                    updatedQuantity += 1;
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, updatedQuantity);
                    Uri newUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);
                    getContentResolver().update(newUri, values, null, null);
                }
            });

            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_DIAL);
                    i.setData(Uri.parse("tel:" + sPhone));
                    startActivity(i);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
