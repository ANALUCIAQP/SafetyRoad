package com.android.analucia.safetyroad.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.TextView;
import com.android.analucia.safetyroad.DistanceAndSpeeds;
import com.android.analucia.safetyroad.R;
import com.android.analucia.safetyroad.database.RoadDataContract;
import com.android.analucia.safetyroad.database.RoadDataDbHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseTask extends AsyncTask<String, Void, Integer[]> {

    private TextView limiteVelocita;
    private TextView velocitaConsigliata;
    private RoadDataDbHelper mDbHelper;

    public DatabaseTask(Activity context) {

        this.mDbHelper = new RoadDataDbHelper(context);
        this.limiteVelocita = (TextView) context.findViewById(R.id.txt_speed);
        this.velocitaConsigliata = (TextView) context.findViewById(R.id.txt_suggested_speed);
    }

    // Downloading data in non-ui thread
    @Override
    protected Integer[] doInBackground(String... params) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        /* Get near coordinates from db */
        double latitude = Double.parseDouble(params[0]);
        double longitude = Double.parseDouble(params[1]);

        double intervalLatitude = 0.0001;
        double intervalLongitude = 0.001;
        double startLatitude = latitude - intervalLatitude;
        double endLatitude = latitude + intervalLatitude;


        double startLongitude = longitude - intervalLongitude;
        double endLongitude = longitude + intervalLongitude;

        /* Selecting correct speed column */
        String speedColumnName;

        int weatherId = Integer.parseInt(params[2]);
        weatherId = weatherId/100;
        switch(weatherId) {

            case 2:
                speedColumnName = RoadDataContract.RoadEntry.COLUMN_NAME_D1;
                break;
            case 3:
                speedColumnName = RoadDataContract.RoadEntry.COLUMN_NAME_C2;
                break;
            case 7:
                speedColumnName = RoadDataContract.RoadEntry.COLUMN_NAME_D2;
                break;
            case 8:
                speedColumnName = RoadDataContract.RoadEntry.COLUMN_NAME_A3;
                break;
            case 6:
                speedColumnName = RoadDataContract.RoadEntry.COLUMN_NAME_D3;
                break;
            case 5:
                speedColumnName = RoadDataContract.RoadEntry.COLUMN_NAME_C3;
                break;
            default:
                speedColumnName = RoadDataContract.RoadEntry.COLUMN_NAME_A2;
                break;
        }


        Cursor c = db.rawQuery(
                "SELECT " + speedColumnName + ", " +
                        RoadDataContract.RoadEntry.COLUMN_NAME_SPEED_LIMITS + ", " +
                        RoadDataContract.RoadEntry.COLUMN_NAME_LATITUDE + ", " +
                        RoadDataContract.RoadEntry.COLUMN_NAME_LONGITUDE + "\n" +
                        "FROM " + RoadDataContract.RoadEntry.TABLE_NAME +
                        " WHERE " + RoadDataContract.RoadEntry.COLUMN_NAME_LATITUDE + " > ? " +
                        "AND " + RoadDataContract.RoadEntry.COLUMN_NAME_LATITUDE + " < ? " +
                        "AND " + RoadDataContract.RoadEntry.COLUMN_NAME_LONGITUDE + " > ? " +
                        "AND " + RoadDataContract.RoadEntry.COLUMN_NAME_LONGITUDE + " < ?",
                new String[]{startLatitude + "", endLatitude + "", startLongitude + "", endLongitude + ""}
        );

        if (c.getCount() > 0) {
            c.moveToFirst();
            int suggestedSpeed, speedLimit;
            double distance, lat, lng;
            List<DistanceAndSpeeds> results = new ArrayList<DistanceAndSpeeds>();
            while (!c.isAfterLast()) {
                suggestedSpeed = c.getInt(
                        c.getColumnIndexOrThrow(RoadDataContract.RoadEntry.COLUMN_NAME_A2)
                );

                speedLimit = c.getInt(
                        c.getColumnIndexOrThrow(RoadDataContract.RoadEntry.COLUMN_NAME_SPEED_LIMITS)
                );

                lat = c.getInt(
                        c.getColumnIndexOrThrow(RoadDataContract.RoadEntry.COLUMN_NAME_LATITUDE));

                lng = c.getInt(
                        c.getColumnIndexOrThrow(RoadDataContract.RoadEntry.COLUMN_NAME_LONGITUDE));

                distance = Math.sqrt(Math.pow(69.1 * (lat - latitude), 2) +
                        Math.pow(69.1 * (longitude - lng) * Math.cos(lat / 57.3), 2));

                results.add(new DistanceAndSpeeds(distance, suggestedSpeed, speedLimit));
                c.moveToNext();
            }

            c.close();

            DistanceAndSpeeds minDistanceAndSpeed = Collections.min(results);

            return new Integer[]{minDistanceAndSpeed.getSuggestedSpeed(),
                    minDistanceAndSpeed.getSpeedLimit()};
        }

        c.close();
        return new Integer[]{0, 0};
    }

    // Executes in UI thread, after the execution of
    // doInBackground()
    @Override
    protected void onPostExecute(Integer[] result) {
        super.onPostExecute(result);

        this.limiteVelocita.setText(result[1] + "");
        this.velocitaConsigliata.setText(result[0] + "");

    }


}
