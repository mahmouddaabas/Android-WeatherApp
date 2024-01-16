package se.umu.mada0474.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.util.prefs.Preferences


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
        val temperatureTxt = findViewById<TextView>(R.id.temperatureTxt);
        val cityTxt = findViewById<TextView>(R.id.cityTxt);
        val temperature = data.getJSONObject("current_weather").getString("temperature")
        cityTxt.text = "$cityName"
        temperatureTxt.text = "$temperature °C"

        setWeatherImage(temperature)
        saveWeatherToHistory(temperature, cityName)

        val long = data.getString("longitude")
        val lat = data.getString("latitude")
        val time = data.getJSONObject("current_weather").getString("time")
        val windspeed = data.getJSONObject("current_weather").getString("windspeed")

        findViewById<TextView>(R.id.longTxt).text = "Longitude: $long"
        findViewById<TextView>(R.id.latTxt).text = "Latitude: $lat"
        findViewById<TextView>(R.id.timeTxt).text = "Time: $time"
        findViewById<TextView>(R.id.windSpeedTxt).text = "Wind Speed: $windspeed"
    }

    fun setWeatherImage(temperature: String){
        val weatherImage = findViewById<ImageView>(R.id.weatherImage)
        val temperatureDouble = temperature.toDouble()
        if(temperatureDouble >= 10){
            weatherImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hot))
        }
        else if(temperatureDouble < 10 && temperatureDouble > 0){
            weatherImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mid))
        }
        else {
            weatherImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.cold))
        }
    }

    private fun saveWeatherToHistory(temperature: String, cityName: String?) {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val gson = Gson()

        // Retrieve existing weather history
        val existingJson = sharedPreferences.getString("WEATHER_HISTORY_KEY", null)
        val existingType = object : TypeToken<ArrayList<String>>() {}.type
        val existingWeatherHistory: ArrayList<String> = gson.fromJson(existingJson, existingType) ?: ArrayList()

        // Add new entry
        existingWeatherHistory.add("$temperature °C $cityName")

        // Convert the updated list to JSON
        val updatedJson = gson.toJson(existingWeatherHistory)

        // Save the updated list to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("WEATHER_HISTORY_KEY", updatedJson)
        }.apply()
    }

}