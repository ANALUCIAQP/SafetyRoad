package com.android.analucia.safetyroad.database;


import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RoadDataDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RoadData.db";

    private static final String INT_TYPE = " INTEGER";
    private static final String DOUBLE_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RoadDataContract.RoadEntry.TABLE_NAME + " (" +
                    RoadDataContract.RoadEntry._ID + " INTEGER PRIMARY KEY," +
                    RoadDataContract.RoadEntry.COLUMN_NAME_LATITUDE + DOUBLE_TYPE + COMMA_SEP +
                    RoadDataContract.RoadEntry.COLUMN_NAME_LONGITUDE + DOUBLE_TYPE + COMMA_SEP +
                    RoadDataContract.RoadEntry.COLUMN_NAME_A1 + INT_TYPE + COMMA_SEP +
                    RoadDataContract.RoadEntry.COLUMN_NAME_A2 + INT_TYPE + COMMA_SEP +
                    RoadDataContract.RoadEntry.COLUMN_NAME_A3 + INT_TYPE + COMMA_SEP +
                    RoadDataContract.RoadEntry.COLUMN_NAME_C1 + INT_TYPE + COMMA_SEP +
                    RoadDataContract.RoadEntry.COLUMN_NAME_C2 + INT_TYPE + COMMA_SEP +
                    RoadDataContract.RoadEntry.COLUMN_NAME_C3 + INT_TYPE + COMMA_SEP +
                    RoadDataContract.RoadEntry.COLUMN_NAME_D1 + INT_TYPE + COMMA_SEP +
                    RoadDataContract.RoadEntry.COLUMN_NAME_D2 + INT_TYPE + COMMA_SEP +
                    RoadDataContract.RoadEntry.COLUMN_NAME_D3 + INT_TYPE + COMMA_SEP +
                    RoadDataContract.RoadEntry.COLUMN_NAME_SPEED_LIMITS + INT_TYPE +
                    " )";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RoadDataContract.RoadEntry.TABLE_NAME;
    private Context context;

    public RoadDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        writeData(db);
    }


    public void writeData(SQLiteDatabase db) {

        Log.d("SafetyRoad", "Writing data into database");
        String mCSVfile = "RoadData.csv";

        AssetManager manager = context.getAssets();
        InputStream inStream = null;
        try {
            inStream = manager.open(mCSVfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));

        String line = "";
        db.beginTransaction();
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(";");


                ContentValues cv = new ContentValues();
                cv.put(RoadDataContract.RoadEntry.COLUMN_NAME_LATITUDE, columns[1].trim());
                cv.put(RoadDataContract.RoadEntry.COLUMN_NAME_LONGITUDE, columns[2].trim());
                cv.put(RoadDataContract.RoadEntry.COLUMN_NAME_A1, columns[3].trim());
                cv.put(RoadDataContract.RoadEntry.COLUMN_NAME_A2, columns[4].trim());
                cv.put(RoadDataContract.RoadEntry.COLUMN_NAME_A3, columns[5].trim());
                cv.put(RoadDataContract.RoadEntry.COLUMN_NAME_C1, columns[6].trim());
                cv.put(RoadDataContract.RoadEntry.COLUMN_NAME_C2, columns[7].trim());
                cv.put(RoadDataContract.RoadEntry.COLUMN_NAME_C3, columns[8].trim());
                cv.put(RoadDataContract.RoadEntry.COLUMN_NAME_D1, columns[9].trim());
                cv.put(RoadDataContract.RoadEntry.COLUMN_NAME_D2, columns[10].trim());
                cv.put(RoadDataContract.RoadEntry.COLUMN_NAME_D3, columns[11].trim());
                cv.put(RoadDataContract.RoadEntry.COLUMN_NAME_SPEED_LIMITS, columns[12].trim());
                db.insert(RoadDataContract.RoadEntry.TABLE_NAME, null, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
