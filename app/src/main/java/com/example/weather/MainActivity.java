package com.example.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefresh;
    TextView temperateUnit, temperature, locationName, weatherCondition, weatherIcon;
    ProgressBar progressBar;

    FusedLocationProviderClient fusedLocationClient;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        temperateUnit = findViewById(R.id.temperatureUnit);
        temperature = findViewById(R.id.temperature);
        locationName = findViewById(R.id.locationName);
        weatherCondition = findViewById(R.id.weatherCondition);
        weatherIcon = findViewById(R.id.weatherIcon);
        progressBar = findViewById(R.id.progressBar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        queue = Volley.newRequestQueue(this);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.places_api_key));
        PlacesClient placesClient = Places.createClient(this);

        initializeSearch();

        getCurrentLocationWeather();

        swipeRefresh.setOnRefreshListener(this::getCurrentLocationWeather);
    }

    private void initializeSearch() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocompleteFragment);
        autocompleteFragment.setHint("Enter a location");
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES);
        autocompleteFragment.setPlaceFields(Collections.singletonList(Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                getWeatherData(place.getLatLng().latitude, place.getLatLng().longitude);
            }

            @Override
            public void onError(@NotNull Status status) {
                System.out.println(status);
            }
        });
    }

    private void getCurrentLocationWeather() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    getWeatherData(location.getLatitude(), location.getLongitude());
                } else {
                    getWeatherData(49.2827, -123.1207);
                }
            });
        } else {
            getWeatherData(49.2827, -123.1207);
        }
    }

    private void getWeatherData(double latitude, double longitude) {
        String url = getURL(latitude, longitude);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null,
                        response -> {
                            Weather currentWeather = parseAPIResponse(response);
                            displayWeatherData(currentWeather);
                        },
                        error -> displayWeatherData(new Weather()));
        queue.add(jsonObjectRequest);
    }

    private String getURL(double latitude, double longitude) {
        final String URL = "https://api.openweathermap.org/data/2.5/weather?";
        final String API_KEY = getResources().getString(R.string.owm_api_key);
        return String.format(Locale.getDefault(), "%slat=%f&lon=%f&appid=%s&units=metric",
                URL, latitude, longitude, API_KEY);
    }

    private Weather parseAPIResponse(JSONObject response) {
        try {
            JSONArray weather = response.getJSONArray("weather");
            JSONObject weatherConditionObject = weather.getJSONObject(0);
            int weatherConditionID = weatherConditionObject.getInt("id");
            String weatherCondition = weatherConditionObject.getString("main");
            JSONObject temperatureObject = response.getJSONObject("main");
            int temperature = (int) Math.round(temperatureObject.getDouble("temp"));
            String locationName = response.getString("name");
            return new Weather(temperature, locationName, weatherCondition, weatherConditionID);
        } catch (Exception e) {
            return new Weather();
        }
    }

    private void displayWeatherData(Weather currentWeather) {
        swipeRefresh.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        temperateUnit.setText(R.string.celsius_symbol);
        temperature.setText(String.valueOf(currentWeather.getTemperature()));
        locationName.setText(currentWeather.getLocationName());
        weatherCondition.setText(currentWeather.getWeatherCondition());
        weatherIcon.setText(currentWeather.getIconID());
    }
}