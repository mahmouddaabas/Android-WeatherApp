package se.umu.mada0474.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.util.Locale
import kotlin.reflect.KMutableProperty


class MainActivity : ToolbarHandler(){

    private lateinit var apiService: ApiService
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var permissions: AppPermissions
    var cityName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        apiService = ApiService(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        addButtonListeners()

        permissions = AppPermissions()
        if (permissions.isLocationOk(this)){
        }else{
            permissions.requestLocationPermission(this)
        }
    }

    fun getDataFromAPISearch(){
        val inputText = findViewById<EditText>(R.id.cityInputTxt)
        apiService.getCoordinatesFromName(inputText.text.toString(), object : ApiService.ApiResponseCallback {
            override fun onResponse(body: String?) {
                // Handle the API response body here
                if (body != null) {
                    val data = JSONObject(body)
                    val latitude = data.getJSONArray("results").getJSONObject(0).getString("latitude")
                    val longitude = data.getJSONArray("results").getJSONObject(0).getString("longitude")
                    val name = data.getJSONArray("results").getJSONObject(0).getString("name")
                    cityName = name //save this to pass to DisplaySearchActivity
                    apiService.getWeatherData(latitude.toDouble(), longitude.toDouble())
                } else {
                    println("Not found.")
                }
            }
            override fun onFailure() {
            }
        })
    }

    fun getDataFromAPIMobileLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    apiService.getWeatherData(location.latitude, location.longitude)
                }
            }
    }

    fun addButtonListeners(){
        findViewById<Button>(R.id.searchBtn).setOnClickListener {
            getDataFromAPISearch()
        }

        findViewById<Button>(R.id.myLocBtn).setOnClickListener {
            getDataFromAPIMobileLocation()
        }
    }

    fun handleReceivedDataFromAPI(json: JSONObject){
        println(json)
        val intent = Intent(this, DisplaySearchActivity::class.java)
        intent.putExtra("weatherData", json.toString())
        if(cityName == null){
            val long = json.getString("longitude")
            val lat = json.getString("latitude")
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address>? = geocoder.getFromLocation(lat.toDouble(), long.toDouble(), 1)
            val cityName: String = addresses!![0].getAddressLine(0)
            intent.putExtra("cityName", cityName)
        }
        else {
            intent.putExtra("cityName", cityName)
            cityName = null
        }
        startActivity(intent)
    }
}