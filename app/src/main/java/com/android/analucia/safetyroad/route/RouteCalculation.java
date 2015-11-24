package com.android.analucia.safetyroad.route;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

import com.android.analucia.safetyroad.tasks.DownloadTaskRoute;
import com.android.analucia.safetyroad.R;

import java.io.IOException;



public class RouteCalculation extends Activity {

    EditText atvPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_calculation_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .9), (int) (height * .6));

        atvPlaces = (EditText) findViewById(R.id.search_edittext);

        /*

        // Getting a reference to the AutoCompleteTextView
        atvPlaces = (AutoCompleteTextView) findViewById(R.id.search_edittext);
        atvPlaces.setThreshold(1);

        // Adding textchange listener
        atvPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Creating a DownloadTaskRoute to download Google Places matching "s"
                placesDownloadTask = new DownloadTaskRoute(atvPlaces, RouteCalculation.this);

                // Getting url to the Google Places Autocomplete api
                String url = getAutoCompleteUrl(s.toString());

                // Start downloading Google Places
                // This causes to execute doInBackground() of DownloadTask class
                placesDownloadTask.execute(url);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
        */

    }

    private String getAutoCompleteUrl(String place) {

        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=AIzaSyDIzVknmoFeAVGb-6zRWtHDNI8XG1VJ1Xo";

        // place to be be searched
        String input = "input=" + place.replace(" ", "%20");

        // place type to be searched
        String types = "types=geocode";

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = input + "&" + types + "&" + sensor + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

        return url;
    }


    public void goRoute_Click(View view)throws IOException {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", atvPlaces.getText().toString());
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}








