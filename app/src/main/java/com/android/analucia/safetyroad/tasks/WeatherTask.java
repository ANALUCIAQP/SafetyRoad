package com.android.analucia.safetyroad.tasks;

import android.content.Context;
import android.os.AsyncTask;
import com.android.analucia.safetyroad.weather.RemoteFetch;
import org.json.JSONObject;

public class WeatherTask extends AsyncTask<String, Void, JSONObject> {

    private Context acctivity;

    public WeatherTask(Context acctivity) {

        this.acctivity = acctivity;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        String latitude = params[0];
        String longitude = params[1];

        return RemoteFetch.getJSON(acctivity, latitude, longitude);
    }

}
