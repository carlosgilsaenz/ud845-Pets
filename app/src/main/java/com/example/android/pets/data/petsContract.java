package com.example.android.pets.data;

import android.provider.BaseColumns;

/**
 * Created by csaenz on 3/7/2017.
 */

public final class petsContract{

    private petsContract(){}

    //Inner class that defines table contents of pets
    public static final class petEntry implements BaseColumns{
        //table name
        public static final String TABLE_NAME = "pets";

        //id label
        public static final String COLUMN_PET_ID = BaseColumns._ID;

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
    }
}
