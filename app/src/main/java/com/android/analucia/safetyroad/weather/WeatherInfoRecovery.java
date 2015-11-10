package com.android.analucia.safetyroad.weather;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.analucia.safetyroad.R;
import com.android.analucia.safetyroad.weather.RemoteFetch;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherInfoRecovery {

    TextView cityField;
    TextView updatedField;
    TextView detailsField ;
    TextView currentTemperatureField;
    TextView weatherIcon;
    Typeface weatherFont;
    Handler handler;

    Activity acctivity;

    int statusIcon = R.string.weather_thunder;

    public WeatherInfoRecovery(Activity acctivity)
    {
        handler = new Handler();
        this.acctivity = acctivity;
    }


    public View onCreateLayout( LayoutInflater inflater)
    {
        weatherFont =  Typeface.createFromAsset(acctivity.getAssets(), "fonts/weather.ttf");

        View rootView = inflater.inflate(R.layout.layout_weather, null);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);

        return rootView;
    }

    private void updateWeatherData(final String latitudine, final String longitudine){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(acctivity, latitudine, longitudine);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(acctivity,
                                    acctivity.getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                            updateWeatherData(latitudine,longitudine);
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp"))+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";

        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                statusIcon = R.string.weather_sunny;
                icon = acctivity.getString(statusIcon);
            } else {
                statusIcon = R.string.weather_clear_night;
                icon = acctivity.getString(statusIcon);
            }
        } else {
            switch(id) {
                case 2 :  statusIcon = R.string.weather_thunder;
                    icon = acctivity.getString(statusIcon);
                    break;
                case 3 : statusIcon = R.string.weather_drizzle;
                    icon = acctivity.getString(statusIcon);
                    break;
                case 7 : statusIcon = R.string.weather_foggy;
                    icon = acctivity.getString(statusIcon);
                    break;
                case 8 :  statusIcon = R.string.weather_cloudy;
                    icon = acctivity.getString(statusIcon);
                    break;
                case 6 : statusIcon = R.string.weather_snowy;
                    icon = acctivity.getString(statusIcon);
                    break;
                case 5 : statusIcon = R.string.weather_rainy;
                    icon = acctivity.getString(statusIcon);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    public int  iconStatusWeather()
    {

        return this.statusIcon;
    }


    public void changeLocation(String latitudine, String longitudine){
        updateWeatherData(latitudine, longitudine);
    }

    public TextView getCityField()
    {

        return cityField;
    }

    public TextView getUpdatedField()
    {

        return updatedField;
    }

    public TextView getDetailsField() {

        return detailsField;
    }

    public TextView getCurrentTemperatureField() {

        return currentTemperatureField;
    }

    public TextView getWeatherIcon() {

        return weatherIcon;
    }

    public Typeface getWeatherFont() {

        return weatherFont;
    }

}
