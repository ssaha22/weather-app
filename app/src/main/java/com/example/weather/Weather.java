package com.example.weather;

import java.lang.reflect.Field;

public class Weather {
    private final int temperature;
    private final String locationName;
    private final String weatherCondition;
    private final int weatherConditionID;

    public Weather(int temperature, String locationName, String weatherCondition, int weatherConditionID) {
        this.temperature = temperature;
        this.locationName = locationName;
        this.weatherCondition = weatherCondition;
        this.weatherConditionID = weatherConditionID;
    }

    public int getIconID() {
        int iconID;
        String iconName = "wi_owm_" + weatherConditionID;
        try {
            Class res = R.string.class;
            Field field = res.getField(iconName);
            iconID = field.getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
            iconID = R.string.wi_owm_800;
        }
        return iconID;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }
}
