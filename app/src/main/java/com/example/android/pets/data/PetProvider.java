package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.pets.data.petsContract.petEntry;

import com.example.android.pets.CatalogActivity;

/**
 * Created by Mick Jagger on 3/12/2017.
 * {@link ContentProvider} for Pets app
 */

public class PetProvider extends ContentProvider{
    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the pets table */
    private static final int PETS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PET_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(petEntry.CONTENT_AUTHORITY, petEntry.PATH_PETS, PETS);
        sUriMatcher.addURI(petEntry.CONTENT_AUTHORITY, petEntry.PATH_PETS + "/#", PET_ID);
    }

    // To access our database, we instantiate our subclass of SQLiteOpenHelper
    // and pass the context, which is the current activity.
    private PetDbHelper mDbHelper;


    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new PetDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(petEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //s
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = petEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(petEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return petEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return petEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + "with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(petEntry.COLUMN_PET_NAME);
        try {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }catch (IllegalArgumentException e){
            Log.e(LOG_TAG,"Error: " + e.getMessage());
            return null;
        }

        //Check that the weight is not null
        Integer weight = values.getAsInteger(petEntry.COLUMN_PET_WEIGHT);
        try{
            if(weight < 0 && weight != null){
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }catch (IllegalArgumentException e){
            Log.e(LOG_TAG,"Error: " + e.getMessage());
            return null;
        }

        //check gender is not null
        Integer gender = values.getAsInteger(petEntry.COLUMN_PET_GENDER);
        try{
            if(gender < petEntry.GENDER_UNKNOWN || gender > petEntry.GENDER_MALE){
                throw new IllegalArgumentException("Invalid Pet Gender");
            }
        }catch (IllegalArgumentException e){
            Log.e(LOG_TAG,"Error: " + e.getMessage());
            return null;
        }


        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new row, returning the primary key value of the new row
        long id = database.insert(petEntry.TABLE_NAME, null, values);

        if(id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        database.close();
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                return database.delete(petEntry.TABLE_NAME, selection, selectionArgs);
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = petEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(petEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = petEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //Data check on Pet name
        if (values.containsKey(petEntry.COLUMN_PET_NAME)){
            String name = values.getAsString(petEntry.COLUMN_PET_NAME);
            try {
                if (name == null || name.isEmpty()) {
                    throw new IllegalArgumentException("Pet requires a name");
                }
            }catch (IllegalArgumentException e){
                Log.e(LOG_TAG,"Error: " + e.getMessage());
                return 0;
            }
        }
        //data check on Pet Gender
        if(values.containsKey(petEntry.COLUMN_PET_GENDER)){
            Integer gender = values.getAsInteger(petEntry.COLUMN_PET_GENDER);
            try{
                if(gender < petEntry.GENDER_UNKNOWN || gender > petEntry.GENDER_MALE){
                    throw new IllegalArgumentException("Invalid Pet Gender");
                }
            }catch (IllegalArgumentException e){
                Log.e(LOG_TAG,"Error: " + e.getMessage());
                return 0;
            }
        }
        //data check on Pet Weight
        if(values.containsKey(petEntry.COLUMN_PET_WEIGHT)){
            Integer weight = values.getAsInteger(petEntry.COLUMN_PET_WEIGHT);
            try{
                if(weight < 0 && weight != null){
                    throw new IllegalArgumentException("Pet requires valid weight");
                }
            }catch (IllegalArgumentException e){
                Log.e(LOG_TAG,"Error: " + e.getMessage());
                return 0;
            }
        }

        //verify there are any values
        if(values.size() == 0){
            return 0;
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //update database and save number of affected rows
        int count = database.update(petEntry.TABLE_NAME, values, selection, selectionArgs);
        //close database and return count
        database.close();
        return count;
    }
}
