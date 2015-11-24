package com.android.analucia.safetyroad;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.analucia.safetyroad.database.RoadDataContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RoadDataDbWriter {


    private boolean tableHasData(SQLiteOpenHelper mDbHelper) {


        SQLiteDatabase mDatabase = mDbHelper.getWritableDatabase();

        Cursor cursor = mDatabase.rawQuery("select * from " + RoadDataContract.RoadEntry.TABLE_NAME, null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }



    public void writeDB(SQLiteOpenHelper mDbHelper, Context context) {

        if (!tableHasData(mDbHelper)) {

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

            // Gets the data repository in write mode
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            String line = "";
            db.beginTransaction();
            try {
                while ((line = buffer.readLine()) != null) {
                    String[] columns = line.split(",");


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
    }
}
