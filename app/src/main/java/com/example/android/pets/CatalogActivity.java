/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.pets.data.petsContract.petEntry;


/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Tag for the log messages */
    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    private static final int PET_LOADER = 0;
    //Create Cursor Adapter for Pets List
    PetCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        //assign listView from Activity_Catalog
        final ListView petListView = (ListView) findViewById(R.id.pet_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        //initialize cursor adapter and set to listView
        mCursorAdapter = new PetCursorAdapter(this, null);
        petListView.setAdapter(mCursorAdapter);

        //initiates Loader
        getSupportLoaderManager().initLoader(PET_LOADER,null, this);

        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View petView, int position, long id) {
                // Create new implicit intent
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                // append URI with ID
                Uri uri = ContentUris.withAppendedId(petEntry.CONTENT_URI, id);
                // setData with Uri within Intents
                intent.setData(uri);
                // start Activity
                startActivity(intent);
            }
        });
    }

    private void insertPet(){
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(petEntry.COLUMN_PET_NAME, getString(R.string.default_name));
        values.put(petEntry.COLUMN_PET_GENDER, petEntry.GENDER_UNKNOWN);
        values.put(petEntry.COLUMN_PET_WEIGHT, 0);

        // Insert the new row, returning the primary key value of the new row
        //long newRowId = db.insert(petEntry.TABLE_NAME, null, values);
        Uri uri = getContentResolver().insert(petEntry.CONTENT_URI, values);

        //perform toast for given outcome
        if(uri == null){
            Toast.makeText(this,R.string.save_unsuccessful,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,R.string.save_successful,Toast.LENGTH_SHORT).show();
        }
    }

    private void byeByePets() {
        int delCount = getContentResolver().delete(petEntry.CONTENT_URI,null,null);

        if(delCount == 0){
            Toast.makeText(this,"Pet Deletion Failed", Toast.LENGTH_SHORT);
        }else{
            Toast.makeText(this,"Pet Deletion Successful", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                byeByePets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //column selection for query method
        String projection[] = {petEntry._ID,
                petEntry.COLUMN_PET_NAME,
                petEntry.COLUMN_PET_BREED };
        //create and return CursorLoader
        return new CursorLoader(this, petEntry.CONTENT_URI,projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //wipe contents
        mCursorAdapter.swapCursor(null);
    }
}
