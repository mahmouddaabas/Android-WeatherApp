package se.umu.mada0474.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject


class MainActivity : ToolbarHandler(){

    private lateinit var apiService: ApiService
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var permissions: AppPermissions


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
        val addressList = apiService.getCoordinates(inputText.text.toString())
        if (addressList != null) {
            apiService.getWeatherData(addressList[0].latitude, addressList[0].longitude)
        }
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
                    println(location)
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
        startActivity(intent)
    }
}