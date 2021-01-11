package com.example.weather;

public class Weather {
    private final int temperature;
    private final String location;
    private final String weatherCondition;
    private final int weatherConditionID;

    public Weather(int temperature, String location, String weatherCondition, int weatherConditionID) {
        this.temperature = temperature;
        this.location = location;
        this.weatherCondition = weatherCondition;
        this.weatherConditionID = weatherConditionID;
    }

    public String getIcon() {
        return "@string/wi_owm" + weatherConditionID;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getLocation() {
        return location;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }
}
