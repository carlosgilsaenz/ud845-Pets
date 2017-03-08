package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.pets.data.petsContract.petEntry;
/**
 * Created by Mick Jagger on 3/7/2017.
 */

public class PetDbHelper extends SQLiteOpenHelper{

    //Create TABLE string
    private static final String SQL_CREATE_PETS_TABLE =
            "CREATE TABLE " + petEntry.TABLE_NAME + " (" +
                    petEntry._ID + " INTEGER PRIMARY KEY," +
                    petEntry.COLUMN_PET_NAME + " TEXT," +
                    petEntry.COLUMN_PET_BREED + " TEXT," +
                    petEntry.COLUMN_PET_GENDER + " INTEGER," +
                    petEntry.COLUMN_PET_WEIGHT + " INTEGER)";

    //Delete TABLE string
    private static final String SQL_DELETE_PETS_TABLE =
            "DROP TABLE IF EXISTS " + petEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Shelter.db";

    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        sqLiteDatabase.execSQL(SQL_DELETE_PETS_TABLE);
        onCreate(sqLiteDatabase);
    }
}
