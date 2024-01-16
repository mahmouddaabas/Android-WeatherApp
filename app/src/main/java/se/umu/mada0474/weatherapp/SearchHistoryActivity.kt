package se.umu.mada0474.weatherapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryActivity : ToolbarHandler() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_history)

        //set toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        loadSearchHistory()
    }

    fun loadSearchHistory(){
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val gson = Gson()

        // Retrieve existing weather history
        val existingJson = sharedPreferences.getString("WEATHER_HISTORY_KEY", null)
        val existingType = object : TypeToken<ArrayList<String>>() {}.type
        val existingWeatherHistory: ArrayList<String> = gson.fromJson(existingJson, existingType) ?: ArrayList()
        populateListView(existingWeatherHistory)
    }

    fun populateListView(existingWeatherHistory: ArrayList<String>) {
        val listView: ListView = findViewById(R.id.listView)
        val adapter = ArrayAdapter(this, R.layout.list_view_row, existingWeatherHistory)
        listView.adapter = adapter
    }
}