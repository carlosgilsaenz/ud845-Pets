package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by csaenz on 3/7/2017.
 */

public final class petsContract{

    //URI creation tools
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PETS = "pets";

    private petsContract(){}

    //Inner class that defines table contents of pets
    public static final class petEntry implements BaseColumns{
        //table name
        public static final String TABLE_NAME = "pets";

        //id label
        public static final String _ID = BaseColumns._ID;

        //name of pets
        public static final String COLUMN_PET_NAME = "pets";

        //breed of pets
        public static final String COLUMN_PET_BREED = "breed";

        //gender
        public static final String COLUMN_PET_GENDER = "gender";

        //weight
        public static final String COLUMN_PET_WEIGHT = "weight";


        //Gender categorizations
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMAIL = 2;

        //URI creation
        public static final String CONTENT_AUTHORITY = "com.example.android.pets";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_PETS = "pets";

        //create URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);
    }
}
