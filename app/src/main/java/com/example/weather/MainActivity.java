package com.example.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView temperateUnit;
    TextView temperature;
    TextView locationName;
    TextView weatherCondition;
    TextView weatherIcon;
    ProgressBar progressBar;
    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temperateUnit = findViewById(R.id.temperatureUnit);
        temperature = findViewById(R.id.temperature);
        locationName = findViewById(R.id.locationName);
        weatherCondition = findViewById(R.id.weatherCondition);
        weatherIcon = findViewById(R.id.weatherIcon);
        progressBar = findViewById(R.id.progressBar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                String coordinates = "lat=" + location.getLatitude() +
                                        "&lon=" + location.getLongitude();
                                String url = "https://api.openweathermap.org/data/2.5/weather?"
                                        + coordinates
                                        + "&appid=9eb0243ad60b07012fa40f831ce447bc&units=metric";
                                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                        (Request.Method.GET, url, null,
                                                response -> {
                                                    try {
                                                        JSONArray weather = response.getJSONArray("weather");
                                                        JSONObject weatherConditionObject = weather.getJSONObject(0);
                                                        int weatherConditionID = weatherConditionObject.getInt("id");
                                                        String weatherCondition = weatherConditionObject.getString("main");
                                                        JSONObject temperatureObject = response.getJSONObject("main");
                                                        int temperature = (int) Math.round(temperatureObject.getDouble("temp"));
                                                        String locationName = response.getString("name");
                                                        Weather currentWeather = new Weather(temperature, locationName, weatherCondition, weatherConditionID);
                                                        setWeatherData(currentWeather);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                },
                                                error -> error.printStackTrace());
                                queue.add(jsonObjectRequest);
                            }
                        }
                    });
        }
    }

    private void setWeatherData(Weather currentWeather) {
        progressBar.setVisibility(View.GONE);
        temperateUnit.setText(R.string.celsius_symbol);
        temperature.setText(String.valueOf(currentWeather.getTemperature()));
        locationName.setText(currentWeather.getLocationName());
        weatherCondition.setText(currentWeather.getWeatherCondition());
        weatherIcon.setText(currentWeather.getIconID());
    }
}