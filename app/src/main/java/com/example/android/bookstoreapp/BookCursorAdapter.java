package com.example.android.bookstoreapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract;
import com.example.android.bookstoreapp.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter implements LoaderManager.LoaderCallbacks<Cursor> {


    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {

        LinearLayout selectedBook = (LinearLayout) view.findViewById(R.id.book);
        TextView nameView = (TextView) view.findViewById(R.id.name);
        TextView priceView = (TextView) view.findViewById(R.id.price);
        final TextView quantityView = (TextView) view.findViewById(R.id.quantity);
        Button detailsButton = (Button) view.findViewById(R.id.details_button);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_NAME));
        String summary = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_QUANTITY));

        String quantityString = String.valueOf(quantity);
        quantityView.setText(quantityString);
        nameView.setText(name);
        priceView.setText(summary);

        final int bookId = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry._ID));

        final Context newContext = context;
        saleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int updatedQuantity = quantity;
                if(updatedQuantity >= 0) {
                    updatedQuantity -= 1;
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, updatedQuantity);
                    Uri newUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);
                    newContext.getContentResolver().update(newUri, values, null, null);
                }
            }
        });

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditorActivity.class);
                Uri currentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);

                intent.setData(currentUri);
                context.startActivity(intent);
            }
        });

        selectedBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                Uri currentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);

                intent.setData(currentUri);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
