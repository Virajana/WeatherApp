package com.androdocs.weatherapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ImageButton searchbtn;
    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, windTxt, pressureTxt, humidityTxt, cityname;


    public void loadWeatherData(String city) {

        /* Showing the ProgressBar, Making the main design GONE */
        findViewById(R.id.loader).setVisibility(View.VISIBLE);
        findViewById(R.id.mainContainer).setVisibility(View.GONE);
        findViewById(R.id.errorText).setVisibility(View.GONE);


        String url = "https://openweathermap.org/data/2.5/weather?q=" + city + "&appid=b6907d289e10d714a6e88b30761fae22";

        //Constructing the OkHttpClient Client object
        OkHttpClient client = new OkHttpClient();
        //Constructing the OkHttpClient request object
        Request request = new Request.Builder().url(url).header("Content-Type", "application/json").build();

        //Call the request using asynchronous calls
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                Toast.makeText(MainActivity.this, mMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                //Get the web response as a String
                final String mMessage = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObj = new JSONObject(mMessage);
                            JSONObject main = jsonObj.getJSONObject("main");
                            JSONObject sys = jsonObj.getJSONObject("sys");
                            JSONObject wind = jsonObj.getJSONObject("wind");
                            JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                            Long updatedAt = jsonObj.getLong("dt");
                            String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                            String temp = main.getString("temp") + "°C";
                            String tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
                            String tempMax = "Max Temp: " + main.getString("temp_max") + "°C";
                            String pressure = main.getString("pressure");
                            String humidity = main.getString("humidity");

                            Long sunrise = sys.getLong("sunrise");
                            Long sunset = sys.getLong("sunset");
                            String windSpeed = wind.getString("speed");
                            String weatherDescription = weather.getString("description");

                            String address = jsonObj.getString("name") + ", " + sys.getString("country");


                            addressTxt.setText(address);
                            updated_atTxt.setText(updatedAtText);
                            statusTxt.setText(weatherDescription.toUpperCase());
                            tempTxt.setText(temp);
                            temp_minTxt.setText(tempMin);
                            temp_maxTxt.setText(tempMax);
                            sunriseTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                            sunsetTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
                            windTxt.setText(windSpeed);
                            pressureTxt.setText(pressure);
                            humidityTxt.setText(humidity);


                            findViewById(R.id.loader).setVisibility(View.GONE);
                            findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            findViewById(R.id.loader).setVisibility(View.GONE);
                            findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);

                            Toast.makeText(MainActivity.this, "Error Occured Please check the name", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initializeItems();
        //First Load
        loadWeatherData("Colombo");

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWeatherData(cityname.getText().toString());
            }
        });

    }

    private void initializeItems() {
        addressTxt = findViewById(R.id.address);
        updated_atTxt = findViewById(R.id.updated_at);
        statusTxt = findViewById(R.id.status);
        tempTxt = findViewById(R.id.temp);
        temp_minTxt = findViewById(R.id.temp_min);
        temp_maxTxt = findViewById(R.id.temp_max);
        sunriseTxt = findViewById(R.id.sunrise);
        sunsetTxt = findViewById(R.id.sunset);
        windTxt = findViewById(R.id.wind);
        pressureTxt = findViewById(R.id.pressure);
        humidityTxt = findViewById(R.id.humidity);

        cityname = findViewById(R.id.cityname);
        searchbtn = findViewById(R.id.searchbtn);
    }

}
