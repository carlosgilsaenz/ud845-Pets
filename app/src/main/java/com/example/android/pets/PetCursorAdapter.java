package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.android.pets.data.petsContract.petEntry;


/**
 * Created by csaenz on 3/16/2017.
 */

public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Find fields to change in list_item
        TextView nameView = (TextView) view.findViewById(R.id.name);
        TextView breedView = (TextView) view.findViewById(R.id.breed);

        //Verify contents by referencing index
        int nameIndex = cursor.getColumnIndex(petEntry.COLUMN_PET_NAME);
        int breedIndex = cursor.getColumnIndex(petEntry.COLUMN_PET_BREED);
        String nameString;
        String breedString;

        //checks name value
        if(nameIndex < 0){
            //no value found use default
            nameString = "Tobi";
        }else{
            nameString = cursor.getString(nameIndex);
        }

        //checks breed value
        if(breedIndex < 0){
            //no value found use default
            breedString = "TBD";
        }else{
            breedString = cursor.getString(breedIndex);
        }

        //set texts for each TextViews within list_item
        nameView.setText(nameString);
        breedView.setText(breedString);
    }
}
