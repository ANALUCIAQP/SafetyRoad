package com.android.analucia.safetyroad.weather;


public class ObjectWeather {

    private String description;
    private String nameCountry;
    private String webIcon;


    public String getWebIcon() {

        return webIcon;
    }

    public void setWebIcon(String webIcon) {

        this.webIcon = webIcon;
    }

    public String getNameCountry() {

        return this.nameCountry;
    }

    public void setNameCountry(String nameCountry) {

        this.nameCountry = nameCountry;
    }


    public String getDescription() {

        return this.description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

}
