package com.android.analucia.safetyroad.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AutoCompleteTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadTaskRoute extends AsyncTask<String, Void, String>{

    ParserTaskRoute placesParserTask;
    AutoCompleteTextView atvPlaces;
    Activity mActivity ;

    // Constructor
    public DownloadTaskRoute(AutoCompleteTextView atv,Activity activity){
        super();
        this.atvPlaces = atv;
        this.mActivity = activity;

    }

    @Override
    protected String doInBackground(String... url) {

        // For storing data from web service
        String data = "";

        try{
            // Fetching the data from web service
            data = downloadUrl(url[0]);
        }catch(Exception e){
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // Creating ParserTask for parsing Google Places
        placesParserTask = new ParserTaskRoute(atvPlaces,mActivity);

        // Start parsing google places json data
        // This causes to execute doInBackground() of ParserTask class
        placesParserTask.execute(result);


    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            Log.e("PROVA", urlConnection.toString());

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception  download url", e.toString());
        }finally{
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();
        }
        Log.e("PROVA2", data);
        return data;
    }


}
