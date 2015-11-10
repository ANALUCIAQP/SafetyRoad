package com.android.analucia.safetyroad.database;


import android.provider.BaseColumns;

public class RoadDataContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public RoadDataContract() {}

    /* Inner class that defines the table contents */
    public static abstract class RoadEntry implements BaseColumns {
        public static final String TABLE_NAME = "roads";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_A1 = "a1";
        public static final String COLUMN_NAME_A2 = "a2";
        public static final String COLUMN_NAME_A3 = "a3";
        public static final String COLUMN_NAME_C1 = "c1";
        public static final String COLUMN_NAME_C2 = "c2";
        public static final String COLUMN_NAME_C3 = "c3";
        public static final String COLUMN_NAME_D1 = "d1";
        public static final String COLUMN_NAME_D2 = "d2";
        public static final String COLUMN_NAME_D3 = "d3";
        public static final String COLUMN_NAME_SPEED_LIMITS = "speed_limits";

    }
}
