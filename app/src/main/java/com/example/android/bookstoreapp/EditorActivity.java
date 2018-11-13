package com.example.android.bookstoreapp;


import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract;
import com.example.android.bookstoreapp.data.BookContract.BookEntry;

import static android.text.TextUtils.isEmpty;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSNameEditText;
    private EditText mSPhoneEditText;
    private Button mAddButton;
    private Button mSubButton;

    private static final int EXISTING_BOOK_LOADER = 0;

    private Uri mCurrentUri;

    private boolean mBookHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent i = getIntent();
        mCurrentUri = i.getData();

        if(mCurrentUri == null){
            setTitle(R.string.add_book);
        } else {
            setTitle(R.string.edit_book);
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        mSNameEditText = (EditText) findViewById(R.id.edit_sname);
        mSPhoneEditText = (EditText) findViewById(R.id.edit_sphone);
        mAddButton = (Button) findViewById(R.id.addQuantity);
        mSubButton = (Button) findViewById(R.id.subQuantity);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSNameEditText.setOnTouchListener(mTouchListener);
        mSPhoneEditText.setOnTouchListener(mTouchListener);
    }

    private void saveBook() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String sNameString = mSNameEditText.getText().toString().trim();
        String sPhoneString = mSPhoneEditText.getText().toString().trim();

        //Makes sure that a blank book isn't saved
        if( mCurrentUri == null && isEmpty(nameString) && isEmpty(priceString) && isEmpty(sNameString) && isEmpty(sPhoneString)){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, priceString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, quantityString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME, sNameString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, sPhoneString);

        Uri newUri;
        if(mCurrentUri == null) {
            getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
        } else {
            int rows = getContentResolver().update(mCurrentUri, values, null, null);
            if (rows == 0) {
                Toast.makeText(this, R.string.save_error,Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.save_success,Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveBook();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Unsaved Changes");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        if(mCurrentUri != null) {
            int rows = getContentResolver().delete(mCurrentUri, null, null);

            if(rows == 0){
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),Toast.LENGTH_SHORT).show();
            }  else {
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
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
            String sPhone = cursor.getString(sPhoneColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(price);
            mQuantityEditText.setText(String.valueOf(quantity));
            mSNameEditText.setText(sName);
            mSPhoneEditText.setText(sPhone);

            mSubButton.setOnClickListener(new View.OnClickListener(){
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

            mAddButton.setOnClickListener(new View.OnClickListener(){
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
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
