package com.android.analucia.safetyroad;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.analucia.safetyroad.database.RoadDataDbHelper;
import com.android.analucia.safetyroad.route.RouteCalculation;
import com.android.analucia.safetyroad.tasks.DatabaseTask;
import com.android.analucia.safetyroad.tasks.DownloadTask;
import com.android.analucia.safetyroad.weather.WeatherInfoRecovery;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends FragmentActivity {


    GoogleMap map;
    private WeatherInfoRecovery infoWeather;
    ImageButton mBtn_search_route;
    private TextView btnmeteo;
    private Location location;
    private Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Getting reference to the find button
        mBtn_search_route = (ImageButton) findViewById(R.id.btn_show);
        btnmeteo = (TextView) findViewById(R.id.btn3);
        Typeface weatherFont = Typeface.createFromAsset(this.getAssets(), "fonts/weather.ttf");
        btnmeteo.setTypeface(weatherFont);

        infoWeather = new WeatherInfoRecovery(this);
        btnmeteo.setText(this.getString(infoWeather.iconStatusWeather()));
        btnmeteo.setTextColor(Color.WHITE);

        setUpMapIfNeeded();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #map} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #map} is not null.
     */
    private void setUpMap() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String provider = locationManager.getBestProvider(criteria, true);

        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                showCurrentLocation(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        locationManager.requestLocationUpdates(provider, 2000, 0, locationListener);

        // Getting initial Location
        location = locationManager.getLastKnownLocation(provider);


        // Show the initial location
        if (location != null) {
            showCurrentLocation(location);
        }
    }

    private void updateWeatherBtn(String latitude, String longitude) {

        infoWeather.changeLocation(latitude, longitude);

    }

    private void showCurrentLocation(Location location) {

        if (marker != null) {
            marker.remove();
        }

        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        marker = map.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude())
                .flat(true)
                .title("I'm here!")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 18));

        DatabaseTask databaseTask = new DatabaseTask(this);

        JSONObject json = infoWeather.getWeatherData(location.getLatitude() + "", location.getLongitude() + "");

        try {
            infoWeather.chooseWeatherIcon(json.getJSONArray("weather").getJSONObject(0).getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

            databaseTask.execute(location.getLatitude() + "", location.getLongitude() + "",
                    (json.getJSONArray("weather").getJSONObject(0).getInt("id") + ""));
            btnmeteo.setText(this.getString(infoWeather.iconStatusWeather()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    /*                         METODI PER IL CALCOLO DEL METEO                        */


    private void runWeather_btn(double lat, double lnt) {

        String latitudine = Double.toString(lat); // returns latitude
        String longitudine = Double.toString(lnt); // returns longitude

        //Toast.makeText(this, "Your Location is - \nLat: " + latitudine + "\nLong: " + longitudine, Toast.LENGTH_LONG).show();

        infoWeather.changeLocation(latitudine, longitudine);

        btnmeteo.setText(this.getString(infoWeather.iconStatusWeather()));
    }


    public void goWeather_Click(View view) throws IOException {
        if (location == null) {
            Toast.makeText(this, "Location not available", Toast.LENGTH_LONG).show();
        } else {
            showPopUp2(location.getLatitude(), location.getLongitude());
        }

    }


    private void showPopUp2(double lat, double lnt) {


        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        helpBuilder.setView(infoWeather.onCreateLayout(inflater));

        runWeather_btn(lat, lnt);

        helpBuilder.setNegativeButton("Chiudi", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        // Remember, create doesn't show the dialog
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }



    /*                        METODI PER CALCOLARE IL PERCORSO TRA DUE PUNTI                           */


    public void routeDesign(String destinazione) {

        if (destinazione.equals("")) {
            Toast.makeText(getBaseContext(), "No Place is entered", Toast.LENGTH_SHORT).show();
            return;
        }

        // Checks, whether start and end locations are captured
        LatLng origin = new LatLng(this.location.getLatitude(), this.location.getLongitude());
        LatLng dest = null;
        try {
            dest = from_Address_to_coordinates(destinazione);
        } catch (IOException e) {
            e.printStackTrace();
        }

        drawStartStopMarkers(origin, dest);
        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask(map);

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }


    private void drawStartStopMarkers(LatLng origin, LatLng dest) {

        map.clear();

        map.addMarker(new MarkerOptions()
                .position(origin)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_flag_marker)));

        map.addMarker(new MarkerOptions()
                .position(dest)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_flag_marker)));


    }

    public LatLng from_Address_to_coordinates(String dAddress) throws IOException {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocationName(dAddress, 1);
        Address address = addresses.get(0);
        double longitude = address.getLongitude();
        double latitude = address.getLatitude();

        return new LatLng(latitude, longitude);
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service

        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    public void goSearchRoute_Click(View view) throws IOException {

        // Creating an intent to open the activity MapActivity
        Intent intent = new Intent(this, RouteCalculation.class);

        // Opening the activity
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //here is your result
                String result = data.getStringExtra("result");

                //draw the route

                routeDesign(result);

            }
        }
    }


}
