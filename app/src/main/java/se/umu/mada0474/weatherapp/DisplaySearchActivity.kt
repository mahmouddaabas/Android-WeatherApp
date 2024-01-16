package se.umu.mada0474.weatherapp

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import org.json.JSONObject
import java.util.Locale


class DisplaySearchActivity : ToolbarHandler() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_search)

        //set toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        //get data from intent
        val intent = intent;
        val weatherData = intent.getStringExtra("weatherData")
        val data = JSONObject(weatherData!!)
        val cityName = intent.getStringExtra("cityName")
        addDataToFields(data, cityName)
    }

    @SuppressLint("SetTextI18n")
    fun addDataToFields(data: JSONObject, cityName: String?) {
        val weatherTxt = findViewById<TextView>(R.id.weatherTxt);
        val temperature = data.getJSONObject("current_weather").getString("temperature")
        weatherTxt.text = "The temperature in $cityName: is $temperature °C"

        val long = data.getString("longitude")
        val lat = data.getString("latitude")
        val time = data.getJSONObject("current_weather").getString("time")
        val windspeed = data.getJSONObject("current_weather").getString("windspeed")

        findViewById<TextView>(R.id.longTxt).text = "Longitude: $long"
        findViewById<TextView>(R.id.latTxt).text = "Latitude: $lat"
        findViewById<TextView>(R.id.timeTxt).text = "Time: $time"
        findViewById<TextView>(R.id.windSpeedTxt).text = "Wind Speed: $windspeed"
    }
}