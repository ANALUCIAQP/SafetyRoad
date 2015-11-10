package com.android.analucia.safetyroad.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;

import com.android.analucia.safetyroad.route.PlaceJSONParserRoute;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class ParserTaskRoute extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

    AutoCompleteTextView atvPlaces;
    Activity mActivity;

    public ParserTaskRoute(AutoCompleteTextView atv,  Activity activity){
        super();
        this.atvPlaces = atv;
        this.mActivity = activity;
    }

    @Override
    protected List<HashMap<String, String>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<HashMap<String, String>> list = null;

        try{
            jObject = new JSONObject(jsonData[0]);

            PlaceJSONParserRoute placeJsonParserRoute = new PlaceJSONParserRoute();
            // Getting the parsed data as a List construct
            list = placeJsonParserRoute.parse(jObject);


        }catch(Exception e){
            Log.d("Exception", e.toString());
        }
        return list;
    }


    @Override
    protected void onPostExecute(List<HashMap<String, String>> result) {


        String[] from = new String[] { "description"};
        int[] to = new int[] { android.R.id.text1 };

        // Creating a SimpleAdapter for the AutoCompleteTextView
        SimpleAdapter adapter = new SimpleAdapter(mActivity.getBaseContext() , result, android.R.layout.simple_list_item_1, from, to);

        // Setting the adapter
        atvPlaces.setAdapter(adapter);


    }


}
