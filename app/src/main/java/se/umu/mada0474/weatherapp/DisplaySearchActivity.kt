package se.umu.mada0474.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject


/**
 * This class manages the display of the weather data.
 * @author Mahmoud Daabas
 */
class DisplaySearchActivity : ToolbarHandlerActivity() {

    /**
     * Creates the view.
     */
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

    /**
     * Adds the data that was passed from MainActivity to fields.
     */
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

    /**
     * Sets an icon(hot/mid/cold) to the ImageView depending on the temperature.
     */
    private fun setWeatherImage(temperature: String){
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

    /**
     * Saves the weather (temperature and city) to the shared preferences.
     * This is then displayed in SearchHistoryActivity.
     */
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

    /**
     * Saves the state of the application.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("temperature", findViewById<TextView>(R.id.temperatureTxt).text.toString())
        outState.putString("city", findViewById<TextView>(R.id.cityTxt).text.toString())
        outState.putString("long", findViewById<TextView>(R.id.longTxt).text.toString())
        outState.putString("lat", findViewById<TextView>(R.id.latTxt).text.toString())
        outState.putString("time", findViewById<TextView>(R.id.timeTxt).text.toString())
        outState.putString("windspeed", findViewById<TextView>(R.id.windSpeedTxt).text.toString())
    }

    /**
     * Restores the state of the application.
     */
    @SuppressLint("SetTextI18n")
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val city = savedInstanceState.getString("city")
        findViewById<TextView>(R.id.cityTxt).text = city
        val temperature = savedInstanceState.getString("temperature")
        findViewById<TextView>(R.id.temperatureTxt).text = "$temperature"
        val long = savedInstanceState.getString("long")
        findViewById<TextView>(R.id.longTxt).text = "$long"
        val lat = savedInstanceState.getString("lat")
        findViewById<TextView>(R.id.latTxt).text = "$lat"
        val time = savedInstanceState.getString("time")
        findViewById<TextView>(R.id.timeTxt).text = "$time"
        val windspeed = savedInstanceState.getString("windspeed")
        findViewById<TextView>(R.id.windSpeedTxt).text = "$windspeed"
    }

}